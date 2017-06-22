package meta

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
object WsApi extends fastparse.WhitespaceApi.Wrapper({
  import fastparse.all._
  NoTrace(StringIn(" ", "\t").rep)
})

object MetaParser {
  import WsApi._
  import fastparse.noApi._

  //Basic
  val digits = "0123456789"
  val Digit = P(CharIn(digits))

  val Newline = P(StringIn("\n", "\r\n", "\r"))
  val Semi = P( ";" | Newline )
  val Semis = P(Semi.rep(1))

  //Literals
  val Comment = P( "#" ~/ AnyChar.rep ~/ Newline )

  val Number = P( Digit.rep(1).! ).map(_.toInt)

  val ValueTerm = {
    val first = P( CharIn('a' to 'z', 'A' to 'Z', "_") )
    val rest = P( CharIn('a' to 'z', 'A' to 'Z', '0' to '9', "_").rep )
    P( first ~ rest ).!.map(MetaAst.Value)
  }

  val IdentTerm = {
    val first = P( CharIn('a' to 'z') )
    val rest = P( CharIn('a' to 'z', 'A' to 'Z', '0' to '9', "_").rep )
    P( first ~ rest ).!.map(MetaAst.Ident)
  }
  val TypeTerm = {
    val first = P( CharIn('A' to 'Z') )
    val rest = P( CharIn('a' to 'z', 'A' to 'Z', '0' to '9', "_").rep )
    P( first ~ rest ).!
  }

  val MacroTerm = {
    val first = P( "%" )
    val rest = P( CharIn('A' to 'Z', "_").rep )
    P( first ~/ rest ).!
  }

  var ListTypeTerm = {
    P("[" ~/ TypeTerm ~ "]")
  }

  val Attr = P( IdentTerm.! ~/ ":".? ~/ AttrDef).map {
    case (ident, attrdef) => MetaAst.Attr(ident, attrdef)
  }

  val AttrDef : P[MetaAst.AttrDef] = P( MacroDesc.? ~ TypeDesc.? ~ WidgetDesc.? ~ ValueDesc.? ~ Restrictions.? ).map {
    case (md, td, wd, vd, res) => MetaAst.AttrDef(md, td, wd, vd, res)
  }

  var MacroDesc = MacroTerm.map(MetaAst.MacroRef)
  val TypeDesc : P[MetaAst.Reference] = P( (TypeTerm.map(MetaAst.TypeRef) | NoCut(ListTypeTerm.map((t) => MetaAst.ListRef(MetaAst.TypeRef(t)))) ))
  val WidgetDesc = P( "::" ~/ StringIn("Text", "Checkbox", "Radio", "Select", "TextArea").!).map(MetaAst.Widget)
  val ValueDesc = P( "$" ~/ ValueTerm.rep(1, sep = ","))
  val Restrictions = P( "|" ~/ Restriction.rep(1, sep = ","))
  val Restriction : P[MetaAst.Restrict] = P( Regexp | NumRange |  Custom )
  /* MultiChoices | SingleChoice */

  val EscapeSeq = P( "\\" ~~ AnyChar )
  val RegexpChar = P( CharPred((c) => c != '/' && c != '\\') | EscapeSeq )
  val Regexp = P( "/" ~~ RegexpChar.repX.! ~~ "/" ).map(MetaAst.RegexpR)
  val NumRange = P( CharIn("[(").! ~/ Number.? ~ "," ~ Number.? ~ CharIn(")]").!).map {
    case (open, min, max, close) => MetaAst.NumberRangeR(min, max, open == '(', close == ')')
  }
  val Custom = P( "Custom(" ~/ (!")" ~ AnyChar).rep.! ~/ ")" ).map(MetaAst.CustomR)
//  val MultiChoices = P( "[" ~/ ValueTerm.rep(sep = ",") ~ "]").map(MetaAst.MultiChoicesR)
//  val SingleChoice = P( "<" ~/ ValueTerm.rep(sep = ",") ~ ">").map(MetaAst.SingleChoiceR)

  val Meta = P(Semis.? ~ "Meta" ~/ BlockExpr ~ Semis.?).map(MetaAst.Root(_))
  val BlockExpr : P[Seq[MetaAst.AstNode]] = P(Semis.? ~ "{" ~/ Block ~ "}")
  val Block = {
    val BlockEnd = P(Semis.? ~ &("}"))
    val Body = P(Chunk.rep(sep = Semis))
    P(Semis.? ~ Body ~ BlockEnd)
  }
  val Chunk = P(NoCut(Attr | Type | Macro))

  val Type = P(TypeTerm ~/ BlockExpr).map {
    case (name, members) => MetaAst.Type(name, members)
  }
  val Macro = P(MacroTerm ~/ "=" ~/ AttrDef).map {
    case (name, definition) => MetaAst.Macro(name, definition)
  }

  def main(args: Array[String]): Unit = {

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
  }
}
