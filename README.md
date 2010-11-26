
sbt-jetty-embed
===============

This is an sbt plugin to create 'executeable' war files by embedding jetty.

This plugin takes a brute force approach, it copies a startup source file
into your source tree, which provides the jetty embed code.  It then overrides
the webapp prepare task to also unpack the jetty jars into the root of the
war file.

How to build the plugin
-----------------------

todo....



How to use
----------

Enable the plugin

Mixin the plugin trait

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



