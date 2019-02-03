package b9.components.editor

import b9.TreeOps._
import b9.short._
import b9.{Dispatcher, ModelerCss}
import facades.materialui.{ExpansionPanel, ExpansionPanelDetails, ExpansionPanelSummary}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import monocle.std.tree._

import scala.scalajs.js
import scalacss.ScalaCssReact._
import js.Dynamic.literal


object Editor {

  def apply(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN]) = component(Props(tree, lens, dispatcher))

  case class Props(tree: TTN, lens: TLens, dispatcher: Dispatcher[TTN])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomNode = {
      val label = p.tree.rootLabel
      val sub = p.tree.subForest

      label.meta.widget flatMap { widget =>
        WidgetRegistry(widget.name)
      } map { w: Widget =>
        <.div(
          label.name,
          " : ",
          w.render(label, p.lens composeLens rootLabel, p.dispatcher)
        ).render
      } getOrElse {
        val subEditors = sub.map { node =>
          Editor(node, p.lens composeLens subForest composeLens at(node), p.dispatcher)(
            keyAttr := "editor-" + label.uuid.toString,
          )
        }
        if (false) {
          ExpansionPanel(defaultExpanded = true, CollapseProps = literal("timeout" -> literal("enter" -> 10, "exit" -> 10)))(
            ExpansionPanelSummary()(
              label.name
            ),
            ExpansionPanelDetails(classes = js.Dictionary("root" -> ModelerCss.panel.htmlClass))(subEditors.toTagMod)
          )
//        } else if (false) {
//          <.div(
//            MDLCss.layout,
//            <.header(
//              MDLCss.layoutHeader,
//              <.div(
//                MDLCss.layoutHeaderRow,
//                <.span(
//                  MDLCss.layoutTitle,
//                  label.name
//                )
//              )
//            ),
//            <.main(
//              MDLCss.layoutContent,
//              <.div(
//                MDLCss.pageContent,
//                subEditors.toVdomArray
//              )
//            )
//          )
        } else {
          <.div(
            label.name,
            <.div(
              ModelerCss.panelBorder,
              subEditors.toTagMod
            )
          )
        }
      }
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .stateless
    .renderBackend[Backend]
    .build

}
