package controllers

import models.ProductResource
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, Controller, Result}
import services.ProductService

import scala.concurrent.ExecutionContext.Implicits.global

object ProductController extends Controller {

  def index: Action[AnyContent] = {
    Action { implicit request =>
      val r: Result = Ok("GET Products")
      r
    }
  }

  def create() = Action(parse.json) { request =>
    def product: ProductResource = ProductService.addProduct(request)

    Created("Product " + product.name + " created.")
  }

  def list: Action[AnyContent] = {
    Action.async { implicit request =>
      ProductService.listAllProducts
        .map(x => x.map(y => Json.toJson(y)).foldLeft(JsArray())({ case (acc, json) => acc :+ json }))
        .map(x => Ok(x).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*", ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept"))
    }
  }

  def findById(id: Long): Action[AnyContent] = {
    Action.async { implicit request =>
      ProductService.findById(id).map { product =>
        Ok(Json.toJson(product))
      }
    }
  }

  def findByCategory(category: String): Action[AnyContent] = {
    Action.async { implicit request =>
      ProductService.findByCategory(category.toLowerCase()).map(x => x.map(y => Json.toJson(y)).foldLeft(JsArray())({ case (acc, json) => acc :+ json }))
        .map(x => Ok(x).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*", ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept"))
    }
  }

  def update(id: String) = TODO

  def delete(id: String) = TODO

}
