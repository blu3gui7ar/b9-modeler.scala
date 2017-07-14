package b9

import b9.short.TN

/**
  * Created by blu3gui7ar on 2017/7/15.
  */
object Idx {
  protected var idx: Int = 0

  def reindex(node: TN): TN = {
    idx = 0
    node.eachBefore { n: TN =>
      n.id = next()
    }
  }

  def next(): Int = {
    idx += 1
    idx
  }

}
