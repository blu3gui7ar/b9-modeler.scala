package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import play.api.libs.json._

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(label: TN, lens: LLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val checked = label.value match {
      case vs: JsArray => vs.value
      case _ => Seq.empty
    }

    label.meta.widget map { w =>
      val boxes = w.parameters map { choice =>
        val active = checked.contains(JsString(choice.name))
        <.span(
          <.input(
            ^.`type` := "checkbox",
            ^.name := ref(label),
            ^.value := choice.name,
            ^.checked := active,
            onChange --> Callback {
              if(active) {
                val filtered = checked filterNot {
                  case s: JsString => s.value == choice.name
                  case _ => false
                }
                update(JsArray(filtered))(label, lens, dispatcher)
              } else {
                update(JsArray(JsString(choice.name) +: checked))(label, lens, dispatcher)
              }
            },
          ),
          choice.name
        )
      }
      boxes.toVdomArray
    } getOrElse EmptyVdom
  }
}
