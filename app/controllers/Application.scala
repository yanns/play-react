package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import java.io.{ByteArrayOutputStream, File}
import com.typesafe.jse.{Node, Engine, Trireme}
import play.api.libs.concurrent.Akka
import scala.concurrent.{Promise, Future}
import akka.util.Timeout
import io.apigee.trireme.core._
import play.api.libs.json.Json
import play.api.templates.Html
import com.typesafe.jse.Engine.JsExecutionResult
import play.api.libs.concurrent.Execution.Implicits._
import ui.HtmlStream
import akka.actor.Props


object Application extends Controller {

  def index = Action {
    Ok(views.html.explanation())
  }

  def clientSide = Action {
    Ok(views.html.index())
  }

  private def initialData = Comments.CommentRepository.getComments map { comments =>
    Json.stringify(Json.toJson(comments))
  }

  // with trireme directly
  def serverSide = Action.async {
    initialData flatMap { data =>
      val serverside = Play.getFile("public/javascripts/serverside.js")
      val stdout = new ByteArrayOutputStream()
      val env = new NodeEnvironment()
      val sandbox = new Sandbox()
      sandbox.setStdout(stdout)
      val script = env.createScript("serverside.js", new File(serverside.toURI), Array(data))
      script.setSandbox(sandbox)
      val htmlResult = Promise[SimpleResult]()
      script.execute().setListener(new ScriptStatusListener() {
        override def onComplete(script: NodeScript, status: ScriptStatus): Unit = {
          val result = stdout.toString("UTF-8")
          htmlResult.success(Ok(views.html.index(Html(result))))
        }
      })
      htmlResult.future
    }
  }


  // with js-engine
  def serverSide2 = serverSideWithJsEngine(Trireme.props())

  // with node
  def serverSideNode = serverSideWithJsEngine(Node.props())

  private def serverSideWithJsEngine(jsEngine: Props) = Action.async { request =>
    import akka.pattern.ask
    import scala.concurrent.duration._

    val serverside = Play.getFile("public/javascripts/serverside.js")
    implicit val timeout = Timeout(5.seconds)
    val engine = Akka.system.actorOf(jsEngine, s"engine-${request.id}")

    for {
      data <- initialData
      result <- (engine ? Engine.ExecuteJs(new File(serverside.toURI), List(data))).mapTo[JsExecutionResult]
    } yield {
      Ok(views.html.index(Html(new String(result.output.toArray, "UTF-8"))))
    }
  }

  def serverSideStream = Action { request =>
    import akka.pattern.ask
    import scala.concurrent.duration._
    import ui.HtmlStreamImplicits._

    val serverside = Play.getFile("public/javascripts/serverside.js")
    implicit val timeout = Timeout(5.seconds)
    val engine = Akka.system.actorOf(Trireme.props(), s"engine-${request.id}")

    val prerendererHtml = for {
      data <- initialData
      result <- (engine ? Engine.ExecuteJs(new File(serverside.toURI), List(data))).mapTo[JsExecutionResult]
    } yield {
      Html(new String(result.output.toArray, "UTF-8"))
    }

    val prerendererHtmlStream = HtmlStream(prerendererHtml)
    Ok.chunked(views.stream.main(prerendererHtmlStream))
  }

}