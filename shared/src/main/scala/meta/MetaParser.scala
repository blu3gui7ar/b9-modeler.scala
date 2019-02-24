package meta

import meta.MetaAst.{Restrict, Widget}

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
object MetaParser {
  import fastparse._
  import SingleLineWhitespace._

  //Basic
  def Digit[_: P] = P(CharIn("0-9"))

  def Newline[_: P] = P(StringIn("\n", "\r\n", "\r"))
  def Semi[_: P] = P( ";" | Newline )
  def Semis[_: P] = P(Semi.rep(1))

  //Literals
  def Comment[_: P] = P( "#" ~/ (AnyChar.rep).! ~/ Newline ).map(MetaAst.Comment)

  def Number[_: P] = P( Digit.rep(1).! ).map(_.toInt)

  def ValueTerm[_: P] =
    P( "\"" ~/ (CharPred(_ != '"').rep).! ~ "\"".rep ).map(MetaAst.Value)

  def IdentTerm[_: P] =
    P( CharIn("_a-z") ~ CharIn("_a-zA-Z0-9").rep ).!.map(MetaAst.Ident)

  def TypeTerm[_: P] =
    P( CharIn("A-Z") ~ CharIn("_a-zA-Z0-9").rep ).!.map(MetaAst.Ident)

  def MacroTerm[_: P] = P( "%" ~ CharIn("A-Z_").rep(1) )

  def Attr[_: P] = P( IdentTerm.! ~/ ":".? ~/ AttrDef).map {
    case (ident, attrdef) => MetaAst.Attr(ident, attrdef)
  }

  def AnnoSep[_: P] = (Newline ~ &("@")).?

  def AttrDef[_: P] : P[MetaAst.AttrDef] = P(MacroDesc.? ~ TypeDesc.? ~ AnnoSep ~ Annotation.rep(sep = AnnoSep))
    .map { case (macrodesc, typedesc, annos) =>
      val widget = annos.collectFirst {
        case wd: Widget => wd
      }
      val restricts = annos.filter(_.isInstanceOf[Restrict]).map(_.asInstanceOf[Restrict])
      MetaAst.AttrDef(macrodesc, typedesc, widget, restricts)
    }

  def Annotation[_: P] = (WidgetDesc | ContainerDesc | Restriction)

  def MacroDesc[_: P] = MacroTerm.!.map(MetaAst.MacroRef)

  def TypeDesc[_: P]: fastparse.P[MetaAst.Reference] = P(
      TypeTerm.!.map(t => MetaAst.TypeRef(t)) |
      NoCut("[" ~/ AttrDef ~ "]" ).map { a => MetaAst.ListRef(a) } |
      NoCut("<" ~/ AttrDef ~ ">" ).map { a => MetaAst.MapRef(a) }
    )

  def WidgetDesc[_: P] = P( "@Widget(" ~/ CharIn("a-zA-Z0-9_").rep(1).! ~ (":" ~ ValueTerm.rep(sep = ",")).? ~ ")").map {
    case (name, params) => MetaAst.Widget(name, params.getOrElse(Seq.empty))
  }

//  def ValueDesc[_: P] = P( "@Value(" ~/ ValueTerm.rep(1, sep = ",") ~/ ")")
  def Restriction[_: P] = P( "@Restrict(" ~/ CharIn("a-zA-Z0-9_").rep(1).! ~ (":" ~ ValueTerm.rep(sep = ",")).? ~ ")").map {
    case (name, params) => MetaAst.Restrict(name, params.getOrElse(Seq.empty))
  }

  def ContainerDesc[_: P] = P( "@Container(" ~/ CharIn("a-zA-Z0-9_").rep(1).! ~ (":" ~ ValueTerm.rep(sep = ",")).? ~ ")").map {
    case (name, params) => MetaAst.Widget(name, params.getOrElse(Seq.empty), false)
  }
  def Meta[_: P] = P(Semis.? ~ MetaAst.ROOT ~/ BlockExpr ~ AnnoSep ~ ContainerDesc.? ~ Semis.?).map {
    case (nodes, container) => MetaAst.Root(nodes, container)
  }

  def BlockExpr[_: P] : P[Seq[MetaAst.AstNode]] = P(Semis.? ~ "{" ~/ Block ~ "}" )

  def Block[_: P] = P(Semis.? ~ Chunk.rep(sep = Semis) ~ Semis.? ~ &("}"))

  def Chunk[_: P] = P(NoCut(Attr | Type | Macro | Comment))

  def Type[_: P] = P(TypeTerm.! ~/ BlockExpr ~ AnnoSep ~ ContainerDesc.?).map {
    case (term, members, container) => MetaAst.Type(term, members, container)
  }
  def Macro[_: P] = P(MacroTerm.! ~/ "=" ~/ AttrDef).map {
    case (name, definition) => MetaAst.Macro(name, definition)
  }

//  def main(args: Array[String]): Unit = {

//    val dataJs = upickle.json.read(Sample.data)
//    val rs = Meta.parse(Sample.meta)
//    import TreeExtractor._
//    print(rs match {
//      case Success(meta, _) => {
//        implicit val macros = MetaAst.macros(meta)
//        implicit val types = MetaAst.types(meta)
//        meta.tree("meta", Some(dataJs))
//      }
//      case _ => ""
//    })

//    val d = QExpr.merge(
//      MetaAst.AttrDef(Some(MetaAst.MacroRef("ABC")), Some(MetaAst.TypeRef("XYZ")), None, None, None),
//      MetaAst.AttrDef(Some(MetaAst.MacroRef("DEf")), None, Some(MetaAst.Widget("Input")), None, None)
//    )
//
//    print(d)
//  }
}
