package entities

import java.sql.Timestamp

/**
  * Created by blu3gui7ar on 2016/11/8.
  */
case class Model(id: Option[Long], name: String, version: String, metadata: String,
                 active: Boolean, edition: Long, updateTime: Option[Timestamp])
