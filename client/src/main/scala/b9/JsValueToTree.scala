package b9

import java.util.UUID

import b9.TreeOps._
import meta.MetaAst._
import meta.MetaTransformerTrait
import play.api.libs.json.{JsArray, JsObject, JsValue}
import scalaz.Tree.Node

class JsValueToTree extends MetaTransformerTrait[JsValue,TTN] {
  override def asMap(item: Option[JsValue]): Map[String, JsValue] = item collect {
    case l: JsObject => l.value match {
      case m: Map[String, JsValue] => m
      case x => x.toMap
    }
  } getOrElse(Map.empty)

  override def asSeq(item: Option[JsValue]): Seq[JsValue] = item collect {
    case l: JsArray => l.value
  } getOrElse(Seq.empty)

  override def createT(name: String, children: Stream[TTN], meta: AttrDef, value: Option[JsValue], parentRef: Option[Reference])
                      (implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]): Option[TTN] =
    value map { v =>
      Node(
        TreeNode(UUID.randomUUID(), name,  meta, v),
        children
      )
    }
}
