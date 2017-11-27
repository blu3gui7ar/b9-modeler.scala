package b9.components.graph

import b9._
import b9.short._
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._

import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object TreeGraph {
  case class Props(
                    model: ModelProxy[GraphState],

                    width: Double,
                    height: Double,

                    left: Int = 50,
                    right: Int = 200,
                    top: Int = 10,
                    bottom: Int = 10
                  )

  class Backend($ : BackendScope[Props, Unit]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(displayRoot: ModelProxy[TMLoc], top: Int): TagMod = {
      val dp = displayRoot()
      val parents: Stream[TMLoc] =  {
        def loop(loc: Option[TMLoc]): Stream[TMLoc] = loc match {
          case Some(l) => loop(l.parent)
          case _ => Stream.empty
        }
        loop(Some(dp))
      }

      val levels = parents.toTagMod { loc =>
        BreadCrum(
          loc.tree.rootLabel.name + "> ",
          displayRoot.dispatchCB(GoUpAction(loc.tree))
        )
      }

      <.text(
        ^.x := 0,
        ^.y := top,
        ModelerCss.breadcrum,
        levels
      )
    }

    def joints(displayRoot: ModelProxy[TMLoc]): Stream[TagMod] = {
      val root = displayRoot().tree
      val tagMods = root.cobind { tree: TM =>
        tree.subForest.toTagMod { child => Joint(displayRoot, Some(tree), child) }
      }
      val rootTag: TagMod = Joint(displayRoot, None, root)
      rootTag +: tagMods.flatten reverse
    }

    def render(p: Props) = {
      val displayRoot = p.model.zoom(_.display)

      <.svg(
        ^.width := p.width.toString, //[BUG] https://github.com/japgolly/scalajs-react/issues/388
        ^.height:= p.height.toString,
        <.g(
          ^.transform := transform(p.left, p.top),
          breadcrums(displayRoot, p.top),
          TagMod(joints(displayRoot): _*)
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("TreeGraph")
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
          val p = scope.props.model.zoom(_.root)
          p.dispatchCB(GoUpAction(p())).async.runNow()
      }
    }
    .build

  def apply(model: ModelProxy[GraphState], width: Double, height: Double) = component(Props(model, width, height))
}
