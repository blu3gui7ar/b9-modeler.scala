package meta

import java.util.UUID

import meta.MetaAst._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
case class TreeNode(uuid: UUID, name: String, children: Seq[TreeNode], meta: AttrDef, value: Js.Value,
                    x: Double = 0, y: Double = 0, fold: Boolean = false)

trait TreeExtractor {
  def tree(name: String, value: Option[Js.Value], meta: AttrDef): Option[TreeNode]
}

object TreeExtractor {
  val emptyAttrDef = AttrDef(None, None, None, None, None)
  val RootAttrDef = AttrDef(None, Some(TypeRef("Meta")), None, None, None)
  val emptyTree = create("empty", Seq.empty, emptyAttrDef)

  def create(name: String, children: Seq[TreeNode], meta: AttrDef, value: Js.Value = Js.Null) = TreeNode(UUID.randomUUID(), name, children, meta, value)

  implicit class RefExtractor(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = ref match {
      case t: TypeRef => t.tree(name, value, meta)
      case l: ListRef => l.tree(name, value, meta)
      case m: MapRef => m.tree(name, value, meta)
    }
  }

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = types.get(ref.name).flatMap(_.tree(name, value, meta))
  }

  implicit class ListDefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = value flatMap {
      case l : Js.Arr => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            l.value match {
              case ll: Seq[Js.Value] => {
                Some(create(name,
                  ll.flatMap(child => subl.tree(name + "[?]", Some(child), meta.copy(t = Some(subl)))),
                  meta,
                  l,
                ))
              }
              case _ => None
            }
          }
          case tr : TypeRef => {
            val typeDef = types.get(tr.name)
            typeDef.map { td: AstNodeWithMembers =>
              create(name,
                l.value.flatMap(child => td.tree(name + "[?]", Some(child), meta.copy(t = Some(tr)))),
                meta,
                l,
              )
            }
          }
        }
      }
      case _ => None
    }
  }

  implicit class MapDefExtractor(ref: MapRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = value flatMap {
      case m : Js.Obj => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            m.value match {
              case ll: Seq[(String, Js.Value)] => {
                Some(create(name,
                  ll flatMap { case (key, child) =>
                    subl.tree(key, Some(child), meta.copy(t = Some(subl)))
                  },
                  meta,
                  m,
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
                  td.tree(key, Some(child), meta.copy(t = Some(tr)))
                },
                meta,
                m,
              )
            }
          }
        }
      }
      case _ => None
    }
  }

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = {
      val expandedAttrDef = expand(meta, macros)
      expandedAttrDef.t flatMap { _.tree(name, value, expandedAttrDef) }
    }
  }

  implicit class AstNodeWithMembersExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = value collect {
      case obj : Js.Obj => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.tree(attr.name, obj.value.toMap.get(attr.name), attr.definition)
        }),
        meta,
        obj,
      )
      case n : Js.Value  => create(name, Seq.empty, meta, n)
    }
  }
}
