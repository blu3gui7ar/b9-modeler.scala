package b9.components.editor

import b9.Dispatcher
import b9.TreeOps._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import monocle.Lens
import monocle.std.tree._


object Editor {

  def apply(tree: TTN, lens: Option[TLens], dispatcher: Dispatcher[TTN]) = component(Props(tree, lens, dispatcher))

  def at(idx: Int): Lens[Stream[TTN], TTN] = {
    Lens[Stream[TTN], TTN](_.apply(idx))(node => _.zipWithIndex map { case (on, i) => if (idx == i) node else on})
  }

  def at(node: TTN): Lens[Stream[TTN], TTN] = Lens[Stream[TTN], TTN]
      { stream => stream.find(_ eq node).getOrElse(stream.head) }
      { newNode => stream => stream.map { n => if (n eq node) newNode else n } }

  val asSet = Lens[Stream[TTN], Set[TTN]](_.toSet)(set => _ => set.toStream)

  case class Props(tree: TTN, lens: Option[TLens], dispatcher: Dispatcher[TTN])

  class Backend($: BackendScope[Props, Unit]) {
    def render(p: Props): VdomTag = {
      val label = p.tree.rootLabel
      val sub = p.tree.subForest

      val llens = p.lens.map( _ composeLens rootLabel) getOrElse rootLabel

      <.div(
        <.span(label.name), //TODO use input to modify map key
        <.span(" : "),

        label.meta.widget flatMap { widget =>
          WidgetRegistry(widget.name)
        } map {
          _.render(label, llens, p.dispatcher)
        } getOrElse {
          sub.map { node =>
            val slens = subForest composeLens at(node)
            val clens = p.lens.map( _ composeLens slens) getOrElse slens
            Editor(node, Some(clens), p.dispatcher)
          } toTagMod
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("Editor")
    .renderBackend[Backend]
    .build

}
