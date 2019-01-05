package b9.components.editor

import b9.{Dispatcher, ModelerState}
import b9.short.TM
import japgolly.scalajs.react.vdom.Exports.EmptyVdom
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import play.api.libs.json.JsValue

object EmptyWidget extends Widget {
  val name = "Empty"

  override def render(ref: String, meta: AttrDef, value: JsValue, node: TM, dispatcher: Dispatcher[ModelerState]): TagMod = EmptyVdom
}
