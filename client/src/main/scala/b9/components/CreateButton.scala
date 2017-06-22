package b9.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import japgolly.scalajs.react.vdom.HtmlAttrs.{className, onClick}

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

  case class State()

  class Backend($ : BackendScope[Props, State]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def mark(name: String) =  name.headOption.getOrElse('?').toUpper.toString

    def classes(valid: Boolean) = s"graph-btn graph-btn-add ${if(valid) "graph-btn-disabled"}"

    def render(p: Props, s: State) = {
      <.g(
        ^.transform := transform(p.x, p.y),
        className := classes(p.valid),
        onClick --> p.click,
        <.circle(
          ^.r := 12,
          ^.cx := 7,
          ^.cy := 12
        ),
        <.text(
          ^.x := 3,
          ^.y := 17,
          mark(p.name)
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("CreateButton")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(name: String, x: Int, y: Int, valid: Boolean, click: Callback) = component(Props(name,x,y,valid,click))
}
