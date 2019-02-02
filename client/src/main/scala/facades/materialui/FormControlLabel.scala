package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.{Callback, ReactEvent}

import scala.scalajs.js

object FormControlLabel extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.FormControlLabel

  def apply(name: js.UndefOr[String] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            control: js.UndefOr[js.Any] = js.undefined,
            label: js.UndefOr[String] = js.undefined,
            labelPlacement: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[ReactEvent => Callback] = js.undefined
           ): WithProps = auto
}
