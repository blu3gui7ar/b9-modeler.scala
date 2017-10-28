package b9.components.editor

import japgolly.scalajs.react.CallbackTo
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import meta.MetaAst.AttrDef
import upickle.Js

object RadioWidget extends Widget {
  val name = "Radio"

  override def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = (
    meta.values map { choices =>
      choices map { choice =>
        <.span(
          <.input(
            ^.`type` := "radio",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := value.toString() == choice.name,
            onChange => CallbackTo.apply({ () => ()})
          ),
          choice.name
        )
      }
    } getOrElse(Seq.empty)
  ).toTagMod
}
