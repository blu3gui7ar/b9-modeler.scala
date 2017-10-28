package b9.components.editor

import japgolly.scalajs.react.CallbackTo
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import upickle.Js

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = {
    val checked = value match {
      case vs: Js.Arr => vs.value.toSeq.map(_.value.toString)
      case _ => Seq.empty
    }

    meta.values map { choices =>
      val boxes = choices map { choice =>
        <.span(
          <.input(
            ^.`type` := "checkbox",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := checked.contains(choice.name),
            onChange => CallbackTo.apply({ () => () })
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
  }
}
