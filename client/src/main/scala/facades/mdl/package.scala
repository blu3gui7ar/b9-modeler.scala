//package facades
//
//import japgolly.scalajs.react.JsComponent
//import japgolly.scalajs.react.vdom.TopNode
//import japgolly.scalajs.react.vdom.all._
//
//package object mdl {
//  implicit class MaterialAble(val elem: TagMod) extends AnyVal {
//    def material: JsComponent[(TagMod, Boolean), Unit, Unit, TopNode] = {
//      MaterialComponent((elem, false))
//    }
//
//    def material(children: Boolean): ReactComponentU[(ReactTag, Boolean), Unit, Unit, TopNode] = {
//      MaterialComponent((elem, children))
//    }
//  }
//}