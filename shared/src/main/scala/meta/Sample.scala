package meta

import fastparse.core.Parsed.Success
import meta.MetaAst.Root

/**
  * Created by blu3gui7ar on 2017/6/1.
  */

object Sample {
  val meta =
    """
      |Meta {
      |  %DEFAULT = String :: Text
      |  %DATE = String :: Text | /[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])/
      |  %CHECK = [String] :: Checkbox
      |  %RADIO = String :: Radio
      |  %BOOL = Boolean :: Radio $ true, false
      |  %SELECT = String :: Select
      |  %HREF = String :: Text | /(https?|mail|ftps?|sftp):\/\/.*/
      |  String {}
      |  Boolean {}
      |  Int {}
      |
      |  processor: String
      |  tpl
      |  pattern
      |  size: Size
      |  time: Time
      |  urlRoot
      |  imgs: [[Img]] | [1,]
      |  effect: Effect
      |
      |  Size { height: Int; width: Int }
      |  Time { start: %DATE; end: %DATE }
      |  Img {
      |    id: Int | (0, 100000)
      |    enable: %LANGS
      |    time: %DATE
      |    tite: Nl
      |    url
      |    active: %BOOL
      |    type: %SELECT $ link, shuffle
      |    areas: [Area]
      |  }
      |  %LANGS = %CHECK $ en,de,fr,es,se,no,it,pt,da,fi,ru,nl | [1,4]
      |  Nl { en; de; fr; es; se; no; it; pt; da; fi; ru; nl }
      |  Effect { type; event; auto: %BOOL; time: %DATE }
      |  Area {
      |    title: Nl
      |    shape
      |    hotspots: Nl
      |    href: HREF
      |  }
      |}
    """.stripMargin

  val data =
    """
      |{
      |  "processor": "This is processor",
      |  "tpl": "a tpl",
      |  "size": {
      |   "height": 150,
      |   "width": 30
      |  },
      |  "imgs": [
      |  [
      |    {
      |      "id": 123
      |    },
      |    {
      |      "id": 789
      |    }
      |  ],
      |  [
      |    {
      |      "id": 456
      |    }
      |  ]
      |  ]
      |
      |}
    """.stripMargin

  def tree(): (Root, TreeNode) = {
    val dataJs = upickle.json.read(Sample.data)
    val rs = MetaParser.Meta.parse(Sample.meta)
    import TreeExtractor._

    (rs match {
      case Success(meta, _) => {
        implicit val macros = MetaAst.macros(meta)
        implicit val types = MetaAst.types(meta)
        meta.tree("meta", Some(dataJs), RootAttrDef) map {
          (meta, _)
        }
      }
      case _ => None
    }).getOrElse((Root(Seq.empty), emptyTree))
  }

  def main(args: Array[String]): Unit = {
    tree() match { case(meta, data) =>
      println(meta)
      println(data)
    }
  }
}
