package pkg

import com.hazelcast.core.Message
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Vertx_scala_test {

  def main(args: Array[String]): Unit = {

    val vertx = Vertx.vertx()

    val startFuture = vertx.deployVerticleFuture(ScalaVerticle.nameForVerticle[HttpVerticle])
    startFuture.onComplete {
      case Success(stat) => println(s"Successfully deployed verticle $stat")
      case Failure(ex) => println(s"Failed to deploy verticle $ex")
    }


    val busFuture = vertx.deployVerticleFuture(ScalaVerticle.nameForVerticle[BusVerticle])
    busFuture.onComplete {
      case Success(stat) => println(s"Successfully deployed verticle $stat")
      case Failure(ex) => println(s"Failed to deploy verticle $ex")
    }

    val future: Future[io.vertx.scala.core.eventbus.Message[String]] =
      vertx
        .eventBus
        .sendFuture(BusVerticle.testAddress, BusVerticle.testMessage)

    future.onComplete {
      case Success(stat) => println(s"received message ${stat.body}")
      case Failure(ex) => println(s"Failed to receive message $ex")
    }

  }
}
