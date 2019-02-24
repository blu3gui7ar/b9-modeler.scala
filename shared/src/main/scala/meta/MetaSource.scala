package meta

import fastparse.Parsed.{Failure, Success}
import fastparse._
import meta.MetaAst.Root

class MetaSource(val metadata: String) {

  val meta: Root = {
    val rs = parse(metadata, MetaParser.Meta(_))
    rs match {
      case Success(meta, _) => meta
      case Failure(label, index, extra) => {
        print(extra.trace(true))
        Root(Seq.empty, None)
      }
    }
  }

  implicit val macros = MetaAst.macros(meta)
  implicit val types = MetaAst.types(meta)
}


