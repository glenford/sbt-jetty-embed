
sbt-jetty-embed
===============

This is an sbt plugin to create 'executeable' war files by embedding jetty.

This plugin takes a brute force approach, it copies a startup source file
into your source tree, which provides the jetty embed code.  It then overrides
the webapp prepare task to also unpack the jetty jars into the root of the
war file.


Important Note
--------------

Requires SBT 0.7.5RC0 or later.  There are bugs in earlier versions of sbt
which will impact the behaviour of this plugin.


Limitations
-----------

Currently only supports Jetty-6

Internally there are some hardwired dependencies on the plugin versioning
beaware if you up the version you will need to update in the code.  This
will be addressed at a later time, this is a very early version.


How to build the plugin
-----------------------

Clone the source

	$ git clone git://github.com/glenford/sbt-jetty-embed.git

Build the plugin and push to your local ivy2 repo.

	$ cd sbt-jetty-embed/plugin
	$ sbt update publish-local



How to use in your own project
------------------------------

Look at the basic-project example if you want to skip direct to code.

Build the plugin as described above

Extend the plugin instead of DefaultWebProject

	import sbt._
	import net.usersource.jettyembed.JettyEmbedWebProject
	
	class BasicProject(info :ProjectInfo) extends JettyEmbedWebProject(info)


Create the plugins directory

	mkdir project/plugins
	vi project/plugins/Plugins.scala

	import sbt._

	class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  		val jettyEmbeddedWar = "net.usersource" % "jetty-embed-plugin" % "0.1"
	}

Update to ensure the jetty libs are downloaded and extract the startup code into your project

	sbt
	> update
	> jetty-embed-prepare


Then package your code as normal

	sbt
	> package

Then run your new war

	$ java -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war 
	2010-11-27 16:03:49.634:INFO::Logging to STDERR via org.mortbay.log.StdErrLog
	2010-11-27 16:03:49.687:INFO::jetty-6.1.x
	2010-11-27 16:03:50.078:INFO::Extract file:/***/***/***/sbt-jetty-embed/basic-project/target/scala_2.8.1/basic-project_2.8.1-0.1.war to /var/folders/jO/jOLrkn6pHZmYOvRY0t8jdE+++TI/-Tmp-/Jetty_0_0_0_0_8080_basic.project_2.8.1.0.1.war____j5kafr/webapp
	2010-11-27 16:03:50.546:INFO::NO JSP Support for /, did not find org.apache.jasper.servlet.JspServlet
	2010-11-27 16:03:50.716:INFO::Started SocketConnector@0.0.0.0:8080


You can override the default port of 8080 by using -DjettyPort e.g.

	$ java -DjettyPort=8000 -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war 

License
-------

The sbt-jetty-embed plugin is licensed under the Apache 2 License



