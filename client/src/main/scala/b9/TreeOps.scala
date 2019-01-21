package b9

import meta._
import monix.execution.Cancelable
import monocle.Lens
import monocle.std.tree._
import monocle.syntax.ApplyLens
import monocle.syntax.all._
import play.api.libs.json._
import scalaz.{Tree, TreeLoc}

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object TreeOps {

  lazy val treeExtractor = new TreeExtractorTpl[Unit]()

  type TN = TreeNode[Unit]
  type TTN = Tree[TreeNode[Unit]]
  type TTNLoc = TreeLoc[TreeNode[Unit]]

  type SLens = Lens[TTN, Stream[TTN]]
  type TLens = Lens[TTN, TTN]
  type LLens = Lens[TTN, TN]

  def at(idx: Int): Lens[Stream[TTN], TTN] = {
    Lens[Stream[TTN], TTN](_.apply(idx))(node => _.zipWithIndex map { case (on, i) => if (idx == i) node else on})
  }

  def at(node: TTN): Lens[Stream[TTN], TTN] = Lens[Stream[TTN], TTN]
    { stream => stream.find(_ eq node).getOrElse(stream.head) }
    { newNode => stream => stream.map { n => if (n eq node) newNode else n } }

  val asSet = Lens[Stream[TTN], Set[TTN]](_.toSet)(set => _ => set.toStream)

  import monix.execution.Scheduler.{global => scheduler}

  import scala.concurrent.duration._
  def deferAction(action: => Unit): Cancelable =
    scheduler.scheduleOnce(ModelerCss.delay.millisecond)(action)

  def labelLens: TTN => ApplyLens[TTN, TTN, TN, TN]  = _ applyLens rootLabel

  def subLens: TTN => ApplyLens[TTN, TTN, Stream[TTN], Stream[TTN]] = _ applyLens subForest

  lazy val initialModel: (TTN, MetaAst.Root) = {
    import meta.Sample
    val ds = new MetaSource(Sample.meta)
    import ds._

//    println(ds.meta)

    val dataJs = Json.parse(Sample.data)
    import treeExtractor._
    val tree = ds.meta.tree("meta", Some(dataJs), RootAttrDef).getOrElse(emptyTree)
    (tree, ds.meta)
//    println(tree.drawTree)

//    val r = init(tree)
//    import JsonExpr._
//    val d: Option[TM] = Some(r)
//    println(ds.meta.json(d))

//    ModelerState(ds.meta, GraphState(r,r.loc,r,r))
  }


//  @tailrec
//  def findParent(loc: TMLoc, target: TM): TMLoc =
//    if(loc.tree.rootLabel eq target.rootLabel) loc
//    else
//      loc.parent match {
//        case Some(parent) => findParent(parent, target)
//        case _ => loc
//      }

//  def valueSet(node: TM, ref: String, value: JsValue) = { state: TreeState =>
//    state
//    val display = state.graph.display
//    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//      loc.modifyTree { case Node(rootLabel, subForest) =>
//        Node(rootLabel.copy(value = value), subForest)
//      }
//    } map { newNodeLoc =>
//      val newDp = findParent(newNodeLoc, display.tree)
//      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
//      state.copy(graph = newGraph)
//    } getOrElse(state)
//  }

//  def valueAdd(node: TM, ref: String, value: JsValue): ModelerState => ModelerState = { state =>
//    val display = state.graph.display
//    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//      loc.modifyTree { case Node(rootLabel, subForest) =>
//        val newV: Seq[JsValue] = rootLabel.value match {
//          case arr: JsArray => value +: arr.value
//          case _ => Seq.empty
//        }
//        Node(rootLabel.copy(value = JsArray(newV)), subForest)
//      }
//    } map { newNodeLoc =>
//      val newDp = findParent(newNodeLoc, display.tree)
//      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
//      state.copy(graph = newGraph)
//    } getOrElse(state)
//  }
//
//  def valueDel(node: TM, ref: String, value: JsValue): ModelerState => ModelerState = { state =>
//    val display = state.graph.display
//    display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//      loc.modifyTree { case Node(rootLabel, subForest) =>
//        val newV: Seq[JsValue] = rootLabel.value match {
//          case arr: JsArray => arr.value.filter(_ != value)
//          case _ => Seq.empty
//        }
//        Node(rootLabel.copy(value = JsArray(newV)), subForest)
//      }
//    } map { newNodeLoc =>
//      val newDp = findParent(newNodeLoc, display.tree)
//      val newGraph = state.graph.copy(root = newDp.root.tree, display = newDp)
//      state.copy(graph = newGraph)
//    } getOrElse(state)
//  }
//
//  def create(node: TM, name: String, meta: AttrDef) =  { state: ModelerState =>
//    val display = state.graph.display
//    display.find(loc => loc.tree.rootLabel eq node.rootLabel) map { loc =>
//      val newTN = treeExtractor.create(name, Stream.empty, meta)
//      newTN.rootLabel.attach.x = loc.tree.rootLabel.attach.x
//      newTN.rootLabel.attach.y = loc.tree.rootLabel.attach.y
//      loc.modifyTree { case Node(rootLabel, subForest) => Node(rootLabel, subForest :+ newTN) }
//    } map {
//      findParent(_, display.tree)
//    } map { newDisplay =>
//      val newRoot = newDisplay.root.tree
////      redisplay(newRoot, newDisplay.tree)
////      applyDisplay(newRoot)
//      val newGraph = state.graph.copy(root = newDisplay.root.tree, display = newDisplay)
//      state.copy(graph = newGraph)
//      //flushHierarchy
//    } getOrElse(state)
//  }
//
//  def remove(node: TM, parent: TM) = { state: ModelerState =>
//    val display = state.graph.display
//    display.find(_.tree.rootLabel eq parent.rootLabel) map { loc =>
//      loc.modifyTree { case Node(rootLabel, subForest) =>
//        Node(rootLabel, subForest.filter(_.rootLabel ne node.rootLabel))
//      }
//    } map { pLoc =>
//      val newDp = findParent(pLoc, display.tree)
//      state.copy(graph = state.graph.copy(root = newDp.root.tree, display = newDp))
//    } getOrElse(state)
//  }
}
