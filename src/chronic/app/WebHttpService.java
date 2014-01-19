/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicHttpxHandler;
import chronic.api.PlainHttpxHandler;
import chronic.handler.access.Forward;
import chronic.handler.access.Sign;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.httphandler.WebHttpHandler;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class WebHttpService implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebHttpService.class);
    private final static WebHttpHandler webHandler = new WebHttpHandler("/chronic/web");
    private final static String handlerPathPrefix = "/chronicapp/";
    private final static String handlerClassPrefix = "chronic.handler.web.";
    private final ChronicApp app;

    public WebHttpService(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("handle {}", path);
        try {
            app.ensureInitialized();
            if (path.equals("/chronicapp/forward")) {
                new Forward(app).handle(httpExchange);
            } else if (path.equals("/sign")) {
                handle(new Sign(), new ChronicHttpx(app, httpExchange));
            } else {
                String handlerName = getHandlerName(path);
                logger.trace("handlerName {} {}", path, handlerName);
                if (handlerName != null) {
                    handle(getHandler(handlerName), new ChronicHttpx(app, httpExchange));
                } else {
                    webHandler.handle(httpExchange);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                IOException | InterruptedException e) {
            logger.warn("error {} {}", path, Exceptions.getMessage(e));
            e.printStackTrace(System.err);
        } finally {
            httpExchange.close();
        }
    }

    private String getHandlerName(String path) {
        if (path.startsWith(handlerPathPrefix)) {
            return path.substring(handlerPathPrefix.length());
        }
        return null;
    }

    private ChronicHttpxHandler getHandler(String handlerName) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        String className = handlerClassPrefix
                + Character.toUpperCase(handlerName.charAt(0)) + handlerName.substring(1);
        logger.trace("handler {}", className);
        return (ChronicHttpxHandler) Class.forName(className).newInstance();
    }

    private void handle(ChronicHttpxHandler handler, ChronicHttpx httpx) {
        ChronicEntityService es = new ChronicEntityService(app);
        try {
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
            es.close();
        }
    }
    
    private void handle(PlainHttpxHandler handler, ChronicHttpx httpx) {
        ChronicEntityService es = new ChronicEntityService(app);
        try {
            es.begin();
            String response = handler.handle(app, httpx, es);
            logger.trace("response {}", response);
            httpx.sendPlainResponse(response);
            es.commit();
        } catch (Exception e) {
            httpx.sendPlainError(String.format("ERROR: %s\n", e.getMessage()));
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            es.close();
        }
    }
}
