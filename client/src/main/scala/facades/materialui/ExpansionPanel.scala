package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsFnComponent}
import japgolly.scalajs.react.raw.SyntheticEvent
import org.scalajs.dom.html

import scala.scalajs.js

object ExpansionPanel {
  lazy val componentValue = MaterialUI.ExpansionPanel

  def apply(
             classes: js.UndefOr[js.Any] = js.undefined,
             disabled: js.UndefOr[Boolean] = js.undefined,
             expanded: js.UndefOr[Boolean] = js.undefined,
             CollapseProps: js.UndefOr[js.Any] = js.undefined,
             defaultExpanded: js.UndefOr[Boolean] = js.undefined,
             onChange: js.UndefOr[(SyntheticEvent[html.Input], Boolean) => Unit] = js.undefined
           )(children: ChildArg*) = component(
    js.Dynamic.literal(
      "classes" -> classes,
      "disabled" -> disabled,
      "expanded" -> expanded,
      "defaultExpanded" -> defaultExpanded,
      "onChange" -> onChange,
    ))(children: _*)

  val component = JsFnComponent[js.Object, Children.Varargs](componentValue)
}
