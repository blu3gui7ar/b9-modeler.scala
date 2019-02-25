package b9.components.editor

import b9.TreeOps._
import b9.{Dispatcher, ModelerCss}
import facades.materialui.{ExpansionPanel, ExpansionPanelDetails, ExpansionPanelSummary}
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaSource
import monocle.std.tree._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

object ExpansionPanelWidget extends Widget with ReactEventTypes {
  val name = "ExpansionPanel"
  override val container: Boolean = true

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN], metaSource: MetaSource): VdomNode = {
    val label = tree.rootLabel
    val subEditors = tree.subForest.map { node =>
      Editor(node, lens composeLens subForest composeLens at(node), dispatcher, metaSource)
    }


    ExpansionPanel(defaultExpanded = true, CollapseProps = literal("timeout" -> literal("enter" -> 10, "exit" -> 10)))(
      ExpansionPanelSummary()(
        label.name
      ),
      ExpansionPanelDetails(classes = js.Dictionary("root" -> ModelerCss.panel.htmlClass))(
        subEditors.toVdomArray
      )
    )
  }
}
