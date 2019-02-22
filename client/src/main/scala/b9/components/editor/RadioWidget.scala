package b9.components.editor

import b9.Dispatcher
import b9.TreeOps.{TLens, TTN}
import b9.short._
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.TypeRef
import play.api.libs.json._

object RadioWidget extends Widget {
  val name = "Radio"

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val label = tree.rootLabel
    label.meta.widget map { w =>
      val boxes = w.parameters map { choice =>
        val newVal: Option[JsValue] = label.meta.t.map {
          case TypeRef(name) =>
            if (name == "Boolean") {
              if (choice.name == "true") {
                JsTrue
              } else {
                JsFalse
              }
            } else {
              JsString(choice.name)
            }
          case _ => JsString(choice.name)
        }
        <.span(
          keyAttr := ref(label) + "-" + choice.name,
          <.input(
            ^.`type` := "radio",
            ^.name := ref(label),
            ^.value := choice.name,
            ^.checked := label.value.toString == choice.name,
            onChange --> updateCB(newVal.getOrElse(JsNull))(label, labelLens(lens), dispatcher)
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
