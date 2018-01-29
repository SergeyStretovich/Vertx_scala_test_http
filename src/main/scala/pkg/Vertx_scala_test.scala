package pkg

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Vertx_scala_test {

  def main(args: Array[String]): Unit = {

    val vertx = Vertx.vertx()
    val startFuture = vertx.deployVerticleFuture(ScalaVerticle.nameForVerticle[HttpVerticle])
    startFuture.onComplete {
      case Success(stat) => println(s"Successfully deployed verticle $stat")
      case Failure(ex) => println(s"Failed to deploy verticle $ex")
    }
  }
}
