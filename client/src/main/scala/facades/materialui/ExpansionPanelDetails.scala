package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js

object ExpansionPanelDetails {
  lazy val componentValue = MaterialUI.ExpansionPanelDetails

  def apply(classes: js.UndefOr[js.Any] = js.undefined)(children: ChildArg*) = component(
    js.Dynamic.literal("classes" -> classes)
  )(children: _*)

  val component = JsComponent[js.Object, Children.Varargs, Null](componentValue)
}
