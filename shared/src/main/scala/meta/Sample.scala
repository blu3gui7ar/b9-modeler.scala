package meta

/**
  * Created by blu3gui7ar on 2017/6/1.
  */

object Sample {
  val meta: String =
    """
      |Meta {
      |  %DEFAULT = String @Widget(Text)
      |  %DATE = String @Widget(Text)
      |     @Restrict(regexp: "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])")
      |  %BOOL = Boolean @Widget(MuiRadio : "true", "false")
      |  %HREF = String @Widget(Text)
      |     @Restrict(regexp: "(https?|mail|ftps?|sftp):\/\/.*")
      |  %INT = Int @Widget(Text) @Restrict(regexp: "[0-9]*")
      |  String {}
      |  Boolean {}
      |  Int {}
      |
      |  processor
      |  tpl
      |  pattern
      |  size: Size @Container(Simple)
      |  time: Time
      |  urlRoot
      |  imgs: [[Img @Container(ExpansionPanel)] @Container(Simple)] @Container(ExpansionPanel) @Restrict(sizeRange: "[1,]")
      |  effect: Effect
      |  marks: <Mark> @Restrict(sizeRange: "[1,]")
      |
      |  Size { height: %INT; width: %INT } @Container(ExpansionPanel)
      |  Time { start: %DATE; end: %DATE }
      |  Img {
      |    id: %INT @Restrict(numRange: "(0, 100000)")
      |    enable: %LANGS
      |    time: %DATE
      |    title: Nl
      |    url
      |    active: %BOOL
      |    type: String @Widget(Select: "link", "shuffle")
      |    areas: [Area]
      |  } @Container(Simple)
      |  %LANGS = [String] @Widget(Checkbox: "en","de","fr","es","se","no","it","pt","da","fi","ru","nl") @Restrict(sizeRange: "[1,4]")
      |  Nl { en; de; fr; es; se; no; it; pt; da; fi; ru; nl } @Container(ExpansionPanel)
      |  Effect { type; event; auto: %BOOL; time: %DATE }
      |  Area {
      |    title: Nl
      |    shape
      |    hotspots: Nl
      |    href: HREF
      |  }
      |
      |  Mark {
      |    location: String @Widget(Select: "header","banner","body")
      |    url
      |  }
      |} @Container(Simple)
    """.stripMargin

//  val data: String =
//    """
//      |{
//      |   "processor": "aaa"
//      |}
//    """.stripMargin
  val data: String =
    """
      |{
      |  "processor": "This is processor",
      |  "tpl": "a tpl",
      |  "size": {
      |   "height": 150,
      |   "width": 30
      |  },
      |  "size2": {
      |   "height": 160,
      |   "width": 40
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
//    val ds = new MetaSource(Sample.meta)
//    print(ds.meta)
//    tree() match { case(_meta, _data, _) =>
//      println(_meta)
//      println(_data)
//    }
//  }
}
