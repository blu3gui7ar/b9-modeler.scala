package b9.components.editor

import b9.short.TN
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import upickle.Js

trait Widget {
  def render(id: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TN]): TagMod
}
