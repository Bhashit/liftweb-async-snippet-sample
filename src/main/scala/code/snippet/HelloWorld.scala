package code 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import code.lib._
import Helpers._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import code.lib.snippet.AsyncRenderer

class HelloWorld {
  
  def render(template: NodeSeq): NodeSeq = {
    AsyncRenderer.render(getAndRenderDetails, template)
  }
  
  // An example of how you might handle the errors.
  def renderWithError(template: NodeSeq): NodeSeq = {
    AsyncRenderer.render(getAndRenderDetailsWithError, template)
  }
  
  private def getAndRenderDetails: Future[CssSel] = {
    getDetails.map { renderDetails }
  }
  
  private def getDetails = Future {
    randomSleep()
    ("Jason Bourne", "jasonb@example.com") :: 
      ("Harmione Granger", "hgrager@example.com") ::
      ("Sheldon Cooper", "coopersheldon@example.com") :: 
      ("Robert Langdon", "langdonr@example.com") :: Nil
  }
  
  private def renderDetails(details: List[(String, String)]) = {
    ".details" #> details.map { case (name, email) =>
      ".name *" #> name &
      ".email *" #> email
    }
  }
  
  private def getAndRenderDetailsWithError: Future[CssSel] = {
    getDetailsWithError.map { renderDetails }.recover {
      case t: Throwable =>
        // Do whatever you need to do when your async operation fails
        "table" #> <p>Houston, we have a problem.</p>
    }
  }
  
  // Just simulates a Future that might fail with an exception.
  private def getDetailsWithError: Future[List[(String, String)]] = Future {
    randomSleep(6)
    throw new RuntimeException
  }
  
  private def randomSleep(min: Int = 2) = {
    val r = new Random
    Thread.sleep(1000 * (r.nextInt(3) + min))  
  }
  
}

