package b9.components.editor

import b9.short._
import b9.Dispatcher
import b9.TreeOps.{LLens, TN, TTN}
import facades.materialui.{FormControlLabel, Radio, RadioGroup}
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.TypeRef
import org.scalajs.dom.html
import play.api.libs.json._

object MuiRadioWidget extends Widget {
  val name = "MuiRadio"

  def v2Js(label: TN, v: String): JsValue =
    label.meta.t.map {
      case TypeRef(name) =>
        if (name == "Boolean") {
          if (v == "true") {
            JsTrue
          } else {
            JsFalse
          }
        } else {
          JsString(v)
        }
      case _ => JsString(v)
    } getOrElse(JsNull)

  override def render(label: TN, lens: LLens, dispatcher: Dispatcher[TTN]): TagMod = {
    label.meta.widget map { w =>
      val boxes = w.parameters map { choice =>
        val r = Radio(value = choice.name)()
        FormControlLabel(value = choice.name, control = r.rawElement, label = choice.name)()
      }
      RadioGroup(
        name = label.uuid.toString,
        value = JsValueToString(label.value),
        row = true,
        onChange = { (_: SyntheticEvent[html.Input], value: String) =>
          update(v2Js(label, value))(label, lens, dispatcher)
        }
      )(boxes:_*)
    } getOrElse EmptyVdom
  }
}
