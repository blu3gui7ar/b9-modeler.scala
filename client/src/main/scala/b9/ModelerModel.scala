package b9

import b9.short.{TM, TMLoc, TN}
import diode.Action
import meta.MetaAst
import meta.MetaAst.AttrDef
import upickle.Js

/**
  * Created by blu3gui7ar on 2017/5/23.
  */
case class GraphState(
                       root: TM,
                       display: TMLoc,
                       active: TM,
                       editing: TM,
                     )

case class ModelerState(
                         meta: MetaAst.Root,
                         editor: TM,
                         graph: GraphState,
                       )


case class FlushDisplayAction(node: TM) extends Action
case class FlushHierarchyAction(nodeLoc: TMLoc) extends Action
case class GoUpAction(node: TM) extends Action
case class GoDownAction(node: TM) extends Action
case class RemoveFromAction(node: TM, parent: TM) extends Action
case class FlushRemoveFromAction(node: TM, parent: TM) extends Action
case class EditAction(node: TM) extends Action
case class FoldAction(node: TM) extends Action
case class ActiveAction(node: TM) extends Action
case class CreateAction(node: TM, name: String, meta: AttrDef) extends Action

case class ValueSetAction(node: TM, ref: String, value: Js.Value) extends Action
case class ValueAddAction(node: TM, ref: String, value: Js.Value) extends Action
case class ValueDelAction(node: TM, ref: String, value: Js.Value) extends Action
