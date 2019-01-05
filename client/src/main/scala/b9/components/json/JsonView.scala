package b9.components.json

import b9.ModelerState
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst

object JsonView {
  def apply(modeler: ModelerState) = component(Props(modeler))

  case class Props(modeler: ModelerState)

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val tree = p.modeler.graph.root
      val meta = p.modeler.meta
      import b9.JsonExpr._
      implicit val macros = MetaAst.macros(meta)
      implicit val types = MetaAst.types(meta)
      <.div(
        meta.json(Some(tree)).whenDefined
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
