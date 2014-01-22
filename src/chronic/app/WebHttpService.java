/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicHttpxHandler;
import chronic.api.PlainHttpxHandler;
import chronic.handler.access.Forward;
import chronic.handler.access.ErrorHttpHandler;
import chronic.handler.access.Sign;
import chronic.handler.access.PersonaLogin;
import chronic.handler.access.PersonaLogout;
import chronic.handler.access.Resolve;
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
    private final ChronicApp app;

    public WebHttpService(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("handle {}", path);
        Thread.currentThread().setName(path);        
        try {
            app.ensureInitialized();
            if (path.equals("/sign")) {
                handle(new Sign(), new ChronicHttpx(app, httpExchange));
            } else if (path.equals("/resolve")) {
                new Resolve(app).handle(httpExchange);
            } else if (path.equals("/chronicapp/personaLogin")) {
                handle(new PersonaLogin(), new ChronicHttpx(app, httpExchange));
            } else if (path.equals("/chronicapp/personaLogout")) {
                handle(new PersonaLogout(), new ChronicHttpx(app, httpExchange));
            } else if (path.startsWith("/chronicapp/")) {
                if (path.endsWith("/forwarded")) {
                    String handlerName = getHandlerName(path);
                    if (handlerName != null) {
                        handle(getHandler(handlerName), new ChronicHttpx(app, httpExchange));
                    } else {
                        new ErrorHttpHandler(app).handle(httpExchange, "Service not found: " + path);
                    }
                } else {
                    new Forward(app).handle(httpExchange);
                }
            } else {
                webHandler.handle(httpExchange);
            }
        } catch (Throwable e) {
            String errorMessage = Exceptions.getMessage(e);
            logger.warn("error {} {}", path, errorMessage);
            e.printStackTrace(System.err);
            new ErrorHttpHandler(app).handle(httpExchange, errorMessage);
        } finally {
            httpExchange.close();
        }
    }

    private String getHandlerName(String path) {
        int index = path.lastIndexOf("/forwarded");
        if (index > 0) {
            path = path.substring(0, index);
        }
        final String handlerPathPrefix = "/chronicapp/";
        if (path.startsWith(handlerPathPrefix)) {
            return path.substring(handlerPathPrefix.length());
        }
        return null;
    }

    private ChronicHttpxHandler getHandler(String handlerName) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        String className = "chronic.handler.app."
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
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            httpx.sendPlainError(String.format("ERROR: %s\n", e.getMessage()));
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            es.close();
        }
    }
}
