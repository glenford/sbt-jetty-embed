
import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val jettyEmbeddedWar = "net.usersource" % "jetty-embed-plugin" % "0.2-SNAPSHOT"
}
