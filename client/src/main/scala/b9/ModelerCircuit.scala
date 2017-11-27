package b9

import b9.short._
import diode.ActionResult.{ModelUpdate, ModelUpdateEffect, NoChange}
import diode._
import diode.react.ReactConnector
import facades.d3js.Hierarchy
import meta._
import upickle.Js

import scala.annotation.tailrec
import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.timers._

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object ModelerCircuit extends Circuit[ModelerState] with ReactConnector[ModelerState] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def actionEffect(action: Action, timeout: Int = 0) = Effect {
    val p = Promise[Action]()
    setTimeout(timeout) { // animation
      p.success(action)
    }
    p.future
  }

  protected def init(root: TM): TM = {
    import js.JSConverters._
    val hroot = Hierarchy.hierarchy[TM, TN](root, { n => n.subForest.toJSArray }: js.Function1[TM, js.Array[TM]])
    val troot = Layout.gather(Idx.reindex(hroot))
    pushAttach(troot).get
  }

  protected def pushAttach(transientTree: TN): Option[TM] = {
    val tTree = transientTree.eachBefore { n: TN =>
      n.data map { m: TM =>
        m.rootLabel.attach.x = n.x.getOrElse(0)
        m.rootLabel.attach.y = n.y.getOrElse(0)
        m.rootLabel.attach.fold = n.fold.getOrElse(false)
      }
    }
    tTree.data.toOption
  }

  override protected def initialModel: ModelerState = {
    import meta.Sample
    val ds = new MetaSource(Sample.meta)
    import ds._

    val dataJs = upickle.json.read(Sample.data)
    val treeExtractor = new TreeExtractorTpl[TreeAttach](new TreeAttach())
    import treeExtractor._
    val tree = ds.meta.tree("meta", Some(dataJs), RootAttrDef).getOrElse(emptyTree)
//    println(tree.drawTree)

    val r = init(tree)
    import JsonExpr._
    val d: Option[TM] = Some(r)
    println(ds.meta.json(d))

    ModelerState(ds.meta, tree, GraphState(r,r.loc,r,r))
  }

  override protected def actionHandler: ModelerCircuit.HandlerFunction = new ModelerActionHandler(ModelerCircuit.zoomRW(identity)((last, current) => current))

  class ModelerActionHandler[M](modelRW: ModelRW[M, ModelerState]) extends ActionHandler(modelRW) {
    import Layout._
    val treeRW = modelRW.zoomTo(_.graph.root)
    val displayRW = modelRW.zoomTo(_.graph.display)
    val editRW = modelRW.zoomTo(_.graph.editing)
    val activeRW = modelRW.zoomTo(_.graph.active)

    override def handle = {
      case GoUpAction(node) => {
        val tree = treeRW()
        val currentDp = displayRW()

        @tailrec
        def findParent(loc: TMLoc, target: TM): TMLoc =
          if((loc.tree eq target) || (loc.tree eq tree))
            loc
          else
            findParent(loc, target)

        val newDp = findParent(currentDp, node)

        ModelUpdateEffect( displayRW.updated {
            redisplay(tree, node)
            applyDisplay(tree)
            newDp
          },
          actionEffect(FlushHierarchyAction(newDp))
        )
      }
//      case FlushDisplayAction(node) => {
//        val tree = treeRW()
//        ModelUpdate(displayRW.updated {
//          applyDisplay(tree)
//          node
//        })
//      }
      case FlushHierarchyAction(nodeLoc) => {
        val tree = treeRW()
        val display = displayRW()
        ModelUpdate(displayRW.updated {
          rehierarchy(tree, display.tree)
          compact(tree, display.tree)(_.rootLabel.attach.display)
          nodeLoc
        })
      }
//      case GoDownAction(node) => {
//        val tree = treeRW()
//        ModelUpdateEffect(treeRW.updated {
//            redisplay(tree, node)
//            rehierarchy(tree, node)
//            compact(tree, node)(_.nextDisplay.getOrElse(true))
//            tree
//          },
//          actionEffect(FlushDisplayAction(node), ModelerCss.delay)
//        )
//      }
//      case FoldAction(node) => {
//        if (node.children.map(_.nonEmpty).getOrElse(false)) {
//          //TODO: direct update is not right
//          val fold = !node.fold.getOrElse(false)
//          node.fold = fold
//          val tree = treeRW()
//          val display = displayRW()
//          if (fold)
//            ModelUpdateEffect(displayRW.updated {
//                redisplay(tree, display)
//                rehierarchy(tree, display)
//                compact(tree, display)(_.nextDisplay.getOrElse(true))
//                display
//              },
//              actionEffect(FlushDisplayAction(display), ModelerCss.delay)
//            )
//          else
//            ModelUpdateEffect( displayRW.updated {
//                redisplay(tree, display)
//                applyDisplay(tree)
//              },
//              actionEffect(FlushHierarchyAction(display))
//            )
//        } else NoChange
//      }
//      case EditAction(node) => {
//        if (editRW() ne node)
//          ModelUpdate(editRW.updated(node))
//        else NoChange
//      }
      case ActiveAction(node) => {
        if (activeRW() ne node)
          ModelUpdate(activeRW.updated(node))
        else NoChange
      }
//      case CreateAction(node, name, meta) =>  {
//        //TODO: direct update is not right
//        val newTN = TreeExtractor.create(name, Seq.empty, meta)
//        val newChild = new RealNode(newTN, node, node.x, node.y, Idx.next()).asInstanceOf[TN]
//        import js.JSConverters._
//        if (node.children.isDefined) {
//          node.children.get += newChild
//        } else {
//          node.children = Seq(newChild).toJSArray
//        }
//        val display = displayRW()
//        val tree = treeRW()
//        ModelUpdateEffect(displayRW.updated {
//            redisplay(tree, display)
//            applyDisplay(tree)
//          },
//          actionEffect(FlushHierarchyAction(display))
//        )
//      }
//      case RemoveFromAction(node, parent) => {
//        val display = displayRW()
//        val tree = treeRW()
//        ModelUpdateEffect( displayRW.updated {
//            node.fold = true
//            redisplay(tree, display)
//            node.nextDisplay = false
//            rehierarchy(tree, display, {_.nextDisplay.getOrElse(true)})
//            compact(tree, display)(_.nextDisplay.getOrElse(true))
//            display
//          },
//          actionEffect(FlushRemoveFromAction(node, parent), ModelerCss.delay)
//        )
//      }
//      case FlushRemoveFromAction(node, parent) => {
//        val display = displayRW()
//        ModelUpdate( displayRW.updated {
//            if (parent.children.isDefined) {
//              parent.children.get -= node
//            }
//            display
//          }
//        )
//      }
//      case ValueSetAction(node, ref, value) => {
//        //TODO: direct update is not right
//        node.data = node.data.map(_.copy(value = value))
//        printTree()
//        ModelUpdate(displayRW.updated(displayRW()))
//      }
//      case ValueAddAction(node, ref, value) => {
//        //TODO: direct update is not right
//        node.data = node.data.map { data =>
//          val newValue: Seq[Js.Value] = data.value match {
//            case arr: Js.Arr => value +: arr.value
//            case _ => Seq.empty
//          }
//          data.copy(value = Js.Arr(newValue: _*))
//        }
//        printTree()
//        ModelUpdate(displayRW.updated(displayRW()))
//      }
//      case ValueDelAction(node, ref, value) => {
//        //TODO: direct update is not right
//        node.data = node.data.map { data =>
//          val newValue: Seq[Js.Value] = data.value match {
//            case arr: Js.Arr => arr.value.filter(_ != value)
//            case _ => Seq.empty
//          }
//          data.copy(value = Js.Arr(newValue: _*))
//        }
//        printTree()
//        ModelUpdate(displayRW.updated(displayRW()))
//      }
    }

//    private def printTree(): Unit = {
//      val state = modelRW()
//      val meta = modelRW().meta
//      state.graph.root.data map { _ =>
//        import JsonExpr._
//        implicit val macros = MetaAst.macros(meta)
//        implicit val types = MetaAst.types(meta)
//        val tree = Some(state.graph.root)
//        println("set: " + meta.json(tree))
//      }
//    }
  }
}
