package b9.components.editor

import b9.{Dispatcher, ModelerCss}
import b9.TreeOps._
import b9.short._
import japgolly.scalajs.react.ReactEventTypes
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import monocle.std.tree._
import facades.materialui.{ExpansionPanel, ExpansionPanelDetails, ExpansionPanelSummary}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

object ExpansionPanelWidget extends Widget with ReactEventTypes {
  val name = "ExpansionPanel"

  override def render(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]): VdomNode = {
    val label = tree.rootLabel
    val subEditors = tree.subForest.map { node =>
      Editor(node, lens composeLens subForest composeLens at(node), dispatcher)(
        keyAttr := "editor-" + label.uuid.toString,
      )
    }


    ExpansionPanel(defaultExpanded = true, CollapseProps = literal("timeout" -> literal("enter" -> 10, "exit" -> 10)))(
      ExpansionPanelSummary()(
        label.name
      ),
      ExpansionPanelDetails(classes = js.Dictionary("root" -> ModelerCss.panel.htmlClass))(subEditors.toTagMod)
    )
  }
}
