package b9.components.editor

import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import upickle.Js

trait Widget {
  def render(id: String, meta: AttrDef, value: Js.Value): TagMod
}
