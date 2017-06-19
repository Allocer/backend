package controllers

import models.OrderResource
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import services.OrderService

import scala.concurrent.ExecutionContext.Implicits.global

object OrderController extends Controller {

  def create() = Action(parse.json) { request =>
    def product: OrderResource = OrderService.addOrder(request)

    Created("Order for " + product.email + " created.")
  }

  def list: Action[AnyContent] = {
    Action.async { implicit request =>
      OrderService.listAllOrders
        .map(x => x.map(y => Json.toJson(y)).foldLeft(JsArray())({ case (acc, json) => acc :+ json }))
        .map(x => Ok(x))
    }
  }
}
