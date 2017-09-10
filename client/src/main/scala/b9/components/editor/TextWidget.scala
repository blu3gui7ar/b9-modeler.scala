package b9.components.editor

import japgolly.scalajs.react.vdom.TagMod
import upickle.Js
import japgolly.scalajs.react.vdom.html_<^._
import b9.short
import meta.MetaAst.AttrDef

object TextWidget extends Widget {
  val name = "Text"

  def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = value match {
    case n: Js.Num => <.input(
      ^.value := n.num.toString
    )
    case s: Js.Str => <.input(
      ^.value := s.str
    )
    case o: Js.Obj => <.input(
      ^.value := o.toString()
    )
    case a: Js.Arr => <.input(
      ^.value := a.toString
    )
    case _ => short.emptyTagMod
  }
}
