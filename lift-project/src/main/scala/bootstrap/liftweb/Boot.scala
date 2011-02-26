package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._


class Boot {

  private def setupLiftSnippets = {
    LiftRules.addToPackages("net.usersource.jettyembed")
  }

  private def setupComet = {
    LiftRules.cometCreation.append {
      case CometCreationInfo("CurrentTime",
                             name,
                             defaultXml,
                             attributes,
                             session) => {
                               new BuildStatusRotate(session, Full("CurrentTime"),name, defaultXml, attributes)
      }
    }

  }

  def boot {
    setupLiftSnippets
    setupComet
  }
}
