package b9.components.graph

import java.util.UUID

import b9.TreeOps._
import b9._
import b9.short._
import facades.d3js.Hierarchy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import meta.MetaSource
import monix.execution.Cancelable
import monocle.Iso
import monocle.std.tree._
import scalacss.ScalaCssReact._

import scala.scalajs.js

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object TreeGraph {
  case class GraphState(display: UUID, edit: UUID, active: UUID, fold: Set[UUID])

  case class Props(tree: TTN, metaSrc: MetaSource,

                   treeDisp: Dispatcher[TTN],
                   graphDisp: Dispatcher[GraphState],

                   width: Double,
                   height: Double,

                   left: Int = 50,
                   right: Int = 200,
                   top: Int = 10,
                   bottom: Int = 10,

                   shift: Int = 10,
                  )

  class Backend($ : BackendScope[Props, GraphState]) {
    var end: Option[Cancelable] = None

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(display: IdNode[TTN], x: Double, y: Double)(implicit dispatcher: Dispatcher[GraphState]): TagMod = {
      val levels = display.ancestors().reverse.toTagMod { node =>
        val label = node.data.get.rootLabel
        BreadCrum(
          label.name + "> ",
          dispatcher.dispatchCB { s => s.copy(display = label.uuid)}
        )
      }

      <.text(
        ^.dominantBaseline := "middle",
        ^.x := x,
        ^.y := y,
        ModelerCss.breadcrum,
        levels
      )
    }

    def joints(node: IdNode[TTN], lens: TLens, slens: Option[SLens], gs: GraphState, metaSrc: MetaSource)
              (implicit td: Dispatcher[TTN], gd: Dispatcher[GraphState]): Stream[TagMod] = {


      val jp: TagMod = Joint(td, gd, metaSrc, lens, slens, node, gs)

      val sub = node.children map { _.toStream flatMap { child =>
        val cslens = lens composeLens subForest
        val nl = cslens composeLens at(child.data.get)
        joints(child, nl, Some(cslens), gs, metaSrc)
      }} getOrElse Stream.empty[TagMod]

      jp +: sub
    }

    def render(p: Props, gs: GraphState) = {
      implicit val td = p.treeDisp
      implicit val gd = p.graphDisp

      import js.JSConverters._
      val rhroot = Hierarchy.hierarchy[TTN, IdNode[TTN]](p.tree, { n: TTN => n.subForest.toJSArray}: js.Function1[TTN, js.Array[TTN]])

      var displayRoot: IdNode[TTN] = rhroot

      rhroot.eachBefore { node =>
        val parentDisplay = node.parent.map(_.display).getOrElse(false)
        node.display =
          node.data.map(_.rootLabel.uuid == gs.display).getOrElse(false) || parentDisplay

        if ( !parentDisplay && node.display) displayRoot = node


        node.fold =
          node.data.map(tn => gs.fold.contains(tn.rootLabel.uuid)).getOrElse(false) || node.parent.map(_.fold).getOrElse(false)
        node.active = node.data.map(gs.active == _.rootLabel.uuid).getOrElse(false)
        node.edit = node.data.map(gs.edit == _.rootLabel.uuid).getOrElse(false)
      }

      val emptyJsArray = new js.Array[IdNode[TTN]]()
      Layout.rehierarchy(displayRoot,
        { n: IdNode[TTN] => if (n.fold) emptyJsArray else n.children.getOrElse(emptyJsArray) }
      )
      Layout.compact(rhroot, p.top, 0.0)

      <.svg(
        ^.width := p.width,
        ^.height:= p.height,
        <.g(
          ^.transform := transform(p.left, p.top),
          joints(rhroot, Iso.id.asLens, None, gs, p.metaSrc).reverse.toTagMod,
          breadcrums(displayRoot, p.top, p.shift)
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("TreeGraph")
    .initialStateFromProps { _.graphDisp.initialModelerState }
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
        val p = scope.props
        scope.backend.end = p.graphDisp.subscribeOpt { newState =>
          scope.modState(_ => newState).runNow()
        }
      }
    }
    .componentWillUnmount { scope =>
      Callback(scope.backend.end.map(_.cancel))
    }
    .build

  def apply(tree: TTN, metaSrc: MetaSource, td: Dispatcher[TTN], gd: Dispatcher[GraphState], width: Double, height: Double) =
    component(Props(tree, metaSrc, td, gd, width, height))
}
