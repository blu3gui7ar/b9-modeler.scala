package b9.components.editor

import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import upickle.Js

object SelectWidget extends Widget {
  val name = "Select"

  override def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = {
    val subs = meta.values map { choices =>
      choices map { choice =>
        <.option(
          ^.value := choice.name,
          choice.name
        )
      }
    } getOrElse(Seq.empty)

    val selected = if (value.value == null)  "" else value.value.toString

    <.select(
      (^.value := selected).when(value.value != null),
      subs.toTagMod
    )
  }
}
