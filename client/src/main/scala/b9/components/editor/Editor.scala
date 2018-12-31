package b9.components.editor

import b9.short.TMLoc
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Editor {
  def apply(mp: ModelProxy[TMLoc]) = component(Props(mp))

  case class Props(mp: ModelProxy[TMLoc])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val tn = p.mp.zoom(_.tree);
      val data = tn();
      <.div(
        <.span(data.rootLabel.name), //TODO use input to modify map key
        <.span(" : "),

        data.rootLabel.meta.widget flatMap { widget =>
          WidgetRegistry(widget.name)
        } map {
          _.render(data.rootLabel.uuid.toString, data.rootLabel.meta, data.rootLabel.value, tn);
        } getOrElse {
          data.subForest.zipWithIndex.map { case (_, idx) =>
            Editor(p.mp.zoom(_.getChild(idx + 1).get))
          } toTagMod
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
