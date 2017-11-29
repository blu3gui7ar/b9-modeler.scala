package b9.components.editor

import b9.ValueSetAction
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import play.api.libs.json.{JsNull, JsString, JsValue}

object SelectWidget extends Widget with ReactEventTypes {
  val name = "Select"

  override def render(ref: String, meta: AttrDef, value: JsValue, mp: ModelProxy[TM]): TagMod = {
    val subs = meta.values map { choices =>
      choices map { choice =>
        <.option(
          ^.value := choice.name,
          choice.name
        )
      }
    } getOrElse(Seq.empty)

    val selected = if (value eq JsNull)  "" else value.toString

    <.select(
      onChange ==> { (e: ReactEventFromInput) =>
        mp.dispatchCB(ValueSetAction(mp(), ref, JsString(e.target.value)))
      },
      (^.value := selected).when(value ne JsNull),
      subs.toTagMod
    )
  }
}
