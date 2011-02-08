package net.usersource.jettyembed;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.URIUtil;

import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;


import org.mortbay.jetty.security.SslSelectChannelConnector;



public class Startup {

    private static final int JETTY_PORT_DEFAULT = 8080;
    private static final String JETTY_PORT_NAME = "jettyPort";

    private static final String JETTY_SSL_PORT_NAME = "jettySslPort";
    private static final String JETTY_SSL_KEY_PASSWORD_NAME = "jettySslKeyPassword";
    private static final String JETTY_SSL_KEY_STOREFILE_NAME = "jettySslKeyStoreFile";


    private static final int JETTY_MAX_IDLE = 30000;


    public static void main(String[] args) throws Exception {

        SelectChannelConnector connector = new SelectChannelConnector();
        SslSelectChannelConnector sslConnector = null;
        connector.setPort(Integer.getInteger(JETTY_PORT_NAME, JETTY_PORT_DEFAULT));
        connector.setMaxIdleTime(Integer.getInteger("jettyMaxIdle", JETTY_MAX_IDLE));

        if( Integer.getInteger(JETTY_SSL_PORT_NAME) != null ) {
            sslConnector = new SslSelectChannelConnector();
            sslConnector.setPort(Integer.getInteger(JETTY_SSL_PORT_NAME));
            sslConnector.setKeyPassword(System.getProperty(JETTY_SSL_KEY_PASSWORD_NAME));
            String keystoreFile = System.getProperty(JETTY_SSL_KEY_STOREFILE_NAME);
            if (keystoreFile != null && keystoreFile != "") {
	            sslConnector.setKeystore(keystoreFile);
            }
        }
        String tempDir = System.getProperty("jettyTempDir");

        Thread.currentThread().setContextClassLoader(WebAppClassLoader.class.getClassLoader());

        ProtectionDomain protectionDomain = Startup.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();

        WebAppContext context = new WebAppContext();
        WebAppClassLoader webAppClassLoader = new WebAppClassLoader(Startup.class.getClassLoader(),context);
        context.setClassLoader(webAppClassLoader);
        context.setContextPath(URIUtil.SLASH);
        context.setWar(location.toExternalForm());

        if( tempDir != null ) {
            File tempDirectory = new File(tempDir);
            context.setTempDirectory(tempDirectory);
        }

        Server server = new Server();
        if( sslConnector != null ) {
            server.setConnectors(new Connector[]{connector,sslConnector});
        }
        else {
            server.setConnectors(new Connector[]{connector});
        }
        server.setHandler(context);
        server.setSendServerVersion(false);

        try {
            server.start();
            server.join();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
