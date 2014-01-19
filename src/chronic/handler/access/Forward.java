/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.access;

import chronic.app.ChronicApp;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMaps;
import vellum.ssl.OpenHostnameVerifier;
import vellum.ssl.OpenTrustManager;
import vellum.ssl.SSLContexts;

/**
 *
 * @author evan.summers
 */
public class Forward implements HttpHandler {

    static Logger logger = LoggerFactory.getLogger(Forward.class);

    ChronicApp app;
    
    public Forward(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        for (String key : http.getRequestHeaders().keySet()) {
            String header = http.getRequestHeaders().getFirst(key);
            logger.debug("incoming request header: {}: {}", key, header);
        }
        String cookie = http.getRequestHeaders().getFirst("Cookie");
        logger.info("Cookie: {}", cookie);
        String referer = http.getRequestHeaders().getFirst("Referer");
        logger.info("Referer: {}", referer);
        try {
            SSLContext sslContext = SSLContexts.create(new OpenTrustManager());
            String url = "https://localhost:8443/chronicapp/certList"; // + http.getRequestURI().getPath();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/json");
            connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("Referer", referer);
            connection.setHostnameVerifier(new OpenHostnameVerifier());
            copyStream(256, http.getRequestBody(), connection.getOutputStream());
            String setCookie = connection.getHeaderField("Set-Cookie");
            logger.info("setCookie {}", setCookie);
            http.getResponseHeaders().set("Content-type", "text/json");
            http.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            copyStream(1024, connection.getInputStream(), http.getResponseBody());
        } catch (GeneralSecurityException e) {
            String errorResponse = JMaps.mapValue("errorMessage", e.getMessage()).toJson();
            sendResponse(http, "plain/json", errorResponse.getBytes());
        } finally {
            http.close();
        }
    }

    public static void sendResponse(HttpExchange http, String contentType, byte[] bytes) throws IOException {
        http.getResponseHeaders().set("Content-type", contentType);
        http.getResponseHeaders().set("Content-length", Integer.toString(bytes.length));
        http.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        http.getResponseBody().write(bytes);
    }
    
    public static void copyStream(final int capacity, InputStream input,
            OutputStream output) throws IOException {
        byte[] buffer = new byte[capacity];
        while (true) {
            int length = input.read(buffer);
            if (length == -1) {
                return;
            }
            output.write(buffer, 0, length);
        }
    }
}
