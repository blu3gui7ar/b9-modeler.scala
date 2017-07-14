import japgolly.scalajs.react.vdom.all.VdomAttr
import meta.TreeNode

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * Created by blu3gui7ar on 2017/6/23.
  */
package object b9 {
  val CssSettings = scalacss.devOrProdDefaults

  object short {
    @js.native
    trait IdNode[D] extends facades.d3js.treeModule.Node[D, IdNode[D]] {
      var id: js.UndefOr[Int] = js.native
      var fold: js.UndefOr[Boolean] = js.native
      var display: js.UndefOr[Boolean] = js.native
      var nextDisplay: js.UndefOr[Boolean] = js.native
      var diffDescendants: js.UndefOr[Int] = js.native
    }

    //  type TN = facades.d3js.treeModule.Node[TreeNode]
    type TN = IdNode[TreeNode]
    type LN = facades.d3js.Link[TN]

    @ScalaJSDefined
    class RealNode(val data: TreeNode, val parent: TN, val x: js.UndefOr[Double], val y: js.UndefOr[Double],
                   val id: js.UndefOr[Int]) extends js.Object


    val keyAttr = VdomAttr("key")

  }
}
