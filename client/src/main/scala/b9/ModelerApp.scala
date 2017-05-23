package b9

import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.Apps

object ModelerApp extends js.JSApp {

  sealed abstract class Action
  case object Add extends Action
  case object Sub extends Action

  def require(): Unit = {
    WebpackRequire.React
    WebpackRequire.ReactDOM
    ()
  }

  def main(): Unit = {
    require()
    init(dom.document.getElementById(Apps.ModelerApp))
  }

  def init(ele: Element): Unit = {
    val test = ScalaComponent.builder[String]("TestComponent")
      .render_P(name => <.div(s"Hello there, $name"))
      .build

    test("MK").renderIntoDOM(ele)


    import dom.ext._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
//    import scalatags.rx.all._
//    import rx.Ctx.Owner.Unsafe._
    // document.head.appendChild(MainCSS.render[scalatags.JsDom.TypedTag[HTMLStyleElement]].render)

//    val meta = Var("")
//    val rxPre = Rx( meta )
//    ele.appendChild(
//      div(
//        h1("Modeler"),
//        button(id := "modeler",
//          onclick := { () => Ajax.get("api/models").foreach(xhr => meta() = xhr.responseText) }
//        )("Click Me!"),
//        pre(rxPre.now)
//      ).render
//    )
  }
}
