package b9.components


import b9.TreeNode
import diode.react.ModelProxy
import facades.d3js.treeModule.Node
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object TreeGraph {

  case class Props(
                    joints: ModelProxy[Seq[TreeNode]],
                    width: Double,
                    height: Double,

                    left: Int = 50,
                    right: Int = 200,
                    top: Int = 10,
                    bottom: Int = 10
                  )

  case class State()

  class Backend($ : BackendScope[Props, State]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(top: Int) = Seq("one", "two", "three")
      .zipWithIndex.toTagMod { case (n, i) => BreadCrum(0, top + 15 * i, n) }

    def joints(nodes: Seq[TreeNode]) = nodes.map({ node =>
      Joint.Props(node.x, node.y, node.name, Map.empty)
    }).toTagMod(Joint(_))

    def render(p: Props, s: State) = {
      <.svg(
        ^.width := p.width.toString, //[BUG] https://github.com/japgolly/scalajs-react/issues/388
        ^.height:= p.height.toString,
        <.g(
          ^.transform := transform(p.left, p.top),
          breadcrums(p.top),
          joints(p.joints())
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("TreeGraph")
      .initialState(State())
      .renderBackend[Backend]
      .build

  def apply(joints: ModelProxy[Seq[TreeNode]], width: Double, height: Double) = component(Props(joints, width, height))

}
