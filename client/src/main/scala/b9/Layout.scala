package b9

import b9.short._
import facades.d3js.Hierarchy

import scala.scalajs.js

/**
  * Created by blu3gui7ar on 2017/7/14.
  */
object Layout {
  val width = 700
  val left = 50
  val right = 200

  val height = 500
  val top = 10
  val bottom = 10

  val tree = {
    val t = Hierarchy.tree()
    t.size(js.Array(height - top - bottom, width - left - right))
    t
  }

  def apply[N](node: IdNode[N]): IdNode[N] = tree(node)

  def gather[N](node: IdNode[N], x: Double, y: Double)(filter: (IdNode[N] => Boolean) = { _: IdNode[N] => true }): IdNode[N] =
    node.eachBefore { n: IdNode[N] =>
      if (filter(n)) {
        n.x = x
        n.y = y
      }
    }

  def gather[N](node: IdNode[N]): IdNode[N] =
    gather(node, (width - left - right) / 2 + left, (height - top - bottom) / 2 + top)()

  def compact[N](treeRoot: IdNode[N], x: Double, y: Double): IdNode[N] = {
    treeRoot.eachBefore { n =>
      if (!n.display) {
        n.x = x
        n.y = y
      } else if (n.parent.map(_.fold).getOrElse(false)) {
        n.x = n.parent.map(_.x).getOrElse(x)
        n.y = n.parent.map(_.y).getOrElse(y)
      }
    }
    treeRoot
  }

  def rehierarchy[N](n: IdNode[N], children: js.Function1[IdNode[N], js.Array[IdNode[N]]]): IdNode[N] = {
    val rhroot = Hierarchy.hierarchy[IdNode[N], IdNode[IdNode[N]]](n, children)
    apply(rhroot) eachBefore { wrapped : IdNode[IdNode[N]] =>
      wrapped.data.map { inner =>
        inner.x = wrapped.x
        inner.y = wrapped.y
      }
    }
    rhroot.data.get
  }
}
