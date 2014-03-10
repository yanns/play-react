package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import java.io.{ByteArrayOutputStream, File}
import com.typesafe.jse.{Engine, Trireme}
import play.api.libs.concurrent.Akka
import com.typesafe.jse.Engine.JsExecutionResult
import scala.concurrent.{Promise, Future}
import akka.util.Timeout
import io.apigee.trireme.core._
import play.api.libs.json.Json
import play.api.templates.Html
import com.typesafe.jse.Engine.JsExecutionResult

object Application extends Controller {

  def index = Action {
    Ok(views.html.explanation())
  }

  def clientSide = Action {
    Ok(views.html.index())
  }

  private def initialData = Json.stringify(Json.toJson(Comments.CommentRepository.comments))

  // with trireme directly
  def serverSide = Action.async {
    val result = Play.resource("public/javascripts/serverside.js") map { serverside =>
      val stdout = new ByteArrayOutputStream()
      val env = new NodeEnvironment()
      val sandbox = new Sandbox()
      sandbox.setStdout(stdout)
      val script = env.createScript("serverside.js", new File(serverside.toURI), Array(initialData))
      script.setSandbox(sandbox)
      val htmlResult = Promise[SimpleResult]
      script.execute().setListener(new ScriptStatusListener() {
        override def onComplete(script: NodeScript, status: ScriptStatus): Unit = {
          val result = stdout.toString("UTF-8")
          htmlResult.success(Ok(views.html.index(Html(result))))
        }
      })
      htmlResult.future
    }
    result getOrElse Future.successful(NotFound)
  }


  // with js-engin
  def serverSide2 = Action.async { request =>
    import akka.pattern.ask
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import scala.concurrent.duration._

    val result = Play.resource("public/javascripts/serverside.js") map { serverside =>
      implicit val timeout = Timeout(5.seconds)
      val engine = Akka.system.actorOf(Trireme.props(), s"engine-${request.id}")

      for {
        result <- (engine ? Engine.ExecuteJs(new File(serverside.toURI), List(initialData))).mapTo[JsExecutionResult]
      } yield {
        Ok(views.html.index(Html(new String(result.output.toArray, "UTF-8"))))
      }
    }
    result getOrElse Future.successful(NotFound)
  }

}