package net.usersource.jettyembed;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.URIUtil;

import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;



public class Startup {

    private static final int JETTY_PORT = 8080;
    private static final int JETTY_MAX_IDLE = 30000;

    public static void main(String[] args) throws Exception {

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.getInteger("jettyPort", JETTY_PORT));
        connector.setMaxIdleTime(Integer.getInteger("jettyMaxIdle", JETTY_MAX_IDLE));

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
        server.setConnectors(new Connector[]{connector});
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