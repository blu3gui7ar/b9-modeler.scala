package b9.components.json

import b9.ModelerCircuit
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst

object JsonView {
  def apply(mp: ModelProxy[TM]) = component(Props(mp))

  case class Props(mp: ModelProxy[TM])

  class Backend($: BackendScope[Props, Unit]) {
    private val metaRO = ModelerCircuit.zoom(_.meta)

    def render(p: Props): VdomTag = {
      val tree = p.mp()
      val meta = metaRO()
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
