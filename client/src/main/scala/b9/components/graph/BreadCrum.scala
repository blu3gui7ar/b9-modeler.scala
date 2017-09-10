package b9.components.graph

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlAttrs.onClick
import japgolly.scalajs.react.vdom.svg_<^._


/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object BreadCrum {

  case class Props(
                    name: String,
                    onClick: Callback
                  )

  private val component = ScalaComponent.builder[Props]("BreadCrum")
    .render_P( p =>
      <.tspan(
        p.name,
        onClick --> p.onClick
      )
    )
    .build

  def apply(name: String, onClick: Callback) = component(Props(name, onClick))
}
