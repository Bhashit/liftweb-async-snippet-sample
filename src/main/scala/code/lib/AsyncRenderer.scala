package code.lib.snippet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text}
import net.liftweb.http.js.JsCmd
import scala.util.{Failure, Success}
import net.liftweb.common.Logger
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmds._
import code.comet.CommandComet

object AsyncRenderer extends Logger {

  // The NodeSeq to use if the Future execution fails. If you want, you can allow the users to
  // pass in custom NodeSeqs for such cases.
  private val DefaultFailureContent = Text("Unfortunately, this operation could not be completed.")
  
  /**
   * This can be made made more configurable by adding more arguments. One additional
   * argument could be the NodeSeq for the placeholder that's shown while the Future
   * is not yet complete.
   * 
   * @param renderer the Future with CssSel transforms that will be used for rendering
   * your template when that Future completes.
   * @param existingCometName name of a comet of type code.comet.CommandComet if such
   * in instance has been already put on the page before. If not, no problem.
   * @param template the original NodeSeq that was passed to your snippet instance and that
   * will be transformed using the renderer that was passed to this method.   
   */
  def render(renderer: Future[CssSel],
      template: NodeSeq,
      existingCometName: Option[String] = None): NodeSeq = {
    implicit val session = S.session.openOrThrowException("An active session is required")
    
    val cometName = existingCometName getOrElse nextFuncName
    
    var transforms: Option[CssSel] = None
    
    val containerId = nextFuncName
    
    // Currently LiftSession#buildDeferredFunction doesn't take arguments, so
    // whatever we want to make available to the deferred function, we have to 
    // do it via closures, like we are doing with the 'transforms' var here.
    val renderFn = session.buildDeferredFunction { () =>
      val result = transforms match {
        case Some(selector) => selector(template)
        case None => DefaultFailureContent 
      }
      val cmd = SetHtml(containerId, result)
      CommandComet.sendJsCmd(cometName, cmd)  
    }
    
    renderer.onComplete { result =>
      result match {
        case Failure(ex) =>
          ex.printStackTrace()
          error("An error occurred during async rendering", ex)
        case Success(selector) => 
          transforms = Some(selector)
      }
      renderFn()
    }
    
    renderer.map { sel =>
      transforms = Some(sel)
    }.recover { case ex: Throwable =>
      ex.printStackTrace()
      error("An error occurred while getting messages", ex)
    }
   
    // If an actor wasn't setup previously, add a new one just for our
    // purposes.
    val actor = existingCometName match {
      case Some(_) => NodeSeq.Empty
      case None => CommandComet.render(cometName)
    }

    // This is the content that will be displayed before the Future is completed.
    // You can take an optional NodeSeq as an argument to this method if you want
    // to allow the users to customize this template.
    val placeholder =
      <div id={containerId}>
	      <img alt="progress indicator" src="/assets/images/ajax-loader.gif" />
	    </div>
    
    placeholder ++ actor
  }
}
