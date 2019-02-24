package shared.validator

import java.util.regex.Pattern

import meta.MetaAst.Restrict
import play.api.libs.json.{JsNumber, JsValue}

class JsRegexpValidator extends Validator[JsValue] {
  val name = "regexp"

  override def validate(restrict: Restrict, value: Option[JsValue]): ValidateResult = {
    val pattern = restrict.parameters.head.name
    val regex = pattern.r
    val result = value map { v =>
      v.toString match {
        case regex() => ValidateResult.success
        case _ => ValidateResult.fail( v.toString + " match failed with Regexp(" + pattern + ").")
      }
    }
    result.getOrElse(ValidateResult.fail("Value not found to match with Regexp(" + pattern + ")."))
  }
}


class JsNumberRangeValidator extends Validator[JsValue] {
  val name = "numRange"

  case class Range[T](minOpen: Boolean, min: T, max: T, maxOpen: Boolean)(implicit n: Numeric[T]) {
    def valid(num: T) = {
      val minValid: Boolean = if (minOpen) n.gt(num, min) else n.gteq(num, min)
      val maxValid: Boolean = if (maxOpen) n.lt(num, max) else n.lteq(num, max)
      minValid && maxValid
    }
  }

  object Range {
    def apply[T](pattern: String)(implicit parse:String => T, n: Numeric[T]): Option[Range[T]] = {
      val regexp = """([\(\[])(\d*),(\d*)([\]\)])""".r
      pattern match {
        case regexp(begin, minStr, maxStr, end) => {
          val minOpen = begin == "("
          val maxOpen = end == ")"
          val min = parse(minStr)
          val max = parse(maxStr)
          Some(Range(minOpen, min, max, maxOpen))
        }
        case _ => None
      }
    }
  }

  override def validate(restrict: Restrict, value: Option[JsValue]): ValidateResult = {
    implicit val str2num: String => BigDecimal = BigDecimal.apply(_)
    val result = for {
      range <- Range[BigDecimal](restrict.parameters.head.name)
      v <- value
    } yield {
      v match {
        case n: JsNumber =>
          if (range.valid(n.value))
            ValidateResult.success
          else
            ValidateResult.fail( v + " match failed with numRange(" + range + ").")
        case _ => ValidateResult.fail( v + " is not a valid number to match numRange(" + range + ").")
      }
    }

    result.getOrElse(ValidateResult.fail( value + " match failed with numRange(" + restrict + ")."))
  }
}


object JsValueValidatorRegistry extends ValidatorRegistry[JsValue] {
  val regexp = new JsRegexpValidator
  val numRange = new JsNumberRangeValidator
  override var registry = Map(
    regexp.name -> regexp,
    numRange.name -> numRange,
  )
}