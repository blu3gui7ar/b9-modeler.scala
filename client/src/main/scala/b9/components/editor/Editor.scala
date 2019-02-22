package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


object Editor {

  def apply(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]) = component(Props(tree, lens, dispatcher))

  case class Props(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomNode = {
      val label = p.tree.rootLabel
      label.meta.widget flatMap { widget =>
        WidgetRegistry(widget.name)
      } map { w: Widget =>
        <.div(
          label.name,
          " : ",
          w.render(p.tree, p.lens, p.dispatcher)
        ).render
      } getOrElse {
        <.div(
          label.name,
          ":",
          <.span( "Widget Not Valid" )
        )
      }
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .stateless
    .renderBackend[Backend]
    .build

}
