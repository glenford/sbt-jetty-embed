
How to build the plugin manually
--------------------------------

Clone the source

	$ git clone git://github.com/glenford/sbt-jetty-embed.git

Build the plugin and push to your local ivy2 repo.

	$ cd sbt-jetty-embed/plugin
	$ sbt update publish-local

Important Note
--------------

If you rebuild the plugin (ie. build a new version) then you should in your own project(s) run:

	sbt clean-plugins
	sbt clean update  package

This will make sure that you have the latest plugin, and ensure it extracts the latest startup file(s) into your project

