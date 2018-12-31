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
import meta.Validator._

object TextWidget extends Widget with ReactEventTypes {
  val name = "Text"

  def onInputChange(mp: ModelProxy[TM], ref: String, meta: AttrDef, toValue: ReactEventFromInput => JsValue)
                 (e: ReactEventFromInput): Callback = {
    val value = toValue(e)
    val validated = meta.restricts map { restricts =>
      restricts map { _.validate(Some(value)) } forall(identity)
    } getOrElse(true)

    if (validated)
      mp.dispatchCB(ValueSetAction(mp(), ref, value))
    else
      Callback.empty
  }

  def render(ref: String, meta: AttrDef, value: JsValue, mp: ModelProxy[TM]): TagMod = value match {
    case n: JsNumber => <.input(
      ^.name := ref,
      ^.defaultValue := n.value.toString,
      onChange ==> onInputChange(mp, ref, meta, {e => JsNumber(BigDecimal(e.target.value))})
    )
    case s: JsString => <.input(
      ^.name := ref,
      ^.defaultValue := s.value,
      onChange ==> onInputChange(mp, ref, meta, {e => JsString(e.target.value)})
    )
    case o: JsObject => <.input(
      ^.name := ref,
      ^.defaultValue := o.toString,
      onChange ==> onInputChange(mp, ref, meta, {e => JsString(e.target.value)})
    )
    case a: JsArray => <.input(
      ^.name := ref,
      ^.defaultValue := a.toString,
      onChange ==> onInputChange(mp, ref, meta, {e => JsString(e.target.value)})
    )
    case _ => <.input(
      ^.name := ref,
      ^.defaultValue := "",
      onChange ==> onInputChange(mp, ref, meta, {e => JsString(e.target.value)})
    )
  }
}
