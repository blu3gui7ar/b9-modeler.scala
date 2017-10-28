package b9.components.graph

import b9._
import scalacss.ScalaCssReact._
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

    def breadcrums(displayRoot: ModelProxy[TN], top: Int): TagMod = {
      val levels = displayRoot().ancestors().reverseInPlace().toTagMod { n =>
        BreadCrum(
          n.data.map(_.name).getOrElse("unknown") + "> ",
          displayRoot.dispatchCB(GoUpAction(n))
        )
      }

      <.text(
        ^.x := 0,
        ^.y := top,
        ModelerCss.breadcrum,
        levels
      )
    }

    def joints(rtn: ModelProxy[TN]): Seq[TagMod] = {
      type ZoomFunc = TN => TN
      def jointsAcc(tn: ModelProxy[TN], f: ZoomFunc, coll: mutable.MutableList[TagMod]): Unit = {
        if (!tn().data.toOption.flatMap(_.meta.widget).isDefined) {
          tn().children map { children =>
            children.toArray.zipWithIndex.foreach {
              case (child, idx) => {
                val g: ZoomFunc = _.children.get.apply(idx)
                val cf = g compose f
                jointsAcc(tn.zoom(g), cf, coll)
              }
            }
          }
        }
        //        val modelerConnection = ModelerCircuit.connect(s => f(s.graph.tree))
        //        coll += modelerConnection(Joint(_))
          coll += Joint(tn)
      }
      val coll = mutable.MutableList[TagMod]()
      jointsAcc(rtn, identity[TN], coll)
      coll
    }

    def render(p: Props) = {
      val root = p.model.zoom(_.tree)
      val displayRoot = p.model.zoom(_.displayRoot)
      <.svg(
        ^.width := p.width.toString, //[BUG] https://github.com/japgolly/scalajs-react/issues/388
        ^.height:= p.height.toString,
        <.g(
          ^.transform := transform(p.left, p.top),
          breadcrums(displayRoot, p.top),
          TagMod(joints(root): _*)
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
