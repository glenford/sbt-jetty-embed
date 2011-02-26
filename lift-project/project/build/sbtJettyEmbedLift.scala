import sbt._
import net.usersource.jettyembed.JettyEmbedWebProject

class sbtJettyEmbedLift(info: ProjectInfo) extends JettyEmbedWebProject(info) with IdeaProject {
  val liftVersion = "2.2"

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default"
  ) ++ super.libraryDependencies

}

