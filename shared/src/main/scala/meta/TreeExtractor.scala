package meta

import meta.MetaAst._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
case class TreeNode(name: String, children: Seq[TreeNode], meta: TypeDef,
                    x: Double = 0, y: Double = 0, fold: Boolean = false)

trait TreeExtractor {
  def tree(name: String, value: Option[Js.Value]): Option[TreeNode]
}

object TreeExtractor {
  val Empty = create("empty", Seq.empty, Type("String", Seq.empty))

  def create(name: String, children: Seq[TreeNode], meta: TypeDef) = TreeNode(name, children, meta)

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = types.get(ref.name).flatMap(_.tree(name, value))
  }

  implicit class ListDefExtractor(ref: ListDef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = value flatten {
      case l : Js.Arr => {
        ref.ref match {
          case subl : ListDef => {
            l.value match {
              case ll: Seq[Js.Value] => {
                create(name,
                  ll.zipWithIndex.flatMap(child => subl.tree(name + '[' + child._2 + ']', Some(child._1))),
                  subl
                )
              }
              case _ =>
            }
          }
          case t : TypeRef => {
            val typeDef = types.get(t.name)
            typeDef.map { td: Type =>
              create(name,
                l.value.zipWithIndex.flatMap(child => td.tree(name + '[' + child._2 + ']', Some(child._1))),
                td
              )
            }
          }
        }
      }
      case _ =>
    }
  }

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = {
      expand(attr.definition, macros).t.flatMap({
        case t: TypeRef => t.tree(name, value)
        case l: ListDef => l.tree(name, value)
      })
    }
  }

  implicit class TypeExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = value collect {
      case obj : Js.Obj => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.tree(attr.name, obj.value.toMap.get(attr.name))
        }),
        t
      )
    }
  }
}
