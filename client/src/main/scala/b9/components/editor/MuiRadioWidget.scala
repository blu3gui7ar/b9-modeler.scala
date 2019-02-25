package b9.components.editor

import b9.Dispatcher
import b9.TreeOps.{TLens, TN, TTN}
import b9.short._
import facades.materialui.{FormControlLabel, Radio, RadioGroup}
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.TypeRef
import meta.MetaSource
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

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode = {
    val label = tree.rootLabel
    label.meta.widget map { w =>
      val boxes = w.parameters map { choice =>
        val r = Radio(value = choice.name)()
        val vn: VdomNode = FormControlLabel(value = choice.name, control = r.rawElement, label = choice.name)()
        vn
      }
      val group: VdomNode = RadioGroup(
        name = ref(label),
        value = JsValueToString(label.value),
        row = true,
        onChange = { (_: SyntheticEvent[html.Input], value: String) =>
          update(v2Js(label, value))(label, labelLens(lens), dispatcher, metaSource)
        }
      )(boxes: _*)
      group
    } getOrElse EmptyVdom
  }
}
