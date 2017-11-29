package b9.components.editor

import b9.ValueSetAction
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.{AttrDef, TypeRef}
import play.api.libs.json._

object RadioWidget extends Widget {
  val name = "Radio"

  override def render(ref: String, meta: AttrDef, value: JsValue, mp: ModelProxy[TM]): TagMod =
    meta.values map { choices =>
      val boxes = choices map { choice =>
        val newVal: Option[JsValue] = meta.t.map {
          case TypeRef(name) =>
            if(name == "Boolean") {
              if(choice.name == "true") {
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
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := value.toString() == choice.name,
            onChange --> mp.dispatchCB(ValueSetAction(mp(), ref, newVal.getOrElse(JsNull)))
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
}
