package controllers

import javax.inject.Inject

import play.api.mvc._
import views.Pages

class Modeler @Inject()(components: ControllerComponents) extends AbstractController(components) {

  def index = Action {
    Ok(Pages.common("Modeler", None, true)).as("text/html")
  }

}
