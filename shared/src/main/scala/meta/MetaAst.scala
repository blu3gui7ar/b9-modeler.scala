package meta

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
object MetaAst {
  sealed abstract class AstNode

  case class Ident(ident: String) extends AstNode

//  case class IdentTerm(name: String) extends SimpleToken;

  case class Attr(name: String, definition: AttrDef) extends AstNode
  case class AttrDef(m: Option[MacroRef], t: Option[Reference], widget: Option[Widget],
                     values: Option[Seq[Value]], restricts: Option[Seq[Restrict]]) extends AstNode

  case class Macro(name: String, definition: AttrDef) extends AstNode
  case class Type(name: String, members: Seq[AstNode]) extends AstNode

//  case class Type(name: String, attrs: Map[String, Attr])
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
}
