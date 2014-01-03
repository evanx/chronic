/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicPlainHttpxHandler;
import chronic.securehandler.Post;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;

/**
 *
 * @author evan.summers
 */
public class ChronicInsecureHttpService implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(ChronicInsecureHttpService.class);
    private final Map<String, Class<? extends ChronicPlainHttpxHandler>> plainHandlerClasses = new HashMap();
    private ChronicApp app;

    public ChronicInsecureHttpService(ChronicApp app) {
        this.app = app;
        plainHandlerClasses.put("/post", Post.class);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        ChronicHttpx httpx = new ChronicHttpx(app, httpExchange);
        try {
            if (!httpx.isLocalhost()) {
                throw new Exception("Only localhost allowed on this insecure http service");
            }
            Class<? extends ChronicPlainHttpxHandler> handlerClass = plainHandlerClasses.get(path);
            logger.trace("handlerClass {} {}", path, handlerClass);
            if (handlerClass == null) {
                throw new InstantiationException("No handler: " + path);
            }
            handle(handlerClass.newInstance(), httpx);
        } catch (Exception e) {
            httpx.sendPlainError(e.getMessage());
            logger.warn("error {} {}", path, Exceptions.getMessage(e));
            e.printStackTrace(System.err);
        } finally {
            httpExchange.close();
        }
    }

    private void handle(ChronicPlainHttpxHandler handler, ChronicHttpx httpx) throws Exception {
        ChronicEntityService es = new ChronicEntityService(app);
        try {
            app.ensureInitialized();
            es.begin();
            String response = handler.handle(app, httpx, es);
            logger.trace("response {}", response);
            httpx.sendPlainResponse(response);
            es.commit();
        } catch (Exception e) {
            es.rollback();
            throw e;
        } finally {
            es.close();
        }
    }
    
}
