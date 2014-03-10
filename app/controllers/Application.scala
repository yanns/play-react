package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import java.io.{ByteArrayOutputStream, File}
import com.typesafe.jse.{Engine, Trireme}
import play.api.libs.concurrent.Akka
import com.typesafe.jse.Engine.JsExecutionResult
import scala.concurrent.Future
import akka.util.Timeout
import io.apigee.trireme.core.{Sandbox, NodeEnvironment}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  // with trireme directly
  def indexTrireme = Action {
    val result = Play.resource("public/javascripts/serverside.js") map { serverside =>
      val stdout = new ByteArrayOutputStream()
      val env = new NodeEnvironment()
      val sandbox = new Sandbox()
      sandbox.setStdout(stdout)
      val script = env.createScript("serverside.js", new File(serverside.toURI), null)
      script.setSandbox(sandbox)
      val status = script.execute().get()
      Ok(stdout.toString("UTF-8"))
    }
    result getOrElse NotFound
  }


  // with js-engin
  def indexJsEngine = Action.async { request =>
    import akka.pattern.ask
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import scala.concurrent.duration._

    val result = Play.resource("public/javascripts/serverside.js") map { serverside =>
      implicit val timeout = Timeout(5.seconds)
      val engine = Akka.system.actorOf(Trireme.props(), s"engine-${request.id}")

      for {
        result <- (engine ? Engine.ExecuteJs(new File(serverside.toURI), Nil)).mapTo[JsExecutionResult]
      } yield {
        Ok(new String(result.output.toArray, "UTF-8"))
      }
    }
    result getOrElse Future.successful(NotFound)
  }

}