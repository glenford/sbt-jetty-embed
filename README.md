
sbt-jetty-embed
===============

This is an sbt plugin to create 'executeable' war files by embedding jetty.
It supports both Jetty-6 & Jetty-7.

This plugin takes a brute force approach, it copies a startup source file
into your source tree, which provides the jetty embed code.  It then overrides
the webapp prepare task to also unpack the jetty jars into the root of the
war file.


Important Note
--------------

Requires SBT 0.7.5RC0 or later.  There are bugs in earlier versions of sbt
which will impact the behaviour of this plugin.

Lift users should be aware that to use Jetty-7 you will need an addition
to your Boot.scala file (this is a Lift limitation).  Please see the 
example lift-project.


How to use in your own project
------------------------------

First create the plugins directory and file

	mkdir project/plugins
	vi project/plugins/Plugins.scala

	import sbt._

	class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
		 val jettyEmbeddedWarRepo = "Embeded Jetty Repo" at "https://github.com/glenford/repo/raw/master/snapshots"
  		val jettyEmbeddedWar = "net.usersource" % "jetty-embed-plugin" % "0.3-SNAPSHOT"
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
	> jetty-embed-prepare


This will copy a Startup file to

	src/main/java/net/usersource/jettyembed/jetty6/Startup.java 


Then package your code as normal

	sbt
	> compile
	> package

Then run your new war

	$ java -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war 
	2010-11-27 16:03:49.634:INFO::Logging to STDERR via org.mortbay.log.StdErrLog
	2010-11-27 16:03:49.687:INFO::jetty-6.1.x
	2010-11-27 16:03:50.078:INFO::Extract file:/***/***/***/sbt-jetty-embed/basic-project/target/scala_2.8.1/basic-project_2.8.1-0.1.war to /var/folders/jO/jOLrkn6pHZmYOvRY0t8jdE+++TI/-Tmp-/Jetty_0_0_0_0_8080_basic.project_2.8.1.0.1.war____j5kafr/webapp
	2010-11-27 16:03:50.546:INFO::NO JSP Support for /, did not find org.apache.jasper.servlet.JspServlet
	2010-11-27 16:03:50.716:INFO::Started SocketConnector@0.0.0.0:8080



Runtime Options
---------------

You can override the default port of 8080 by using -DjettyPort e.g.

	$ java -DjettyPort=8000 -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war 

You can set the temporary directory (where jetty unpacks the war) by using -DjettyTempDir e.g.

	$ java -DjettyTempDir=myTempDir -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war

You can override the default max idle time of 30000 milliseconds by using -DjettyMaxIdle e.g.

	$ java -DjettyMaxIdle=60000 -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war

You can run in interactive mode so you can press any key to exit by using -DjettyInteractive=true (default is false)

	$ java -DjettyInteractive=true -jar target/scala_2.8.1/basic-project_2.8.1-0.1.war

See SSL options below.


Using SSL
---------

The embedded server now supports the Jetty SSL Engine.

You can set the https port using -DjettySslPort
You can set the key store file using -DjettySslKeyStoreFile
You can set the password for the key store using -DjettySslKeyPassword

To demonstrate this using the basic project

	$ cd basic-project
	$ keytool -genkey -alias localhost -keyalg RSA -keystore newkeystore.jks -keysize 2048
	Enter keystore password: password
	Re-enter new password: password
	What is your first and last name?
	  [Unknown]:  default
	What is the name of your organizational unit?
	  [Unknown]:  default
	What is the name of your organization?
	  [Unknown]:  default
	What is the name of your City or Locality?
	  [Unknown]:  default
	What is the name of your State or Province?
	  [Unknown]:  default
	What is the two-letter country code for this unit?
	  [Unknown]:  XX
	Is CN=default, OU=default, O=default, L=default, ST=default, C=XX correct?
	  [no]:  yes

	Enter key password for <localhost>
		(RETURN if same as keystore password):

        $ sbt update jetty-embed-prepare package
	...
	$ java -jar -DjettySslPort=8443 -DjettySslKeyStoreFile=./keystore.jks -DjettySslKeyPassword=password target/scala_2.8.1/basic-project_2.8.1-0.1.war 
	2011-02-08 11:10:05.689:INFO::Logging to STDERR via org.mortbay.log.StdErrLog
	2011-02-08 11:10:05.690:INFO::jetty-6.1.x
	2011-02-08 11:10:05.743:INFO::Extract file:/***/***/***/sbt-jetty-embed/basic-project/target/scala_2.8.1/basic-project_2.8.1-0.1.war to /var/folders/Kg/KgM30VT2FvaHmZ1PuJ4D9++++TI/-Tmp-/Jetty_0_0_0_0_8080_basic.project_2.8.1.0.1.war____j5kafr/webapp
	2011-02-08 11:10:06.708:INFO::NO JSP Support for /, did not find org.apache.jasper.servlet.JspServlet
	2011-02-08 11:10:06.899:INFO::Started SelectChannelConnector@0.0.0.0:8080
	2011-02-08 11:10:07.276:INFO::Started SslSelectChannelConnector@0.0.0.0:8443

You can then point your browser at https://localhost:8443/ (you will need to accept the self-signed cert.)
 


Using a different version of Jetty
----------------------------------

You can override the default 6.1.22 version of Jetty to use by:

	override val jettyEmbedVersion = "6.1.26"

In your project definition.




How to build the plugin manually
--------------------------------

Clone the source

	$ git clone git://github.com/glenford/sbt-jetty-embed.git

Build the plugin and push to your local ivy2 repo.

	$ cd sbt-jetty-embed/plugin
	$ sbt update publish-local

Important Note
--------------

If you rebuild the plugin (ie. build a new version) then you should in your own project run:

	sbt clean-plugins
	sbt clean jetty-embed-prepare compile package

This will make sure that you have the latest plugin, and ensure it extracts the latest startup file(s) into your project


License
-------

The sbt-jetty-embed plugin is licensed under the Apache 2 License



