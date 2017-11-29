package b9.components.editor

import b9.ValueSetAction
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventTypes}
import meta.MetaAst.AttrDef
import play.api.libs.json._

object TextWidget extends Widget with ReactEventTypes {
  val name = "Text"

  def onTextChange(mp: ModelProxy[TM], ref: String)(e: ReactEventFromInput): Callback =
    mp.dispatchCB(ValueSetAction(mp(), ref, JsString(e.target.value)))

  def onNumChange(mp: ModelProxy[TM], ref: String)(e: ReactEventFromInput): Callback =
    mp.dispatchCB(ValueSetAction(mp(), ref, JsNumber(e.target.value.toDouble)))

  def render(ref: String, meta: AttrDef, value: JsValue, mp: ModelProxy[TM]): TagMod = value match {
    case n: JsNumber => <.input(
      ^.name := ref,
      ^.defaultValue := n.value.toString,
      onChange ==> onNumChange(mp, ref)
    )
    case s: JsString => <.input(
      ^.name := ref,
      ^.defaultValue := s.value,
      onChange ==> onTextChange(mp, ref)
    )
    case o: JsObject => <.input(
      ^.name := ref,
      ^.defaultValue := o.toString,
      onChange ==> onTextChange(mp, ref)
    )
    case a: JsArray => <.input(
      ^.name := ref,
      ^.defaultValue := a.toString,
      onChange ==> onTextChange(mp, ref)
    )
    case _ => <.input(
      ^.name := ref,
      ^.defaultValue := "",
      onChange ==> onTextChange(mp, ref)
    )
  }
}
