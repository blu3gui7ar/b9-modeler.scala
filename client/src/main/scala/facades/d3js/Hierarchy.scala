package facades.d3js


import facades.d3js.treeModule.{Node, TreeGenerator}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Created by blu3gui7ar on 2017/5/27.
  */

@JSImport("d3-hierarchy", JSImport.Namespace)
@js.native
object Hierarchy extends js.Object {
  def hierarchy[D, N <: Node[D, N]](data: D, getChildren: js.UndefOr[js.Function1[D, js.Array[D]]] = js.undefined) : N = js.native

  def tree(): TreeGenerator = js.native
}

@js.native
trait Link[Node] extends js.Object {
  var source: js.UndefOr[Node] = js.native
  var target: js.UndefOr[Node] = js.native
}

@js.native
trait BaseNode[D, N <: BaseNode[D, N]] extends js.Object {
  var data: js.UndefOr[D] = js.native
  var depth: js.UndefOr[Int] = js.native
  def height: js.UndefOr[Int] = js.native
  var parent: js.UndefOr[N] = js.native
  var children: js.UndefOr[js.Array[N]] = js.native
  var value: js.UndefOr[Double] = js.native

  def ancestors(): js.Array[N] = js.native
  def descendants(): js.Array[N] = js.native
  def leaves(): js.Array[N] = js.native
  def links(): js.Array[Link[N]] = js.native
  def count(): N = js.native
  def sort(compare: js.Function2[N,N,Integer]): N = js.native

  def eachAfter(callback: js.Function1[N, Unit]): N = js.native
  def eachBefore(callback: js.Function1[N, Unit]): N = js.native
}

package treeModule {
  @js.native
  trait TreeGenerator extends js.Object {
    def apply[N](root: N): N = js.native
    def size(size: js.Array[Double]): Unit = js.native
  }
  @js.native
  trait Node[D, N <: Node[D, N]] extends BaseNode[D, N] {
    var x: js.UndefOr[Double] = js.native
    var y: js.UndefOr[Double] = js.native
  }
}

