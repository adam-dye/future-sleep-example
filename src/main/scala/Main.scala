import akka.actor.ActorSystem

object Main extends App {
  val system = ActorSystem("name")

  system.terminate()
}
