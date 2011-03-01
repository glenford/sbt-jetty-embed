
//
// Copyright 2011, Glen Ford
//
// Apache 2.0 License
// Please see README.md, LICENSE and NOTICE
//

import sbt._

class JettyEmbedPluginProject(info: ProjectInfo) extends PluginProject(info) with IdeaProject {
  val publishTo = Resolver.file("sbt-jetty-embed-repo", new java.io.File("repo-out")) 

  val jetty6Version = "6.1.22"
  val jetty6Dependencies = "org.mortbay.jetty" % "jetty" % jetty6Version % "compile->default"
  val jetty6Extras = "org.mortbay.jetty" % "jetty-sslengine" % jetty6Version % "compile->default"

  val jetty7Version = "7.3.0.v20110203"
  val jetty7Dependencies = "org.eclipse.jetty" % "jetty-webapp" % jetty7Version % "compile->default"

  def jetty6Resources = mainJavaSourcePath / "net" / "usersource" / "jettyembed" / "jetty6" /  "Startup.java"
  def jetty7Resources = mainJavaSourcePath / "net" / "usersource" / "jettyembed" / "jetty7" / "Startup.java"
  override def mainResources = super.mainResources +++ jetty6Resources +++ jetty7Resources
}

