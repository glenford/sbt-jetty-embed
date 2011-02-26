import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val jettyEmbeddedWar = "net.usersource" % "jetty-embed-plugin" % "0.3-SNAPSHOT"

  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.3-SNAPSHOT"
}

