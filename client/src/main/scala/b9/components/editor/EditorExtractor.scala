package b9.components.editor

import b9.short
import japgolly.scalajs.react.vdom.TagMod
import meta.MetaAst._
import japgolly.scalajs.react.vdom.html_<^._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */

trait EditorExtractor {
  def editor(name: String, value: Option[Js.Value], meta: AttrDef): Option[TagMod]
}

object EditorExtractor {

  def create(name: String, children: Seq[TagMod], meta: AttrDef, value: Js.Value = Js.Null) = {
    val ref = "abc"

    val editor = meta.widget flatMap { widget =>
      WidgetRegistry(widget.name)
    } map { _.render(ref, meta, value) } getOrElse(short.emptyTagMod)

    <.div(
      <.span(name),
      <.span(" : "),
      TagMod(children: _*).unless(children.isEmpty),
      editor.when(children.isEmpty)
    )
  }


  implicit class RefExtractor(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = ref match {
      case t: TypeRef => t.editor(name, value, meta)
      case l: ListRef => l.editor(name, value, meta)
      case m: MapRef => m.editor(name, value, meta)
    }
  }

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = types.get(ref.name).flatMap(_.editor(name, value, meta))
  }

  implicit class ListRefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = value flatMap {
      case l : Js.Arr => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            l.value match {
              case ll: Seq[Js.Value] => {
                Some(create(name,
                  ll.flatMap(child => subl.editor(name + "[?]", Some(child), meta.copy(t = Some(subl)))),
                  meta,
                  l
                ))
              }
              case _ => None
            }
          }
          case tr : TypeRef => {
            val typeDef = types.get(tr.name)
            typeDef.map { td: AstNodeWithMembers =>
              create(name,
                l.value.flatMap(child => td.editor(name + "[?]", Some(child), meta.copy(t = Some(tr)))),
                meta,
                l
              )
            }
          }
        }
      }
      case _ => None
    }
  }

  implicit class MapRefExtractor(ref: MapRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = value flatMap {
      case m : Js.Obj => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            m.value match {
              case ll: Seq[(String, Js.Value)] => {
                Some(create(name,
                  ll flatMap { case (key, child) =>
                    subl.editor(key, Some(child), meta.copy(t = Some(subl)))
                  },
                  meta,
                  m
                ))
              }
              case _ => None
            }
          }
          case tr : TypeRef => {
            val typeDef = types.get(tr.name)
            typeDef.map { td: AstNodeWithMembers =>
              create(name,
                m.value flatMap { case (key, child) =>
                  td.editor(key, Some(child), meta.copy(t = Some(tr)))
                },
                meta,
                m
              )
            }
          }
        }
      }
      case _ => None
    }
  }

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = {
      val expandedAttrDef = expand(attr.definition, macros)
      expandedAttrDef.t flatMap { _.editor(name, value, expandedAttrDef) }
    }
  }

  implicit class AstNodeWithMembersExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends EditorExtractor {
    def editor(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TagMod] = value collect {
      case obj : Js.Obj => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.editor(attr.name, obj.value.toMap.get(attr.name), meta)
        }),
        meta,
        obj
      )
      case n : Js.Value  => create(name, Seq.empty, meta, n)
    }
  }
}
