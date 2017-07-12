package b9.components

import b9.ModelerCss
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.onClick
import japgolly.scalajs.react.vdom.svg_<^._

import scalacss.ScalaCssReact._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object CreateButton {

  case class Props(
                    name: String,
                    x: Int,
                    y: Int,
                    valid: Boolean,
                    click: Callback
                  )

  class Backend($ : BackendScope[Props, Unit]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def mark(name: String) =  name.headOption.getOrElse('?').toUpper.toString

    def render(p: Props) = {
      <.g(
        ^.transform := transform(p.x, p.y),
        ModelerCss.button,
        ModelerCss.buttonAdd,
        ModelerCss.buttonDisabled.when(!p.valid),
        onClick --> p.click,
        <.title(p.name),
        <.circle(
          ^.r := 12,
          ^.cx := 7,
          ^.cy := 12
        ),
        <.text(
          ^.x := 6,
          ^.y := 17,
          mark(p.name)
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("CreateButton")
    .renderBackend[Backend]
    .build

  def apply(name: String, x: Int, y: Int, valid: Boolean, click: Callback) = component(Props(name,x,y,valid,click))
}
