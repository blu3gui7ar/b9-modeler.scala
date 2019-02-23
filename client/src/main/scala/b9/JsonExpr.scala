//package b9
//
//import b9.TreeOps.TTN
//import meta.MetaAst._
//
//
///**
//  * Created by blu3gui7ar on 2017/6/1.
//  */
//trait JsonExpr {
//  def json(value: Option[TTN]): Option[String]
//}
//
//trait JsonExtractor {
//
//  implicit class ReferenceToJsonExpr(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
//    def json(value: Option[TTN]): Option[String] = ref match {
//      case t: TypeRef => types.get(t.name) flatMap {
//        _.json(value)
//      }
//      case l: ListRef => value flatMap { v =>
//        val subJsons = v.subForest flatMap { child =>
//          l.definition.t.map(_.json(Some(child)))
//        }
//        Some("[" + subJsons.mkString(", ") + "]")
//      }
//      case m: MapRef =>  value flatMap { v =>
//        val subJsons = v.subForest flatMap { child =>
//          m.definition.t map {
//            _.json(Some(child)) map { subJson =>
//              s""""${child.rootLabel.name}": $subJson"""
//            }
//          }
//        }
//        Some("{" + subJsons.mkString(", ") + "}")
//      }
//    }
//  }
//
//  implicit class AttrToJsonExpr(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
//    def json(value: Option[TTN]): Option[String] = value flatMap { v: TTN =>
//      val expandedAttrDef = expandMacro(attr.definition)(macros)
//      if(expandedAttrDef.isLeaf) {
//        Some(v.rootLabel.value.toString)
//      } else
//        expandedAttrDef.t flatMap { t =>
//          t.json(value) map { v => s""""${attr.name}": $v""" }
//        }
//    }
//  }
//
//  implicit class AstNodeWithMembersToJsonExpr(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends JsonExpr {
//    def json(value: Option[TTN]): Option[String] = value map { v: TTN =>
//      val attrs: Map[String, Attr] = t.members.filter(_.isInstanceOf[Attr]).map { a =>
//        val attr = a.asInstanceOf[Attr]
//        (attr.name -> attr)
//      }(collection.breakOut)
//
//      if (attrs.isEmpty) {
//        v.rootLabel.value.toString
//      } else {
//        val subJsons = for {
//          child <- v.subForest
//          attr <- attrs.get(child.rootLabel.name)
//          subJson <- attr.json(Some(child))
//        } yield {
//          s""""${attr.name}": ${subJson}"""
//        }
//        "{" + subJsons.mkString(", ") + "}"
//      }
//    }
//  }
//}
//
//object JsonExpr extends JsonExtractor
