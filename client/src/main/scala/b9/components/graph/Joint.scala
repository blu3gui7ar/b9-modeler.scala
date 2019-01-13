package b9.components.graph

import b9._
import b9.short.{TM, keyAttr}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.{onClick, onDoubleClick, onMouseOver}
import japgolly.scalajs.react.vdom.svg_<^._
import meta.MetaAst
import meta.MetaAst._
import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/25.
  */
object Joint {
  case class Props(dispatcher: Dispatcher[ModelerState], modeler: ModelerState, parent: Option[TM], current: TM)

  class Backend($: BackendScope[Props, Unit]) {

    def click(target: TM)(e: ReactMouseEvent)(implicit dispatcher: Dispatcher[ModelerState]): Callback =
      if (e.altKey)
        Callback {
          dispatcher.dispatch(ModelerOps.goDown(target))
          ModelerOps.deferAction {
            dispatcher.dispatch(ModelerOps.flushDisplay(target))
          }
        }
      else
        Callback.empty

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def isActive(tn: TM)(implicit graph: GraphState): Boolean = graph.active eq tn

    def isEditing(tn: TM)(implicit graph: GraphState): Boolean = graph.editing eq tn

    def isFolded(tn: TM): Boolean = tn.rootLabel.attach.fold

    def isMoving(tn: TM): Boolean = tn.rootLabel.attach.display != tn.rootLabel.attach.nextDisplay

    def canGoParent(tn: TM)(implicit graph: GraphState): Boolean = graph.display.tree.rootLabel.attach eq tn.rootLabel.attach

    def canRemove(tn: TM)(implicit graph: GraphState): Boolean = isActive(tn) && !canGoParent(tn)

    def canEdit(tn: TM)(implicit graph: GraphState): Boolean = true && !isEditing(tn)

    def creates(node: TM, macros: Map[String, MetaAst.Macro], types: Map[String, MetaAst.AstNodeWithMembers])
               (implicit dispatcher: Dispatcher[ModelerState]): TagMod  = {
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
        CreateButton(name, 18 + 30 * idx, 10, true, Callback {
          dispatcher.dispatch(ModelerOps.create(node, name, MetaAst.expand(meta, macros)))
          ModelerOps.deferAction {
            dispatcher.dispatch(ModelerOps.flushHierarchy())
          }
        })
      }
    }

    def parentBtn(parent: TM)(implicit dispatcher: Dispatcher[ModelerState]): TagMod =
       ParentButton(-41, -25, Callback {
         dispatcher.dispatch(ModelerOps.goUp(parent))
         ModelerOps.deferAction {
           dispatcher.dispatch(ModelerOps.flushHierarchy())
         }
       })

    def removeBtn(current: TM, parent: TM)(implicit dispatcher: Dispatcher[ModelerState]): TagMod =
      RemoveButton(-42, 10, Callback {
        dispatcher.dispatch(ModelerOps.removeFrom(current, parent))
        ModelerOps.deferAction {
          dispatcher.dispatch(ModelerOps.flushRemoveFrom(current, parent))
        }
      })

    def link(parent: TM, current: TM): TagMod = {
      val pInfo = parent.rootLabel.attach
      val cInfo = current.rootLabel.attach
      Link(Path(
        id = "%s-%s".format(parent.rootLabel.uuid, current.rootLabel.uuid),
        display = pInfo.display && cInfo.display,
        moving = isMoving(current),
        sx = pInfo.x,
        sy = pInfo.y,
        tx = cInfo.x,
        ty = cInfo.y
      ))
    }

    def render(p: Props): VdomElement = {
      implicit val dispatcher = p.dispatcher
      implicit val graph = p.modeler.graph
      val metaRoot = p.modeler.meta
      val types = MetaAst.types(metaRoot)
      val macros = MetaAst.macros(metaRoot)

      val tn = p.current
      val parent = p.parent
      <.g(
        ModelerCss.hidden.unless(tn.rootLabel.attach.display),
        parent.map(link(_, p.current)).whenDefined,
        <.g(
          ModelerCss.joint,
          ModelerCss.jointActive.when(isActive(tn)),
          ModelerCss.jointEditing.when(isEditing(tn)),
          ModelerCss.jointFolded.when(isFolded(tn)),
          ModelerCss.moving.when(isMoving(tn)),
          keyAttr := tn.rootLabel.uuid.toString,
          ^.transform := transform(tn.rootLabel.attach.y, tn.rootLabel.attach.x),
          (onDoubleClick --> Callback {
            if (tn.rootLabel.attach.fold) {
              dispatcher.dispatch(ModelerOps.unfold(tn))
              ModelerOps.deferAction {
                dispatcher.dispatch(ModelerOps.flushHierarchy())
              }
            } else {
              dispatcher.dispatch(ModelerOps.fold(tn))
              ModelerOps.deferAction {
                dispatcher.dispatch(ModelerOps.flushDisplay(graph.display.tree))
              }
            }
          }).when(tn.subForest.nonEmpty),
          <.circle(
            ^.r := 6,
            (onMouseOver --> dispatcher.dispatchCB(ModelerOps.active(tn)))
              .when(!isActive(tn)),
            onClick ==> click(tn)
          ),
          <.text(
            ^.x := 15,
            ^.y := 3,
            ^.textAnchor := "start",
            onClick --> Callback.empty,
            tn.rootLabel.name
          ),
          parent.map(parentBtn(_)).whenDefined.when(canGoParent(tn)),
          parent.map(removeBtn(tn, _)).whenDefined.when(canRemove(tn)),
          EditButton(-12, 10, dispatcher.dispatchCB(ModelerOps.edit(tn))).when(canEdit(tn)),
          creates(tn, macros, types)
        )
      )
    }
}


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

  def apply(dispatcher: Dispatcher[ModelerState], modeler: ModelerState, parent: Option[TM], current: TM) =
    component(Props(dispatcher, modeler, parent, current))
}
