import akka.actor.{Actor, ActorSystem, Props}
import java.time.LocalDateTime

import scala.concurrent.{ExecutionContext, Future, Promise}

/*
1. Write a function "runJob" that:
   - Takes a job number as a parameter
   - Prints "Starting job N"
   - Waits for 2 seconds
   - Prints "Finishing job N", plus the time elapsed
2. Write a function "runAllJobs" that:
   - Calls runJob 100 times in parallel
     (tip: Use Future.traverse or Future.sequence)
3. Verify that this is all very slow.
   You should see batches of N jobs running in parallel,
   where N is: Runtime.getRuntime.availableProcessors
*/

object Main extends App {

  val system = ActorSystem("name")

  implicit val dispatcher: ExecutionContext = system.dispatcher

  val scheduler = system.scheduler


  scheduler.scheduleOnce(5.seconds) {

  }

  val promise = Promise[Unit]()
  val future = promise.future

  promise.success(())

  println("Runtime availableProcessors: " + Runtime.getRuntime.availableProcessors)

  def runJob(jobNumber: Int) = {
    Future {
      val startTime = LocalDateTime.now().getSecond
      println("Starting at job " + jobNumber)
      Thread.sleep(2000)
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
  system.terminate()
}