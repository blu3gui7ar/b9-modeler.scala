package b9.components.json

import b9.TreeOps.TTN
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst

object JsonView {
  def apply(tree: TTN, meta: MetaAst.Root) = component(Props(tree, meta))

  case class Props(tree: TTN, meta: MetaAst.Root)

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val tree = p.tree
      val meta = p.meta
      import b9.JsonExpr._
      implicit val macros = MetaAst.macros(meta)
      implicit val types = MetaAst.types(meta)
      <.div(
        meta.json(Some(tree)).whenDefined
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .stateless
    .renderBackend[Backend]
    .build

}
