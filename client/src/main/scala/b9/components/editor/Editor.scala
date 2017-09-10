package b9.components.editor

import b9._
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import meta.{MetaAst, TreeExtractor}

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object Editor {
  case class Props(model: ModelProxy[GraphState])

  class Backend($ : BackendScope[Props, Unit]) {

    def render(p: Props) = {
      val root = p.model.zoom(_.tree)
      val displayRoot = p.model.zoom(_.displayRoot)
      val meta = p.model.zoom(_.meta).value

      import JsonExpr._
      implicit val macros = MetaAst.macros(meta)
      implicit val types = MetaAst.types(meta)
      val jsStr = meta.json(Some(root.value))
      val json = upickle.json.read(jsStr.getOrElse("{}"))
      import EditorExtractor._
      val tagMod = meta.editor("meta", Some(json), TreeExtractor.RootAttrDef).getOrElse(b9.short.emptyTagMod)
      <.div(
        "Editor DIV",
        tagMod
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("AstNodeWithMembersEditor")
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
          val p = scope.props.model.zoom(_.tree)
          p.dispatchCB(GoUpAction(p())).async.runNow()
      }
    }
    .build

  def apply(model: ModelProxy[GraphState]) = component(Props(model))
}
