package b9

import org.scalajs.dom
import shared.SharedMessages

import scala.scalajs.js

/**
  * Created by blu3gui7ar on 2016/12/30.
  */
object MainClass extends js.JSApp {
  def main(): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.itWorks
  }
}
