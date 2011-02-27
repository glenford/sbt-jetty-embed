
import sbt._
import net.usersource.jettyembed._

class sbtJettyEmbedLift(info: ProjectInfo) extends JettyEmbedWebProject(info,JETTY7) with IdeaProject {
  val liftVersion = "2.2"

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
  ) ++ super.libraryDependencies

}

