package facades

import com.payalabs.scalajs.react.bridge.JsWriter
import japgolly.scalajs.react.vdom.VdomElement
import play.api.libs.json._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

package object materialui {

  @JSImport("@material-ui/core", JSImport.Namespace)
  @js.native
  object MaterialUI extends js.Object {
    val Radio: js.Any = js.native
    val RadioGroup: js.Any = js.native
    val FormControlLabel: js.Any = js.native
    val ExpansionPanel: js.Any = js.native
    val ExpansionPanelSummary: js.Any = js.native
    val ExpansionPanelDetails: js.Any = js.native
//    val styles = {
//      def withStyles(f: js.Function1[js.Any, js.Any])(component: js.Any)
//    }
  }

  implicit val JsValueToJs: JsWriter[JsValue] = JsWriter { value: JsValue =>
    value match {
      case b: JsBoolean => b.value
      case s: JsString => s.value
      case o: JsObject => o.value
      case a: JsArray => a.value
      case n: JsNumber => n.value.toDouble
      case _ => null
    }
  }
}