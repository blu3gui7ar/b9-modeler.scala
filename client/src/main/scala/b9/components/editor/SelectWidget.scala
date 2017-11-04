package b9.components.editor

import b9.short.TN
import b9.{GraphState, ValueSetAction}
import diode.react.ModelProxy
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import upickle.Js

object SelectWidget extends Widget with ReactEventTypes {
  val name = "Select"

  override def render(ref: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TN]): TagMod = {
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
      onChange ==> { (e: ReactEventFromInput) =>
        mp.dispatchCB(ValueSetAction(mp(), ref, Js.Str(e.target.value)))
      },
      (^.value := selected).when(value.value != null),
      subs.toTagMod
    )
  }
}
