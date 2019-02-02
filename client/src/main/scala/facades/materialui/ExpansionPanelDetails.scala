package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js

object ExpansionPanelDetails extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.ExpansionPanelDetails

  def apply(classes: js.UndefOr[js.Any] = js.undefined
           ): WithProps = auto
}
