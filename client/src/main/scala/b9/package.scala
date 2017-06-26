import meta.TreeNode

import scala.scalajs.js
import japgolly.scalajs.react.vdom.all._


/**
  * Created by blu3gui7ar on 2017/6/23.
  */
package object b9 {
  val CssSettings = scalacss.devOrProdDefaults

  @js.native
  trait IdNode[D] extends facades.d3js.treeModule.Node[D, IdNode[D]] {
    var id: js.UndefOr[Int] = js.native
    var fold: js.UndefOr[Boolean] = js.native
    var display: js.UndefOr[Boolean] = js.native
    var diffDescendants: js.UndefOr[Int] = js.native
  }
//  type TN = facades.d3js.treeModule.Node[TreeNode]
  type TN = IdNode[TreeNode]
  type LN = facades.d3js.Link[TN]

  val keyAttr = VdomAttr("key")
}
