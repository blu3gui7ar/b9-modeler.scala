package b9.components.editor
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst.AttrDef
import upickle.Js

object EmptyWidget extends Widget {
  val name = "Empty"

  override def render(ref: String, meta: AttrDef, value: Js.Value): TagMod = b9.short.emptyTagMod
}
