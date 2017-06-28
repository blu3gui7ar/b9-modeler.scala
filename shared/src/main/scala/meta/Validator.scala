package meta

import MetaAst._
import upickle.Js
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
trait Validator {
  def validate(value: Option[Js.Value]): Boolean
}

object Validator {

  implicit class RestrictValidator (restrict: Restrict) extends Validator {
    override def validate(value: Option[Js.Value]): Boolean = ???
  }

  implicit class ListDefValidator(r: ListDef)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[Js.Value]): Boolean = value exists {
      case l : Js.Arr => {
        r.ref match {
          case subl : ListDef => {
            l.value match {
              case ll: Seq[Js.Value] => ll.forall(child => subl.validate(Some(child)))
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
    override def validate(value: Option[Js.Value]): Boolean = types.get(r.name).exists(_.validate(value))
  }

  implicit class AttrValidator (attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[Js.Value]): Boolean = {
      val expanded = expand(attr.definition, macros)
      val typeValid = expanded.t.forall({
        case t: TypeRef => t.validate(value)
        case l: ListDef => l.validate(value)
      })
      val restrictValid = expanded.restricts.forall(_.forall(_.validate(value)))
      typeValid && restrictValid
    }
  }

  implicit class TypeValidator(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, Type]) extends Validator {
    override def validate(value: Option[Js.Value]): Boolean = value exists {
      case obj : Js.Obj => t.members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr])
        .forall(attr => attr.validate(obj.value.toMap.get(attr.name)))
      case _ => false
    }
  }
}
