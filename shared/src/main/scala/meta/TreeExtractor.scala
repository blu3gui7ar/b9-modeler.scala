package meta

import meta.MetaAst._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
case class TreeNode(name: String, children: Seq[TreeNode], meta: AttrDef, value: Js.Value,
                    x: Double = 0, y: Double = 0, fold: Boolean = false)

trait TreeExtractor {
  def tree(name: String, value: Option[Js.Value], meta: AttrDef): Option[TreeNode]
}

object TreeExtractor {
  val emptyAttrDef = AttrDef(None, None, None, None, None)
  val RootAttrDef = AttrDef(None, Some(TypeRef("Meta")), None, None, None)
  val emptyTree = create("empty", Seq.empty, emptyAttrDef)

  def create(name: String, children: Seq[TreeNode], meta: AttrDef, value: Js.Value = Js.Null) = TreeNode(name, children, meta, value)

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = types.get(ref.name).flatMap(_.tree(name, value, meta))
  }

  implicit class ListDefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = value flatMap {
      case l : Js.Arr => {
        ref.ref match {
          case subl : ListRef => {
            l.value match {
              case ll: Seq[Js.Value] => {
                Some(create(name,
                  ll.zipWithIndex.flatMap(child => subl.tree(name + '[' + child._2 + ']', Some(child._1), meta)),
                  meta,
                  l
                ))
              }
              case _ => None
            }
          }
          case t : TypeRef => {
            val typeDef = types.get(t.name)
            typeDef.map { td: AstNodeWithMembers =>
              create(name,
                l.value.zipWithIndex.flatMap(child => td.tree(name + '[' + child._2 + ']', Some(child._1), meta)),
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

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = {
      val expandedAttrDef = expand(attr.definition, macros)
      expandedAttrDef.t.flatMap({
        case t: TypeRef => t.tree(name, value, expandedAttrDef)
        case l: ListRef => l.tree(name, value, expandedAttrDef)
      })
    }
  }

  implicit class TypeExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value], meta: AttrDef) : Option[TreeNode] = value collect {
      case obj : Js.Obj => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.tree(attr.name, obj.value.toMap.get(attr.name), meta)
        }),
        meta,
        obj
      )
      case n : Js.Value  => create(name, Seq.empty, meta, n)
    }
  }
}
