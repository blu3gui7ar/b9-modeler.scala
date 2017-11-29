package meta

/**
  * Created by blu3gui7ar on 2017/6/1.
  */

object Sample {
  val meta: String =
    """
      |Meta {
      |  %DEFAULT = String :: Text
      |  %DATE = String :: Text | /[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])/
      |  %CHECK = [String] :: Checkbox
      |  %RADIO = String :: Radio
      |  %BOOL = Boolean :: Radio $ true, false
      |  %SELECT = String :: Select
      |  %HREF = String :: Text | /(https?|mail|ftps?|sftp):\/\/.*/
      |  %INT = Int :: Text | /[0-9]*/
      |  String {}
      |  Boolean {}
      |  Int {}
      |
      |  processor
      |  tpl
      |  pattern
      |  size: Size
      |  time: Time
      |  urlRoot
      |  imgs: [[Img]] | [1,]
      |  effect: Effect
      |  marks: <Mark> | [1,]
      |
      |  Size { height: %INT; width: %INT }
      |  Time { start: %DATE; end: %DATE }
      |  Img {
      |    id: %INT | (0, 100000)
      |    enable: %LANGS
      |    time: %DATE
      |    title: Nl
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
      |
      |  Mark {
      |    location: :: Select $ header,banner,body
      |    url
      |  }
      |}
    """.stripMargin

  val data: String =
    """
      |{
      |  "processor": "This is processor",
      |  "tpl": "a tpl",
      |  "size": {
      |   "height": 150,
      |   "width": 30
      |  },
      |  "imgs": [
      |    [
      |      {
      |        "id": 123,
      |        "active": true,
      |        "enable": [ "en", "fr"]
      |      },
      |      {
      |        "id": 789,
      |        "active": false
      |      }
      |    ],
      |    [
      |      {
      |        "id": 456,
      |        "active": true
      |      }
      |    ]
      |  ],
      |  "marks": {
      |    "m1": {
      |      "location": "header",
      |      "url": "/hhh"
      |    },
      |    "m2": {
      |      "location": "body",
      |      "url": "/bbb"
      |    }
      |  }
      |}
    """.stripMargin

//  def tree(): (Root, TreeNode, Js.Value) = {
//    val ds = new MetaSource(meta)
//    import ds._
////    println(ds.meta)
//
//    val dataJs = upickle.json.read(data)
//    import TreeExtractor._
//    val tree = ds.meta.tree("meta", Some(dataJs), RootAttrDef)
////    println(tree)
//
//    (ds.meta, tree.getOrElse(TreeExtractor.emptyTree), dataJs)
//  }

//  def main(args: Array[String]): Unit = {
//    tree() match { case(_meta, _data, _) =>
//      println(_meta)
//      println(_data)
//    }
//  }
}
