//package b9
//
//import b9.short._
//import diode.ActionResult.{ModelUpdate, ModelUpdateEffect, NoChange}
//import diode._
//import diode.react.ReactConnector
//import facades.d3js.Hierarchy
//import meta._
//import play.api.libs.json._
//
//import scala.annotation.tailrec
//import scala.concurrent.Promise
//import scala.scalajs.js
//import scala.scalajs.js.timers._
//import scalaz.Tree.Node
//
///**
//  * Created by blu3gui7ar on 2017/6/24.
//  */
//object ModelerCircuit extends Circuit[ModelerState] with ReactConnector[ModelerState] {
//
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  protected lazy val treeExtractor = new TreeExtractorTpl[TreeAttach](new TreeAttach())
//
//  def actionEffect(action: Action, timeout: Int = 0) = Effect {
//    val p = Promise[Action]()
//    setTimeout(timeout) { // animation
//      p.success(action)
//    }
//    p.future
//  }
//
//  protected def init(root: TM): TM = {
//    import js.JSConverters._
//    val hroot = Hierarchy.hierarchy[TM, IdNode[TM]](root, { n => n.subForest.toJSArray }: js.Function1[TM, js.Array[TM]])
//    val troot = Layout.gather(hroot)
//    pushAttach(troot).get
//  }
//
//  protected def pushAttach(transientTree: IdNode[TM]): Option[TM] = {
//    val tTree = transientTree.eachBefore { n: IdNode[TM] =>
//      n.data map { m: TM =>
//        m.rootLabel.attach.x = n.x.getOrElse(0)
//        m.rootLabel.attach.y = n.y.getOrElse(0)
//      }
//    }
//    tTree.data.toOption
//  }
//
//  override protected def initialModel: ModelerState = {
//    import meta.Sample
//    val ds = new MetaSource(Sample.meta)
//    import ds._
//
////    println(ds.meta)
//
//    val dataJs = Json.parse(Sample.data)
//    import treeExtractor._
//    val tree = ds.meta.tree("meta", Some(dataJs), RootAttrDef).getOrElse(emptyTree)
////    println(tree.drawTree)
//
//    val r = init(tree)
////    import JsonExpr._
////    val d: Option[TM] = Some(r)
////    println(ds.meta.json(d))
//
//    ModelerState(ds.meta, GraphState(r,r.loc,r,r))
//  }
//
//  override protected def actionHandler: ModelerCircuit.HandlerFunction = new ModelerActionHandler(ModelerCircuit.zoomRW(identity)((last, current) => current))
//
//  class ModelerActionHandler[M](modelRW: ModelRW[M, ModelerState]) extends ActionHandler(modelRW) {
//    import Layout._
//    val treeRW = modelRW.zoomTo(_.graph.root)
//    val displayRW = modelRW.zoomTo(_.graph.display)
//    val editRW = modelRW.zoomTo(_.graph.editing)
//    val activeRW = modelRW.zoomTo(_.graph.active)
//
//    @tailrec
//    private def findParent(loc: TMLoc, target: TM): TMLoc =
//      if(loc.tree.rootLabel eq target.rootLabel) loc
//      else
//        loc.parent match {
//          case Some(parent) => findParent(parent, target)
//          case _ => loc
//        }
//
//    override def handle = {
//      case GoUpAction(node) => {
//        val tree = treeRW()
//        val currentDp = displayRW()
//
//        val newDp = findParent(currentDp, node)
//
//        ModelUpdateEffect(
//          displayRW.updated {
//            redisplay(tree, node)
//            applyDisplay(tree)
//            newDp
//          },
//          actionEffect(FlushHierarchyAction())
//        )
//      }
//      case FlushHierarchyAction() => {
//        val tree = treeRW()
//        val currentDp = displayRW()
//        ModelUpdate(displayRW.updated {
//          rehierarchy(currentDp.tree)
//          compact(tree, currentDp.tree)(_.rootLabel.attach.display)
//          currentDp
//        })
//      }
//      case GoDownAction(node) => {
//        val tree = treeRW()
//        val currentDp = displayRW()
//
//        ModelUpdateEffect(displayRW.updated {
//          redisplay(tree, node)
//          rehierarchy(node)
//          compact(tree, node)(_.rootLabel.attach.nextDisplay)
//          currentDp
//        },
//          actionEffect(FlushDisplayAction(node), ModelerCss.delay)
//        )
//      }
//      case FlushDisplayAction(node) => {
//        val tree = treeRW()
//        val currentDp = displayRW()
//        currentDp.find { loc =>
//          loc.tree.rootLabel eq node.rootLabel
//        } map { newDp =>
//          ModelUpdate(displayRW.updated {
//            applyDisplay(tree)
//            newDp
//          })
//        } getOrElse(NoChange)
//      }
//      case FlushDisplayAction(node) => {
//        val tree = treeRW()
//        val currentDp = displayRW()
//        currentDp.find { loc =>
//          loc.tree.rootLabel eq node.rootLabel
//        } map { newDp =>
//          ModelUpdate(displayRW.updated {
//            applyDisplay(tree)
//            newDp
//          })
//        } getOrElse(NoChange)
//      }
//      case FoldAction(node) => {
//        if (node.subForest.nonEmpty) {
//          //TODO: direct update is not right
//          val fold = !node.rootLabel.attach.fold
//          node.rootLabel.attach.fold = fold
//          val tree = treeRW()
//          val display = displayRW().tree
//          if (fold)
//            ModelUpdateEffect(displayRW.updated {
//                redisplay(tree, display)
//                rehierarchy(display)
//                compact(tree, display)(_.rootLabel.attach.nextDisplay)
//                displayRW()
//              },
//              actionEffect(FlushDisplayAction(display), ModelerCss.delay)
//            )
//          else
//            ModelUpdateEffect( displayRW.updated {
//                redisplay(tree, display)
//                applyDisplay(tree)
//                displayRW()
//              },
//              actionEffect(FlushHierarchyAction())
//            )
//        } else NoChange
//      }
//      case EditAction(node) => {
//        if (editRW() ne node)
//          ModelUpdate(editRW.updated(node))
//        else NoChange
//      }
//      case ActiveAction(node) => {
//        if (activeRW() ne node)
//          ModelUpdate(activeRW.updated(node))
//        else NoChange
//      }
//      case CreateAction(node, name, meta) =>  {
//        val graphRW = modelRW.zoomTo(_.graph)
//        val display = displayRW()
//        display.find(loc => loc.tree.rootLabel eq node.rootLabel) map { loc =>
//          val newTN = treeExtractor.create(name, Stream.empty, meta)
//          newTN.rootLabel.attach.x = loc.tree.rootLabel.attach.x
//          newTN.rootLabel.attach.y = loc.tree.rootLabel.attach.y
//          loc.modifyTree { case Node(rootLabel, subForest) => Node(rootLabel, subForest :+ newTN) }
//        } map {
//          findParent(_, display.tree)
//        } map { newDisplay =>
//          val newRoot = newDisplay.root.tree
//          ModelUpdateEffect(graphRW.updated {
//            redisplay(newRoot, newDisplay.tree)
//            applyDisplay(newRoot)
//            graphRW().copy(root = newDisplay.root.tree, display = newDisplay)
//          },
//            actionEffect(FlushHierarchyAction())
//          )
//        } getOrElse(NoChange)
//      }
//      case RemoveFromAction(node, parent) => {
//        val display = displayRW()
//        val tree = treeRW()
//        ModelUpdateEffect( displayRW.updated {
//            node.rootLabel.attach.fold = true
//            redisplay(tree, display.tree)
//            node.rootLabel.attach.nextDisplay = false
//            rehierarchy(display.tree, _.rootLabel.attach.nextDisplay)
//            compact(tree, display.tree)(_.rootLabel.attach.nextDisplay)
//            display
//          },
//          actionEffect(FlushRemoveFromAction(node, parent), ModelerCss.delay)
//        )
//      }
//      case FlushRemoveFromAction(node, parent) => {
//        val graphRW = modelRW.zoomTo(_.graph)
//        val display = displayRW()
//        display.find(_.tree.rootLabel eq parent.rootLabel) map { loc =>
//          loc.modifyTree { case Node(rootLabel, subForest) =>
//            Node(rootLabel, subForest.filter(_.rootLabel ne node.rootLabel))
//          }
//        } map { pLoc =>
//          ModelUpdate( graphRW.updated {
//            val newDp = findParent(pLoc, display.tree)
//            graphRW().copy(root = newDp.root.tree, display = newDp)
//          })
//        } getOrElse(NoChange)
//      }
//      case ValueSetAction(node, ref, value) => {
//        val graphRW = modelRW.zoomTo(_.graph)
//        val display = displayRW()
//        display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//          loc.modifyTree { case Node(rootLabel, subForest) =>
//              Node(rootLabel.copy(value = value), subForest)
//          }
//        } map { newNodeLoc =>
//          ModelUpdate(graphRW.updated {
//            val newDp = findParent(newNodeLoc, display.tree)
//            graphRW().copy(root = newDp.root.tree, display = newDp)
//          })
//        } getOrElse(NoChange)
//      }
//      case ValueAddAction(node, ref, value) => {
//        val graphRW = modelRW.zoomTo(_.graph)
//        val display = displayRW()
//        display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//          loc.modifyTree { case Node(rootLabel, subForest) =>
//            val newV: Seq[JsValue] = rootLabel.value match {
//              case arr: JsArray => value +: arr.value
//              case _ => Seq.empty
//            }
//            Node(rootLabel.copy(value = JsArray(newV)), subForest)
//          }
//        } map { newNodeLoc =>
//          ModelUpdate(graphRW.updated {
//            val newDp = findParent(newNodeLoc, display.tree)
//            graphRW().copy(root = newDp.root.tree, display = newDp)
//          })
//        } getOrElse(NoChange)
//      }
//      case ValueDelAction(node, ref, value) => {
//        val graphRW = modelRW.zoomTo(_.graph)
//        val display = displayRW()
//        display.find(_.tree.rootLabel eq node.rootLabel) map { loc =>
//          loc.modifyTree { case Node(rootLabel, subForest) =>
//            val newV: Seq[JsValue] = rootLabel.value match {
//              case arr: JsArray => arr.value.filter(_ != value)
//              case _ => Seq.empty
//            }
//            Node(rootLabel.copy(value = JsArray(newV)), subForest)
//          }
//        } map { newNodeLoc =>
//          ModelUpdate(graphRW.updated {
//            val newDp = findParent(newNodeLoc, display.tree)
//            graphRW().copy(root = newDp.root.tree, display = newDp)
//          })
//        } getOrElse(NoChange)
//      }
//    }
//  }
//}
