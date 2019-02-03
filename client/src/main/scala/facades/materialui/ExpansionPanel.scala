package facades.materialui

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.raw.SyntheticEvent
import org.scalajs.dom.html

import scala.scalajs.js

object ExpansionPanel extends ReactBridgeComponent {
  override lazy val componentValue = MaterialUI.ExpansionPanel

  def apply(classes: js.UndefOr[js.Any] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            expanded: js.UndefOr[Boolean] = js.undefined,
            CollapseProps: js.UndefOr[js.Any] = js.undefined,
            defaultExpanded: js.UndefOr[Boolean] = js.undefined,
            onChange: js.UndefOr[(SyntheticEvent[html.Input], Boolean) => Unit] = js.undefined
           ): WithProps = auto
}
