package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import b9.short._
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource
import play.api.libs.json.JsString

object SelectWidget extends Widget with ReactEventTypes {
  val name = "Select"

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode = {
    val label = tree.rootLabel
    val value = label.value

    val selected = value.asOpt[String].getOrElse("")
    val subs = label.meta.widget map { w =>
      w.parameters map { choice =>
        <.option(
          keyAttr := ref(label) + "-" + choice.name,
          ^.value := choice.name,
          choice.name
        )
      }
    } getOrElse(Seq.empty)


    <.select(
      keyAttr := ref(label),
      onChange ==> { (e: ReactEventFromInput) =>
        updateCB(JsString(e.target.value))(label, labelLens(lens), dispatcher, metaSource)
      },
      ^.value := selected,
      subs.toTagMod
    )
  }
}
