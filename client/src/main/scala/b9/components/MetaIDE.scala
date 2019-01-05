package b9.components

import b9.components.editor.Editor
import b9.components.graph.TreeGraph
import b9.components.json.JsonView
import b9.{Dispatcher, ModelerCss, ModelerOps, ModelerState}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import monix.execution.Cancelable

object MetaIDE {
  def apply(dispatcher: Dispatcher[ModelerState]) = component(Props(dispatcher))

  case class Props(dispatcher: Dispatcher[ModelerState])

  class Backend($: BackendScope[Props, ModelerState]) {
    var end: Option[Cancelable] = None

    def render(p: Props, s: ModelerState): VdomTag = {
      <.div(
        TreeGraph(p.dispatcher, s, 700, 500),
        Editor(s.graph.display, p.dispatcher),
        JsonView(s)
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("MetaIDE")
    .initialStateFromProps(_.dispatcher.initialModelerState)
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
        val p = scope.props
        scope.backend.end = p.dispatcher.subscribeOpt { newState =>
          scope.modState(_ => newState).runNow()
        }
      }
    }
    .componentWillUnmount { scope =>
      Callback(scope.backend.end.map(_.cancel))
    }
    .build

}
