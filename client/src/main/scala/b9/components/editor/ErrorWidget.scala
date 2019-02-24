package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource

object NotFoundWidget extends Widget {
  val name = "NotFound"

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource)
                         : VdomNode =
    <.span("Widget not found")
}


object NotMatchWidget extends Widget {
  val name = "NotMatch"

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource)
                         : VdomNode =
    <.span("Widget not match")
}


object NotValidWidget extends Widget {
  val name = "NotValid"

  override def renderForm(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode =
    <.span("Widget not valid")
}
