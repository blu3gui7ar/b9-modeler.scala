package b9.components.editor

import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import play.api.libs.json.JsValue

trait Widget {
  def render(id: String, meta: AttrDef, value: JsValue, proxy: ModelProxy[TM]): TagMod
}
