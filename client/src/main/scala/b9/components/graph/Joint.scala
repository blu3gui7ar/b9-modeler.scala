package b9.components.graph

import b9._
import b9.short.{TN, keyAttr}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.{onClick, onDoubleClick, onMouseOver}
import japgolly.scalajs.react.vdom.svg_<^._
import meta.MetaAst
import meta.MetaAst.{Attr, ListRef, MapRef, TypeRef}

import scala.scalajs.js
import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/25.
  */
object Joint {
  case class Props(n: ModelProxy[TN])

  class Backend($: BackendScope[Props, Unit]) {
    val displayRootRO = ModelerCircuit.zoom(_.graph.displayRoot)
    val activeRO = ModelerCircuit.zoom(_.graph.activeNode)
    val editingRO = ModelerCircuit.zoom(_.graph.editingNode)
    val metaRO = ModelerCircuit.zoom(_.graph.meta)

    def click(pn: ModelProxy[TN])(e: ReactMouseEvent): Callback = {
      if (e.altKey)
        pn.dispatchCB(GoDownAction(pn()))
      else
        Callback.empty
    }

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def isActive(tn: TN): Boolean = activeRO() eq tn

    def isEditing(tn: TN): Boolean = editingRO() eq tn

    def isFolded(tn: TN): Boolean = tn.fold.getOrElse(false)

    def isMoving(tn: TN): Boolean = tn.display != tn.nextDisplay

    def canGoParent(tn: TN): Boolean = displayRootRO() eq tn

    def canRemove(tn: TN): Boolean = isActive(tn)

    def canEdit(tn: TN): Boolean = true

    def creates(pn: ModelProxy[TN]): TagMod  = {
      val node = pn()

      if (node.data == null || node.data.isEmpty) EmptyVdom
      else {
        val metaRoot = metaRO()
        val types = MetaAst.types(metaRoot)

        val children = node.data.toOption flatMap { tn =>
          val meta = tn.meta
          meta.t flatMap {
            case TypeRef(t) => {
              val cs = node.children.getOrElse(js.Array()) flatMap {
                _.data.toOption map { _.name }
              }
//              val v = node.data.get.value
//              val obj = v match {
//                case o: Js.Obj => o.obj
//                case _ => Map.empty[String, Js.Value]
//              }
              types.get(t) map {
                _.members collect {
                  case Attr(name, adef) if !cs.contains(name) => (name, adef)
                }
              }
            }
            case ListRef(l) => Some(Seq(
              (node.data.map(_.name).getOrElse("") + "[?]", meta.copy(t = Some(l)))
            ))
            case MapRef(m) => Some(Seq(
              (node.data.map(_.name).getOrElse("") + "[?]", meta.copy(t = Some(m)))
            ))
            case _ => None
          }
        }

        children.getOrElse(Seq.empty).zipWithIndex.toTagMod {
          case ((name, meta), idx) => CreateButton(name, 18 + 30 * idx, 10, true, pn.dispatchCB(CreateAction(pn(), name, meta)))
        }
      }
    }

    def hasParent(tn: TN): Boolean = (tn.parent.isDefined && tn.parent != null)

    def parentBtn(proxy: ModelProxy[TN]): TagMod = proxy().parent.toOption match {
      case Some(null) => EmptyVdom
      case Some(parent) => ParentButton(-41, -25, proxy.dispatchCB(GoUpAction(parent)))
      case _ => EmptyVdom
    }

    def removeBtn(proxy: ModelProxy[TN]): TagMod = proxy().parent.toOption match {
      case Some(null) => EmptyVdom
      case Some(parent) => RemoveButton(-42, 10, proxy.dispatchCB(RemoveFromAction(proxy(), parent)))
      case _ => EmptyVdom
    }

    def link(rtn: TN): TagMod = rtn.parent.toOption match {
      case Some(null) => EmptyVdom
      case Some(parent) => Link(Path(
          id = parent.id.getOrElse(0).toString + "-" + rtn.id.getOrElse(0).toString,
          display = parent.display.getOrElse(true) && rtn.display.getOrElse(true),
          moving = isMoving(rtn),
          sx = parent.x.getOrElse(0),
          sy = parent.y.getOrElse(0),
          tx = rtn.x.getOrElse(0),
          ty = rtn.y.getOrElse(0)
        ))
      case _ => EmptyVdom
    }

    def render(p: Props) = {
      val tn = p.n()
      <.g(
        link(tn),
        <.g(
          ModelerCss.joint,
          ModelerCss.jointActive.when(isActive(tn)),
          ModelerCss.jointEditing.when(isEditing(tn)),
          ModelerCss.jointFolded.when(isFolded(tn)),
          ModelerCss.hidden.unless(tn.display.getOrElse(true)),
          ModelerCss.moving.when(isMoving(tn)),
          keyAttr := tn.id.getOrElse(-1).toString,
          ^.transform := transform(tn.y.getOrElse(0.0), tn.x.getOrElse(0.0)),
          onDoubleClick --> p.n.dispatchCB(FoldAction(tn)),
          <.circle(
            ^.r := 6,
            (onMouseOver --> p.n.dispatchCB(ActiveAction(tn))).when(!isActive(tn)),
            onClick ==> click(p.n)
          ),
          <.text(
            ^.x := 15,
            ^.y := 3,
            ^.textAnchor := "start",
            onClick --> Callback.empty,
            tn.data.map(_.name).getOrElse("unkonwn"): String
          ),
          parentBtn(p.n).when(canGoParent(tn)),
          removeBtn(p.n),
          EditButton(-12, 10, p.n.dispatchCB(EditAction(tn))).when(canEdit(tn)),
          creates(p.n)
        )
      )
    }
  }


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

  def apply(tn: ModelProxy[TN]) = component(Props(tn))
}