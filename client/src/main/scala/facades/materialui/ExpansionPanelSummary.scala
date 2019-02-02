package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js

object ExpansionPanelSummary extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.ExpansionPanelSummary

  def apply(classes: js.UndefOr[js.Object] = js.undefined,
            expandIcon: js.UndefOr[js.Object] = js.undefined,
            IconButtonProps: js.UndefOr[js.Object] = js.undefined
           ): WithProps = auto
}
