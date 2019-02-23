package meta

import meta.MetaAst._

trait MetaTransformerTrait[F, T] {

  def asMap(item: Option[F]): Map[String, F]
  def asSeq(item: Option[F]): Seq[F]
  def create(name: String, children: Stream[T], meta: AttrDef, value: Option[F], parentRef: Option[Reference])
            (implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]): Option[T]

  trait Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T]
  }

  implicit class RefExtractor(ref: Reference)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T] = {
      if(meta.isLeaf) {
        create(name, Stream.empty, meta, value, parentRef)
      }
      else
        ref match {
          case t: TypeRef => t.transform(name, value, meta, parentRef)
          case l: ListRef => l.transform(name, value, meta, parentRef)
          case m: MapRef => m.transform(name, value, meta, parentRef)
        }
    }
  }

  implicit class TypeRefExtractor(ref: TypeRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T] = types.get(ref.name).flatMap { t: AstNodeWithMembers =>
      t.transform(name, value, expandWidget(ref.name, meta), parentRef)
    }
  }

  implicit class ListRefExtractor(ref: ListRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T] = {
      val expandedAttr = expandMacro(ref.definition)
      val children = asSeq(value)

      val members = expandedAttr.t map { ref =>
        children.flatMap(child => ref.transform(name + "[?]", Some(child), expandedAttr, meta.t)).toStream
      } getOrElse(Stream.empty)

      create(name, members, expandWidget(MetaAst.ROOT, meta), value, parentRef)
    }
  }

  implicit class MapRefExtractor(ref: MapRef)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T] = {
      val expandedAttr = expandMacro(ref.definition)
      val children = asMap(value)
      val members = expandedAttr.t map { ref =>
        children flatMap { case (key, child) =>
          ref.transform(key, Some(child), expandedAttr, meta.t)
        } toStream
      } getOrElse(Stream.empty)

      create(name, members, expandWidget(MetaAst.ROOT, meta), value, parentRef)
    }
  }

  implicit class AstNodeWithMembersExtractor(t: AstNodeWithMembers)(implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]) extends Transformer {
    def transform(name: String, value: Option[F], meta: AttrDef, parentRef: Option[Reference]): Option[T] = {
      val attrs =  t.attrs
      if (attrs.isEmpty)
        create(name, Stream.empty, meta, value, parentRef)
      else {
        val children = asMap(value)
        val members = attrs flatMap { attr =>
          val expandedAttrDef = expandMacro(attr.definition)
          expandedAttrDef.t flatMap { _.transform(attr.name, children.get(attr.name), expandedAttrDef, meta.t) }
        } toStream

        create(name, members, meta, value, parentRef)
      }
    }
  }
}
