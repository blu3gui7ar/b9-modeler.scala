import japgolly.scalajs.react.vdom.all.VdomAttr
import meta.TreeNode

import scala.scalajs.js
import scalaz.{Show, Tree, TreeLoc}

/**
  * Created by blu3gui7ar on 2017/6/23.
  */
package object b9 {
  val CssSettings = scalacss.devOrProdDefaults

  object short {
    @js.native
    trait IdNode[D] extends facades.d3js.treeModule.Node[D, IdNode[D]] {
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

    implicit val TreeShows: Show[TreeNode[TreeAttach]] = Show.shows { tn: TreeNode[TreeAttach] =>
      tn.toString
    }

    val keyAttr = VdomAttr("key")

    val console = js.Dynamic.global.console
  }
}
