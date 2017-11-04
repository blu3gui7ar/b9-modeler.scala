package b9.components.editor

import b9.ValueSetAction
import b9.short.TN
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventTypes}
import meta.MetaAst.AttrDef
import upickle.Js

object TextWidget extends Widget with ReactEventTypes {
  val name = "Text"

  def onTextChange(mp: ModelProxy[TN], ref: String)(e: ReactEventFromInput): Callback =
    mp.dispatchCB(ValueSetAction(mp(), ref, Js.Str(e.target.value)))

  def onNumChange(mp: ModelProxy[TN], ref: String)(e: ReactEventFromInput): Callback =
    mp.dispatchCB(ValueSetAction(mp(), ref, Js.Num(e.target.value.toDouble)))

  def render(ref: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TN]): TagMod = value match {
    case n: Js.Num => <.input(
      ^.name := ref,
      ^.defaultValue := n.num.toString,
      onChange ==> onNumChange(mp, ref)
    )
    case s: Js.Str => <.input(
      ^.name := ref,
      ^.defaultValue := s.str,
      onChange ==> onTextChange(mp, ref)
    )
    case o: Js.Obj => <.input(
      ^.name := ref,
      ^.defaultValue := o.toString,
      onChange ==> onTextChange(mp, ref)
    )
    case a: Js.Arr => <.input(
      ^.name := ref,
      ^.defaultValue := a.toString,
      onChange ==> onTextChange(mp, ref)
    )
    case _ => <.input(
      ^.name := ref,
      ^.defaultValue := "",
      onChange ==> onTextChange(mp, ref)
    )
  }
}
