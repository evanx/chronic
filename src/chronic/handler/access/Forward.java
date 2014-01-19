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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMaps;
import vellum.ssl.OpenHostnameVerifier;

/**
 *
 * @author evan.summers
 */
public class Forward implements HttpHandler {

    private static Logger logger = LoggerFactory.getLogger(Forward.class);
    public static final Pattern SERVER_PATTERN = Pattern.compile(" server=([^;]*)");

    ChronicApp app;
    
    public Forward(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        String cookie = http.getRequestHeaders().getFirst("Cookie");
        String referer = http.getRequestHeaders().getFirst("Referer");
        logger.info("cookie {}", cookie);
        String server = "localhost:8443"; // TODO
        Matcher matcher = SERVER_PATTERN.matcher(cookie);
        if (!matcher.find()) {
            logger.warn("server not found: {}", cookie);
        } else {        
            server = matcher.group(1);
        }
        logger.info("server", server);
        try {
            String urlString = String.format("https://%s%s/forwarded", server, http.getRequestURI().getPath());
            logger.info("url {}", urlString);
            HttpsURLConnection connection = (HttpsURLConnection) new URL(urlString).openConnection();
            connection.setSSLSocketFactory(app.getProxyClientSSLContext().getSocketFactory());
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
        } catch (IOException e) {
            String errorResponse = JMaps.mapValue("errorMessage", e.getMessage()).toJson();
            sendResponse(http, "plain/json", errorResponse.getBytes());
        } finally {
            http.close();
        }
    }

    public static void sendResponse(HttpExchange http, String contentType, byte[] bytes) 
            throws IOException {
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
