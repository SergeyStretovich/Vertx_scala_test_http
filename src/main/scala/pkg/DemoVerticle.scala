package pkg

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.{Route, Router}
import pkg.HttpVerticle.{response, routePath}



class DemoVerticle extends ScalaVerticle {

  override def start(): Unit = {

    vertx
      .createHttpServer()
      .requestHandler(_.response().end("Hello World"))
      .listen(8666)
   // vertx.eventBus().consumer[String]("",a=>println(a)).completionFuture()
    //vertx.eventBus().consumer("hallo", a => println(a.body())).completionFuture()

  }
}
