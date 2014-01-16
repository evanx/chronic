/*
 * Source https://github.com/evanx by @evanxsummers

 */
package chronic.app;

import chronic.api.ChronicPlainHttpxHandler;
import chronic.handler.secure.AdminEnroll;
import chronic.handler.secure.AlertPoll;
import chronic.handler.secure.PushRegister;
import chronic.handler.secure.CertSubscribe;
import chronic.handler.secure.Post;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.httpserver.Httpx;

/**
 *
 * @author evan.summers
 */
public class SecureHttpService implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(SecureHttpService.class);
    private final Map<String, Class<? extends ChronicPlainHttpxHandler>> plainHandlerClasses = new HashMap();
    private ChronicApp app;

    public SecureHttpService(ChronicApp app) {
        this.app = app;
        plainHandlerClasses.put("/post", Post.class);
        plainHandlerClasses.put("/push", PushRegister.class);
        plainHandlerClasses.put("/enroll", AdminEnroll.class);
        plainHandlerClasses.put("/subscribe", CertSubscribe.class);
        plainHandlerClasses.put("/poll", AlertPoll.class);        
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        ChronicHttpx httpx = new ChronicHttpx(app, httpExchange);
        logger.trace("handle {}", path);
        try {
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
