package b9.components.editor

import b9.short.{TN, ZoomFunc}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Editor {
  def apply(mp: ModelProxy[TN]) = component(Props(mp))

  case class Props(mp: ModelProxy[TN])

  class Backend($: BackendScope[Props, Unit]) {
//    private val displayRootRO = ModelerCircuit.zoom(_.graph.displayRoot)
//    private val activeRO = ModelerCircuit.zoom(_.graph.activeNode)
//    private val editingRO = ModelerCircuit.zoom(_.graph.editingNode)
//    private val metaRO = ModelerCircuit.zoom(_.graph.meta)

    def render(p: Props): VdomTag = {
      val tn = p.mp;
      val data = tn().data
      <.div(
        <.span(data.map(_.name).getOrElse("Unknown"): String),
        <.span(" : "),

        data.toOption.flatMap { d =>
          d.meta.widget flatMap { widget =>
            WidgetRegistry(widget.name)
          } map {
            _.render(d.uuid.toString, d.meta, d.value, p.mp);
          }
        } getOrElse {
          tn().children map { children =>
            children.zipWithIndex.map {
              case (_, idx) => {
                val g: ZoomFunc = _.children.get.apply(idx)
                Editor(p.mp.zoom(g))
              }
            }.toTagMod
          } getOrElse (EmptyVdom)
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
