package b9.components.editor

import japgolly.scalajs.react.vdom.TagMod
import upickle.Js
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef

object TextWidget extends Widget {
  val name = "Text"

  def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = value match {
    case n: Js.Num => <.input(
      ^.defaultValue := n.num.toString
    )
    case s: Js.Str => <.input(
      ^.defaultValue := s.str
    )
    case o: Js.Obj => <.input(
      ^.defaultValue := o.toString()
    )
    case a: Js.Arr => <.input(
      ^.defaultValue := a.toString
    )
    case _ => <.input(
      ^.defaultValue := ""
    )
  }
}
