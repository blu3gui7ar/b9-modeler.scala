package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.SyntheticEvent
import japgolly.scalajs.react.{Children, JsFnComponent}
import org.scalajs.dom.html

import scala.scalajs.js

object Radio {
  lazy val componentValue = MaterialUI.Radio

  def apply(checked: js.UndefOr[Boolean] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[(SyntheticEvent[html.Input], Boolean) => Unit] = js.undefined
           )(children: ChildArg*)  = component(
    js.Dynamic.literal(
      "checked" -> checked,
      "value" -> value,
      "id" -> id,
      "onChange" -> onChange,
    ))(children:_*)

  val component = JsFnComponent[js.Object, Children.Varargs](componentValue)
}

