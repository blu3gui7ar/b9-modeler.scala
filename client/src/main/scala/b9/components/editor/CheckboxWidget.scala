package b9.components.editor

import b9._
import b9.short.TM
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.HtmlAttrs.onChange
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.html_<^._
import meta.MetaAst.AttrDef
import upickle.Js

object CheckboxWidget extends Widget {
  val name = "Checkbox"

  override def render(ref: String, meta: AttrDef, value: Js.Value, mp: ModelProxy[TM]): TagMod = {
    val checked = value match {
      case vs: Js.Arr => vs.value.toSeq.map(_.value.toString)
      case _ => Seq.empty
    }

    meta.values map { choices =>
      val boxes = choices map { choice =>
        val active = checked.contains(choice.name)
        <.span(
          <.input(
            ^.`type` := "checkbox",
            ^.name := ref,
            ^.value := choice.name,
            ^.checked := active,
            onChange --> mp.dispatchCB(
              if(active){
//                val parent = mp()
//                val action = parent.children flatMap { children =>
//                  val choiceChild = children.filter(_.data.map(_.name == choice.name).getOrElse(false)).headOption
//                  choiceChild.map(RemoveFromAction(_, parent))
//                }
//                action.getOrElse(NoAction)
                ValueDelAction(mp(), ref, Js.Str(choice.name))
              }
              else {
//                val parent = mp()
//                val action = parent flatMap { children =>
//                  val choiceChild = children.filter(_.data.map(_.name == choice.name).getOrElse(false)).headOption
//                  choiceChild
//                  choiceChild.map(CreateAction(parent, choice.name, meta))
//                }
//                action.getOrElse(NoAction)
                ValueAddAction(mp(), ref, Js.Str(choice.name))
              }
            ),
          ),
          choice.name
        )
      }
      boxes.toTagMod
    } getOrElse EmptyVdom
  }
}
