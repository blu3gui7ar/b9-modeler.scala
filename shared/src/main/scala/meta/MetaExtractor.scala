package meta

import meta.MetaAst._

trait MetaExtractorTpl[F, T] {
  trait MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef): Option[T]
  }

  val emptyAttrDef = AttrDef(None, None, None, Seq.empty)
  val emptyTree = create("empty", Stream.empty, emptyAttrDef, None)

  def asMap(item: Option[F]): Map[String, F]
  def asSeq(item: Option[F]): Seq[F]
  def create(name: String, children: Stream[T], meta: AttrDef, value: Option[F]): Option[T]

  implicit class RefExtractor(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef) : Option[T] = {
      if(meta.isLeaf) {
        create(name, Stream.empty, meta, value)
      }
      else
        ref match {
          case t: TypeRef => t.extract(name, value, meta)
          case l: ListRef => l.extract(name, value, meta)
          case m: MapRef => m.extract(name, value, meta)
        }
    }
  }

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef) : Option[T] = types.get(ref.name).flatMap { t: AstNodeWithMembers =>
      t.extract(name, value, expandWidget(ref.name, meta))
    }
  }

  implicit class ListRefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef): Option[T] = {
      val expandedAttr = expand(ref.attr, macros)
      val children = asSeq(value)

      val members = expandedAttr.t map { ref =>
        children.flatMap(child => ref.extract(name + "[?]", Some(child), expandedAttr)).toStream
      } getOrElse(Stream.empty)

      create(name, members, expandWidget(MetaAst.ROOT, meta), value)
    }
  }

  implicit class MapRefExtractor(ref: MapRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef) : Option[T] = {
      val expandedAttr = expand(ref.attr, macros)
      val children = asMap(value)
      val members = expandedAttr.t map { ref =>
        children flatMap { case (key, child) =>
          ref.extract(key, Some(child), expandedAttr)
        } toStream
      } getOrElse(Stream.empty)

      create(name, members, expandWidget(MetaAst.ROOT, meta), value)
    }
  }

  implicit class AttrExtractor(attr: Attr)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef) : Option[T] = {
      val expandedAttrDef = expand(meta, macros)
      expandedAttrDef.t flatMap { _.extract(name, value, expandedAttrDef) }
    }
  }


  implicit class AstNodeWithMembersExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends MetaExtractor {
    def extract(name: String, value: Option[F], meta: AttrDef): Option[T] = {
      val attrs =  t.members.filter(_.isInstanceOf[Attr]).map(_.asInstanceOf[Attr])
      if (attrs.isEmpty)
        create(t.name, Stream.empty, meta, value)
      else {
        val children = asMap(value)
        val members = attrs flatMap { attr =>
          attr.extract(attr.name, children.get(attr.name), attr.definition)
        } toStream

        create(t.name, members, meta, value)
      }
    }
  }
}
