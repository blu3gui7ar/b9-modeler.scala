package b9

import meta._
import monix.execution.Cancelable
import monocle.Lens
import monocle.std.tree._
import monocle.syntax.ApplyLens
import monocle.syntax.all._
import play.api.libs.json._
import scalaz.{Show, Tree, TreeLoc}

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object TreeOps {

  lazy val treeExtractor = new TreeExtractorTpl[Unit]()

  type TN = TreeNode[Unit]
  type TTN = Tree[TreeNode[Unit]]
  type TTNLoc = TreeLoc[TreeNode[Unit]]

  implicit val TreeShows: Show[TN] = Show.shows { tn: TN =>
    tn.toString
  }

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

  def initialModel: (TTN, MetaAst.Root) = {
    import meta.Sample
    val ds = new MetaSource(Sample.meta)
    import ds._

//    println(ds.meta)

    val dataJs = Json.parse(Sample.data)
    import treeExtractor._
    val tree = ds.meta.tree("meta", Some(dataJs), rootAttrDef()).getOrElse(emptyTree)
//    println(tree.drawTree)
    (tree, ds.meta)

//    val r = init(tree)
//    import JsonExpr._
//    val d: Option[TM] = Some(r)
//    println(ds.meta.json(d))

//    ModelerState(ds.meta, GraphState(r,r.loc,r,r))

  }
}
