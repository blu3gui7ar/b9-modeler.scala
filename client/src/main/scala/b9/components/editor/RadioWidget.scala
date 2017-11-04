package b9.components.editor

import b9.ValueSetAction
import b9.short.TN
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.{AttrDef, TypeRef}
import upickle.Js

object RadioWidget extends Widget {
  val name = "Radio"

  override def render(ref: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TN]): TagMod =
    meta.values map { choices =>
      val boxes = choices map { choice =>
        val newVal: Option[Js.Value] = meta.t.map {
          case TypeRef(name) =>
            if(name == "Boolean") {
              if(choice.name == "true") {
                Js.True
              } else {
                Js.False
              }
            } else {
              Js.Str(choice.name)
            }
          case _ => Js.Str(choice.name)
        }
        <.span(
          <.input(
            ^.`type` := "radio",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := value.toString() == choice.name,
            onChange --> mp.dispatchCB(ValueSetAction(mp(), ref, newVal.getOrElse(Js.Null)))
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
}
