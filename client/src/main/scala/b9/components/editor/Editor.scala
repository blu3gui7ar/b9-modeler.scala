package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource


object Editor {

  def key(tree: TTN) = "editor-" + tree.rootLabel.uuid.toString

  def apply(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource) =
    component.withKey(key(tree))(Props(tree, lens, dispatcher, metaSrc))

  case class Props(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource)

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomNode = {
      val label = p.tree.rootLabel
      label.meta.widget map { widget =>
        WidgetRegistry(widget.name, !widget.isLeaf)
          .render(p.tree, p.lens, p.dispatcher, p.metaSrc)
      }
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .stateless
    .renderBackend[Backend]
    .build

}
