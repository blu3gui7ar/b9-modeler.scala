package b9.components.graph

import b9.ModelerCss
import b9.short.keyAttr
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._

import scalacss.ScalaCssReact._
/**
  * Created by blu3gui7ar on 2017/5/24.
  */

case class Path(id: String, display: Boolean, moving: Boolean, sx: Double, sy: Double, tx: Double, ty: Double)

object Path {
  val Empty = Path("", false, false, 0, 0, 0, 0)
}

object Link {
  case class Props(path: Path)

  class Backend($ : BackendScope[Props, Unit]) {
//    val linkGen = Shape.linkHorizontal[LN, TN]().x(_.y).y(_.x)
//    def diagonal(link: LN): String = linkGen(link).toString

    def diagonal(p: Path): String =
      s"M ${p.sy} ${p.sx} C ${(p.sy + p.ty) / 2} ${p.sx}, ${(p.sy + p.ty) / 2} ${p.tx}, ${p.ty} ${p.tx}"

    def render(p: Props) = {
      <.path(
        keyAttr := p.path.id,
        ModelerCss.link,
        ModelerCss.hidden.unless(p.path.display),
        ModelerCss.moving.when(p.path.moving),
        ^.d := diagonal(p.path)
      )
    }
  }
  private val component = ScalaComponent.builder[Props]("Link")
    .renderBackend[Backend]
    .build

  def apply(path: Path) = component(Props(path))
}
