package pkg

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Route, Router, RoutingContext}

import scala.concurrent.Future
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import io.netty.handler.codec.http.HttpContentEncoder.Result
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpResponseStatus._
import io.vertx.core.json.Json
import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.{Router, RoutingContext}


object HttpVerticle {
  val routePath = "/hello"
  val response = "world"
  val testArrayGet="/testarrayget"
  val testObjectGet="/testobjectget"
  val testObjectGetParam="/testobjectgetparam"
  val testArrayPost="/testarraypost"
  val testObjectPost="/testobjectpost"

  val countries=List( Country("New Zealand", "NZ"),
    Country("Australia", "AU"),
    Country("Singapore", "SG"))
}

case class Toon(id: String, avatar: String)
case class Country(val name: String, val code: String)

class HttpVerticle extends ScalaVerticle {
  import HttpVerticle._

  override def startFuture(): Future[_] = {
    // Create a router to answer GET-requests to "/hello" with "world"
    val router = Router.router(vertx)

    val route: Route = router       // used in requestHandler below
      .get(routePath)
      .handler(_.response.end(response))

    val mapper = new ObjectMapper with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val panda = Toon("panda", "smile")
    router.get(testObjectGet).produces("application/json").handler(_.response.end(mapper.writeValueAsString(panda)))
    router.get(testArrayGet).produces("application/json").handler(_.response.end(mapper.writeValueAsString(countries)))
    //io.vertx.core.http.HttpMethod.POST
    router.route(io.vertx.core.http.HttpMethod.GET,testObjectGetParam).handler((routingContext: io.vertx.scala.ext.web.RoutingContext) => {
      apiTest(routingContext)
    })

    vertx
      .createHttpServer()
      .requestHandler(router.accept(_))
      .listenFuture(8666, "127.0.0.1")    // listen in promiscuous mode
      .map { httpServer =>
      println(s"""httpServer.isMetricsEnabled: ${ httpServer.isMetricsEnabled }
                 |httpServer connected on port: ${ httpServer.actualPort }
                 |""".stripMargin)
    }
  }
  private def apiTest(context: RoutingContext): HttpServerResponse = context.request().getParam("id") match {
    case Some(id) => id match {
      case "ha" => {
        var response = context.response()
        response.putHeader("content-type", "application/json")
        val mapper = new ObjectMapper with ScalaObjectMapper
        mapper.registerModule(DefaultScalaModule)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
       /*
       http://localhost:8666/testobjectgetparam?id=ha
        */
        response.end(mapper.writeValueAsString(Toon("type","paremeter")))
        response
      }

      case _ =>{
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
