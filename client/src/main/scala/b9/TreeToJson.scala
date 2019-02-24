package b9

import b9.TreeOps._
import meta.MetaAst._
import meta.MetaTransformerTrait

class TreeToJson extends MetaTransformerTrait[TTN, String] {
  override def asMap(item: Option[TTN]): Map[String, TTN] = item map { tn =>
    tn.subForest map { child =>
      child.rootLabel.name -> child
    } toMap
  } getOrElse(Map.empty)

  override def asSeq(item: Option[TTN]): Seq[TTN] = item.map(_.subForest).getOrElse(Seq.empty)

  override def createT(name: String, children: Stream[String], meta: AttrDef, value: Option[TTN], parentRef: Option[Reference])
                      (implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]): Option[String] =
    (for {
      tn <- value
      ref <- meta.t
    } yield {
      val body =  ref match {
        case tr: TypeRef => types.get(tr.name) map { t =>
          if (t.isSimple)
            tn.rootLabel.value.toString
          else
            "{" + children.mkString(", ") + "}"
        }
        case _: ListRef => Some("[" + children.mkString(", ") + "]")
        case _: MapRef => Some("{" + children.mkString(", ") + "}")
      }

      parentRef match {
        case Some(_: ListRef) => body
        case _ => body.map(b => s""""${tn.rootLabel.name}": ${b}""")
      }
    }).flatten
}
