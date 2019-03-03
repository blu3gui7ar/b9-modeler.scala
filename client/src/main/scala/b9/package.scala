import japgolly.scalajs.react.vdom.all.VdomAttr
import play.api.libs.json._

import scala.scalajs.js
import scala.scalajs.js.UndefOr

/**
  * Created by blu3gui7ar on 2017/6/23.
  */
package object b9 {
  val CssSettings = scalacss.devOrProdDefaults

  object short {
    implicit def underToOption[N](in: UndefOr[N]): Option[N] = asOption(in)

    def asOption[N](in: UndefOr[N]): Option[N] = {
      in.toOption match {
        case Some(null) => None
        case a  => a
      }
    }

    @js.native
    trait IdNode[D] extends facades.d3js.treeModule.Node[D, IdNode[D]] {
      var fold: Boolean = false
      var display: Boolean = false
      var nextDisplay: Boolean = false

      var active: Boolean = false
      var edit: Boolean = false
    }

    implicit def JsValueToString(value: JsValue): String = value match {
      case b: JsBoolean => b.value.toString
      case s: JsString => s.value
      case o: JsObject => o.value.toString
      case a: JsArray => a.value.toString
      case n: JsNumber => n.value.toString
      case _ => "null"
    }


//    class TreeAttach (
//      var x: Double = 0,
//      var y: Double = 0,
//      var fold: Boolean = false,
//      var display: Boolean = true,
//      var nextDisplay: Boolean = true
//    )
//
//    type TM = Tree[TreeNode[TreeAttach]]
//    type TMLoc = TreeLoc[TreeNode[TreeAttach]]


    val keyAttr = VdomAttr("key")

    val console = js.Dynamic.global.console
  }
}
