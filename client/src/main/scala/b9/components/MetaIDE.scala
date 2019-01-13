package b9.components

import b9.Dispatcher
import b9.TreeOps.TTN
import b9.components.editor.Editor
import b9.components.json.JsonView
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.Root
import monix.execution.Cancelable

object MetaIDE {
//  def apply(dispatcher: Dispatcher[ModelerState]) = component(Props(dispatcher))
//  case class Props(dispatcher: Dispatcher[ModelerState])

  def apply(dispatcher: Dispatcher[TTN], meta: Root) = component(Props(dispatcher, meta))
  case class Props(dispatcher: Dispatcher[TTN], meta: Root)

  class Backend($: BackendScope[Props, TTN]) {
    var end: Option[Cancelable] = None

    def render(p: Props, s: TTN): VdomTag = {
      <.div(
//        TreeGraph(p.dispatcher, s, 700, 500),
        Editor(s, None, p.dispatcher),
        JsonView(s, p.meta)
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
