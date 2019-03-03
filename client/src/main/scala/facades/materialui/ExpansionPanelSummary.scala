package facades.materialui

import japgolly.scalajs.react.CtorType.ChildArg

import scala.scalajs.js
import japgolly.scalajs.react.{Children, JsFnComponent}

object ExpansionPanelSummary {
  lazy val componentValue = MaterialUI.ExpansionPanelSummary

  def apply(classes: js.UndefOr[js.Object] = js.undefined,
            expandIcon: js.UndefOr[js.Object] = js.undefined,
            IconButtonProps: js.UndefOr[js.Object] = js.undefined
           )(children: ChildArg*) = component(
    js.Dynamic.literal(
      "classes" -> classes,
      "expandIcon" -> expandIcon,
      "IconButtonProps" -> IconButtonProps,
    ))(children: _*)

  val component = JsFnComponent[js.Object, Children.Varargs](componentValue)
}
