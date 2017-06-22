package b9.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import japgolly.scalajs.react.vdom.HtmlAttrs.{className, onClick, onMouseOver}

/**
  * Created by blu3gui7ar on 2017/5/25.
  */
object Joint {

  case class Props(
                    x: Double,
                    y: Double,
                    name: String,
                    buttons: Map[String, Boolean]
                  )

  case class State()

  class Backend($ : BackendScope[Props, State]) {

    def transform(x: Double, y: Double) = s"translate($x, $y)"

    def transition() = s"translate()"

    def mouseOver()  =  Callback {}

    def circleClick()  =  Callback {}

    def textClick()  =  Callback {}

    def canGoParent() = true

    def goParent()  =  Callback {}

    def canRemove()  =  true

    def remove()  =  Callback {}

    def edit()  =  Callback {}

    def create()  =  Callback {}

    def creates(buttons: Map[String, Boolean]) = buttons.zipWithIndex.toTagMod {
      case ((name, valid), idx) => CreateButton(name, 18 + 30 * idx, 10, valid, create)
    }

    def render(p: Props, s: State) = {
      <.g(
        ^.transform := transform(p.y, p.x),
        className := "node-class",
//        ^.transition
        onMouseOver --> mouseOver(),
        <.circle(
          ^.r := 6,
          className :=  "circle-class",
          onClick --> circleClick()
        ),
        <.text(
          ^.x := 15,
          ^.y := 3,
          ^.textAnchor := "start",
          onClick --> textClick(),
          p.name
        ),
        ParentButton(-41, -25, true, goParent).when(canGoParent()),
        RemoveButton(-42, 10, canRemove, remove),
        EditButton(-12, 10, true, edit),
        creates(p.buttons)
      )
    }

  }

  private val component = ScalaComponent.builder[Props]("Joint")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(n: Props) = component(n)
}
