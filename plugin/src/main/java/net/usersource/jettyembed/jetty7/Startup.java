
//
// Copyright 2011, Glen Ford
//
// Apache 2.0 License
// Please see README.md, LICENSE and NOTICE
//


package net.usersource.jettyembed.jetty7;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.URIUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;




public class Startup {

    private static final int JETTY_PORT_DEFAULT = 8080;

    private static final String JETTY_PORT_NAME = "jettyPort";
    private static final String JETTY_SSL_PORT_NAME = "jettySslPort";
    private static final String JETTY_SSL_KEY_PASSWORD_NAME = "jettySslKeyPassword";
    private static final String JETTY_SSL_KEY_STOREFILE_NAME = "jettySslKeyStoreFile";
    private static final String JETTY_INTERACTIVE_NAME = "jettyInteractive";
    private static final String JETTY_USE_NIO_NAME = "jettyNio";
    
    private static final int JETTY_MAX_IDLE = 30000;

    private static boolean isInteractive = false;

    private static boolean usingNIO = true;
    private static boolean usingSSL = false;

    private static String tempDir = null;


    private static void processOptions() {
        Boolean useNIO = Boolean.getBoolean(JETTY_USE_NIO_NAME);
        if( useNIO != null ) usingNIO = useNIO.booleanValue();

        Boolean interactive = Boolean.getBoolean(JETTY_INTERACTIVE_NAME);
        if( interactive != null ) isInteractive = interactive.booleanValue();

        if( Integer.getInteger(JETTY_SSL_PORT_NAME) != null ) usingSSL = true;

        tempDir = System.getProperty("jettyTempDir");
    }


     private static Connector buildConnector() {
        Connector connector;
        if (usingNIO) {
            connector = new SelectChannelConnector();
        } else {
            connector = new SocketConnector();
        }
        connector.setPort(Integer.getInteger(JETTY_PORT_NAME, JETTY_PORT_DEFAULT));
        connector.setMaxIdleTime(Integer.getInteger("jettyMaxIdle", JETTY_MAX_IDLE));
        return connector;
    }

    private static Connector buildSslConnector() {
        SslConnector sslConnector;
        if( usingNIO ) {
            sslConnector = new SslSelectChannelConnector();
        }
        else {
            sslConnector = new SslSocketConnector();
        }
        sslConnector.setPort(Integer.getInteger(JETTY_SSL_PORT_NAME));
        sslConnector.setKeyPassword(System.getProperty(JETTY_SSL_KEY_PASSWORD_NAME));
        String keystoreFileName = System.getProperty(JETTY_SSL_KEY_STOREFILE_NAME);
        if (keystoreFileName != null && keystoreFileName.length() != 0 ) {
            sslConnector.setKeystore(keystoreFileName);
        }
        return sslConnector;
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

        if( tempDir != null ) {
            File tempDirectory = new File(tempDir);
            context.setTempDirectory(tempDirectory);
        }

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
