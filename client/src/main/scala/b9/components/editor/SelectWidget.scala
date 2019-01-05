package b9.components.editor

import b9.short.TM
import b9.{Dispatcher, ModelerOps, ModelerState}
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventTypes}
import meta.MetaAst.AttrDef
import play.api.libs.json.{JsString, JsValue}

object SelectWidget extends Widget with ReactEventTypes {
  val name = "Select"

  override def render(ref: String, meta: AttrDef, value: JsValue,node: TM, dispatcher: Dispatcher[ModelerState]): TagMod = {
    val selected = value.asOpt[String].getOrElse("")
    val subs = meta.values map { choices =>
      choices map { choice =>
        <.option(
          ^.value := choice.name,
          choice.name
        )
      }
    } getOrElse(Seq.empty)


    <.select(
      onChange ==> { (e: ReactEventFromInput) =>
        Callback {
          dispatcher.dispatch(ModelerOps.valueSet(node, ref, JsString(e.target.value)))
        }
      },
      ^.value := selected,
      subs.toTagMod
    )
  }
}
