package models

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag

case class Category(id: Option[Long], title: String)

class CategoryTableDef(tag: Tag) extends Table[Category](tag, "category") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def title = column[String]("title")

  override def * = (id.?, title) <> (Category.tupled, Category.unapply)
}

