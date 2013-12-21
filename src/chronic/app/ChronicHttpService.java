/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.app;

import chronic.api.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.handler.CertAction;
import chronic.handler.CertActionAll;
import chronic.handler.CertActionNone;
import chronic.handler.AdminEnroll;
import chronic.handler.EnrollOrg;
import chronic.handler.AlertList;
import chronic.handler.CertList;
import chronic.handler.SubscriberList;
import chronic.handler.TopicList;
import chronic.handler.RoleList;
import chronic.handler.Post;
import chronic.handler.RoleAction;
import chronic.handler.RoleActionAll;
import chronic.handler.CertSubscribe;
import chronic.handler.SubscriberAction;
import chronic.handler.SubscriberActionAll;
import chronic.handler.SubscriberActionNone;
import chronic.handler.TopicAction;
import chronic.handler.TopicActionAll;
import chronic.handler.TopicActionNone;
import chronic.jpa.JpaDatabase;
import chronic.persona.PersonaLogin;
import chronic.persona.PersonaLogout;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.httphandler.WebHttpHandler;

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
        logger.trace("handle {} {}", path, httpExchange.getRequestURI().getHost());
        try {
            if (path.startsWith("/chronicapp/")) {
                String handlerClassName = "chronic.handler." + 
                        path.substring("/chronicapp/".length());
                handle((ChronicHttpxHandler) Class.forName(handlerClassName).newInstance(), 
                        httpExchange);
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Exception e) {
            logger.warn("error {} {}", path, Exceptions.getMessage(e));
            e.printStackTrace(System.err);            
        }
    }

    private void handle(ChronicHttpxHandler handler, HttpExchange httpe) {
        ChronicHttpx httpx = new ChronicHttpx(app, httpe);
        EntityManager em = null;
        Connection connection = null;
        try {
            em = app.getEntityManagerFactory().createEntityManager();
            ChronicDatabase database = new JpaDatabase(app, connection, em);
            httpx.setDatabase(database);
            em.getTransaction().begin();
            httpx.sendResponse(handler.handle(httpx));
            em.getTransaction().commit();
        } catch (Exception e) {
            httpx.sendError(e);
            if (em != null) {
                em.getTransaction().rollback();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqle) {
                logger.warn("connection close", sqle);
            }
        }
        httpx.close();
    }

}
