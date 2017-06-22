package b9

import diode.Circuit
import diode.react.ReactConnector
import facades.d3js.Hierarchy
import facades.d3js.treeModule.Node

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * Created by blu3gui7ar on 2017/5/23.
  */

@ScalaJSDefined
class ProxyNode(var inner: TreeNode) extends Node[ProxyNode] {
  import js.JSConverters._

  protected var _x: Double = inner.x
  protected var _y: Double = inner.y
  protected var _children: Seq[ProxyNode] = inner.children.map(t => new ProxyNode(t))
  protected var dirty: Boolean = false

  def sync(force: Boolean = false): Boolean = {
    val dirtyChildren = _children.map(_.sync()).reduce((p, n) => p || n)
    if (force || dirty || dirtyChildren) {
      inner = inner.copy(x = _x, y = _y, children = _children.map(_.inner))
    }
    val rs = dirty || dirtyChildren
    dirty = false
    rs
  }

  override def x: Double = _x

  override def x_(x: Double): Unit = {
    _x = x
    dirty = true
  }

  override def y: Double = inner.y

  override def y_(y: Double): Unit = {
    _y = y
    dirty = true
  }

  // change on return will not affect property, must set
  override def children: js.Array[ProxyNode] =  _children.toJSArray

  override def children_(children: js.Array[ProxyNode]): Unit = {
    _children = children
    dirty = true
  }
}

case class State(
                  tree: TreeNode,
//                  displayRoot: Node,
//                  activeNode: Node,
//                  relocateSource: Node,
                  a: Int
                )

object ModelerCircuit extends Circuit[State] with ReactConnector[State] {
  protected def init(root: TreeNode) : TreeNode = {
    val hroot = new ProxyNode(root)
//    js.Dynamic.global.console.log(proot)
//    val hroot = Hierarchy.hierarchy(proot)
//    js.Dynamic.global.console.log(hroot)
    val tree = Hierarchy.tree()
    tree.size(js.Array(500 - 20, 700 - 250))
    tree(hroot)
    js.Dynamic.global.console.log(hroot)
    hroot.sync()
    hroot.inner
  }

  override protected def initialModel: State = State(init(Sample.tree()), 3)

  override protected def actionHandler: ModelerCircuit.HandlerFunction = ???
}
