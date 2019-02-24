package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.vdom.Exports.EmptyVdom
import japgolly.scalajs.react.vdom.VdomNode
import meta.MetaSource

object EmptyWidget extends Widget {
  val name = "Empty"

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode = EmptyVdom
}
