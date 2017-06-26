package b9.components

import b9._
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.{onClick, onDoubleClick, onMouseOver}
import japgolly.scalajs.react.vdom.svg_<^._
import meta.TreeNode

import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/25.
  */
object Joint {
  type TN = IdNode[TreeNode]

  case class Props(n: ModelProxy[TN], onUp: Option[Callback], onRemove: Option[Callback])

  class Backend($: BackendScope[Props, Unit]) {
    val displayRootRW = ModelerCircuit.zoomTo(_.graph.displayRoot)
    val activeRW = ModelerCircuit.zoomTo(_.graph.activeNode)
    val editingRW = ModelerCircuit.zoomTo(_.graph.editingNode)

    def click(pn: ModelProxy[TN])(e: ReactMouseEvent): Callback = {
      if (e.altKey)
        pn.dispatchCB(FoldAction(pn()))
      else
        pn.dispatchCB(EditAction(pn()))
    }

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def isActive(tn: TN): Boolean = activeRW().map(_ == tn).getOrElse(false)

    def isEditing(tn: TN): Boolean = editingRW().map(_ == tn).getOrElse(false)

    def isFolded(tn: TN): Boolean = tn.data.map(_.fold).getOrElse(false)

    def canGoParent(tn: TN): Boolean = displayRootRW() == tn

    def canRemove(tn: TN): Boolean = isActive(tn)

    def canEdit(tn: TN): Boolean = true

//    def creates(buttons: Map[String, Boolean]) = buttons.zipWithIndex.toTagMod {
//      case ((name, valid), idx) => CreateButton(name, 18 + 30 * idx, 10, valid, create)
//    }

    def hasParent(tn: TN): Boolean = (tn.parent.isDefined && tn.parent != null)

    def render(p: Props) = {
      val tn = p.n()
      <.g(
        ModelerCss.joint,
        ModelerCss.jointActive.when(isActive(tn)),
        ModelerCss.jointEditing.when(isEditing(tn)),
        ModelerCss.jointFolded.when(isFolded(tn)),
        ModelerCss.hidden.unless(tn.display.getOrElse(true)),
        b9.keyAttr := tn.id.getOrElse(-1).toString,
        ^.transform := transform(tn.y.getOrElse(0.0), tn.x.getOrElse(0.0)),
        onMouseOver --> p.n.dispatchCB(ActiveAction(tn)),
        onDoubleClick --> p.n.dispatchCB(DisplayFromAction(tn)),
        <.circle(
          ^.r := 6,
          onClick ==> click(p.n),
        ),
        <.text(
          ^.x := 15,
          ^.y := 3,
          ^.textAnchor := "start",
          onClick --> Callback.empty,
          tn.data.map(_.name).getOrElse("unkonwn"): String
        ),
        ParentButton(-41, -25, p.onUp.getOrElse(Callback.empty)).when(p.onUp.isDefined && canGoParent(tn)),
        RemoveButton(-42, 10, p.onRemove.getOrElse(Callback.empty)).when(p.onRemove.isDefined),
        EditButton(-12, 10, p.n.dispatchCB(EditAction(tn))).when(canEdit(tn))
        //        creates(p.buttons)
      )
    }
  }


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

  def apply(tn: ModelProxy[TN], onUp: Option[Callback] = None, onRemove: Option[Callback] = None) =
    component(Props(tn, onUp, onRemove))
}
