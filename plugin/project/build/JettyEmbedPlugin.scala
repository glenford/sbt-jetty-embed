
import sbt._

class JettyEmbedPluginProject(info: ProjectInfo) extends PluginProject(info) with IdeaProject {
  val jettyVersion = "6.1.22"
  val jettyDependencies = "org.mortbay.jetty" % "jetty" % jettyVersion % "compile->default"
  val jettyExtras = "org.mortbay.jetty" % "jetty-sslengine" % jettyVersion % "compile->default"

  def extraResources = mainJavaSourcePath / "net" / "usersource" / "jettyembed" / "Startup.java"
  override def mainResources = super.mainResources +++ extraResources
}

