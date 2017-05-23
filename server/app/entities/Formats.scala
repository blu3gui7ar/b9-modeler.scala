package entities

import java.sql.Timestamp
import java.text.SimpleDateFormat
import play.api.libs.json._

/**
  * Created by blu3gui7ar on 2016/11/8.
  */
object Formats {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit object metadataFormat extends Format[Metadata] {
    def reads(json: JsValue) = {
      JsSuccess(Metadata(json.toString()))
    }
    def writes(m: Metadata) = Json.parse(m.meta)
  }
}
