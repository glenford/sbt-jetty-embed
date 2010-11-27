
package net.usersource.jettyembed


import java.util.jar.{Manifest,Attributes}
import java.io.File
import _root_.sbt._
import _root_.sbt.FileUtilities._
import Attributes.Name.CLASS_PATH


class JettyEmbedWebProject( info: ProjectInfo ) extends DefaultWebProject(info) {

  val description = "Creates a war with embedded jetty"

  val pluginJar = "project" / "plugins" / "lib_managed" / "scala_2.7.7" / "jetty-embed-plugin-0.1-SNAPSHOT.jar"

  val jettyEmbedVersion = "6.1.22"
  val jettyEmbedDependencies = "org.mortbay.jetty" % "jetty" % jettyEmbedVersion % "jettyEmbed, compile"
  val jettyEmbedConf = config("jettyEmbed")
  def jettyEmbedClasspath = managedClasspath(jettyEmbedConf)
  
  val warMainClass = "net.usersource.jettyembed.Startup"
  val warClassPath = "WEB_INF/classes/ WEB_INF/lib/"
  val warManifestVersion = "1.0"

  override def packageOptions = List(new MainClass(warMainClass), new ManifestAttributes((CLASS_PATH,warClassPath)))

  override def libraryDependencies = Set(jettyEmbedDependencies) ++ super.libraryDependencies

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
      val startupFile = classesTargetDirectory / "net" / "usersource" / "jettyembed" / "Startup.class"

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
                copyFile( startupFile, warPath / "net" / "usersource" / "jettyembed" / "Startup.class", log )
                None.toLeft()
              }
            }
        }
      }}).left.toOption
    }
  }

}

