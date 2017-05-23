package b9

import scalacss.Defaults._

/**
  * Created by blu3gui7ar on 2016/12/30.
  */
object MainCSS extends StyleSheet.Inline {
  import dsl._

  val checkbox = style(
    addClassName("form-switch")
  )

  val checkboxIcon = style(
    addClassName("form-icon")
  )

}
