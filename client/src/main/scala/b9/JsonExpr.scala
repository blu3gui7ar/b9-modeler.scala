package b9

import b9.short.TN
import meta.MetaAst._

/**
  * Created by blu3gui7ar on 2017/6/1.
  */
trait JsonExpr {
  def json(value: Option[TN]): Option[String]
}

object JsonExpr {

  implicit class ReferenceToJsonExpr(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TN]): Option[String] = ref match {
      case t: TypeRef => types.get(t.name) flatMap {
        _.json(value)
      }
      case l: ListRef => value flatMap { v =>
        v.children.toOption match {
          case Some(null) => None
          case Some(children) => {
            val subJsons = children flatMap { child =>
              l.ref.json(Some(child))
            }
            if (subJsons.nonEmpty) Some("[" + subJsons.mkString(", ") + "]") else None
          }
          case _ => None
        }
      }
      case m: MapRef => value flatMap { v =>
        v.children.toOption match {
          case Some(null) => None
          case Some(children) => {
            val subJsons = children flatMap { child =>
              m.ref.json(Some(child)) flatMap { subJson =>
                child.data.toOption map { tn =>
                  s""""${tn.name}": $subJson"""
                }
              }
            }
            if (subJsons.nonEmpty) Some("{" + subJsons.mkString(", ") + "}") else None
          }
          case _ => None
        }
      }
    }
  }

  implicit class AttrToJsonExpr(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TN]): Option[String] = value flatMap { _ =>
      expand(attr.definition, macros).t flatMap { t =>
        t.json(value) map { v => s""""${attr.name}": $v""" }
      }
    }
  }

  implicit class AstNodeWithMembersToJsonExpr(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TN]): Option[String] = value flatMap { v =>
      val attrs: Map[String, Attr] = t.members.filter(_.isInstanceOf[Attr]).map { a =>
        val attr = a.asInstanceOf[Attr]
        (attr.name -> attr)
      }(collection.breakOut)

      if (attrs.isEmpty) {
        v.data.toOption map { _.value.toString }
      } else {
        v.children.toOption match {
          case Some(null) => None
          case Some(children) => {
            val subJsons = children flatMap { child =>
              child.data.toOption flatMap { tn =>
                val attr = attrs.get(tn.name)
                attr flatMap {
                  _.json(Some(child))
                }
              }
            }
            if (subJsons.nonEmpty) Some("{" + subJsons.mkString(", ") + "}") else None
          }
          case _ => None
        }
      }
    }
  }
}
