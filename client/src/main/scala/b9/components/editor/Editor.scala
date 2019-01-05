package b9.components.editor

import b9.{Dispatcher, ModelerState}
import b9.short.{TM, TMLoc}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Editor {
  def apply(loc: TMLoc, dispatcher: Dispatcher[ModelerState]) = component(Props(loc, dispatcher))

  case class Props(loc: TMLoc, dispatcher: Dispatcher[ModelerState])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val data = p.loc.tree;
      <.div(
        <.span(data.rootLabel.name), //TODO use input to modify map key
        <.span(" : "),

        data.rootLabel.meta.widget flatMap { widget =>
          WidgetRegistry(widget.name)
        } map {
          _.render(data.rootLabel.uuid.toString, data.rootLabel.meta, data.rootLabel.value, data, p.dispatcher);
        } getOrElse {
          data.subForest.zipWithIndex.map { case (_, idx) =>
            Editor(p.loc.getChild(idx + 1).get, p.dispatcher)
          } toTagMod
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
