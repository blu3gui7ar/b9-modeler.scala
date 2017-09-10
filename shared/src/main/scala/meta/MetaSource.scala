package meta

import fastparse.core.Parsed.Success
import meta.MetaAst.Root

class MetaSource(val metadata: String) {

  val meta: Root = {
    val rs = MetaParser.Meta.parse(metadata)
    rs match {
      case Success(meta, _) => meta
      case _ => Root(Seq.empty)
    }
  }

  implicit val macros = MetaAst.macros(meta)
  implicit val types = MetaAst.types(meta)
}


