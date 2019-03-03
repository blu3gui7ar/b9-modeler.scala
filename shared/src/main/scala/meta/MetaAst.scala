package meta

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
object MetaAst {
  val DEFAULT = "%DEFAULT"
  val ROOT = "Meta"

  sealed abstract class AstNode
  sealed abstract class AstNodeWithMembers(val name: String, val members: Seq[AstNode], val container: Option[Widget]) extends AstNode {
    def attrs = members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr])
    def isSimple: Boolean = attrs.isEmpty
  }

  case class Ident(ident: String) extends AstNode

  case class Root(override val members: Seq[AstNode], override val container: Option[Widget])
    extends AstNodeWithMembers(ROOT, members, container)

  case class Attr(name: String, definition: AttrDef) extends AstNode
  case class AttrDef(m: Option[MacroRef], t: Option[Reference], widget: Option[Widget], restricts: Seq[Restrict]) extends AstNode {
    def isLeaf: Boolean = widget.map(_.isLeaf).getOrElse(false)
  }

  case class Comment(content: String) extends AstNode
  case class Macro(name: String, definition: AttrDef) extends AstNode
  case class Type(override val name: String, override val members: Seq[AstNode], override val container: Option[Widget])
    extends AstNodeWithMembers(name, members, container)

  case class Widget(name: String, parameters: Seq[Value], isLeaf: Boolean = true) extends AstNode
  case class Value(name: String) extends AstNode

  case class Restrict(name: String, parameters: Seq[Value]) extends AstNode

  case class MacroRef(name: String) extends AstNode

  sealed trait Reference extends AstNode
  case class TypeRef(name: String) extends Reference
  case class ListRef(definition: AttrDef) extends Reference
  case class MapRef(definition: AttrDef) extends Reference


  def rootAttrDef()(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) =
    AttrDef(None, Some(TypeRef(ROOT)), types.get(ROOT).flatMap(_.container), Seq.empty)

  def expandMacro(attrDef: AttrDef)(implicit macros: Map[String, Macro]): AttrDef = {
    val expanded = expand0(attrDef, macros)
    expanded match {
      case AttrDef(_, None, _,  _) =>
        val default = macros.get(DEFAULT) map { m => MacroRef(m.name)}
        expand0(expanded.copy(m = default), macros)
      case _ => expanded
    }
  }

  protected def expand0(attrDef: AttrDef, macros: Map[String, Macro]): AttrDef = {
    attrDef match {
      case AttrDef(None, _, _,  _) => attrDef
      case AttrDef(m, _, _, _) =>
        val merged = for {
          mf <- m
          md <- macros.get(mf.name)
        } yield merge(md.definition, attrDef)
        merged match {
          case None => attrDef
          case Some(mdef) => expand0(mdef, macros)
        }
    }
  }

  def merge(defMacro: AttrDef, defRefined: AttrDef): AttrDef = AttrDef(
    defMacro.m,
    (defRefined.t ++ defMacro.t).reduceLeftOption((a, _) => a),
    (defRefined.widget ++ defMacro.widget).reduceLeftOption((a, b) =>
      if (b.name == "_")
        Widget(a.name, a.parameters ++ b.parameters)
      else
        b
    ),
    defRefined.restricts ++ defMacro.restricts
  )

  def expandWidget(typeName: String, meta: AttrDef)(implicit types: Map[String, AstNodeWithMembers]): AttrDef = {
    val w: Option[Widget]  =
      meta.widget match {
        case Some(_) => meta.widget
        case _ => types.get(typeName).flatMap(_.container)
      }

    meta.copy(widget = w)
  }

  def macros(r: Root): Map[String, Macro] = (r.members collect { case m: Macro =>
    (m.name, m)
  }).toMap

  def types(r: Root): Map[String, AstNodeWithMembers] = {
    val types: Seq[(String, AstNodeWithMembers)] = r.members collect { case t: Type =>
      val expanded = t.container match {
        case None => t.copy(container = r.container)
        case _ => t
      }
      (t.name, expanded)
    }
    types.toMap + (r.name -> r)
  }
}
