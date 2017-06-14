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

case class Product(id: Option[Long], name: String, description: String, price: BigDecimal, amount: Int, category: String)

class ProductTableDef(tag: Tag) extends Table[Product](tag, "product") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[String]("description")

  def price = column[BigDecimal]("price")

  def amount = column[Int]("amount")

  def category = column[String]("category")

  override def * =
    (id.?, name, description, price, amount, category) <> (Product.tupled, Product.unapply)
}

case class ProductResource(id: Option[Long], name: String, description: String, price: BigDecimal, amount: Int, category: String)

object ProductResource {
  implicit val implicitWrites = new Writes[ProductResource] {
    def writes(product: ProductResource): JsValue = {
      Json.obj(
        "id" -> product.id,
        "name" -> product.name,
        "description" -> product.description,
        "price" -> product.price,
        "amount" -> product.amount,
        "category" -> product.category
      )
    }
  }
}

object Products {

  val products = TableQuery[ProductTableDef]

  private def db: Database = Database.forDataSource(DB.getDataSource())

  def add(product: Product): Product = {
    try {
      Await.result(db.run(DBIO.seq(
        MTable.getTables map (tables => {
          if (!tables.exists(_.name.name == products.baseTableRow.tableName))
            products.schema.create
        }),
        products += (product),
        products.result.map(println))), Duration.Inf)
      return product
    } finally db.close
  }

  def delete(id: Long): Future[Int] = {
    try db.run(products.filter(_.id === id).delete)
    finally db.close()
  }

  def findById(id: Long): Future[Option[Product]] = {
    try db.run(products.filter(_.id === id).result.headOption)
    finally db.close()
  }

  def findByCategory(category: String): Future[Seq[Product]] = {
    try db.run(products.filter(_.category === category).result)
    finally db.close()
  }

  def listAll: Future[Seq[Product]] = {
    try db.run(products.result)
    finally db.close()
  }
}