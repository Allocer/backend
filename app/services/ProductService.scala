package services

import models.{Product, ProductResource, Products}
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ProductService {

  def addProduct(request: Request[JsValue]): ProductResource = {
    val name = (request.body \ "name").as[String]
    val description = (request.body \ "description").as[String]
    val price = (request.body \ "price").as[BigDecimal]
    val amount = (request.body \ "amount").as[Int]
    val category = (request.body \ "category").as[String]

    val product = Product(None, name, description, price, amount, category)
    return createProductResource(Products.add(product))
  }

  def deleteProduct(id: Long): Future[Int] = {
    Products.delete(id)
  }

  def findById(id: Long): Future[Option[ProductResource]] = {
    Products.findById(id).map { maybeProduct =>
      maybeProduct.map { product =>
        createProductResource(product)
      }
    }
  }

  def findByCategory(category: String): Future[Seq[ProductResource]] = {
    Products.findByCategory(category).map { maybeProduct =>
      maybeProduct.map { product =>
        createProductResource(product)
      }
    }
  }

  def listAllProducts: Future[Seq[ProductResource]] = {
    Products.listAll.map { postDataList =>
      postDataList.map(postData => createProductResource(postData))
    }
  }

  private def createProductResource(p: Product): ProductResource = {
    ProductResource(p.id, p.name, p.description, p.price, p.amount, p.category)
  }
}
