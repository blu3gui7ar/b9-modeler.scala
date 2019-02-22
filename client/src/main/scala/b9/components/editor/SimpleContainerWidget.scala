package b9.components.editor

import b9.{Dispatcher, ModelerCss}
import b9.TreeOps._
import b9.short._
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._
import monocle.std.tree._

object SimpleContainerWidget extends Widget with ReactEventTypes {
  val name = "Simple"

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val label = tree.rootLabel
    val subEditors = tree.subForest.map { node =>
      Editor(node, lens composeLens subForest composeLens at(node), dispatcher)(
        keyAttr := "editor-" + label.uuid.toString,
      )
    }
    <.div(
      label.name,
      <.div(
        ModelerCss.panelBorder,
        subEditors.toTagMod
      )
    )
  }
}
