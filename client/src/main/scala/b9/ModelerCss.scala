package b9

import b9.CssSettings._

/**
  * Created by blu3gui7ar on 2016/12/30.
  */
object ModelerCss extends StyleSheet.Inline {
  import dsl._

  val checkbox = style(
    addClassName("form-switch")
  )

  val checkboxIcon = style(
    addClassName("form-icon")
  )

  val breadcrum = style (
    font := "bold 16px cursive"
  )

  val link = style (
    transition := "all 0.5s ease",

    svgFill := none,
    svgStroke.grey(204),
    svgStrokeWidth := "1.5px"
  )

  val joint = style (
    transition := "all 0.5s ease",

    position.relative,
    display.inlineBlock,
    font := "10px sans-serif",

    unsafeChild("> circle")(
      svgFill := white,
      svgStroke.steelblue,
      svgStrokeWidth := "1.5px"
    ),

    unsafeChild("> text")(
      svgFillOpacity(1)
    )
  )

  val button = style (
    transition := "all 0.5s ease",
    opacity(0),
    //    transitionProperty := "all",
    //    transitionDuration(FiniteDuration(500, TimeUnit.MILLISECONDS)),
    //    transitionTimingFunction.ease
    cursor.pointer
  )

  val buttonDisabled = style (
    unsafeChild("*") (
      opacity(0.3)
    )
  )

  val buttonAdd = style (
    svgFill := c"#57D37E",

    unsafeChild("> circle") (
      svgFill := white,
      svgStroke.steelblue,
      svgStrokeWidth := "1.5px"
    ),
    unsafeChild("> text") (
      font := "bold 16px cursive",
      svgTextAnchor := "middle"
    )
  )

  val buttonEdit = style (
    svgFill := c"#79AFE9"
  )

  val buttonRemove = style (
    svgFill := c"#E97979",

    unsafeChild("> circle") (
      svgFill := white
    )
  )

  val buttonParent = style (
    svgFill := c"#57D37E"
  )

  val jointActive = style (
    unsafeChild("*")(
      display.inlineBlock
    ),
    unsafeChild("> circle")(
      svgStroke.red
    ),
    &.hover(
      unsafeChild("> ." +  button.className.value) (
        opacity(1)
      )
    )
  )

  val jointEditing = style (
    unsafeChild("> circle")(
      svgFill := red
    )
  )

  val jointFolded = style (
    unsafeChild("> circle")(
      svgFill := lightsteelblue
    )
  )

  val hidden = style (
    display.none
  )

}
