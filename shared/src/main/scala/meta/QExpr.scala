package meta

import MetaAst._
/**
  * Created by blu3gui7ar on 2017/6/1.
  */
trait QExpr {
  def expr(): String
}

object QExpr {
  implicit class ReferenceToQExpr(ref: Reference) extends QExpr {
    def expr() = ref match {
      case TypeRef(n) => n
      case ListRef(n) => s"Seq[${n.expr()}]"
      case MapRef(n) => s"Map[String, ${n.expr()}]"
    }
  }
  implicit class AttrToQExpr(attr: Attr)(implicit macros: Map[String, Macro]) extends QExpr {
    def expr() = s"${attr.name}: ${expand(attr.definition, macros).t.getOrElse(TypeRef("String")).expr()}"
  }

  implicit class TypeToQExpr(t: Type)(implicit macros: Map[String, Macro]) extends QExpr {
    def expr() = s"case class ${t.name} (" + t.members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr].expr()).mkString(", ") + ")"
  }

  implicit class RootToQExpr(r: Root)(implicit macros: Map[String, Macro]) extends QExpr {
    def expr() =
      Seq(
        r.members.filter(_.isInstanceOf[Type]).map(_.asInstanceOf[Type].expr()).mkString("\n"),
        "case class Meta (" + r.members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr].expr()).mkString(", ") + ")"
      ).mkString("\n")
  }
}
