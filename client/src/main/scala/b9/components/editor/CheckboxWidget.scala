package b9.components.editor

import japgolly.scalajs.react.CallbackTo
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import upickle.Js

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = {
    val subs = meta.values map { choices =>
      choices map { choice =>
        <.span(
          <.input(
            ^.`type` := "checkbox",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := value.toString() == choice.name,
            onChange => CallbackTo.apply({ () => () })
          ),
          choice.name
        )
      }
    } getOrElse(Seq.empty)

    TagMod(subs: _*)
  }
}
