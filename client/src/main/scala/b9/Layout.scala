package b9

import b9.short.{IdNode, TM}
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

  def compact(treeRoot: TM, displayRoot: TM)(f: TM => Boolean = { n: TM => n.rootLabel.attach.display }): TM = {
    if(!f(treeRoot)) {
      treeRoot.rootLabel.attach.x = displayRoot.rootLabel.attach.x
      treeRoot.rootLabel.attach.y = displayRoot.rootLabel.attach.y
    }
    traverse(treeRoot, { (parent: TM, child: TM) =>
      if (!f(child)) {
        child.rootLabel.attach.x = parent.rootLabel.attach.x
        child.rootLabel.attach.y = parent.rootLabel.attach.y
      }
    })
    treeRoot
  }

  def relocate[N](node: IdNode[N]): IdNode[N] = {
    //rebuild depth
    val dn = node.eachBefore { n: IdNode[N] =>
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

  def applyDisplay(treeRoot: TM) = {
    treeRoot.rootLabel.attach.display = treeRoot.rootLabel.attach.nextDisplay
    traverse(treeRoot, { (_, child) =>
      child.rootLabel.attach.display = child.rootLabel.attach.nextDisplay
    })
  }

  protected def traverse(node: TM, f: (TM, TM) => Unit): Unit =
    node.subForest.foreach { child: TM =>
      f(node, child)
      traverse(child, f)
    }

  protected def eq(a: TM, b: TM): Boolean = a.rootLabel eq b.rootLabel

  def redisplay(treeRoot: TM, displayRoot: TM): TM = {
    treeRoot.rootLabel.attach.nextDisplay = eq(treeRoot, displayRoot)
    traverse(treeRoot, { (parent: TM, child: TM) =>
      child.rootLabel.attach.nextDisplay = eq(child, displayRoot) ||
        (parent.rootLabel.attach.nextDisplay && !parent.rootLabel.attach.fold )
    })
    treeRoot
  }

  def rehierarchy(displayRoot: TM, displayCheck: TM => Boolean = _.rootLabel.attach.display): TM = {
    import js.JSConverters._
    val empty = js.Array[TM]()
    val rhroot = Hierarchy.hierarchy[TM, IdNode[TM]](displayRoot,
      { n: TM =>
        if (n.rootLabel.attach.fold) empty
        else n.subForest.filter(displayCheck).toJSArray
      }: js.Function1[TM, js.Array[TM]]
    )
    (apply(rhroot) eachBefore { n: IdNode[TM] =>
      n.data.toOption match {
        case Some(rn) => {
          rn.rootLabel.attach.x = n.x.getOrElse(0.0)
          rn.rootLabel.attach.y = n.y.getOrElse(0.0)
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
