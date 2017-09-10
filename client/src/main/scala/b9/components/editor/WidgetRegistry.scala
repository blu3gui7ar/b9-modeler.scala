package b9.components.editor

object WidgetRegistry {
  protected var registry = Map[String, Widget](
    EmptyWidget.name -> EmptyWidget,
    TextWidget.name -> TextWidget
  )

  def register(name: String, widget: Widget): Unit = {
    registry += name -> widget
  }

  def unregister(name: String): Option[Widget] = {
    val w = registry.get(name)
    registry -= name
    w
  }

  def apply(name: String): Option[Widget] = registry.get(name)
}
