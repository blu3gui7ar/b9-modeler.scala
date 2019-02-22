package b9.components.editor

object WidgetRegistry {
  protected var registry = Map[String, Widget](
    EmptyWidget.name -> EmptyWidget,
    TextWidget.name -> TextWidget,
    CheckboxWidget.name -> CheckboxWidget,
    RadioWidget.name -> RadioWidget,
    SelectWidget.name -> SelectWidget,
    MuiRadioWidget.name -> MuiRadioWidget,
    SimpleContainerWidget.name -> SimpleContainerWidget,
    ExpansionPanelWidget.name -> ExpansionPanelWidget,
    NotFoundWidget.name -> NotFoundWidget,
    NotMatchWidget.name -> NotMatchWidget,
    NotValidWidget.name -> NotValidWidget,
  )

  def register(name: String, widget: Widget): Unit = {
    registry += name -> widget
  }

  def unregister(name: String): Option[Widget] = {
    val w = registry.get(name)
    registry -= name
    w
  }

  def apply(name: String, container: Boolean): Option[Widget] = registry.get(name) match {
    case None => registry.get(NotFoundWidget.name)
    case Some(w) => if(w.container == container) Some(w) else registry.get(NotMatchWidget.name)
  }
}
