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
  def Comment[_: P] = P( "#" ~/ AnyChar.rep ~/ Newline )

  def Number[_: P] = P( Digit.rep(1).! ).map(_.toInt)

  def ValueTerm[_: P] =
    P( CharIn("_a-zA-Z") ~ CharIn("_a-zA-Z0-9").rep ).!.map(MetaAst.Value)

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

  def Annotation[_: P] = (WidgetDesc | Restriction)

  def MacroDesc[_: P] = MacroTerm.!.map(MetaAst.MacroRef)

  def TypeDesc[_: P]: fastparse.P[MetaAst.Reference] = P(
      TypeTerm.!.map(term => MetaAst.TypeRef(term)) |
      NoCut("[" ~/ TypeDesc ~ "]").map(t => MetaAst.ListRef(t)) |
      NoCut("<" ~/ TypeDesc ~ ">").map(t => MetaAst.MapRef(t))
    )

  def WidgetDesc[_: P] = P( "@Widget(" ~/ CharIn("a-zA-Z0-9_").rep(1).! ~ (":" ~ ValueTerm.rep(sep = ",")).? ~ ")").map {
    case (name, params) => MetaAst.Widget(name, params.getOrElse(Seq.empty))
  }

//  def ValueDesc[_: P] = P( "@Value(" ~/ ValueTerm.rep(1, sep = ",") ~/ ")")
//  def Restrictions[_: P] = P( "@Restrict(" ~/ Restriction.rep(1, sep = ",") ~/ ")")
  def Restriction[_: P] : P[MetaAst.Restrict] = P( "@Restrict(" ~/ (Regexp | NumRange |  Custom) ~/ ")" )
  /* MultiChoices | SingleChoice */

  def EscapeSeq[_: P] = P( "\\" ~~ AnyChar )
  def RegexpChar[_: P] = P( CharPred((c) => c != '/' && c != '\\') | EscapeSeq )
  def Regexp[_: P] = P( "/" ~~ RegexpChar.repX.! ~~ "/" ).map(MetaAst.RegexpR)
  def NumRange[_: P] = P( CharIn("[(").! ~/ Number.? ~ "," ~ Number.? ~ CharIn(")]").!).map {
    case (open, min, max, close) => MetaAst.NumberRangeR(min, max, open == '(', close == ')')
  }
  def Custom[_: P] = P( "Custom(" ~/ (!")" ~ AnyChar).rep.! ~/ ")" ).map(MetaAst.CustomR)
//  val MultiChoices = P( "[" ~/ ValueTerm.rep(sep = ",") ~ "]").map(MetaAst.MultiChoicesR)
//  val SingleChoice = P( "<" ~/ ValueTerm.rep(sep = ",") ~ ">").map(MetaAst.SingleChoiceR)

  def Meta[_: P] = P(Semis.? ~ "Meta" ~/ BlockExpr ~ Semis.?).map(MetaAst.Root)

  def BlockExpr[_: P] : P[Seq[MetaAst.AstNode]] = P(Semis.? ~ "{" ~/ Block ~ "}")
//  def Body[_: P] = Chunk.rep(sep = Semis)
//  def BlockEnd[_: P] = Semis.? ~ &("}")
  def Block[_: P] = P(Semis.? ~ Chunk.rep(sep = Semis) ~ Semis.? ~ &("}"))

  def Chunk[_: P] = P(NoCut(Attr | Type | Macro))

  def Type[_: P] = P(TypeTerm.! ~/ BlockExpr).map {
    case (term, members) => MetaAst.Type(term, members)
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
