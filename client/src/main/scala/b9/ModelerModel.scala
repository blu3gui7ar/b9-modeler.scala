package b9

import b9.short.TN
import diode.Action
import meta.MetaAst
import meta.MetaAst.AttrDef
import upickle.Js

/**
  * Created by blu3gui7ar on 2017/5/23.
  */
case class GraphState(
                       meta: MetaAst.Root,
                       tree: TN,
                       displayRoot: TN,
                       relocateSource: TN,
                       activeNode: TN,
                       editingNode: TN,
                       json: Js.Value
                     )
case class State(graph: GraphState)


case class FlushDisplayAction(node: TN) extends Action
case class FlushHierarchyAction(node: TN) extends Action
case class GoUpAction(node: TN) extends Action
case class GoDownAction(node: TN) extends Action
case class RemoveFromAction(node: TN, parent: TN) extends Action
case class FlushRemoveFromAction(node: TN, parent: TN) extends Action
case class EditAction(node: TN) extends Action
case class FoldAction(node: TN) extends Action
case class ActiveAction(node: TN) extends Action
case class CreateAction(node: TN, name: String, meta: AttrDef) extends Action
