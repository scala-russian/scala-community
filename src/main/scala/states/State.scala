package states

import korolev.Context
import korolev.execution._
import korolev.state.javaSerialization._

import scala.concurrent.Future

trait SocietyState

case class State(
  selectedTab: String = "Tab1",
  todos: Map[String, Vector[State.Todo]] = Map(
    "Tab1" -> State.Todo(5),
    "Tab2" -> State.Todo(7),
    "Tab3" -> State.Todo(2)
  )
) extends SocietyState

object State {
  val globalContext = Context[Future, SocietyState, Any]
  case class Todo(text: String, done: Boolean)
  object Todo {
    def apply(n: Int): Vector[Todo] = (0 to n).toVector map {
      i => Todo(s"This is TODO #$i", done = false)
    }
  }
}