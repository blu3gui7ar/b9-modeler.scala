package b9.components.editor

import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.Exports.EmptyVdom
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import upickle.Js

object EmptyWidget extends Widget {
  val name = "Empty"

  override def render(ref: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TM]): TagMod = EmptyVdom
}
