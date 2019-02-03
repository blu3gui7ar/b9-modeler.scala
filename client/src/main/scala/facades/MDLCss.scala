package facades

import b9.CssSettings._

/**
  * Created by blu3gui7ar on 2016/12/30.
  */
object MDLCss extends StyleSheet.Inline {
  import dsl._

  val layout = style(
    addClassNames("mdl-layout", "mdl-js-layout")
  )

  val layoutHeader = style(
    addClassName("mdl-layout__header")
  )

  val layoutHeaderRow = style(
    addClassName("mdl-layout__header-row")
  )

  val layoutTitle = style(
    addClassName("mdl-layout-title")
  )

  val layoutContent = style(
    addClassName("mdl-layout__content")
  )

  val pageContent = style(
    addClassName("page-content")
  )
}
