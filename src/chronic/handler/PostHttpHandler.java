/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.StatusRecord;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class PostHttpHandler implements HttpHandler {

    final static int contentLengthLimit = 4000;
    Logger logger = LoggerFactory.getLogger(PostHttpHandler.class);
    ChronicApp app;

    public PostHttpHandler(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String hostAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
        if (!hostAddress.equals(app.getProperties().getRemoteAddress())) {
            logger.warn("remote hostname {}", hostAddress);
            writePlainResponse(httpExchange, "error: untrusted IP address: " + hostAddress);
            return;
        }
        String path = httpExchange.getRequestURI().getPath();
        int contentLength = Integer.parseInt(
                httpExchange.getRequestHeaders().get("Content-length").get(0));
        logger.trace("content-length {}", contentLength);
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
            logger.trace("content lines {}: {}", record.getLineList().size(),
                    Strings.formatFirst(record.getLineList()));
            logger.debug("record {} {}", record.getSource(), record.getStatusType());
            app.putRecord(record);
            writePlainResponse(httpExchange, "ok");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            writePlainResponse(httpExchange, "error: %s: %s", e.getClass(), e.getMessage());
        }
    }

    public static void writePlainResponse(HttpExchange httpExchange, String responseString,
            Object ... args) 
            throws IOException {
        responseString = String.format(responseString, args) + "\n";
        byte[] responseBytes = responseString.getBytes();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR,
                responseBytes.length);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        httpExchange.getResponseBody().write(responseBytes);
        httpExchange.close();
    }
}
