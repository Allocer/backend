package services

import models._
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OrderService {

  def addOrder(request: Request[JsValue]): OrderResource = {
    val name = (request.body \ "name").as[String]
    val description = (request.body \ "email").as[String]
    val price = (request.body \ "amount").as[BigDecimal]

    val product = Order(None, name, description, price)
    Orders.add(product)
    return createOrderResource(product)
  }

  def listAllOrders: Future[Seq[OrderResource]] = {
    Orders.listAll.map { postDataList =>
      postDataList.map(order => createOrderResource(order))
    }
  }

  private def createOrderResource(p: Order): OrderResource = {
    OrderResource(p.id, p.number, p.email, p.amount)
  }
}
