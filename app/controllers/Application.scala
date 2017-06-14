package controllers

import play.api.mvc._

object Application extends Controller {

  def getName = Action {
    Ok("Your new application is ready.")
  }

}