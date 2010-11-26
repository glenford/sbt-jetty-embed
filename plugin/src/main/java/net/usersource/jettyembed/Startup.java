package net.usersource.jettyembed;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;

public class Startup {

    private static final int JETTY_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(Integer.getInteger("jettyPort", JETTY_PORT));
        server.setConnectors(new Connector[]{connector});

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");

        ProtectionDomain protectionDomain = Startup.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.addHandler(context);

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
