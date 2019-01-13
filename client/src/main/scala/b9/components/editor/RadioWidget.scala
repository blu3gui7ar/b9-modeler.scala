package b9.components.editor

import b9.Dispatcher
import b9.TreeOps.{LLens, TN, TTN}
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.TypeRef
import play.api.libs.json._

object RadioWidget extends Widget {
  val name = "Radio"

  override def render(label: TN, lens: LLens, dispatcher: Dispatcher[TTN]): TagMod = {
    label.meta.values map { choices =>
      val boxes = choices map { choice =>
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
          <.input(
            ^.`type` := "radio",
            ^.name := label.uuid.toString,
            ^.value := choice.name,
            ^.checked := label.value.toString == choice.name,
            onChange --> updateCB(newVal.getOrElse(JsNull))(label, lens, dispatcher)
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
  }
}
