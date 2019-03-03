package b9.components.json

import b9.TreeOps.TTN
import b9.TreeToJson
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst._

object JsonView {
  def apply(tree: TTN, meta: Root) = component(Props(tree, meta))

  case class Props(tree: TTN, meta: Root)

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val tree = p.tree
      val meta = p.meta
      implicit val m = macros(meta)
      implicit val t = types(meta)
      val jsonTransformer = new TreeToJson()
      import jsonTransformer._
      <.div(
        meta.transform("meta", Some(tree), rootAttrDef(), None).whenDefined
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("JsonView")
    .stateless
    .renderBackend[Backend]
    .build

}
