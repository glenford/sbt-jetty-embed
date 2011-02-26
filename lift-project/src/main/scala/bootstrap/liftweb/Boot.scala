package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._

import net.usersource.jettyembed.comet.CurrentTime


class Boot {

  private def setupComet = {
    LiftRules.cometCreation.append {
      case CometCreationInfo("CurrentTime",
                             name,
                             defaultXml,
                             attributes,
                             session) => new CurrentTime(session, Full("CurrentTime"),name, defaultXml, attributes)
    }

  }

  def boot {
    setupComet
  }
}
