
package net.usersource.jettyembed


import java.util.jar.{Manifest,Attributes}
import java.io.File
import _root_.sbt._
import _root_.sbt.FileUtilities._
import Attributes.Name.CLASS_PATH


sealed trait JettyVersion
case object JETTY6 extends JettyVersion
case object JETTY7 extends JettyVersion


class JettyEmbedWebProject( info: ProjectInfo, jettyEmbedVersion: JettyVersion ) extends DefaultWebProject(info) {

  def this(info: ProjectInfo) = this(info,JETTY6)

  val description = "Creates a war with embedded jetty"

  // TODO: need to change this - brittle ... not sure how ... yet
  val pluginJar = "project" / "plugins" / "lib_managed" / "scala_2.7.7" / "jetty-embed-plugin-0.3-SNAPSHOT.jar"


  //val jettyEmbedVersion: JettyVersion = JETTY6
  var warMainClass = ""
  var startupPath = ""

  val jetty6EmbedVersion = "6.1.22"
  val jetty6EmbedDependencies = "org.mortbay.jetty" % "jetty" % jetty6EmbedVersion % "jetty6Embed, compile, test"
  val jetty6EmbedSSLDependencies = "org.mortbay.jetty" % "jetty-sslengine" % jetty6EmbedVersion % "jetty6Embed, compile, test"
  val jetty6config = config("jetty6Embed")

  val jetty7EmbedVersion = "7.3.0.v20110203"
  val jetty7EmbedDependencies = "org.eclipse.jetty" % "jetty-webapp" % jetty7EmbedVersion % "jetty7Embed, compile, test"
  val jetty7config = config("jetty7Embed")

  val jettyEmbedConf = jettyEmbedVersion match {
    case JETTY6 => {
      warMainClass = "net.usersource.jettyembed.jetty6.Startup"
      startupPath = "net/usersource/jettyembed/jetty6/Startup.class"
      jetty6config
    }
    case JETTY7 => {
      warMainClass = "net.usersource.jettyembed.jetty7.Startup"
      startupPath = "net/usersource/jettyembed/jetty7/Startup.class"
      jetty7config
    }
  }

  def jettyEmbedClasspath = managedClasspath(jettyEmbedConf)
  
  val warClassPath = "WEB_INF/classes/ WEB_INF/lib/"
  val warManifestVersion = "1.0"

  override def packageOptions = List(new MainClass(warMainClass), new ManifestAttributes((CLASS_PATH,warClassPath)))

  override def libraryDependencies = super.libraryDependencies
  
  unzip(pluginJar, mainJavaSourcePath, "*.java", log)

  lazy val jettyEmbedPrepare = jettyEmbedPrepareAction

  protected def jettyEmbedPrepareAction = jettyEmbedPrepareTask describedAs "Copies Embeded Jetty Startup into your source tree"

  private def jettyEmbedPrepareTask = {
    task {
      unzip(pluginJar, mainJavaSourcePath, "*.java", log)
      None
    }
  }

  override protected def prepareWebappAction =
                prepareEmbeddedWebappTask(webappResources, temporaryWarPath, webappClasspath, mainDependencies.scalaJars) dependsOn(compile, copyResources)

  override protected def packageAction = {
    packageTask(descendents(temporaryWarPath ##, "*"), warPath, packageOptions) dependsOn(prepareWebappAction) describedAs "Creates a standalone war"
  }

  protected def prepareEmbeddedWebappTask(webappContents: PathFinder, warPath: => Path, classpath: PathFinder, extraJars: => Iterable[File]): Task =
                prepareEmbeddedWebappTask(webappContents, warPath, classpath, Path.lazyPathFinder(extraJars.map(Path.fromFile)))

  protected def prepareEmbeddedWebappTask(webappContents: PathFinder, warPath: => Path, classpath: PathFinder, extraJars: PathFinder): Task = {
    task {
      val webInfPath = warPath / "WEB-INF"
      val webLibDirectory = webInfPath / "lib"
      val classesTargetDirectory = webInfPath / "classes"
      val startupFile =Path.fromString(classesTargetDirectory, startupPath)

      val (libs, directories) = classpath.get.toList.partition(ClasspathUtilities.isArchive)
      val (embedLibs, embedDirectories) = jettyEmbedClasspath.get.toList.partition(ClasspathUtilities.isArchive)

      val classesAndResources = descendents(Path.lazyPathFinder(directories) ##, "*")

      if(log.atLevel(Level.Debug)) directories.foreach(d => log.debug(" Copying the contents of directory " + d + " to " + classesTargetDirectory))

      import FileUtilities.{copy, copyFile, copyFlat, copyFilesFlat, clean => fclean}

      embedLibs.foreach( embedLib => FileUtilities.unzip(embedLib,warPath,log) )

      (copy(webappContents.get, warPath, log).right flatMap { copiedWebapp =>
        copy(classesAndResources.get, classesTargetDirectory, log).right flatMap { copiedClasses =>
          copyFlat(libs, webLibDirectory, log).right flatMap { copiedLibs =>
            copyFilesFlat(extraJars.get.map(_.asFile), webLibDirectory, log).right flatMap {
              copiedExtraLibs => {
                fclean( warPath / "META-INF" / "MANIFEST.MF", log )
                try {
                  log.debug( "Using startup file [" + startupFile + "]" )
                  log.debug( "With path [" + startupPath + "]" )
                  copyFile( startupFile, Path.fromString(warPath, startupPath), log )
                }
                catch {
                  case e: Exception => log.info( "Unable to copy startup class due to : " + e.getMessage )
                }
                None.toLeft()
              }
            }
        }
      }}).left.toOption
    }
  }


}

