package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource
import monocle.std.tree._
import play.api.libs.json.JsValue
import shared.validator.{JsValueValidator, ValidateResult}

trait Widget {
  val name: String
  val container = false

  val validator = new JsValueValidator()

  def renderForm(tree: TTN, nodeLens: TLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource): VdomNode = ???

  def render(tree: TTN, nodeLens: TLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource): VdomNode = {
    val error = tree.rootLabel.meta.widget map { w =>
      if (w.isLeaf && container)
        Some("Wrong widget: " + w.toString)
      else
        None
    } getOrElse Some("Widget not declared")


    <.div(
      tree.rootLabel.name,
      " : ",
      error.map(<.span(_))
        .getOrElse(renderForm(tree, nodeLens, dispatcher, metaSrc))
    )
  }

  protected def labelLens(lens: TLens): LLens = lens composeLens rootLabel

  def ref(node: TN) = "editor-widget-" + node.uuid.toString

  def updateCB(value: JsValue)
              (implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource) =
    Callback { update(value) }

  def update(value: JsValue)
            (implicit node: TN, lens: LLens, dispatcher: Dispatcher[TTN], metaSrc: MetaSource) = {
    import metaSrc._
    import validator._
    val newNode = node.copy(value = value)
    val result = newNode.meta.t flatMap { ref =>
      ref.transform(node.name, Some(value), node.meta, None)
    }

    //TODO error msg
    result match {
      case Some(ValidateResult(true, _)) => dispatcher.dispatch( lens.set( newNode ) )
      case Some(ValidateResult(_, msgs)) => println(msgs)
      case _ => ()
    }
  }
}
