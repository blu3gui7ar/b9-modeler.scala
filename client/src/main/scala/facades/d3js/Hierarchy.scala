package facades.d3js


import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}

/**
  * Created by blu3gui7ar on 2017/5/27.
  */

@JSImport("d3-hierarchy", JSImport.Namespace)
@js.native
object Hierarchy extends js.Object {
  def hierarchy[N](data: N, getChildren: js.UndefOr[js.Function1[N, js.Array[N]]] = js.undefined) : N = js.native

  def tree(): TreeGenerator = js.native
}

@js.native
trait TreeGenerator extends js.Object {
  def apply[Node](root: Node): Unit = js.native
  def size(size: js.Array[Double]): Unit = js.native
}

@ScalaJSDefined
trait Link[Node] extends js.Object {
  def source:Node
  def target:Node
}

@ScalaJSDefined
class SimpleLink[Node](sourceNode:Node,targetNode:Node) extends Link[Node] {
  def source = sourceNode
  def target = targetNode
}

@ScalaJSDefined
trait BaseNode[N <: BaseNode[N]] extends js.Object {
  var data: js.UndefOr[js.Any] = js.undefined
  var depth: js.UndefOr[Int] = js.undefined
  var height: js.UndefOr[Int] = js.undefined
  var parent: js.UndefOr[N] = js.undefined
  var value: js.UndefOr[Double] = js.undefined

  def children: js.Array[N]
  def children_(c: js.Array[N]): Unit

  def ancestors: js.UndefOr[js.Array[N]] = js.undefined
  def descendants: js.UndefOr[js.Array[N]] = js.undefined
  def leaves: js.UndefOr[js.Array[N]] = js.undefined
  def links: js.UndefOr[js.Array[Link[N]]] = js.undefined
}

package treeModule {
  @ScalaJSDefined
  trait Node[N <: Node[N]] extends BaseNode[N] {
//    protected var _x: js.UndefOr[Double] = js.undefined
    def x: Double
    def x_(x: Double): Unit

//    protected var _y: js.UndefOr[Double] = js.undefined
    def y: Double
    def y_(y: Double): Unit
  }
}

