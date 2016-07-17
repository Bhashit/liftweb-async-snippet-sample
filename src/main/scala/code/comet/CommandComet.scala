package code.comet

import net.liftweb.http.{CometListener, CometActor}
import scala.xml.NodeSeq
import net.liftweb.util.Helpers
import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.RequestVar
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.S
import net.liftweb.http.LiftSession
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

/**
 * A comet actor for sending generic JsCmds messages to web pages. 
 */
class CommandComet extends CometActor {
  
  def render = NodeSeq.Empty

  override def mediumPriority = {
    case msg: JsCmd => partialUpdate(msg)
  }
}

object CommandComet {
  
  /**
   * You can get the name of the comet actor for the current request
   * from here (if it has been setup).
   */
  object currentPageCometNameReqVar extends RequestVar[Option[String]](None)

  def render(cometName: String) = {
    <lift:comet type="CommandComet" name={cometName} />
  }
  
  def sendJsCmd(cmd: JsCmd): Unit = {
    S.session match {
      case Full(s) => sendJsCmd(cmd, s)
      case other =>
        throw new AssertionError("This must be called from within the context of a session")
    }
  }
  
  private def sendJsCmd(cmd: JsCmd, s: LiftSession): Unit = {
    currentPageCometNameReqVar.get match {
      case Some(cometName) => sendJsCmd(cometName, cmd)(s)
      case _ =>
        throw new AssertionError("The comet must have been setup before calling this method")
    }
  }
  
  def sendJsCmd(actorName: String, cmd: JsCmd)(implicit s: LiftSession): Unit = {
    S.initIfUninitted(s) {
      s.sendCometActorMessage("CommandComet", Full(actorName), cmd)
    }
  }
}