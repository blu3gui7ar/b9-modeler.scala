package b9.components.editor

import b9.short.TMLoc
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Editor {
  def apply(mp: ModelProxy[TMLoc]) = component(Props(mp))

  case class Props(mp: ModelProxy[TMLoc])

  class Backend($: BackendScope[Props, Unit]) {
//    private val displayRootRO = ModelerCircuit.zoom(_.graph.displayRoot)
//    private val activeRO = ModelerCircuit.zoom(_.graph.activeNode)
//    private val editingRO = ModelerCircuit.zoom(_.graph.editingNode)
//    private val metaRO = ModelerCircuit.zoom(_.graph.meta)

    def render(p: Props): VdomTag = {
      val tn = p.mp.zoom(_.tree);
      val data = tn();
//      println(data.rootLabel.meta)
      <.div(
        <.span(data.rootLabel.name),
        <.span(" : "),

        data.rootLabel.meta.widget flatMap { widget =>
          WidgetRegistry(widget.name)
        } map {
//          println(data.rootLabel.value)
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
