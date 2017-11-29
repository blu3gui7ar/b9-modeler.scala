package b9.components.editor

import b9._
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import play.api.libs.json._

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(ref: String, meta: AttrDef, value: JsValue, mp: ModelProxy[TM]): TagMod = {
    val checked = value match {
      case vs: JsArray => vs.value
      case _ => Seq.empty
    }

    meta.values map { choices =>
      val boxes = choices map { choice =>
        val active = checked.contains(JsString(choice.name))
        <.span(
          <.input(
            ^.`type` := "checkbox",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := active,
            onChange --> mp.dispatchCB(
              if(active){
                ValueDelAction(mp(), ref, JsString(choice.name))
              }
              else {
                ValueAddAction(mp(), ref, JsString(choice.name))
              }
            ),
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
  }
}
