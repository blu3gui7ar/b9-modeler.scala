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
    override def validate(value: Option[JsValue]): Boolean = ???
  }

  implicit class ListDefValidator(r: ListRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = value exists {
      case l : JsArray => {
        r.ref match {
          case subl : ListRef => {
            l.value match {
              case ll: Seq[JsValue] => ll.forall(child => subl.validate(Some(child)))
              case _ => false
            }
          }
          case t : TypeRef => {
            val typeDef = types.get(t.name)
            l.value.forall(child => typeDef.exists(_.validate(Some(child))))
          }
        }
      }
      case _ => false
    }
  }

  implicit class TypeRefValidator(r: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = types.get(r.name).exists(_.validate(value))
  }

  implicit class AttrValidator (attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[JsValue]): Boolean = {
      val expanded = expand(attr.definition, macros)
      val typeValid = expanded.t.forall({
        case t: TypeRef => t.validate(value)
        case l: ListRef => l.validate(value)
      })
      val restrictValid = expanded.restricts.forall(_.forall(_.validate(value)))
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
