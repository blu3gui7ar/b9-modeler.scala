package b9.components.editor

import b9.Dispatcher
import japgolly.scalajs.react.vdom.Exports.EmptyVdom
import japgolly.scalajs.react.vdom.{TagMod, VdomNode}
import b9.TreeOps._

object EmptyWidget extends Widget {
  val name = "Empty"

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = EmptyVdom
}
