package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import scala.concurrent.{Future, Promise}
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._

object Comments extends Controller {

  case class Comment(author: String, text: String)
  object Comment {
    implicit val jsonFormat = Json.format[Comment]
  }

  object CommentRepository {
    private var comments = List(
      Comment(author = "Pete Hunt", text = "This is one comment"),
      Comment(author = "Jordan Walke", text = "This is *another* comment")
    )

    def addComment(comment: Comment) {
      comments = comment :: comments
    }

    def getComments = {
      import play.api.Play.current
      import scala.concurrent.duration._

      // simulate slow backend
      val promise = Promise[Seq[Comment]]()
      Akka.system.scheduler.scheduleOnce(500 milliseconds) {
        promise.success(comments)
      }
      promise.future
    }
  }

  def list = Action.async {
    CommentRepository.getComments map { comments =>
      Ok(Json.toJson(comments))
    }
  }

  def newPost = Action.async { request =>
    val result = for {
      json <- request.body.asJson
      comment <- json.asOpt[Comment]
    } yield {
      CommentRepository.addComment(comment)
      CommentRepository.getComments map { comments =>
        Ok(Json.toJson(comments))
      }
    }
    result getOrElse Future.successful(BadRequest)
  }

}
