package dao

import javax.inject._

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

/**
  * Created by blu3gui7ar on 2017/5/5.
  * This class is a pointer to an execution context configured to point to "database.dispatcher"
  * in the "application.conf" file.
  */
@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.dispatcher")
