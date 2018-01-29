package pkg

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router, RoutingContext}

import scala.concurrent.Future


object HttpVerticle {
  val routePath = "/hello"
  val response = "world"
  val testArrayGet = "/testarrayget"
  val testObjectGet = "/testobjectget"
  val testObjectGetParam = "/testobjectgetparam"
  val testArrayPost = "/testarraypost"
  val testObjectPost = "/testobjectpost"


  val countries = List(Country("New Zealand", "NZ"),
    Country("Australia", "AU"),
    Country("Singapore", "SG"))
}

case class Toon(id: String, avatar: String)

case class Country(val name: String, val code: String)

case class UserRequest(id: Int, email: String)

class HttpVerticle extends ScalaVerticle {

  import HttpVerticle._

  var mapper: ObjectMapper = null

  override def startFuture(): Future[_] = {
    // Create a router to answer GET-requests to "/hello" with "world"
    val router = Router.router(vertx)

    val route: Route = router // used in requestHandler below
      .get(routePath)
      .handler(_.response.end(response))

    mapper = new ObjectMapper with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val panda = Toon("panda", "smile")
    router.route(io.vertx.core.http.HttpMethod.POST, testObjectPost).handler(BodyHandler.create())
    router.get(testObjectGet).produces("application/json").handler(_.response.end(mapper.writeValueAsString(panda)))
    router.get(testArrayGet).produces("application/json").handler(_.response.end(mapper.writeValueAsString(countries)))
    //io.vertx.core.http.HttpMethod.POST
    router.route(io.vertx.core.http.HttpMethod.GET, testObjectGetParam).handler((routingContext: io.vertx.scala.ext.web.RoutingContext) => {
      apiTest(routingContext)
    })

    router.route(io.vertx.core.http.HttpMethod.POST, testObjectPost)
      .consumes("application/json")
      .handler((routingContext: io.vertx.scala.ext.web.RoutingContext) => {
        //   {"id":"2","email":"shop@gmail.com"}
        println(routingContext.getBodyAsString("UTF-8"))
        val userJson = routingContext.getBodyAsString("UTF-8").getOrElse("").toString
        val userRequest = mapper.readValue(userJson, classOf[UserRequest])
        val resp = "id " + userRequest.id + " email " + userRequest.email
        var response = routingContext.response()
        response.putHeader("content-type", "text/plain")
        response.end(resp)
        response
      })
    vertx
      .createHttpServer()
      .requestHandler(router.accept(_))
      .listenFuture(8666, "127.0.0.1") // listen in promiscuous mode
      .map { httpServer =>
      println(
        s"""httpServer.isMetricsEnabled: ${httpServer.isMetricsEnabled}
           |httpServer connected on port: ${httpServer.actualPort}
           |""".stripMargin)
    }
  }

  private def apiTest(context: RoutingContext): HttpServerResponse = context.request().getParam("id") match {
    case Some(id) => id match {
      case "ha" => {
        var response = context.response()
        response.putHeader("content-type", "application/json")
        /*
        http://localhost:8666/testobjectgetparam?id=ha
         */
        response.end(mapper.writeValueAsString(Toon("type", "paremeter")))
        response
      }

      case _ => {
        var response = context.response()
        response.putHeader("content-type", "text/plain")
        response.end("nothing")
        response
      }
    }
    case None => {
      var response = context.response()
      response.putHeader("content-type", "text/plain")
      response.end("nothing")
      response
    }
  }
}
