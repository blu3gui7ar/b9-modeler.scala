package meta

import java.util.UUID

import meta.MetaAst._
import play.api.libs.json._

import scalaz.Tree
import scalaz.Tree.Node
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
case class TreeNode[T](uuid: UUID, name: String, meta: AttrDef, value: play.api.libs.json.JsValue, attach: T)

class TreeExtractorTpl[T](defaultT: => T) {
  type N = Tree[TreeNode[T]]
  trait TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef): Option[N]
  }

  val emptyAttrDef = AttrDef(None, None, None, None, None)
  val RootAttrDef = AttrDef(None, Some(TypeRef("Meta")), None, None, None)
  val emptyTree = create("empty", Stream.empty, emptyAttrDef)

  def create(name: String, children: Stream[N], meta: AttrDef, value: JsValue = JsNull): N = Node(
    TreeNode(UUID.randomUUID(), name,  meta, value, defaultT),
    children
  )

  implicit class RefExtractor(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = value flatMap { v =>
      if(meta.isLeaf)
        Some(create(name, Stream.empty, meta, v))
      else
        ref match {
          case t: TypeRef => t.tree(name, value, meta)
          case l: ListRef => l.tree(name, value, meta)
          case m: MapRef => m.tree(name, value, meta)
        }
    }
  }

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = types.get(ref.name).flatMap(_.tree(name, value, meta))
  }

  implicit class ListDefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = value flatMap {
      case l : JsArray => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            l.value match {
              case ll: Seq[JsValue] => {
                Some(create(name,
                  ll.flatMap(child => subl.tree(name + "[?]", Some(child), meta.copy(t = Some(subl)))).toStream,
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
                l.value.flatMap(child => td.tree(name + "[?]", Some(child), meta.copy(t = Some(tr)))).toStream,
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

  implicit class MapDefExtractor(ref: MapRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = value flatMap {
      case m : JsObject => {
        ref.ref match {
          case subl @ (_: ListRef | _: MapRef) => {
            m.value match {
              case ll: Seq[(String, JsValue)] => {
                Some(create(name,
                  ll flatMap { case (key, child) =>
                    subl.tree(key, Some(child), meta.copy(t = Some(subl)))
                  } toStream,
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
                } toStream,
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

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = {
      val expandedAttrDef = expand(meta, macros)
      expandedAttrDef.t flatMap { _.tree(name, value, expandedAttrDef) }
    }
  }

  implicit class AstNodeWithMembersExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor[T] {
    def tree(name: String, value: Option[JsValue], meta: AttrDef) : Option[N] = value collect {
      case obj : JsObject => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.tree(attr.name, obj.value.toMap.get(attr.name), attr.definition)
        }).toStream,
        meta,
        obj,
      )
      case n : JsValue  => create(name, Stream.empty, meta, n)
    }
  }
}
