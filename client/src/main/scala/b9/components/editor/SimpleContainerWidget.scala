package b9.components.editor

import b9.TreeOps._
import b9.short._
import b9.{Dispatcher, ModelerCss}
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource
import monocle.std.tree._
import scalacss.ScalaCssReact._

object SimpleContainerWidget extends Widget with ReactEventTypes {
  val name = "Simple"
  override val container: Boolean = true

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode = {
    val label = tree.rootLabel
    val subEditors = tree.subForest.map { node =>
      Editor(node, lens composeLens subForest composeLens at(node), dispatcher, metaSource)(
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
