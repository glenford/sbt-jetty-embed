
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
	
	class BasicProject(info :ProjectInfo) extends JettyEmbedWebProject(info) with IdeaProject


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

	$ java -jar your_new.war


License
-------

The sbt-jetty-embed plugin is licensed under the Apache 2 License



