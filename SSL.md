Using SSL
---------

The embedded server supports the Jetty SSL Engine.

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
 
