package b9.components


import b9._
import b9.short.TN
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._

import scala.collection.mutable

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
object TreeGraph {
  case class Props(
                    model: ModelProxy[GraphState],

                    width: Double,
                    height: Double,

                    left: Int = 50,
                    right: Int = 200,
                    top: Int = 10,
                    bottom: Int = 10
                  )

  class Backend($ : BackendScope[Props, Unit]) {

    def transform(x: Int, y: Int) = s"translate($x, $y)"

    def breadcrums(top: Int): TagMod = Seq("one", "two", "three")
      .zipWithIndex.toTagMod { case (n, i) => BreadCrum(0, top + 15 * i, n) }

    def joints(root: ModelProxy[TN]): Seq[ModelProxy[TN]] = {
      def jointsAcc(_tn: ModelProxy[TN], coll: mutable.MutableList[ModelProxy[TN]]): Unit = {
        coll += (_tn)
        _tn().children map {
          _.toArray.zipWithIndex.foreach { case (_, idx) => jointsAcc(_tn.zoom(_.children.get.apply(idx)), coll) }
        }
      }

      type ZoomFunc = TN => TN
      def zoomFuncAcc(rtn: TN, f: ZoomFunc, coll: mutable.MutableList[ZoomFunc]): Unit ={
        coll += f
        rtn.children map {
          _.toArray.zipWithIndex.foreach {
            case (child, idx) => {
              val g: ZoomFunc = _.children.get.apply(idx)
              val cf = f compose  g
              zoomFuncAcc(child, cf, coll)
            }
          }
        }
      }
      val coll = new mutable.MutableList[ModelProxy[TN]]
      jointsAcc(root, coll)
      coll
    }

    def isMoving(tn: TN): Boolean = tn.display != tn.nextDisplay

    def links(tns: Seq[ModelProxy[TN]]) = {
      def tolink(tn: ModelProxy[TN]): Option[Path] = {
        val rtn = tn()
        if (rtn.parent != null)
          (rtn.parent map { parent =>
            Path(
              id = parent.id.getOrElse(0).toString + "-" + rtn.id.getOrElse(0).toString,
              display = parent.display.getOrElse(true) && rtn.display.getOrElse(true),
              moving = isMoving(rtn),
              sx = parent.x.getOrElse(0),
              sy = parent.y.getOrElse(0),
              tx = rtn.x.getOrElse(0),
              ty = rtn.y.getOrElse(0)
            )
          }).toOption
        else
          None
      }
      tns.flatMap(tolink(_))
    }

    def render(p: Props) = {
      val root = p.model.zoom(_.tree)
      val tns = joints(root)
      <.svg(
        ^.width := p.width.toString, //[BUG] https://github.com/japgolly/scalajs-react/issues/388
        ^.height:= p.height.toString,
        <.g(
          ^.transform := transform(p.left, p.top),
          breadcrums(p.top),
          links(tns).toTagMod(Link(_)),
          tns.toTagMod { tn  =>
            val node = tn()
            val onUp = node.parent.toOption match {
              case Some(null) => None
              case Some(parent) => Some(tn.dispatchCB(GoUpAction(parent)))
              case _ => None
            }
            val onRemove = node.parent.toOption match {
              case Some(null) => None
              case Some(parent) => Some(tn.dispatchCB(RemoveFromAction(node, parent)))
              case _ => None
            }
            Joint(tn, onUp, onRemove)
          }
        )
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("TreeGraph")
    .renderBackend[Backend]
    .componentDidMount { scope =>
      Callback {
          val p = scope.props.model.zoom(_.tree)
          p.dispatchCB(GoUpAction(p())).async.runNow()
      }
    }
    .build

  def apply(model: ModelProxy[GraphState], width: Double, height: Double) = component(Props(model, width, height))
}
