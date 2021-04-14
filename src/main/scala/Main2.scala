import akka.actor.{ActorSystem, Scheduler}
import java.time.LocalDateTime
import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
/*
  1. Create a method called `def sleep(): Future[Unit]`
     - Take a FiniteDuration as a parameter
     - Create a Promise[Unit]
     - Use the scheduler to complete the promise after the duration
     - Return the promise's future
  2. Use sleep() in your definition of runJob()
  3. Check the change in behaviour when you run the code
  */
object Main2 extends App {
  val system = ActorSystem("name")
  implicit val dispatcher: ExecutionContext = system.dispatcher
  val scheduler: Scheduler = system.scheduler
  def sleep(duration: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]()
    scheduler.scheduleOnce(duration) {
      promise.success(())
    }
    promise.future
  }
  def runJob(jobNumber: Int) = {
    val startTime = LocalDateTime.now().getSecond
    println("Starting at job " + jobNumber)
    sleep(2.seconds).map { _ =>
      val endTime = LocalDateTime.now().getSecond - startTime
      println("Finishing job " + jobNumber)
      println("Time taken: " + endTime + " seconds")
    }
  }
  def runAllJobs = {
    val jobNumbers: List[Int] = List.range(1, 100)
    Future.traverse(jobNumbers){ jobNum =>
      runJob(jobNum)
    }
  }
  runAllJobs
  //  system.terminate()
}