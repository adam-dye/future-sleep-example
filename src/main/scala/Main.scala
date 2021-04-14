import Main.scheduler
import akka.actor.{ActorSystem, Scheduler}

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try

class FutureSyntax(scheduler: Scheduler) {
  def sleep(dur: FiniteDuration)(implicit ec: ExecutionContext): Future[Unit] = {
    val promise = Promise[Unit]()

    scheduler.scheduleOnce(dur) {
      promise.complete(Try(()))
    }

    promise.future
  }
}

object Main extends App {
  val system = ActorSystem("name")
  val scheduler = system.scheduler
  implicit val dispatcher = system.dispatcher
  val syntax = new FutureSyntax(scheduler)

  import syntax._

  def job(number: Int): Future[Unit] = {
    def now: Long =
      System.currentTimeMillis()

    val start: Long = now

    Future(println(s"Starting job ${number}"))
      .flatMap(_ => sleep(2.seconds))
      .map(_ => println(s"Finishing job ${number} " + (now - start)))
      .recover(_ => println(s"Failed job ${number} " + (now - start)))
  }

  val demo = Future.traverse((1 to 100).toList)(job).map(_ => "All done!")

  println(Await.result(demo, 10.seconds))

  system.terminate()
}