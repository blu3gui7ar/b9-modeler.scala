package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Callback, ReactEvent}
import japgolly.scalajs.react.{Children, JsFnComponent}

import scala.scalajs.js

object FormControlLabel {
  lazy val componentValue = MaterialUI.FormControlLabel

  def apply(name: js.UndefOr[String] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            control: js.UndefOr[js.Object] = js.undefined,
            label: js.UndefOr[String] = js.undefined,
            labelPlacement: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[ReactEvent => Callback] = js.undefined
           )(children: ChildArg*) = component(
    js.Dynamic.literal(
      "name" -> name,
      "value" -> value,
      "control" -> control,
      "label" -> label,
      "labelPlacement" -> labelPlacement,
      "onChange" -> onChange,
    ))(children: _*)

  val component = JsFnComponent[js.Object, Children.Varargs](componentValue)
}
