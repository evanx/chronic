/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicHttpxHandler;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.handler.AdminEnroll;
import chronic.handler.AlertPoll;
import chronic.handler.PushRegister;
import chronic.handler.CertSubscribe;
import chronic.handler.Post;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, Class<? extends ChronicPlainHttpxHandler>> plainHandlerClasses = new HashMap();
    private ChronicApp app;

    public ChronicHttpService(ChronicApp app) {
        this.app = app;
        plainHandlerClasses.put("/post", Post.class);
        plainHandlerClasses.put("/push", PushRegister.class);
        plainHandlerClasses.put("/enroll", AdminEnroll.class);
        plainHandlerClasses.put("/subscribe", CertSubscribe.class);
        plainHandlerClasses.put("/poll", AlertPoll.class);        
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("handle");
        String path = httpExchange.getRequestURI().getPath();
        logger.trace("handle {}", path);
        try {
            Class<? extends ChronicPlainHttpxHandler> handlerClass = plainHandlerClasses.get(path);
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
        ChronicEntityService es = new ChronicEntityService(app);
        try {
            app.ensureInitialized();
            es.begin();
            JMap responseMap = handler.handle(app, httpx, es);
            logger.trace("response {}", responseMap);
            httpx.sendResponse(responseMap);
            es.commit();
        } catch (Exception e) {
            httpx.sendError(e);
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            httpx.close();
            es.close();
        }
    }
    
    private void handle(ChronicPlainHttpxHandler handler, HttpExchange httpe) {
        ChronicHttpx httpx = new ChronicHttpx(app, httpe);        
        ChronicEntityService es = new ChronicEntityService(app);
        try {
            app.ensureInitialized();
            es.begin();
            String response = handler.handle(app, httpx, es);
            logger.trace("response {}", response);
            httpx.sendPlainResponse(response);
            es.commit();
        } catch (Exception e) {
            httpx.sendPlainError(e.getMessage());
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            es.close();
        }
    }
    
}
