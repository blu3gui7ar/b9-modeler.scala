package meta

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
object MetaAst {
  sealed abstract class AstNode
  sealed abstract class AstNodeWithMembers(val name: String, val members: Seq[AstNode]) extends AstNode

  case class Ident(ident: String) extends AstNode

  case class Root(override val members: Seq[AstNode]) extends AstNodeWithMembers("Meta", members)

  case class Attr(name: String, definition: AttrDef) extends AstNode
  case class AttrDef(m: Option[MacroRef], t: Option[Reference], widget: Option[Widget],
                     values: Option[Seq[Value]], restricts: Option[Seq[Restrict]]) extends AstNode

  case class Macro(name: String, definition: AttrDef) extends AstNode
  case class Type(override val name: String, override val members: Seq[AstNode]) extends AstNodeWithMembers(name, members)

  case class Widget(name: String) extends AstNode
  case class Value(name: String) extends AstNode

  sealed abstract class Restrict extends AstNode

  case class DummyR() extends Restrict
  case class NumberRangeR(min: Option[Int], max: Option[Int], minOpen: Boolean, maxOpen: Boolean) extends Restrict
  case class RegexpR(regexp: String) extends Restrict
  case class MultiChoicesR(choices: Seq[Value]) extends Restrict
  case class SingleChoiceR(choices: Seq[Value]) extends Restrict
  case class CustomR(exp: String) extends Restrict

  case class MacroRef(name: String) extends AstNode

  sealed abstract class Reference extends AstNode
  case class TypeRef(name: String) extends Reference
  case class ListRef(ref: TypeRef) extends Reference


  def expand(attrDef: AttrDef, macros: Map[String, Macro]): AttrDef = {
    attrDef match {
      case AttrDef(None, _, _, _, _) => attrDef
      case AttrDef(m, _, _, _, _) => {
        val merged = for {
          mf <- m
          md <- macros.get(mf.name)
        } yield merge(md.definition, attrDef)
        merged match {
          case None => attrDef
          case Some(mdef) => expand(mdef, macros)
        }
      }
    }
  }

  def merge(defMacro: AttrDef, defRefined: AttrDef): AttrDef = AttrDef(
    defMacro.m,
    (defRefined.t ++ defMacro.t).reduceLeftOption((a, _) => a),
    (defRefined.widget ++ defMacro.widget).reduceLeftOption((a, _) => a),
    (defRefined.values ++ defMacro.values).reduceLeftOption((a, _) => a),
    (defRefined.restricts ++ defMacro.restricts).reduceLeftOption((a, _) => a)
  )

  def macros(r: Root) = r.members.filter(_.isInstanceOf[Macro]).map(_.asInstanceOf[Macro]).map((m) => (m.name, m)).toMap

  def types(r: Root) = r.members.filter(_.isInstanceOf[Type]).map(_.asInstanceOf[Type]).map((t) => (t.name, t)).toMap
}