package b9

import b9.model.Model
import diode.Action

/**
  * Created by blu3gui7ar on 2017/5/24.
  */
case class AddNode(path: Seq[String], node: Model) extends Action
case class ActivateNode(path: Seq[String]) extends Action
case class DeleteNode(path: Seq[String]) extends Action
case class EditNode(path: Seq[String], node: Model) extends Action
case class FoldNode(path: Seq[String]) extends Action
