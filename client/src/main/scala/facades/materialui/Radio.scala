package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.raw.SyntheticEvent
import org.scalajs.dom.html
import play.api.libs.json.JsValue

import scala.scalajs.js

object Radio extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.Radio

  def apply(checked: js.UndefOr[Boolean] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[(SyntheticEvent[html.Input], Boolean) => Unit] = js.undefined
           ): WithProps = auto
}

