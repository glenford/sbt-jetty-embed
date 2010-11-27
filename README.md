
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

Currently only supports Jetty-6


How to build the plugin
-----------------------

Clone the source

	$ git clone git://github.com/glenford/sbt-jetty-embed.git

Build the plugin

	$ cd sbt-jetty-embed/plugin
	$ sbt update publish-local


Examples
--------

basic-project - demonstrates the most basic use of the plugin


How to use in your own project
------------------------------

Build the plugin as above

Extend the plugin instead of DefaultWebProject

Extract the startup code into your project

	sbt
	> jetty-embed-prepare


Then package your code as normal

	sbt
	> package

Then run your new war

	$ java -jar your_new.war


License
-------

Apache 2 ....



