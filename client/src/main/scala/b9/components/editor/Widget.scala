package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode
import play.api.libs.json.JsValue
import japgolly.scalajs.react.vdom.html_<^._
import monocle.std.tree._


trait Widget {
  val name: String
  val container = false

  def renderForm(tree: TTN, nodeLens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = ???

  def render(tree: TTN, nodeLens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val error = tree.rootLabel.meta.widget map { w =>
      if (w.isLeaf && container)
        Some("Wrong widget: " + w.toString)
      else
        None
    } getOrElse Some("Widget not found")


    <.div(
      tree.rootLabel.name,
      " : ",
      error.map(<.span(_))
        .getOrElse(renderForm(tree, nodeLens, dispatcher))
    )
  }

  protected def labelLens(lens: TLens): LLens = lens composeLens rootLabel

  def ref(node: TN) = "editor-widget-" + node.uuid.toString

  def updateCB(value: JsValue)(implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN]) =
    Callback { update(value) }

  def update(value: JsValue)(implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN]) =
    dispatcher.dispatch( lens.set( node.copy(value = value) ) )
}
