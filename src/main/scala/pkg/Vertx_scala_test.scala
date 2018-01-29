package pkg



import io.vertx.core.AsyncResult
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
//import pkg.DemoVerticle

object Vertx_scala_test {

  def main(args: Array[String]): Unit =
  {

val vertx = Vertx.vertx()
    val startFuture = vertx.deployVerticleFuture(ScalaVerticle.nameForVerticle[HttpVerticle])
    startFuture.onComplete{
      case Success(stat) => println(s"Successfully deployed verticle $stat")
      case Failure(ex) => println(s"Failed to deploy verticle $ex")
    }
  }
}
