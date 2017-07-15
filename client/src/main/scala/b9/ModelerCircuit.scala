package b9

import b9.short.{RealNode, TN}
import diode.ActionResult.{ModelUpdate, ModelUpdateEffect, NoChange}
import diode._
import diode.react.ReactConnector
import facades.d3js.Hierarchy
import meta.{TreeExtractor, TreeNode}

import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.timers._

/**
  * Created by blu3gui7ar on 2017/6/24.
  */
object ModelerCircuit extends Circuit[State] with ReactConnector[State] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def actionEffect(action: Action, timeout: Int = 0) = Effect {
    val p = Promise[Action]()
    setTimeout(timeout) { // animation
      p.success(action)
    }
    p.future
  }


  protected def init(root: TreeNode): TN = {
    import js.JSConverters._
    val hroot = Hierarchy.hierarchy[TreeNode, TN](root, { n => n.children.toJSArray }: js.Function1[TreeNode, js.Array[TreeNode]])
    Layout.gather(Idx.reindex(hroot))
  }

  override protected def initialModel: State =
    meta.Sample.tree() match {
      case (meta, tree: TreeNode) => {
        val r = init(tree)
        State(GraphState(meta, r, r, r, r, r))
      }
    }

  override protected def actionHandler: ModelerCircuit.HandlerFunction = new ModelerActionHandler(ModelerCircuit.zoomTo(s => s.graph))

  class ModelerActionHandler[M](modelRW: ModelRW[M, GraphState]) extends ActionHandler(modelRW) {
    import Layout._
    override def handle = {
      case FlushDisplayAction(node) => {
        val tree = modelRW.zoom(_.tree).value
        ModelUpdate(modelRW.zoomTo(_.displayRoot).updated {
          applyDisplay(tree)
          node
        })
      }
      case FlushHierarchyAction(node) => {
        val tree = modelRW.zoom(_.tree).value
        val displayRoot = modelRW.zoom(_.displayRoot).value
        ModelUpdate(modelRW.zoomTo(_.displayRoot).updated {
          rehierarchy(tree, displayRoot)
          compact(tree, displayRoot)(_.display.getOrElse(true))
          node
        })
      }
      case GoDownAction(node) => {
        val tree = modelRW.zoom(_.tree).value
        ModelUpdateEffect(
          modelRW.zoomTo(_.displayRoot).updated {
            redisplay(tree, node)
            rehierarchy(tree, node)
            compact(tree, node)(_.nextDisplay.getOrElse(true))
            node
          },
          actionEffect(FlushDisplayAction(node), ModelerCss.delay)
        )
      }
      case GoUpAction(node) => {
        val tree = modelRW.zoom(_.tree).value
        ModelUpdateEffect(
          modelRW.zoomTo(_.displayRoot).updated {
            redisplay(tree, node)
            applyDisplay(tree)
            node
          },
          actionEffect(FlushHierarchyAction(node))
        )
      }
      case FoldAction(node) => {
        if (node.children.map(_.nonEmpty).getOrElse(false)) {
          //TODO: direct update is not right
          val fold = !node.fold.getOrElse(false)
          node.fold = fold
          val tree = modelRW.zoom(_.tree).value
          val displayRoot = modelRW.zoom(_.displayRoot).value
          if (fold)
            ModelUpdateEffect(
              modelRW.zoomTo(_.displayRoot).updated {
                redisplay(tree, displayRoot)
                rehierarchy(tree, displayRoot)
                compact(tree, displayRoot)(_.nextDisplay.getOrElse(true))
                displayRoot
              },
              actionEffect(FlushDisplayAction(displayRoot), ModelerCss.delay)
            )
          else
            ModelUpdateEffect(
              modelRW.zoomTo(_.displayRoot).updated {
                redisplay(tree, displayRoot)
                applyDisplay(tree)
              },
              actionEffect(FlushHierarchyAction(displayRoot))
            )
        } else NoChange
      }
      case EditAction(node) => {
        val editRW = modelRW.zoomTo(_.editingNode)
        if (editRW() ne node)
          ModelUpdate(editRW.updated(node))
        else NoChange
      }
      case ActiveAction(node) => {
        val activeRW = modelRW.zoomTo(_.activeNode)
        if (activeRW() ne node)
          ModelUpdate(activeRW.updated(node))
        else NoChange
      }
      case CreateAction(node, name, meta) =>  {
        //TODO: direct update is not right
        val newTN = TreeExtractor.create(name, Seq.empty, meta)
        val newChild = new RealNode(newTN, node, node.x, node.y, Idx.next()).asInstanceOf[TN]
        import js.JSConverters._
        if (node.children.isDefined) {
          node.children.get += newChild
        } else {
          node.children = Seq(newChild).toJSArray
        }
        val displayRoot = modelRW.zoom(_.displayRoot).value
        val tree = modelRW.zoom(_.tree).value
        ModelUpdateEffect(
          modelRW.zoomTo(_.displayRoot).updated {
            redisplay(tree, displayRoot)
            applyDisplay(tree)
          },
          actionEffect(FlushHierarchyAction(displayRoot))
        )
      }
      case RemoveFromAction(node, parent) => {
        val displayRoot = modelRW.zoom(_.displayRoot).value
        val tree = modelRW.zoom(_.tree).value
        ModelUpdateEffect(
          modelRW.zoomTo(_.displayRoot).updated {
            node.nextDisplay = false
            rehierarchy(tree, displayRoot, {_.nextDisplay.getOrElse(true)})
            compact(tree, displayRoot)(_.nextDisplay.getOrElse(true))
            displayRoot
          },
          actionEffect(FlushRemoveFromAction(node, parent), ModelerCss.delay)
        )
      }
      case FlushRemoveFromAction(node, parent) => {
        val displayRoot = modelRW.zoom(_.displayRoot).value
        ModelUpdate(
          modelRW.zoomTo(_.displayRoot).updated {
            if (parent.children.isDefined) {
              parent.children.get -= node
            }
            displayRoot
          }
        )
      }
    }
  }
}
