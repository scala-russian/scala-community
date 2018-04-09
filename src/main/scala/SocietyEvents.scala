import State.globalContext
import korolev.Router.Root
import korolev.state.StateStorage
import korolev.server._
import korolev.blazeServer._
import korolev.execution._
import korolev.state.javaSerialization._
import korolev._

import scala.concurrent.Future

//noinspection TypeAnnotation
object SocietyEvents extends KorolevBlazeServer {

  import State.globalContext._
  import State.globalContext.symbolDsl._

  val storage: StateStorage[Future, State] = StateStorage.default[Future, State](State())
  val inputId: globalContext.ElementId     = elementId()


  private val pageHead = Seq(
    'title ("Russian Scala Society Events"),
    'link (
      'href /= "/main.css",
      'rel /= "stylesheet",
      'type /= "text/css"
    )
  )

  private val renderer: Render = {
    case state =>
      'body (
        'div ("Super TODO tracker"),
        'div (
          state.todos.keys map { name =>
            'a (
              event('click) { access =>
                access.transition(_.copy(selectedTab = name))
              },
              'href /= "/" + name.toLowerCase,
              disableHref,
              'marginLeft @= 10,
              if (name == state.selectedTab) 'strong (name)
              else name
            )
          }
        ),
        'div (
          'class /= "todos",
          state.todos(state.selectedTab).zipWithIndex map {
            case (todo, i) =>
              'div (
                'div (
                  'class /= {
                    if (!todo.done) "checkbox"
                    else "checkbox checkbox__checked"
                  },
                  // Generate transition when clicking checkboxes
                  event('click) { access =>
                    access.transition { s =>
                      val todos = s.todos(s.selectedTab)
                      val updated = todos.updated(i, todos(i).copy(done = !todo.done))
                      s.copy(todos = s.todos + (s.selectedTab -> updated))
                    }
                  }
                ),
                if (!todo.done) 'span (todo.text)
                else 'strike (todo.text)
              )
          }
        ),
        'form (
          // Generate AddTodo action when 'Add' button clicked
          event('submit) { access =>
            access.property(inputId, 'value) flatMap { value =>
              val todo = State.Todo(value, done = false)
              access.transition { s =>
                s.copy(todos = s.todos + (s.selectedTab -> (s.todos(s.selectedTab) :+ todo)))
              }
            }
          },
          'input (
            inputId,
            'type /= "text",
            'placeholder /= "What should be done?"
          ),
          'button ("Add todo")
        )
      )
  }

  val service = blazeService[Future, State, Any] from KorolevServiceConfig[Future, State, Any](
    stateStorage = storage,
    head = pageHead,
    render = renderer,
    router = { (deviceId, _) =>
      Router(
        fromState = {
          case State(tab, _) =>
            Root / tab.toLowerCase
        },
        toState = {
          case (Some(s), Root) =>
            val u = s.copy(selectedTab = s.todos.keys.head)
            Future.successful(u)
          case (Some(s), Root / name) =>
            val key = s.todos.keys.find(_.toLowerCase == name)
            Future.successful(key.fold(s)(k => s.copy(selectedTab = k)))
          case (None, Root) =>
            storage.createTopLevelState(deviceId)
          case (None, Root / name) =>
            storage.createTopLevelState(deviceId) map { s =>
              val key = s.todos.keys.find(_.toLowerCase == name)
              key.fold(s)(k => s.copy(selectedTab = k))
            }
        }
      )
    }
  )
}
