package b9

import b9.short.TM
import meta.MetaAst._

/**
  * Created by blu3gui7ar on 2017/6/1.
  */
trait JsonExpr {
  def json(value: Option[TM]): Option[String]
}

object JsonExpr {

  implicit class ReferenceToJsonExpr(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TM]): Option[String] = ref match {
      case t: TypeRef => types.get(t.name) flatMap {
        _.json(value)
      }
      case l: ListRef => value flatMap { v =>
        val subJsons = v.subForest flatMap { child =>
          l.ref.json(Some(child))
        }
        Some("[" + subJsons.mkString(", ") + "]")
      }
      case m: MapRef => value flatMap { v =>
        val subJsons = v.subForest flatMap { child =>
          m.ref.json(Some(child)) map { subJson =>
              s""""${child.rootLabel.name}": $subJson"""
          }
        }
        Some("{" + subJsons.mkString(", ") + "}")
      }
    }
  }

  implicit class AttrToJsonExpr(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TM]): Option[String] = value flatMap { v: TM =>
      val expandedAttrDef = expand(attr.definition, macros)
      if(expandedAttrDef.widget.isDefined) {
        Some(v.rootLabel.value.toString)
      } else
        expandedAttrDef.t flatMap { t =>
          t.json(value) map { v => s""""${attr.name}": $v""" }
        }
    }
  }

  implicit class AstNodeWithMembersToJsonExpr(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
    def json(value: Option[TM]): Option[String] = value map { v: TM =>
      val attrs: Map[String, Attr] = t.members.filter(_.isInstanceOf[Attr]).map { a =>
        val attr = a.asInstanceOf[Attr]
        (attr.name -> attr)
      }(collection.breakOut)

      if (attrs.isEmpty) {
        v.rootLabel.value.toString
      } else {
        val subJsons = v.subForest flatMap { child =>
          val attr = attrs.get(child.rootLabel.name)
          attr flatMap {
            _.json(Some(child))
          }
        }
        "{" + subJsons.mkString(", ") + "}"
      }
    }
  }
}
