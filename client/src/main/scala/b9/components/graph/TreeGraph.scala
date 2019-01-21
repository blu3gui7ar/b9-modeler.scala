package b9.components.graph

import java.util.UUID

import b9.TreeOps._
import b9._
import b9.short._
import facades.d3js.Hierarchy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import meta.MetaAst
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

  case class Props(tree: TTN, meta: MetaAst.Root,

                   treeDisp: Dispatcher[(TTN, Callback)],
                   graphDisp: Dispatcher[(GraphState, Callback)],

                   width: Double,
                   height: Double,

                   left: Int = 50,
                   right: Int = 200,
                   top: Int = 10,
                   bottom: Int = 10,
                  )

  class Backend($ : BackendScope[Props, GraphState]) {
    var end: Option[Cancelable] = None

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(display: IdNode[TTN], top: Int)(implicit dispatcher: Dispatcher[GraphState]): TagMod = {
      val parents: Stream[IdNode[TTN]] =  {
        def loop(node: Option[IdNode[TTN]]): Stream[IdNode[TTN]] = node match {
          case Some(n) => n +: loop(n.parent)
          case _ => Stream.empty
        }
        loop(Some(display))
      }

      val levels = parents.reverse.toTagMod { node =>
        val label = node.data.get.rootLabel
        BreadCrum(
          label.name + "> ",
          dispatcher.dispatchCB { s => s.copy(display = label.uuid)}
        )
      }

      <.text(
        ^.x := 0,
        ^.y := top,
        ModelerCss.breadcrum,
        levels
      )
    }

    def joints(node: IdNode[TTN], lens: TLens, slens: Option[SLens], gs: GraphState, meta: MetaAst.Root)
              (implicit td: Dispatcher[TTN], gd: Dispatcher[GraphState], top: Int): Stream[TagMod]  = {

      val parent: Option[IdNode[TTN]] = node.parent

      val parentDisplay = parent.map(_.display).getOrElse(false)
      node.display =
        node.data.map(_.rootLabel.uuid == gs.display).getOrElse(false) || parentDisplay

      if (node.display != parentDisplay) {
        Layout.apply(node)
      }


      node.fold =
        node.data.map(tn => gs.fold.contains(tn.rootLabel.uuid)).getOrElse(false) || parent.map(_.fold).getOrElse(false)
      node.active = node.data.map(gs.active == _.rootLabel.uuid).getOrElse(false)
      node.edit = node.data.map(gs.edit == _.rootLabel.uuid).getOrElse(false)


      val children: Option[js.Array[IdNode[TTN]]] = node.children
      val sub: Stream[TagMod] = children map { _.toStream flatMap { child =>
        val slens = lens composeLens subForest
        val nl = slens composeLens at(child.data.get)
        joints(child, nl, Some(slens), gs, meta)
      }} getOrElse(Stream.empty)

      if (node.display) {
        val j: TagMod = Joint(td, gd, meta, lens, slens, node, gs)
        val tagmods = j +: sub.reverse
        if (!parentDisplay)
          breadcrums(node, top)  +: tagmods
        else
          tagmods
      }
      else sub
    }

    def render(p: Props, gs: GraphState) = {
      implicit val td = p.treeDisp
      implicit val gd = p.graphDisp
      implicit val top = p.top

      import js.JSConverters._
      val rhroot = Hierarchy.hierarchy[TTN, IdNode[TTN]](p.tree,
        { n: TTN =>
          if (gs.fold.contains(n.rootLabel.uuid)) new js.Array[TTN]()
          else n.subForest.toJSArray
        }: js.Function1[TTN, js.Array[TTN]]
      )

      <.svg(
        ^.width := p.width,
        ^.height:= p.height,
        <.g(
          ^.transform := transform(p.left, p.top),
          joints(rhroot, Iso.id.asLens, None, gs, p.meta).toTagMod
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
          scope.modState(_ => newState._1, newState._2).runNow()
        }
      }
    }
    .componentWillUnmount { scope =>
      Callback(scope.backend.end.map(_.cancel))
    }
//    .componentDidMount { scope =>
//      Callback {
//        val p = scope.props
//        p.graphDisp.dispatch(ModelerOps.goUp(p.modeler.graph.root))
//        ModelerOps.deferAction {
//          p.dispatcher.dispatch(ModelerOps.flushHierarchy())
//        }
//      }
//    }
    .build

  def apply(tree: TTN, meta: MetaAst.Root, td: Dispatcher[(TTN, Callback)], gd: Dispatcher[(GraphState, Callback)], width: Double, height: Double) =
    component(Props(tree, meta, td, gd, width, height))
}
