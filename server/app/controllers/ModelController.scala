package controllers

import javax.inject.Inject

import dao.ModelRepo
import entities.{Metadata, Model}
import java.sql.Timestamp
import play.api.libs.json._
import play.api.mvc._
import shared.SharedMessages

import scala.concurrent.{ExecutionContext, Future}

class ModelController @Inject()(components: ControllerComponents,
                                protected val modelDAO: ModelRepo)
                               (implicit ec: ExecutionContext)
  extends AbstractController(components) {

  implicit val timestampFormat: Format[Timestamp] = Format(
    Reads.of[Long] map (new Timestamp(_)), Writes { (ts: Timestamp) => JsNumber(ts.getTime)}
  )
//  implicit val metaFormat: Format[Metadata] = Format(
//    Reads.of[String] map (Metadata(_)), Writes{ (meta: Metadata) => Json.parse(meta.value) }
//  )
  implicit val modelFormat = Json.format[Model]

//  def index = Action {
//    Ok(views.html.index(SharedMessages.itWorks))
//  }

  def list = Action.async {
    val rs = modelDAO.all()
    rs.map(models => Ok(Json.toJson(models)))
  }

  def get(name: String, ver: String) = Action.async {
    val rs = modelDAO.get(name, ver)
    rs.map(m => Ok(Json.toJson(m)))
  }

  def create = Action.async(parse.json) { request =>
    val modelRs = request.body.validate[Model]
    modelRs.fold(
      errors => {
        Future(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      model => {
        val rs = modelDAO.insert(model)
        rs.map(id => Ok(Json.obj("status" -> "OK", "message" -> id)))
      }
    )
  }

  def delete(name: String, ver: String) = Action.async {
    val rs = modelDAO.delete(name, ver)
    rs.map(succ => Ok(Json.obj("status" -> "OK", "message" -> succ)))
  }

  def update(name: String, ver: String) = Action.async(parse.json) { request =>
    val modelRs = request.body.validate[Model]
    modelRs.fold(
      errors => {
        Future(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      model => {
        val rs = modelDAO.update(model)
        rs.map(succ => Ok(Json.obj("status" -> "OK", "message" -> succ)))
      }
    )
  }
}

