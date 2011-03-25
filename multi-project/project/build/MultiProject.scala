
import sbt._
import net.usersource.jettyembed._

class MultiProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject {
 lazy val commons = project("commons", "commons", new Commons(_))
 lazy val app = project("app", "app", new App(_), commons)

 class Commons(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
   // ...
 }

 class App(info: ProjectInfo) extends JettyEmbedWebProject(info, JETTY7) with IdeaProject {
   // ...
 }
}
