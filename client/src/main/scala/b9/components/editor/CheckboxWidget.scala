package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import b9.short._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.html_<^._
import play.api.libs.json._

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val label = tree.rootLabel
    val checked = label.value match {
      case vs: JsArray => vs.value
      case _ => Seq.empty
    }

    label.meta.widget map { w =>
      val boxes = w.parameters map { choice =>
        val active = checked.contains(JsString(choice.name))
        <.span(
          keyAttr := ref(label) + "-" + choice.name,
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
                update(JsArray(filtered))(label, labelLens(lens), dispatcher)
              } else {
                update(JsArray(JsString(choice.name) +: checked))(label, labelLens(lens), dispatcher)
              }
            },
          ),
          choice.name
        )
      }
      <.span(
        keyAttr := ref(label),
        boxes.toVdomArray
      ).render
    } getOrElse EmptyVdom
  }
}
