/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronicapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class ChronicHttpHandler implements HttpHandler {
    final static int contentLengthLimit = 4000;
    
    Logger logger = LoggerFactory.getLogger(ChronicHttpHandler.class);
    ChronicApp app;
    
    public ChronicHttpHandler(ChronicApp app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.trace("path {}", path);
        int contentLength = Integer.parseInt(
                httpExchange.getRequestHeaders().get("Content-length").get(0));
        logger.info("content-length {}", contentLength);
        if (contentLength > contentLengthLimit) {
            httpExchange.close();
            return;
        }
        byte[] content = new byte[contentLength];
        httpExchange.getRequestBody().read(content);
        try {
            String contentString = new String(content);
            logger.trace("content {}", contentString);
            StatusRecord record = StatusRecord.parse(contentString);
            logger.trace("content line {}", record.getLineList().get(0));
            logger.info("record {}", record);      
            app.putRecord(record);
            String responseString = "OK\n";
            byte[] responseBytes = responseString.getBytes();
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            httpExchange.getResponseHeaders().set("Content-type", "text/plain");
            httpExchange.getResponseBody().write(responseBytes);
            httpExchange.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            String responseString = "ERROR: " + e.getClass() + ": " + e.getMessage() + "\n";
            byte[] responseBytes = responseString.getBytes();
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, responseBytes.length);
            httpExchange.getResponseHeaders().set("Content-type", "text/plain");
            httpExchange.getResponseBody().write(responseBytes);
            httpExchange.close();
        }
    }

}
