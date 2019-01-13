package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.TagMod
import play.api.libs.json.JsValue

trait Widget {
  def render(node: TN, nodeLens: LLens, dispatcher: Dispatcher[TTN]): TagMod

  def ref(node: TN) = node.uuid.toString

  def updateCB(value: JsValue)(implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN]) =
    Callback { update(value) }

  def update(value: JsValue)(implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN]) =
    dispatcher.dispatch(lens.set(node.copy(value = value)))
}
