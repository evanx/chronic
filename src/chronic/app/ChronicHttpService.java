/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicHttpxHandler;
import chronicexp.jdbc.CachingJdbcDatabase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
        logger.info("handle {}", path);
        try {
            Class<ChronicHttpxHandler> handlerClass = app.getHandlerClasses().get(path);
            logger.trace("handlerClass {} {}", path, handlerClass);
            if (handlerClass != null) {
                handle(handlerClass.newInstance(), httpExchange);
            } else {
                String handlerName = getHandlerName(path);
                logger.trace("handlerName {} {}", path, handlerName);
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
        logger.trace("handler {}", className);
        return (ChronicHttpxHandler) Class.forName(className).newInstance();
    }

    private void handle(ChronicHttpxHandler handler, HttpExchange httpe) {
        ChronicHttpx httpx = new ChronicHttpx(app, httpe);        
        CachingJdbcDatabase database = new CachingJdbcDatabase(app);
        ChronicEntityService es = new ChronicEntityService();
        try {
            app.ensureInitialized();
            database.open();
            database.begin();
            httpx.setDatabase(database);
            es.begin(app.emf.createEntityManager());
            JMap responseMap = handler.handle(app, httpx, es);
            logger.trace("response {}", responseMap);
            httpx.sendResponse(responseMap);
            database.commit();
            es.commit();
        } catch (Exception e) {
            httpx.sendError(e);
            database.rollback();
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            httpx.close();
            database.close();
            es.close();
        }
    }
}
