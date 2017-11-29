package b9.components.graph

import b9._
import b9.short.{TM, TMLoc, keyAttr}
import diode.react.ModelProxy
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
  case class Props(proxy: ModelProxy[TMLoc], parent: Option[TM], current: TM)

  class Backend($: BackendScope[Props, Unit]) {
    private val displayRootRO = ModelerCircuit.zoom(_.graph.display)
    private val activeRO = ModelerCircuit.zoom(_.graph.active)
    private val editingRO = ModelerCircuit.zoom(_.graph.editing)
    private val metaRO = ModelerCircuit.zoom(_.meta)
    private val metaRoot = metaRO()
    private val types = MetaAst.types(metaRoot)
    private val macros = MetaAst.macros(metaRoot)

    def click(proxy: ModelProxy[TMLoc], target: TM)(e: ReactMouseEvent): Callback =
      if (e.altKey)
        proxy.dispatchCB(GoDownAction(target))
      else
        Callback.empty

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def isActive(tn: TM): Boolean = activeRO() eq tn

    def isEditing(tn: TM): Boolean = editingRO() eq tn

    def isFolded(tn: TM): Boolean = tn.rootLabel.attach.fold

    def isMoving(tn: TM): Boolean = tn.rootLabel.attach.display != tn.rootLabel.attach.nextDisplay

    def canGoParent(tn: TM): Boolean = displayRootRO().tree.rootLabel.attach eq tn.rootLabel.attach

    def canRemove(tn: TM): Boolean = isActive(tn)

    def canEdit(tn: TM): Boolean = true

    def creates(proxy: ModelProxy[TMLoc], node: TM): TagMod  = {
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
        CreateButton(name, 18 + 30 * idx, 10, true, proxy.dispatchCB(CreateAction(node, name, MetaAst.expand(meta, macros))))
      }
    }

    def parentBtn(proxy: ModelProxy[TMLoc], parent: TM): TagMod =
       ParentButton(-41, -25, proxy.dispatchCB(GoUpAction(parent)))

    def removeBtn(proxy: ModelProxy[TMLoc], current: TM, parent: TM): TagMod =
      RemoveButton(-42, 10, proxy.dispatchCB(RemoveFromAction(current, parent)))

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
      val tn = p.current
      val parent = p.parent
      val proxy = p.proxy
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
          onDoubleClick --> proxy.dispatchCB(FoldAction(tn)),
          <.circle(
            ^.r := 6,
            (onMouseOver --> proxy.dispatchCB(ActiveAction(tn))).when(!isActive(tn)),
            onClick ==> click(proxy, tn)
          ),
          <.text(
            ^.x := 15,
            ^.y := 3,
            ^.textAnchor := "start",
            onClick --> Callback.empty,
            tn.rootLabel.name
          ),
          parent.map(parentBtn(proxy, _)).whenDefined.when(canGoParent(tn)),
          parent.map(removeBtn(proxy, tn, _)).whenDefined,
          EditButton(-12, 10, proxy.dispatchCB(EditAction(tn))).when(canEdit(tn)),
          creates(proxy, tn)
        )
      )
    }
}


  private val component = ScalaComponent.builder[Props]("Joint")
    .renderBackend[Backend]
    .build

  def apply(proxy: ModelProxy[TMLoc], parent: Option[TM], current: TM) = component(Props(proxy, parent, current))
}
