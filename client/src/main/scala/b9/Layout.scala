package b9

import b9.short.{IdNode, TN}
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

  def compact[N](treeRoot: IdNode[N], displayRoot: IdNode[N])(f: IdNode[N] => Boolean = { n: IdNode[N] => n.display.getOrElse(true) }): IdNode[N] =
    treeRoot.eachBefore { n: IdNode[N] =>
      if (!f(n)) {
        val parent = n.parent.getOrElse(displayRoot)
        if (parent != null) {
          n.x = parent.x
          n.y = parent.y
        } else {
          n.x = displayRoot.x
          n.y = displayRoot.y
        }
      }
    }

  def relocate(node: TN): TN = {
    //rebuild depth
    val dn = node.eachBefore { n: TN =>
      n.depth = n.parent.toOption match {
        case Some(null) => 0
        case Some(parent) => if (n == node) 0 else parent.depth.getOrElse(0) + 1
        case None => 0
      }
    }
    apply(dn)
    //count diff nodes
    //    node.eachAfter { n: TN =>
    //      val v: Int = n.data.map(d => {
    //        if (n.x.map(_ == d.x).getOrElse(false) && n.y.map(_ == d.y).getOrElse(false)) 0
    //        else 1
    //      }).getOrElse(0)
    //
    //      n.diffDescendants = v + n.children.map(_.map(_.diffDescendants.getOrElse(0)).fold(0)(_ + _)).getOrElse(0)
    //    }
  }

  def applyDisplay(treeRoot: TN) = {
    treeRoot.eachBefore { n: TN =>
      n.display = n.nextDisplay
    }
  }

  def redisplay(treeRoot: TN, displayRoot: TN): TN = treeRoot.eachBefore { n: TN =>
    n.nextDisplay = (n == displayRoot) ||
      (n.parent.toOption match {
        case Some(null) => false
        case None => false
        case Some(parent) => parent.nextDisplay.getOrElse(false) && !parent.fold.getOrElse(false)
      })
  }

  def rehierarchy(treeRoot: TN, displayRoot: TN, displayCheck: (TN) => Boolean = {_.display.getOrElse(true)}): TN = {
    val empty = js.Array[TN]()
    val rhroot = Hierarchy.hierarchy[TN, IdNode[TN]](displayRoot,
      { n =>
        if (n.fold.getOrElse(false)) empty else n.children.getOrElse(empty).filter(displayCheck)
      }: js.Function1[TN, js.Array[TN]]
    )
    (apply(rhroot) eachBefore { n: IdNode[TN] =>
      n.data.toOption match {
        case Some(rn) => {
          rn.x = n.x
          rn.y = n.y
        }
        case _ =>
      }
    }).data.getOrElse(displayRoot)
  }


/*  protected def syncPos(tn: TN): TreeNode = {
    tn.data.map( n =>
      if (tn.x.map(_ != n.x).getOrElse(true) || tn.y.map(_ != n.y).getOrElse(true)) {
        if (tn.diffDescendants.map(_ > 1).getOrElse(false)) {
          val nc: js.Array[TreeNode] = tn.children.map(_.map(syncPos(_))).getOrElse(Empty)
          n.copy(x = tn.x.getOrElse(n.x), y = tn.y.getOrElse(n.y), children = nc)
        }
        else n.copy(x = tn.x.getOrElse(n.x), y = tn.y.getOrElse(n.y))
      } else {
        if (tn.diffDescendants.map(_ > 0).getOrElse(false)) {
          val nc: js.Array[TreeNode] = tn.children.map(_.map(syncPos(_))).getOrElse(Empty)
          n.copy(children = nc)
        }
        else n
      }
    ).getOrElse(TreeExtractor.Empty)
  }*/
}
