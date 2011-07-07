
sbt-jetty-embed
===============

This is an sbt plugin to create 'executeable' war files by embedding jetty.
It supports both Jetty-6 & Jetty-7.

This plugin takes a brute force approach, it copies a startup source file
into a local managed source tree, which provides the jetty embed code.  It
then overrides the webapp prepare task to also unpack the jetty jars into
the root of the war file.

Important Note
--------------

If you are using xsbt (sbt 0.10+) please see https://github.com/glenford/xsbt-war-plugins

Lift users: if you are using Lift 2.3-M1 and newer it automatically detects
Jetty 6, Jetty 7, and Servlet 3.0.  However earlier versions should be aware
that to use Jetty-7 you will need an addition to your Boot.scala file.
Please see the example lift-project.

Further Information
-------------------

See SSL.md for information regarding using SSL.
See DEVELOPER.md for information regarding building manually.


How to use in your own project
------------------------------

First create the plugins directory and file

	mkdir project/plugins
	vi project/plugins/Plugins.scala

	import sbt._

	class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
		val jettyEmbeddedWarRepo = "Embeded Jetty Repo" at "https://github.com/glenford/repo/raw/master"
  		val jettyEmbeddedWar = "net.usersource" % "sbt-jetty-embed-plugin" % "0.6.1"
	}

Extend the plugin instead of DefaultWebProject

	import sbt._
	import net.usersource.jettyembed._
	
	class BasicProject(info :ProjectInfo) extends JettyEmbedWebProject(info)


By default it uses Jetty-6, if you would like to use Jetty-7 then use

	class BasicProject(info :ProjectInfo) extends JettyEmbedWebProject(info,JETTY7)



Update to ensure the jetty libs are downloaded and extract the startup code into your project

	sbt
	> update


This will copy a Startup file to

	src_managed/main/java/net/usersource/jettyembed/jetty6/Startup.java 


Then package your code as normal

	sbt
	> compile
	> package

Then run your new war

	$ java -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war 
	2011-03-03 21:10:56.157:INFO::Logging to STDERR via org.mortbay.log.StdErrLog
	2011-03-03 21:10:56.158:INFO::jetty-6.1.x
	2011-03-03 21:10:56.209:INFO::Extract file:/Users/glen/UserSource/Public/sbt-jetty-embed/basic-project/target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war to /var/folders/Kg/KgM30VT2FvaHmZ1PuJ4D9++++TI/-Tmp-/Jetty_0_0_0_0_8080_sbt.jetty.embed.basic_2.8.1-0.6.1.war____.a3s7p1/webapp
	2011-03-03 21:10:57.569:INFO::NO JSP Support for /, did not find org.apache.jasper.servlet.JspServlet
	2011-03-03 21:10:57.754:INFO::Started SelectChannelConnector@0.0.0.0:8080


You can still use your 'executeable' war in a regular web container unchanged.


Runtime Options
---------------

You can override the default port of 8080 by using -DjettyPort e.g.

	$ java -DjettyPort=8000 -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war

You can set the temporary directory (where jetty unpacks the war) by using -DjettyTempDir e.g.

	$ java -DjettyTempDir=myTempDir -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war

You can override the default max idle time of 30000 milliseconds by using -DjettyMaxIdle e.g.

	$ java -DjettyMaxIdle=60000 -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war

You can run in interactive mode so you can press any key to exit by using -DjettyInteractive=true (default is false)

	$ java -DjettyInteractive=true -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war

By default Jetty will use NIO, you can turn this off by using

        $ java -DjettyNio=false -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war

You can enable debug logging, both for the startup and Jetty by using

	$ java -DjettyDebug=true -jar target/scala_2.8.1/sbt-jetty-embed-basic_2.8.1-0.6.1.war


Using a different version of Jetty
----------------------------------

You can override the default 6.1.22 version of Jetty to use by:

	override val jettyEmbedVersion = "6.1.26"

In your project definition.


License
-------

The sbt-jetty-embed plugin is licensed under the Apache 2 License
Please see the LICENSE and NOTICE files.


