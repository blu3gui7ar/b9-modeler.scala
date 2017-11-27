import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.all.VdomAttr
import meta.TreeNode

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scalaz.{Tree, TreeLoc}

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

    class TreeAttach (
      var x: Double = 0,
      var y: Double = 0,
      var fold: Boolean = false,
      var display: Boolean = true,
      var nextDisplay: Boolean = true
    )

    type TM = Tree[TreeNode[TreeAttach]]
    type TMLoc = TreeLoc[TreeNode[TreeAttach]]
    type TN = IdNode[TM]
    type LN = facades.d3js.Link[TN]
    type ZoomFunc = TN => TN



    class RealNode(val data: TreeNode[TreeAttach], val parent: TN, val x: js.UndefOr[Double], val y: js.UndefOr[Double],
                   val id: js.UndefOr[Int]) extends js.Object


    val keyAttr = VdomAttr("key")

    val console = js.Dynamic.global.console
  }
}
