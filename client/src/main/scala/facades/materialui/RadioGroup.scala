package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.raw.SyntheticEvent
import org.scalajs.dom.html
import play.api.libs.json.JsValue

import scala.scalajs.js

object RadioGroup extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.RadioGroup

  def apply(name: js.UndefOr[String] = js.undefined,
            defaultValue: js.UndefOr[String] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            row: js.UndefOr[Boolean] = js.undefined,
            onChange: js.UndefOr[(SyntheticEvent[html.Input], String) => Unit] = js.undefined
           ): WithProps = auto
}
