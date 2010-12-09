package net.usersource.jettyembed;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;


public class Startup {

    private static final int JETTY_PORT = 8080;
    private static final int JETTY_MAX_IDLE = 30000;

    public static void main(String[] args) throws Exception {

        SocketConnector connector = new SocketConnector();
        connector.setPort(Integer.getInteger("jettyPort", JETTY_PORT));
        connector.setMaxIdleTime(Integer.getInteger("jettyMaxIdle", JETTY_MAX_IDLE));

        ProtectionDomain protectionDomain = Startup.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar(location.toExternalForm());

        WebAppClassLoader loader = new WebAppClassLoader( Startup.class.getClassLoader(), context );
        context.setClassLoader(loader);

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