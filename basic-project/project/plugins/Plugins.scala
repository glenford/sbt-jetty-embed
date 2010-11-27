
import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val standAloneWar = "net.usersource" % "jetty-embed-plugin" % "0.1"
  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.1-SNAPSHOT"
}
