package facades.d3js

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Created by blu3gui7ar on 2017/6/23.
  */
@JSImport("d3-shape", JSImport.Namespace)
@js.native
object Shape extends js.Object {
  def linkVertical[L, N](): LinkGeneratorN[L, N] = js.native
  def linkHorizontal[L, N](): LinkGeneratorN[L, N] = js.native
  def linkRadial[L, N](): LinkGeneratorR[L, N] = js.native
}

@js.native
trait BaseLinkGenerator[L, G <: BaseLinkGenerator[L, G]] extends js.Object {
  type Accessor = js.Function1[L, js.Any]
  def apply(arg: js.Any): G = js.native

  def source(): Accessor = js.native
  def source(accessor: Accessor): G = js.native
  def target(): Accessor = js.native
  def target(accessor: Accessor): G = js.native
}

@js.native
trait LinkGeneratorN[L, N] extends BaseLinkGenerator[L, LinkGeneratorN[L, N]] {
  type XYAccessor = js.Function1[N, js.Any]
  def x(): XYAccessor = js.native
  def x(accessor: XYAccessor): LinkGeneratorN[L, N] = js.native
  def y(): XYAccessor = js.native
  def y(accessor: XYAccessor): LinkGeneratorN[L, N] = js.native
}

@js.native
trait LinkGeneratorR[L, N] extends BaseLinkGenerator[L, LinkGeneratorR[L, N]] {
  type ARAccessor = js.Function1[N, js.Any]
  def angle(): ARAccessor = js.native
  def angle(accessor: ARAccessor): LinkGeneratorR[L, N] = js.native
  def radius(): ARAccessor = js.native
  def radius(accessor: ARAccessor): LinkGeneratorR[L, N] = js.native
}

