package shared.validator

import meta.MetaAst.Restrict


case class ValidateResult(valid: Boolean, msgs: Seq[String])

object ValidateResult {
  val success = ValidateResult(true, Seq.empty)

  def fail(msg: String) = ValidateResult(false, Seq(msg))

  def merge(r1: ValidateResult, r2: ValidateResult): ValidateResult = ValidateResult(r1.valid && r2.valid, r1.msgs ++ r2.msgs)
}


trait Validator[T] {
  def validate(restrict: Restrict, value: Option[T]): ValidateResult
}

trait ValidatorRegistry[T] {
  protected var registry: Map[String, Validator[T]]

  def register(name: String, validator: Validator[T]): Unit = {
    registry += name -> validator
  }

  def unregister(name: String): Option[Validator[T]] = {
    val v = registry.get(name)
    registry -= name
    v
  }

  def apply(name: String): Option[Validator[T]] = registry.get(name)
}
