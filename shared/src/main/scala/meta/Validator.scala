package meta

import MetaAst._
import play.api.libs.json._
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
trait Validator {
  def validate(value: Option[JsValue]): Boolean
}

object Validator {

  implicit class RestrictValidator (restrict: Restrict) extends Validator {
    //TODO
    override def validate(value: Option[JsValue]): Boolean = true
  }

  implicit class ListDefValidator(r: ListRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = {
      val expandedAttr = expandMacro(r.definition)(macros)
      value exists {
        case l : JsArray => {
          expandedAttr.t match {
            case Some(subl : ListRef) => {
              l.value match {
                case ll: Seq[JsValue] => ll.forall(child => subl.validate(Some(child)))
                case _ => false
              }
            }
            case Some(t : TypeRef) => {
              val typeDef = types.get(t.name)
              l.value.forall(child => typeDef.exists(_.validate(Some(child))))
            }
          }
        }
        case _ => false
      }
    }
  }

  implicit class TypeRefValidator(r: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = types.get(r.name).exists(_.validate(value))
  }

  implicit class AttrValidator (attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = {
      val expanded = expandMacro(attr.definition)(macros)
      val typeValid = expanded.t.forall({
        case t: TypeRef => t.validate(value)
        case l: ListRef => l.validate(value)
      })
      val restrictValid = expanded.restricts.forall(_.validate(value))
      typeValid && restrictValid
    }
  }

  implicit class TypeValidator(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = value exists {
      case obj : JsObject => t.members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr])
        .forall(attr => attr.validate(obj.value.toMap.get(attr.name)))
      case _ => false
    }
  }
}
