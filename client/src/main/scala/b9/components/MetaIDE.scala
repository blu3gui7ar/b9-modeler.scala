package b9.components

import b9.Dispatcher
import b9.TreeOps.TTN
import b9.components.editor.Editor
import b9.components.graph.TreeGraph
import b9.components.graph.TreeGraph.GraphState
import b9.components.json.JsonView
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.Root
import monix.execution.Cancelable
import monocle.Iso

object MetaIDE {
  def apply(treeDisp: Dispatcher[TTN], graphDisp: Dispatcher[GraphState], meta: Root) =
    component(Props(treeDisp, graphDisp, meta))
  case class Props(treeDisp: Dispatcher[TTN], graphDisp: Dispatcher[GraphState], meta: Root)

  class Backend($: BackendScope[Props, TTN]) {
    var end: Option[Cancelable] = None

    def render(p: Props, s: TTN): VdomTag = {
      <.div(
        TreeGraph(s, p.meta, p.treeDisp, p.graphDisp, 700, 500),
        Editor(s, Iso.id.asLens, p.treeDisp),
        JsonView(s, p.meta)
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("MetaIDE")
    .initialStateFromProps(_.treeDisp.initialModelerState)
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
        val p = scope.props
        scope.backend.end = p.treeDisp.subscribeOpt { newState =>
          scope.modState(_ => newState).runNow()
        }
      }
    }
    .componentWillUnmount { scope =>
      Callback(scope.backend.end.map(_.cancel))
    }
    .build

}
