package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import monocle.std.tree._

object Editor {

  def apply(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]) = component(Props(tree, lens, dispatcher))

  case class Props(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val label = p.tree.rootLabel
      val sub = p.tree.subForest

      <.div(
        <.span(label.name), //TODO use input to modify map key
        <.span(" : "),

        label.meta.widget flatMap { widget =>
          WidgetRegistry(widget.name)
        } map {
          _.render(label, p.lens composeLens rootLabel, p.dispatcher)
        } getOrElse {
          sub.map { node =>
            Editor(node, p.lens composeLens subForest composeLens at(node), p.dispatcher)
          } toTagMod
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .stateless
    .renderBackend[Backend]
    .build

}
