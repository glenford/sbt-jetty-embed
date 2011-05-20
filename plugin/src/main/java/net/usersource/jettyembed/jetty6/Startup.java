
//
// Copyright 2011, Glen Ford
//
// Apache 2.0 License
// Please see README.md, LICENSE and NOTICE
//


package net.usersource.jettyembed.jetty6;

import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.URIUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;


import org.mortbay.jetty.security.SslSelectChannelConnector;



public class Startup {

    private static final String JETTY_PORT_NAME = "jettyPort";
    private static final String JETTY_SSL_PORT_NAME = "jettySslPort";
    private static final String JETTY_SSL_KEY_PASSWORD_NAME = "jettySslKeyPassword";
    private static final String JETTY_SSL_KEY_STOREFILE_NAME = "jettySslKeyStoreFile";
    private static final String JETTY_INTERACTIVE_NAME = "jettyInteractive";
    private static final String JETTY_USE_NIO_NAME = "jettyNio";
    private static final String JETTY_DEBUG_NAME = "jettyDebug";
    private static final String JETTY_MAX_IDLE_NAME = "jettyMaxIdle";

    private static final int JETTY_MAX_IDLE = 30000;

    private static boolean debug = false;
    private static boolean isInteractive = false;
    private static boolean usingNIO = true;
    private static boolean usingSSL = false;
    private static String tempDir = null;
    private static int jettyPort = 8080;
    private static int jettySSLPort = 0;
    private static String keyFileName = null;
    private static String keyPassword = null;
    private static int maxIdleTime = 30000;



    private static void processOptions() {

        if( System.getProperty(JETTY_DEBUG_NAME) != null ) debug = Boolean.getBoolean(JETTY_DEBUG_NAME);

        if( System.getProperty(JETTY_USE_NIO_NAME) != null ) usingNIO = Boolean.getBoolean(JETTY_USE_NIO_NAME);

        if( System.getProperty(JETTY_INTERACTIVE_NAME) != null ) isInteractive = Boolean.getBoolean(JETTY_INTERACTIVE_NAME);

        tempDir = System.getProperty("jettyTempDir");

        maxIdleTime = Integer.getInteger(JETTY_MAX_IDLE_NAME,maxIdleTime);

        jettyPort = Integer.getInteger(JETTY_PORT_NAME, jettyPort);
        jettySSLPort = Integer.getInteger(JETTY_SSL_PORT_NAME,jettySSLPort);
        usingSSL = (jettySSLPort != 0);

        if( usingSSL ) {
            keyFileName = System.getProperty(JETTY_SSL_KEY_STOREFILE_NAME,keyFileName);
            keyPassword = System.getProperty(JETTY_SSL_KEY_PASSWORD_NAME,keyPassword);
        }


        if( debug ) {
            System.out.println("=================");
            System.out.println("Jetty Embed Debug");
            System.out.println("=================");
            System.out.println("Interactive : " + isInteractive );
            System.out.println("NIO : " + usingNIO );
            System.out.println("HTTP Port : "  + jettyPort );
            System.out.println("Max Idle Time : " + maxIdleTime );
            System.out.println("SSL Port : " + (usingSSL ? jettySSLPort : "(disabled)") );
            if(usingSSL) {
                System.out.println("SSL Key Password : " + keyPassword );
                System.out.println("SSL Key File Name : " + keyFileName );
            }
            System.out.println("=================");
            System.setProperty("DEBUG", "true");
        }
    }

    private static Connector buildSslConnector() {
        if( usingNIO ) {
            SslSelectChannelConnector sslConnector = new SslSelectChannelConnector();
            sslConnector.setPort(jettySSLPort);
            sslConnector.setMaxIdleTime(maxIdleTime);
            sslConnector.setKeyPassword(keyPassword);
            if (keyFileName != null && keyFileName.length() != 0 ) {
                sslConnector.setKeystore(keyFileName);
            }
            return sslConnector;
        }
        else {
            SslSocketConnector sslConnector = new SslSocketConnector();
            sslConnector.setPort(jettySSLPort);
            sslConnector.setMaxIdleTime(maxIdleTime);
            sslConnector.setKeyPassword(keyPassword);
            if (keyFileName != null && keyFileName.length() != 0 ) {
                sslConnector.setKeystore(keyFileName);
            }
            return sslConnector;
        }
    }

    private static Connector buildConnector() {
        Connector connector;
        if (usingNIO) {
            connector = new SelectChannelConnector();
        } else {
            connector = new SocketConnector();
        }
        connector.setPort(jettyPort);
        connector.setMaxIdleTime(maxIdleTime);
        return connector;
    }

    private static void setThreadClassLoader() {
        Thread.currentThread().setContextClassLoader(WebAppClassLoader.class.getClassLoader());
    }

    private static WebAppContext buildContext() throws IOException {
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
        return context;
    }

    public static void main(String[] args) throws Exception {
        setThreadClassLoader();
        processOptions();
        WebAppContext context = buildContext();

        Server server = new Server();
        if( usingSSL ) {
            server.setConnectors(new Connector[]{buildConnector(),buildSslConnector()});
        }
        else {
            server.setConnectors(new Connector[]{buildConnector()});
        }
        server.setHandler(context);
        server.setSendServerVersion(false);

        run(server);
    }


    private static void run(Server server) {
        try {
            server.start();
            if( isInteractive ) {
                System.out.println("Press any key to exit.");
                System.in.read();
                server.stop();
            }
            else {
                server.join();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
