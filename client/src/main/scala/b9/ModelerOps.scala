package b9

import b9.short._
import facades.d3js.Hierarchy
import meta.MetaAst.AttrDef
import meta._
import monix.execution.Cancelable
import play.api.libs.json._
import scalaz.Tree.Node

import scala.annotation.tailrec
import scala.scalajs.js

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object ModelerOps {

  protected lazy val treeExtractor = new TreeExtractorTpl[TreeAttach](new TreeAttach())

  import monix.execution.Scheduler.{global => scheduler}
  import scala.concurrent.duration._
  def deferAction(action: => Unit): Cancelable =
    scheduler.scheduleOnce(ModelerCss.delay.millisecond)(action)

  protected def init(root: TM): TM = {
    import js.JSConverters._
    val hroot = Hierarchy.hierarchy[TM, IdNode[TM]](root, { n => n.subForest.toJSArray }: js.Function1[TM, js.Array[TM]])
    val troot = Layout.gather(hroot)
    pushAttach(troot).get
  }

  protected def pushAttach(transientTree: IdNode[TM]): Option[TM] = {
    val tTree = transientTree.eachBefore { n: IdNode[TM] =>
      n.data map { m: TM =>
        m.rootLabel.attach.x = n.x.getOrElse(0)
        m.rootLabel.attach.y = n.y.getOrElse(0)
      }
    }
    tTree.data.toOption
  }

  lazy val initialModel: ModelerState = {
    import meta.Sample
    val ds = new MetaSource(Sample.meta)
    import ds._

//    println(ds.meta)

    val dataJs = Json.parse(Sample.data)
    import treeExtractor._
    val tree = ds.meta.tree("meta", Some(dataJs), RootAttrDef).getOrElse(emptyTree)
//    println(tree.drawTree)

    val r = init(tree)
//    import JsonExpr._
//    val d: Option[TM] = Some(r)
//    println(ds.meta.json(d))

    ModelerState(ds.meta, GraphState(r,r.loc,r,r))
  }

  import Layout._

  @tailrec
  def findParent(loc: TMLoc, target: TM): TMLoc =
    if(loc.tree.rootLabel eq target.rootLabel) loc
    else
      loc.parent match {
        case Some(parent) => findParent(parent, target)
        case _ => loc
      }

  def goUp(node: TM): ModelerState => ModelerState = { state =>
    val newDp = findParent(state.graph.display, node)
    redisplay(state.graph.root, newDp.tree)
    applyDisplay(state.graph.root)
    state.copy(graph = state.graph.copy(display = newDp))
  }

  def goDown(node: TM) = { state: ModelerState =>
    val tree = state.graph.root

    redisplay(tree, node)
    rehierarchy(node)
    compact(tree, node)(_.rootLabel.attach.nextDisplay)
    state
  }

  def flushHierarchy(): ModelerState => ModelerState = { state =>
    rehierarchy(state.graph.display.tree)
    compact(state.graph.root, state.graph.display.tree)(_.rootLabel.attach.display)
    state
  }

  def flushDisplay(node: TM) = { state: ModelerState =>
    state.graph.display.find { loc =>
      loc.tree.rootLabel eq node.rootLabel
    } map { newDp =>
      applyDisplay(state.graph.root)
      state.copy(graph = state.graph.copy(display = newDp))
    } getOrElse(state)
  }


  def valueSet(node: TM, ref: String, value: JsValue): ModelerState => ModelerState = { state =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        Node(rootLabel.copy(value = value), subForest)
      }
    } map { newNodeLoc =>
      val newDp = findParent(newNodeLoc, display.tree)
      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
      state.copy(graph = newGraph)
    } getOrElse(state)
  }

  def valueAdd(node: TM, ref: String, value: JsValue): ModelerState => ModelerState = { state =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        val newV: Seq[JsValue] = rootLabel.value match {
          case arr: JsArray => value +: arr.value
          case _ => Seq.empty
        }
        Node(rootLabel.copy(value = JsArray(newV)), subForest)
      }
    } map { newNodeLoc =>
      val newDp = findParent(newNodeLoc, display.tree)
      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
      state.copy(graph = newGraph)
    } getOrElse(state)
  }

  def valueDel(node: TM, ref: String, value: JsValue): ModelerState => ModelerState = { state =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        val newV: Seq[JsValue] = rootLabel.value match {
          case arr: JsArray => arr.value.filter(_ != value)
          case _ => Seq.empty
        }
        Node(rootLabel.copy(value = JsArray(newV)), subForest)
      }
    } map { newNodeLoc =>
      val newDp = findParent(newNodeLoc, display.tree)
      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
      state.copy(graph = newGraph)
    } getOrElse(state)
  }

  //TODO
  def fold(node: TM) = { state: ModelerState =>
    val tree = state.graph.root
    val display = state.graph.display.tree
    node.rootLabel.attach.fold = true
    redisplay(tree, display)
    rehierarchy(display)
    compact(tree, display)(_.rootLabel.attach.nextDisplay)
    state
    //actionEffect(FlushDisplayAction(display), ModelerCss.delay)
  }

  def unfold(node: TM) = { state: ModelerState =>
    val tree = state.graph.root
    val display = state.graph.display.tree
    node.rootLabel.attach.fold = false
    redisplay(tree, display)
    applyDisplay(tree)
    //actionEffect(FlushHierarchyAction())
    state
  }

  def edit(node: TM) = { state: ModelerState =>
      state.copy(graph = state.graph.copy(editing = node))
  }

  def active(node: TM) = { state: ModelerState =>
    state.copy(graph = state.graph.copy(active = node))
  }

  def create(node: TM, name: String, meta: AttrDef) =  { state: ModelerState =>
    val display = state.graph.display
    display.find(loc => loc.tree.rootLabel eq node.rootLabel) map { loc =>
      val newTN = treeExtractor.create(name, Stream.empty, meta)
      newTN.rootLabel.attach.x = loc.tree.rootLabel.attach.x
      newTN.rootLabel.attach.y = loc.tree.rootLabel.attach.y
      loc.modifyTree { case Node(rootLabel, subForest) => Node(rootLabel, subForest :+ newTN) }
    } map {
      findParent(_, display.tree)
    } map { newDisplay =>
      val newRoot = newDisplay.root.tree
      redisplay(newRoot, newDisplay.tree)
      applyDisplay(newRoot)
      val newGraph = state.graph.copy(root = newDisplay.root.tree, display = newDisplay)
      state.copy(graph = newGraph)
      //flushHierarchy
    } getOrElse(state)
  }
  def removeFrom(node: TM, parent: TM) = { state: ModelerState  =>
    val display = state.graph.display
    val tree = state.graph.root

    node.rootLabel.attach.fold = true
    redisplay(tree, display.tree)
    node.rootLabel.attach.nextDisplay = false
    rehierarchy(display.tree, _.rootLabel.attach.nextDisplay)
    compact(tree, display.tree)(_.rootLabel.attach.nextDisplay)

    state
  }
  def flushRemoveFrom(node: TM, parent: TM) = { state: ModelerState =>
    val display = state.graph.display
    display.find(_.tree.rootLabel eq parent.rootLabel) map { loc =>
      loc.modifyTree { case Node(rootLabel, subForest) =>
        Node(rootLabel, subForest.filter(_.rootLabel ne node.rootLabel))
      }
    } map { pLoc =>
      val newDp = findParent(pLoc, display.tree)
      state.copy(graph = state.graph.copy(root = newDp.root.tree, display = newDp))
    } getOrElse(state)
  }
}
