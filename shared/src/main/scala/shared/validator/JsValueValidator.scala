package shared.validator

import meta.MetaAst._
import meta.MetaTransformerTrait
import play.api.libs.json.{JsArray, JsObject, JsValue}

class JsValueValidator extends MetaTransformerTrait[JsValue, ValidateResult] {
  val registry = JsValueValidatorRegistry

  override def asMap(item: Option[JsValue]): Map[String, JsValue] = item collect {
    case l: JsObject => l.value match {
      case m: Map[String, JsValue] => m
      case x => x.toMap
    }
  } getOrElse(Map.empty)

  override def asSeq(item: Option[JsValue]): Seq[JsValue] = item collect {
    case l: JsArray => l.value
  } getOrElse(Seq.empty)

  override def createT(name: String, children: Stream[ValidateResult], meta: AttrDef, value: Option[JsValue], parentRef: Option[Reference])
                      (implicit macros: Map[String, Macro], types: Map[String, AstNodeWithMembers]): Option[ValidateResult] = {
    val merged = meta.restricts.foldLeft(ValidateResult.success){ (result, restrict) =>
      val current = registry(restrict.name).map(_.validate(restrict, value))
        .getOrElse(ValidateResult(false, Seq(value.toString + " failed on unknown " + restrict.toString)))
      ValidateResult.merge(result, current)
    }

    Some(
      children.foldLeft(merged)(ValidateResult.merge)
    )
  }
}
