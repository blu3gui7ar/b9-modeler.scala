package b9

import meta.MetaAst._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */

trait TreeExtractor {
  def tree(name: String, value: Option[Js.Value]): Option[TreeNode]
}

case class TreeNode(name: String, children: Seq[TreeNode], x: Double = 0, y: Double = 0)

object TreeExtractor {
  val Empty = create("empty", Seq.empty)

  def create(name: String, children: Seq[TreeNode]) = TreeNode(name, children)

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = types.get(ref.name).flatMap(_.tree(name, value))
  }

  implicit class ListRefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = value collect {
      case l : Js.Arr => {
        val typeDef = types.get(ref.ref.name)
        create(name, l.value.zipWithIndex.flatMap(child => typeDef.flatMap(_.tree(name + '[' + child._2 + ']', Some(child._1)))))
      }
    }
  }

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = {
      expand(attr.definition, macros).t.flatMap({
        case t: TypeRef => t.tree(name, value)
        case l: ListRef => l.tree(name, value)
      })
    }
  }

  implicit class TypeExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends TreeExtractor {
    def tree(name: String, value: Option[Js.Value]) : Option[TreeNode] = value collect {
      case obj : Js.Obj => create(name,
        t.members.filter(_.isInstanceOf[Attr]).flatMap({ m =>
          val attr = m.asInstanceOf[Attr]
          attr.tree(attr.name, obj.value.toMap.get(attr.name))
        })
      )
    }
  }
}
