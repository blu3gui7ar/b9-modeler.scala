package dao

import java.sql.Timestamp

import javax.inject._
import entities.{Metadata, Model}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

/**
  * Created by blu3gui7ar on 2016/10/19.
  */
/*@NamedDatabase("default")*/
@Singleton
class ModelRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                         (implicit ec: DatabaseExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def all(): Future[Seq[Model]] = db.run(models.result)

  def byUname(name: String, version: String) =
    (m: ModelsTable) => m.name === name && m.version === version

  def get(name: String, version: String): Future[Option[Model]] =
    db.run(models.filter(byUname(name, version)).result.headOption)

//  def get2(name: String, version: String): Future[Option[Model]] =
//    db.run((for ( m <- models if m.name === name && m.version === version) yield m).result.headOption)

  def insert(model: Model): Future[Model] =
    db.run(
      (models returning models.map(_.id)
        into ((m, nid) => m.copy(id = nid))
      ) += model)

  def delete(name: String, version: String): Future[Int] =
    db.run(models.filter(byUname(name, version)).delete)

  def update(model: Model): Future[Int] =
    db.run(models.filter(byUname(model.name, model.version)).update(model))


  private val models = TableQuery[ModelsTable]

//  implicit val metaColumnType = MappedColumnType.base[Metadata, String](
//    {meta => meta.value}, {s => Metadata(s)}
//  )

  protected class ModelsTable(tag: Tag) extends Table[Model](tag, "MODEL") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def version = column[String]("version")
    def metadata = column[String]("metadata")
    def active = column[Boolean]("active")
    def edition = column[Long]("edition")
    def updateTime = column[Option[Timestamp]]("updateTime")

    def pk = index("identifier", (name, version), unique = true)

    override def * : ProvenShape[Model] = (id, name, version, metadata, active, edition, updateTime) <> (Model.tupled, Model.unapply)
  }
}
