package controllers

import play.api.mvc.{SimpleResult, BodyParser, Action, Controller}
import play.api.libs.json.Json
import scala.concurrent.Promise
import play.api.libs.concurrent.Akka

object Comments extends Controller {

  case class Comment(author: String, text: String)
  object Comment {
    implicit val jsonFormat = Json.format[Comment]
  }

  object CommentRepository {
    var comments = List(
      Comment(author = "Pete Hunt", text = "This is one comment"),
      Comment(author = "Jordan Walke", text = "This is *another* comment")
    )
  }

  def list = Action.async {
    import play.api.Play.current
    import play.api.libs.concurrent.Execution.Implicits._
    import scala.concurrent.duration._

    // simulate slow backend
    val promise = Promise[SimpleResult]
    Akka.system.scheduler.scheduleOnce(500 milliseconds) {
      promise.success(Ok(Json.toJson(CommentRepository.comments)))
    }
    promise.future
  }

  def newPost = Action { request =>
    val result = for {
      json <- request.body.asJson
      comment <- json.asOpt[Comment]
    } yield {
      CommentRepository.comments = comment :: CommentRepository.comments
      Ok(Json.toJson(CommentRepository.comments))
    }
    result.getOrElse(BadRequest)
  }

}
