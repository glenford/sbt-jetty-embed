
import sbt._
import net.usersource.jettyembed.JettyEmbedWebProject

class BasicProject(info :ProjectInfo) extends JettyEmbedWebProject(info) with IdeaProject
