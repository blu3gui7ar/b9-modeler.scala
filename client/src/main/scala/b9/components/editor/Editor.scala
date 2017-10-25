package b9.components.editor

import java.util.UUID

import b9.short.TN
import b9.ModelerCircuit
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Editor {
  def apply(tn: ModelProxy[TN]) = component(Props(tn))

  case class Props(n: ModelProxy[TN])

  class Backend($: BackendScope[Props, Unit]) {
    val displayRootRO = ModelerCircuit.zoom(_.graph.displayRoot)
    val activeRO = ModelerCircuit.zoom(_.graph.activeNode)
    val editingRO = ModelerCircuit.zoom(_.graph.editingNode)
    val metaRO = ModelerCircuit.zoom(_.graph.meta)

    def render(p: Props): VdomTag = {
      val tn = p.n()
      tn.data.map { data =>
        <.div(
          <.span(data.name),
          <.span(" : "),
          tn.children map { children =>
            if (children.isEmpty) {
              data.meta.widget flatMap { widget =>
                WidgetRegistry(widget.name)
              } map {
                _.render(uuid, data.meta, data.value)
              } getOrElse (EmptyVdom)
            } else {
               children.zipWithIndex.map {
                case (child, idx) => {
                  Editor(p.n.zoom(_.children.get.apply(idx)))
                }
              } toTagMod
            }
          } getOrElse(EmptyVdom)
        )
      }
    } getOrElse(<.div())
  }

  def uuid() = UUID.randomUUID().toString

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
