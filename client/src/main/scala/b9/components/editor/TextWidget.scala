package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import b9.short._
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventTypes}
import meta.Validator._
import play.api.libs.json._

object TextWidget extends Widget with ReactEventTypes {
  val name = "Text"

  def onInputChange(label:TN, lens: LLens, dispatcher: Dispatcher[TTN], toValue: ReactEventFromInput => JsValue)
                 (e: ReactEventFromInput): Callback = {
    val value = toValue(e)
    val validated = label.meta.restricts map { _.validate(Some(value)) } forall(identity)

    if (validated)
      updateCB(value)(label, lens, dispatcher)
    else
      Callback.empty
  }

  override def render(tree: TTN, tlens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val label = tree.rootLabel
    val lens = labelLens(tlens)
    label.value match {
      case n: JsNumber => <.input(
        ^.name := ref(label),
        keyAttr := ref(label),
        ^.defaultValue := n.value.toString,
        onChange ==> onInputChange(label, lens, dispatcher, {e => JsNumber(BigDecimal(e.target.value))})
      )
      case s: JsString => <.input(
        ^.name := ref(label),
        keyAttr := ref(label),
        ^.defaultValue := s.value,
        onChange ==> onInputChange(label, lens, dispatcher, {e => JsString(e.target.value)})
      )
      case o: JsObject => <.input(
        ^.name := ref(label),
        keyAttr := ref(label),
        ^.defaultValue := o.toString,
        onChange ==> onInputChange(label, lens, dispatcher, {e => JsString(e.target.value)})
      )
      case a: JsArray => <.input(
        ^.name := ref(label),
        keyAttr := ref(label),
        ^.defaultValue := a.toString,
        onChange ==> onInputChange(label, lens, dispatcher, {e => JsString(e.target.value)})
      )
      case _ => <.input(
        ^.name := ref(label),
        keyAttr := ref(label),
        ^.defaultValue := "",
        onChange ==> onInputChange(label, lens, dispatcher, {e => JsString(e.target.value)})
      )
    }
  }
}
