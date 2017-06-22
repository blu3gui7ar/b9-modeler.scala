package b9.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object BreadCrum {

  case class Props(
                    x: Int,
                    y: Int,
                    name: String
                  )

  private val component = ScalaComponent.builder[Props]("BreadCrum")
    .render_P( p =>
      <.text(
        ^.x := p.x,
        ^.y := p.y,
        p.name
      )
    )
    .build

  def apply(x: Int, y: Int, name: String) = component(Props(x, y, name))
}
