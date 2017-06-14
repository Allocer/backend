package models

import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.{JsValue, Json, Writes}
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class Order(id: Option[Long], number: String, email: String, amount: BigDecimal)

class OrderTableDef(tag: Tag) extends Table[Order](tag, "product") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def number = column[String]("number")

  def email = column[String]("email")

  def amount = column[BigDecimal]("amount")

  override def * =
    (id.?, number, email, amount) <> (Order.tupled, Order.unapply)
}

case class OrderResource(id: Option[Long], number: String, email: String, amount: BigDecimal)

object OrderResource {
  implicit val implicitWrites = new Writes[OrderResource] {
    def writes(product: OrderResource): JsValue = {
      Json.obj(
        "id" -> product.id,
        "number" -> product.number,
        "email" -> product.email,
        "amount" -> product.amount
      )
    }
  }
}

object Orders {

  val orders = TableQuery[OrderTableDef]
  private def db: Database = Database.forDataSource(DB.getDataSource())

  def add(order: Order): Order = {
    try {
      Await.result(db.run(DBIO.seq(
        MTable.getTables map (tables => {
          if (!tables.exists(_.name.name == orders.baseTableRow.tableName))
            orders.schema.create
        }),
        orders += (order),
        orders.result.map(println))), Duration.Inf)
      return order
    } finally db.close
  }

  def listAll: Future[Seq[Order]] = {
    try db.run(orders.result)
    finally db.close()
  }
}