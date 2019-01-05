package b9.components.graph

import b9._
import b9.short._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import monix.execution.Cancelable
import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object TreeGraph {
  case class Props(
                    dispatcher: Dispatcher[ModelerState],
                    modeler: ModelerState,

                    width: Double,
                    height: Double,

                    left: Int = 50,
                    right: Int = 200,
                    top: Int = 10,
                    bottom: Int = 10
                  )

  class Backend($ : BackendScope[Props, Unit]) {
    var end: Option[Cancelable] = None

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(displayRoot: TMLoc, top: Int)(implicit dispatcher: Dispatcher[ModelerState]): TagMod = {
      val dp = displayRoot
      val parents: Stream[TMLoc] =  {
        def loop(loc: Option[TMLoc]): Stream[TMLoc] = loc match {
          case Some(l) => l +: loop(l.parent)
          case _ => Stream.empty
        }
        loop(Some(dp))
      }

      val levels = parents.reverse.toTagMod { loc =>
        BreadCrum(
          loc.tree.rootLabel.name + "> ",
          Callback {
            dispatcher.dispatch(ModelerOps.goUp(loc.tree))
            ModelerOps.deferAction {
              dispatcher.dispatch(ModelerOps.flushHierarchy())
            }
          }
        )
      }

      <.text(
        ^.x := 0,
        ^.y := top,
        ModelerCss.breadcrum,
        levels
      )
    }

    def joints(proxy: TMLoc, modeler: ModelerState)(implicit dispatcher: Dispatcher[ModelerState]): Stream[TagMod] = {
      val tagMods = modeler.graph.root.cobind { tree: TM =>
        tree.subForest.reverse.toTagMod { child => Joint(dispatcher, modeler, Some(tree), child) }
      }
      val rootTag: TagMod = Joint(dispatcher, modeler, None, modeler.graph.root)
      rootTag +: tagMods.flatten reverse
    }

    def render(p: Props) = {
      val displayRoot = p.modeler.graph.display
      implicit val dispatcher = p.dispatcher
      <.svg(
        ^.width := p.width,
        ^.height:= p.height,
        <.g(
          ^.transform := transform(p.left, p.top),
          breadcrums(displayRoot, p.top),
          TagMod(joints(displayRoot, p.modeler): _*)
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("TreeGraph")
    .stateless
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
        val p = scope.props
        p.dispatcher.dispatch(ModelerOps.goUp(p.modeler.graph.root))
        ModelerOps.deferAction {
          p.dispatcher.dispatch(ModelerOps.flushHierarchy())
        }
      }
    }
    .build

  def apply(dispatcher: Dispatcher[ModelerState], modeler: ModelerState, width: Double, height: Double) =
    component(Props(dispatcher, modeler, width, height))
}
