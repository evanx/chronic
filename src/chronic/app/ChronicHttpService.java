/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.api.ChronicHttpxHandler;
import chronic.jpa.JpaDatabase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.httphandler.WebHttpHandler;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpService implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(ChronicHttpService.class);
    private final static WebHttpHandler webHandler = new WebHttpHandler("/chronic/web");
    private ChronicApp app;

    public ChronicHttpService(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("handle {} {}", path, httpExchange.getRequestURI().getHost());
        try {
            Class<ChronicHttpxHandler> handlerClass = app.getHandlerClasses().get(path);
            logger.info("handlerClass {} {}", path, handlerClass);
            if (handlerClass != null) {
                handle(handlerClass.newInstance(), httpExchange);
            } else {
                String handlerName = getHandlerName(path);
                logger.info("handlerName {} {}", path, handlerName);
                if (handlerName != null) {
                    handle(getHandler(handlerName), httpExchange);
                } else {
                    webHandler.handle(httpExchange);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                IOException e) {
            logger.warn("error {} {}", path, Exceptions.getMessage(e));
            e.printStackTrace(System.err);
        }
    }

    private String getHandlerName(String path) {
        final String prefix = "/chronicapp/";
        if (path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }
        return null;
    }

    private ChronicHttpxHandler getHandler(String handlerName) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        String className = "chronic.handler."
                + Character.toUpperCase(handlerName.charAt(0)) + handlerName.substring(1);
        logger.info("handler {}", className);
        return (ChronicHttpxHandler) Class.forName(className).newInstance();
    }

    private void handle(ChronicHttpxHandler handler, HttpExchange httpe) {
        ChronicHttpx httpx = new ChronicHttpx(app, httpe);
        EntityManager em = null;
        Connection connection = null;
        try {
            app.ensureInitialized();
            connection = app.getDataSource().getConnection();
            em = app.getEntityManagerFactory().createEntityManager();
            ChronicDatabase database = new JpaDatabase(app, connection, em);
            httpx.setDatabase(database);
            em.getTransaction().begin();
            JMap responseMap = handler.handle(httpx);
            logger.info("response {}", responseMap);
            httpx.sendResponse(responseMap);
            em.getTransaction().commit();
        } catch (Exception e) {
            httpx.sendError(e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException sqle) {
                logger.warn("connection rollback", sqle);
            }
            if (em != null) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(System.out);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqle) {
                logger.warn("connection close", sqle);
            }
            if (em != null) {
                em.close();
            }
            httpx.close();
        }
    }
}
