package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import play.api.libs.json.JsString

object SelectWidget extends Widget with ReactEventTypes {
  val name = "Select"

  override def render(label: TN, lens: LLens, dispatcher: Dispatcher[TTN]): TagMod = {
    val value = label.value

    val selected = value.asOpt[String].getOrElse("")
    val subs = label.meta.values map { choices =>
      choices map { choice =>
        <.option(
          ^.value := choice.name,
          choice.name
        )
      }
    } getOrElse(Seq.empty)


    <.select(
      onChange ==> { (e: ReactEventFromInput) =>
        updateCB(JsString(e.target.value))(label, lens, dispatcher)
      },
      ^.value := selected,
      subs.toTagMod
    )
  }
}
