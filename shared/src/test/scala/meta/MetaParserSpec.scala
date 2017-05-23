package meta

import fastparse.core.Parsed
import org.scalatest._

/**
  * Created by blu3gui7ar on 2017/4/27.
  */
class MetaParserSpec extends FlatSpec with Matchers {

  "A simple Attr def" should "have be parsed" in {
    MetaParser.Attr.parse("process: String") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr with default def" should "have be parsed" in {
    MetaParser.Attr.parse("process") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr def with extra blanks" should "have be parsed" in {
    MetaParser.Attr.parse("process   : String   ") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr def with multiple lines" should "have be parsed" in {
    MetaParser.Attr.parse("process \n \n :\n  String  \n  \n") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr def with restrictions" should "have be parsed" in {
    MetaParser.Attr.parse("process: String | [1, 1000) ") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr def with widget" should "have be parsed" in {
    MetaParser.Attr.parse("process: String :: Text | [1, 1000)") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A Attr def with value" should "have be parsed" in {
    MetaParser.Attr.parse("process: String :: Select $ abc,bcd,cde,def | [1, 1000)") shouldBe a [Parsed.Success[_, _, _]]
  }

  "A formatted Attr def" should "have be parsed" in {
    MetaParser.Attr.parse(
      """process
        |: String
        |:: Select
        |$ abc,bcd,cde,def
        || [1, 1000)
      """.stripMargin) shouldBe a [Parsed.Success[_, _, _]]
  }

}
