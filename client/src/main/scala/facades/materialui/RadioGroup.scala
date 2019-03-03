package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.{Children, JsFnComponent}
import org.scalajs.dom.html

import scala.scalajs.js

object RadioGroup {
  lazy val componentValue = MaterialUI.RadioGroup

  def apply(name: js.UndefOr[String] = js.undefined,
            defaultValue: js.UndefOr[String] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            row: js.UndefOr[Boolean] = js.undefined,
            onChange: js.UndefOr[(SyntheticEvent[html.Input], String) => Unit] = js.undefined
           )(children: ChildArg*) = component(
    js.Dynamic.literal(
      "name" -> name,
      "value" -> value,
      "defaultValue" -> defaultValue,
      "row" -> row,
      "onChange" -> onChange,
    ))(children: _*)

  val component = JsFnComponent[js.Object, Children.Varargs](componentValue)
}
