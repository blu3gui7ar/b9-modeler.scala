package b9.components

import b9.ModelerCss
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.{className, onClick}
import japgolly.scalajs.react.vdom.svg_<^._

import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object Button {
  val EDIT = "edit"
  val REMOVE = "remove"
  val PARENT = "parent"

  private val ICONS = Map(
    EDIT -> "M20.307 1.998c-0.839-0.462-3.15-1.601-4.658-1.913-1.566-0.325-3.897 5.79-4.638 5.817-1.202 0.043-0.146-4.175 0.996-5.902-1.782 1.19-4.948 2.788-5.689 4.625-1.432 3.551 2.654 9.942 0.474 10.309-0.68 0.114-2.562-4.407-3.051-5.787-1.381 2.64-0.341 5.111 0.801 8.198v0.192c-0.044 0.167-0.082 0.327-0.121 0.489h0.121v4.48c0 0.825 0.668 1.493 1.493 1.493 0.825 0 1.493-0.668 1.493-1.493v-4.527c2.787-0.314 4.098 0.6 6.007-3.020-1.165 0.482-3.491-0.987-3.009-1.68 0.97-1.396 4.935 0.079 7.462-4.211-4 1.066-4.473-0.462-4.511-1.019-0.080-1.154 3.999-0.542 5.858-2.146 1.078-0.93 2.37-3.133 0.97-3.905z",
    REMOVE -> "M3.514 20.485c-4.686-4.686-4.686-12.284 0-16.97 4.688-4.686 12.284-4.686 16.972 0 4.686 4.686 4.686 12.284 0 16.97-4.688 4.687-12.284 4.687-16.972 0zM18.365 5.636c-3.516-3.515-9.214-3.515-12.728 0-3.516 3.515-3.516 9.213 0 12.728 3.514 3.515 9.213 3.515 12.728 0 3.514-3.515 3.514-9.213 0-12.728zM8.818 17.303l-2.121-2.122 3.182-3.182-3.182-3.182 2.121-2.122 3.182 3.182 3.182-3.182 2.121 2.122-3.182 3.182 3.182 3.182-2.121 2.122-3.182-3.182-3.182 3.182z",
    PARENT -> "m 0,12.362183 10,12.883883 0,-4.883883 10,0 0,-16.0000004 -10,0 0,-4.2928932 z"
  )

  case class Props(
                    name: String,
                    x: Int,
                    y: Int,
                    click: Callback
                  )

  class Backend($ : BackendScope[Props, Unit]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def render(p: Props) = {
      <.g(
        ^.transform := transform(p.x, p.y),
        ModelerCss.button,
        ModelerCss.buttonParent.when(p.name == PARENT),
        ModelerCss.buttonEdit.when(p.name == EDIT),
        ModelerCss.buttonRemove.when(p.name == REMOVE),
        onClick --> p.click,
        <.path(^.d := ICONS.getOrElse(p.name, "")),
        <.circle(
          ^.opacity := "1e-6",
          ^.r := 12,
          ^.cx := 11,
          ^.cy := 12
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("EditButton")
    .renderBackend[Backend]
    .build

  def apply(n: Props) = component(n)
}

object ParentButton {
  def apply(x: Int, y: Int, click: Callback) = Button(Button.Props(Button.PARENT, x, y, click))
}

object EditButton {
  def apply(x: Int, y: Int, click: Callback) = Button(Button.Props(Button.EDIT, x, y, click))
}

object RemoveButton {
  def apply(x: Int, y: Int, click: Callback) = Button(Button.Props(Button.REMOVE, x, y, click))
}
