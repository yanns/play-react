package controllers

import java.io.{ByteArrayOutputStream, File}

import akka.actor.Props
import akka.util.Timeout
import com.typesafe.jse.Engine.JsExecutionResult
import com.typesafe.jse.{Engine, Node, JavaxEngine, Trireme}
import io.apigee.trireme.core._
import play.api.Play.current
import play.api._
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import ui.HtmlStream

import scala.concurrent.Promise
import scala.concurrent.duration._


object Application extends Controller {

  def index = Action {
    Ok(views.html.explanation())
  }

  def clientSide = Action {
    Ok(views.html.index())
  }

  private def initialData() = Comments.CommentRepository.getComments map { comments =>
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
      val htmlResult = Promise[Result]()
      script.execute().setListener(new ScriptStatusListener() {
        override def onComplete(script: NodeScript, status: ScriptStatus): Unit = {
          val result = stdout.toString("UTF-8")
          htmlResult.success(Ok(views.html.index(Html(result))))
        }
      })
      htmlResult.future
    }
  }

  def serverSideJavax = serverSideWithJsEngine(JavaxEngine.props())

  // with js-engine
  def serverSideTrireme = serverSideWithJsEngine(Trireme.props())

  // with node
  def serverSideNode = serverSideWithJsEngine(Node.props())

  private def serverSideWithJsEngine(jsEngine: Props) = Action.async { request =>
    import akka.pattern.ask

    val serverside = Play.getFile("public/javascripts/serverside.js")
    implicit val timeout = Timeout(5.seconds)
    val engine = Akka.system.actorOf(jsEngine, s"engine-${request.id}")

    for {
      data <- initialData()
      result <- (engine ? Engine.ExecuteJs(
        source = new File(serverside.toURI),
        args = List(data),
        timeout = timeout.duration
      )).mapTo[JsExecutionResult]
    } yield {
      Ok(views.html.index(Html(new String(result.output.toArray, "UTF-8"))))
    }
  }

  def serverSideStream = Action { request =>
    import akka.pattern.ask
    import ui.HtmlStreamImplicits._


    val serverside = Play.getFile("public/javascripts/serverside.js")
    implicit val timeout = Timeout(5.seconds)
    val engine = Akka.system.actorOf(Trireme.props(), s"engine-${request.id}")

    val prerendererHtml = for {
      data <- initialData()
      result <- (engine ? Engine.ExecuteJs(
        source = new File(serverside.toURI),
        args = List(data),
        timeout = timeout.duration
      )).mapTo[JsExecutionResult]
    } yield {
      Html(new String(result.output.toArray, "UTF-8"))
    }

    val prerendererHtmlStream = HtmlStream(prerendererHtml)
    Ok.chunked(views.stream.main(prerendererHtmlStream))
  }

}