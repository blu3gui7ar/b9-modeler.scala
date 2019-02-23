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
import play.api.libs.json.JsNull

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
      else
        Callback.empty

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def canGoParent(tn: IdNode[TTN]): Boolean = {
      val p: Option[IdNode[TTN]] = tn.parent
      tn.display && p.map(!_.display).getOrElse(false)
    }

    def canRemove(tn: IdNode[TTN])(implicit graph: GraphState): Boolean = tn.active && !canGoParent(tn)

    def canEdit(tn: IdNode[TTN])(implicit graph: GraphState): Boolean = true && !tn.edit

    def creates(node: TTN, lens: TLens, macros: Map[String, MetaAst.Macro], types: Map[String, MetaAst.AstNodeWithMembers])
               (implicit dispatcher: Dispatcher[TTN]): TagMod  = {
      val meta = node.rootLabel.meta
      val children =
        if (!meta.isLeaf)
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
                (node.rootLabel.name + "[?]", meta.copy(t = l.t))
              ))
            case MapRef(m) => Some(Seq(
                (node.rootLabel.name + "[?]", meta.copy(t = m.t))
              ))
            case _ => None
          }
        else None

      children.getOrElse(Seq.empty).zipWithIndex.toTagMod { case ((name, meta), idx) =>
        CreateButton(name, 18 + 30 * idx, 10, true,
          dispatcher.dispatchCB {
            val expanded = MetaAst.expandMacro(meta)(macros)
            val newNode = treeExtractor.create(name, Stream.empty, expanded, Some(JsNull), meta.t)(macros,types)
            newNode map { n =>
              (lens composeLens subForest).set(n +: node.subForest)
            } getOrElse(identity: TTN => TTN) //TODO error msg
          }
        )
      }
    }

    def parentBtn(parent: TTN)(implicit dispatcher: Dispatcher[GraphState]): TagMod =
       ParentButton(-41, -25, dispatcher.dispatchCB { s => s.copy(display = parent.rootLabel.uuid) })

    def removeBtn(current: TTN, parent: TTN, slens: SLens)(implicit dispatcher: Dispatcher[TTN]): TagMod =
      RemoveButton(-42, 10, dispatcher.dispatchCB {
        slens.set(parent.subForest.filterNot { child => child.rootLabel.uuid == current.rootLabel.uuid })
      })

    def link(parent: IdNode[TTN], current: IdNode[TTN]): TagMod =
      Link(Path(
        id = "%s-%s".format(parent.data.get.rootLabel.uuid, current.data.get.rootLabel.uuid),
        display = parent.display && current.display,
        moving = false,
        sx = parent.x.get,
        sy = parent.y.get,
        tx = current.x.get,
        ty = current.y.get
      ))

    def render(p: Props): VdomElement = {
      implicit val treeDisp = p.td
      implicit val graphDisp = p.gd
      implicit val graphState = p.gs
      val metaRoot = p.meta
      val types = MetaAst.types(metaRoot)
      val macros = MetaAst.macros(metaRoot)

      val tn = p.current
      val parent = tn.parent
      val parentFold = tn.parent.map(_.fold).getOrElse(false)
      val show = tn.display && !parentFold
      <.g(
        parent.map(link(_, p.current)).whenDefined,
        <.g(
          ModelerCss.joint,
          ModelerCss.jointActive.when(tn.active),
          ModelerCss.jointEditing.when(tn.edit),
          ModelerCss.jointFolded.when(tn.fold),
          ModelerCss.fade.when(!show),
          keyAttr := tn.data.get.rootLabel.uuid.toString,
          ^.transform := transform(tn.y.get, tn.x.get),
          <.circle(
            ^.r := 6,
            (onMouseOver --> graphDisp.dispatchCB { s =>
              s.copy(active = tn.data.get.rootLabel.uuid)
            }).when(!tn.active && show),
            (onClick ==> click(tn.data.get)).when(show),
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
            }).when(tn.data.get.subForest.nonEmpty && show),
          ),
          <.text(
            ^.x := 15,
            ^.y := 3,
            ^.textAnchor := "start",
            ModelerCss.fade.when(!show),
            (onClick --> Callback.empty).when(show),
            tn.data.get.rootLabel.name
          ),
          parent.map(_p => parentBtn(_p.data.get)).whenDefined.when(canGoParent(tn)),
          parent.map(_p => removeBtn(tn.data.get, _p.data.get, p.slens.get)).whenDefined.when(canRemove(tn)),
          EditButton(-12, 10, graphDisp.dispatchCB(gs => gs.copy(edit = tn.data.get.rootLabel.uuid))).when(canEdit(tn)),
          creates(tn.data.get, p.lens, macros, types)
        )
      )
    }
}


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

  def apply(td: Dispatcher[TTN], gd: Dispatcher[GraphState], meta: MetaAst.Root, lens: TLens, slens: Option[SLens],
            current: IdNode[TTN], gs: GraphState) =
    component(Props(td, gd, meta, lens, slens, current, gs))
}
