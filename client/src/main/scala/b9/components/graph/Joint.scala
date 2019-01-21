package b9.components.graph

import b9.TreeOps._
import b9._
import b9.components.graph.TreeGraph.GraphState
import b9.short.{IdNode, keyAttr, _}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs._
import japgolly.scalajs.react.vdom.svg_<^._
import meta.MetaAst
import meta.MetaAst._
import scalacss.ScalaCssReact._
import monocle.std.tree._

/**
  * Created by blu3gui7ar on 2017/5/25.
  */
object Joint {
  case class Props(td: Dispatcher[TTN], gd: Dispatcher[GraphState], meta: MetaAst.Root, lens: TLens,
                   slens: Option[SLens], current: IdNode[TTN], gs: GraphState)

  class Backend($: BackendScope[Props, Unit]) {

    def click(target: TTN)(e: ReactMouseEvent)(implicit gd: Dispatcher[GraphState]): Callback =
      if (e.altKey)
        gd.dispatchCB { s => s.copy(display = target.rootLabel.uuid)}
//        Callback {
//          gd.dispatch(ModelerOps.goDown(target))
//          ModelerOps.deferAction {
//            dispatcher.dispatch(ModelerOps.flushDisplay(target))
//          }
//        }
      else
        Callback.empty

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def isActive(tn: TTN)(implicit graph: GraphState): Boolean = graph.active eq tn.rootLabel.uuid

    def isEditing(tn: TTN)(implicit graph: GraphState): Boolean = graph.edit eq tn.rootLabel.uuid

//    def isMoving(tn: TTN): Boolean = tn.rootLabel.attach.display != tn.rootLabel.attach.nextDisplay

    def canGoParent(tn: TTN)(implicit graph: GraphState): Boolean = graph.display == tn.rootLabel.uuid

    def canRemove(tn: TTN)(implicit graph: GraphState): Boolean = isActive(tn) && !canGoParent(tn)

    def canEdit(tn: TTN)(implicit graph: GraphState): Boolean = true && !isEditing(tn)

    def creates(node: TTN, lens: TLens, macros: Map[String, MetaAst.Macro], types: Map[String, MetaAst.AstNodeWithMembers])
               (implicit dispatcher: Dispatcher[TTN]): TagMod  = {
      val meta = node.rootLabel.meta
      val children =
        if (meta.widget.isEmpty)
          meta.t flatMap {
            case TypeRef(t) => {
              val cs = node.subForest.map(_.rootLabel.name)
              types.get(t) map {
                _.members collect {
                  case Attr(name, adef) if !cs.contains(name) => (name, adef)
                }
              }
            }
            case ListRef(l) => Some(Seq(
              (node.rootLabel.name + "[?]", meta.copy(t = Some(l)))
            ))
            case MapRef(m) => Some(Seq(
              (node.rootLabel.name + "[?]", meta.copy(t = Some(m)))
            ))
            case _ => None
          }
        else None

      children.getOrElse(Seq.empty).zipWithIndex.toTagMod { case ((name, meta), idx) =>
        CreateButton(name, 18 + 30 * idx, 10, true,
          dispatcher.dispatchCB {
            val expanded = MetaAst.expand(meta, macros)
            val newNode = treeExtractor.create(name, Stream.empty, expanded)
            (lens composeLens subForest).set(newNode +: node.subForest)
          }
//          Callback {
//          dispatcher.dispatch(ModelerOps.create(node, name, MetaAst.expand(meta, macros)))
//          ModelerOps.deferAction {
//            dispatcher.dispatch(ModelerOps.flushHierarchy())
//          }
//        }
        )
      }
    }

    def parentBtn(parent: TTN)(implicit dispatcher: Dispatcher[GraphState]): TagMod =
       ParentButton(-41, -25, dispatcher.dispatchCB { s => s.copy(display = parent.rootLabel.uuid ) })

//         Callback {
//         dispatcher.dispatch(ModelerOps.goUp(parent))
//         ModelerOps.deferAction {
//           dispatcher.dispatch(ModelerOps.flushHierarchy())
//         }
//       })

    def removeBtn(current: TTN, parent: TTN, slens: SLens)(implicit dispatcher: Dispatcher[TTN]): TagMod =
      RemoveButton(-42, 10, dispatcher.dispatchCB {
        slens.set(parent.subForest.filterNot { child => child.rootLabel.uuid == current.rootLabel.uuid })
      })

//        Callback {
//        dispatcher.dispatch(ModelerOps.removeFrom(current, parent))
//        ModelerOps.deferAction {
//          dispatcher.dispatch(ModelerOps.flushRemoveFrom(current, parent))
//        }
//      })

    def link(parent: IdNode[TTN], current: IdNode[TTN]): TagMod = {
      if (parent.display && current.display)
        Link(Path(
          id = "%s-%s".format(parent.data.get.rootLabel.uuid, current.data.get.rootLabel.uuid),
          display = parent.display && current.display,
          //        moving = isMoving(current),
          moving = false,
          sx = parent.x.get,
          sy = parent.y.get,
          tx = current.x.get,
          ty = current.y.get
        ))
      else TagMod.empty
    }

    def render(p: Props): VdomElement = {
      implicit val treeDisp = p.td
      implicit val graphDisp = p.gd
      implicit val graphState = p.gs
      val metaRoot = p.meta
      val types = MetaAst.types(metaRoot)
      val macros = MetaAst.macros(metaRoot)

      val tn = p.current
      val parent: Option[IdNode[TTN]] = p.current.parent
      <.g(
        ModelerCss.hidden.unless(tn.display),
        parent.map(link(_, p.current)).whenDefined,
        <.g(
          ModelerCss.joint,
          ModelerCss.jointActive.when(tn.active),
          ModelerCss.jointEditing.when(tn.edit),
          ModelerCss.jointFolded.when(tn.fold),
//          ModelerCss.moving.when(isMoving(tn)),
          keyAttr := tn.data.get.rootLabel.uuid.toString,
          ^.transform := transform(tn.y.get, tn.x.get),
          (onDoubleClick --> Callback {
            if (tn.fold) {
              graphDisp.dispatch { gs =>
                gs.copy(fold = gs.fold.filterNot(_ == tn.data.get.rootLabel.uuid))
              }
            } else {
              graphDisp.dispatch { gs =>
                gs.copy(fold = gs.fold + tn.data.get.rootLabel.uuid)
              }
            }
          }).when(tn.data.get.subForest.nonEmpty),
          <.circle(
            ^.r := 6,
            (onMouseOver --> graphDisp.dispatchCB { s =>
              s.copy(active = tn.data.get.rootLabel.uuid)
            }).when(!tn.active),
            onClick ==> click(tn.data.get)
          ),
          <.text(
            ^.x := 15,
            ^.y := 3,
            ^.textAnchor := "start",
            onClick --> Callback.empty,
            tn.data.get.rootLabel.name
          ),
          parent.map(_p => parentBtn(_p.data.get)).whenDefined.when(canGoParent(tn.data.get)),
          parent.map(_p => removeBtn(tn.data.get, _p.data.get, p.slens.get)).whenDefined.when(canRemove(tn.data.get)),
          EditButton(-12, 10, graphDisp.dispatchCB(gs => gs.copy(edit = tn.data.get.rootLabel.uuid))).when(canEdit(tn.data.get)),
          creates(tn.data.get, p.lens, macros, types)
        )
      )
    }
}


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

//  def apply(td: Dispatcher[TTN], gd: Dispatcher[GraphState], lens: TLens, parent: Option[TTN], current: TTN,
//            gs: GraphState, display: Boolean, fold: Boolean) =
//    component(Props(td, gd, lens, parent, current, gs, display, fold))

  def apply(td: Dispatcher[TTN], gd: Dispatcher[GraphState], meta: MetaAst.Root, lens: TLens, slens: Option[SLens],
            current: IdNode[TTN], gs: GraphState) =
    component(Props(td, gd, meta, lens, slens, current, gs))
}
