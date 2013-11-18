package fr.twiced.ucoinj;

import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.DispatcherServlet;

public class JettyServer {
    private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

    public static final String WEB_APP_ROOT = "webapp"; // that folder has to be just somewhere in classpath
    public static final String MVC_SERVLET_NAME = "mvcDispatcher";
    public static final String JSP_SERVLET_NAME = "jspServlet";

    private final int port;

    private Server server;

    public JettyServer(int port) {
        this.port = port;
    }

    public void start() {
        server = new Server(port);
        server.setHandler( getServletHandler() );

        try {
            server.start();
        } catch (Exception e) {
            log.error("Failed to start server", e);
            throw new RuntimeException();
        }

        log.info("Server started");
    }

    private ServletContextHandler getServletHandler() {
    	// Setup Spring MVC Servlet holder
        ServletHolder mvcServletHolder = new ServletHolder(MVC_SERVLET_NAME, new DispatcherServlet());
        mvcServletHolder.setInitParameter("contextConfigLocation", "web-context.xml");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.addServlet(mvcServletHolder, "/");
        context.setResourceBase( getBaseUrl() );
        
        // Setup Spring context
        context.addEventListener(new ContextLoaderListener());
        context.setInitParameter("contextConfigLocation", "classpath*:**/applicationContext.xml");

        return context;
    }

    public void join() throws InterruptedException {
        server.join();
    }

    private String getBaseUrl() {
        URL webInfUrl = getClass().getClassLoader().getResource(WEB_APP_ROOT);
        if (webInfUrl == null) {
            throw new RuntimeException("Failed to find web application root: " + WEB_APP_ROOT);
        }
        return webInfUrl.toExternalForm();
    }
}
