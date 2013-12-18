/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.StatusRecord;
import chronic.app.StatusRecordParser;
import chronic.check.StatusCheck;
import chronic.entity.Cert;
import chronic.transaction.EnrollTransaction;
import chronic.transaction.SubscribeTransaction;
import chronic.transaction.TopicTransaction;
import chronic.transaction.VerifyCertTransaction;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Post implements HttpHandler {

    final static int contentLengthLimit = 4000;
    Logger logger = LoggerFactory.getLogger(Post.class);
    ChronicApp app;

    public Post(ChronicApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            X509Certificate certificate = ((HttpsExchange) httpExchange).getSSLSession().
                    getPeerCertificateChain()[0];
            String hostAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
            Cert cert = new VerifyCertTransaction().handle(app, hostAddress, certificate);
            int contentLength = Integer.parseInt(
                    httpExchange.getRequestHeaders().get("Content-length").get(0));
            logger.trace("contentLength {}", contentLength);
            if (contentLength > contentLengthLimit) {
                httpExchange.close();
                return;
            }
            byte[] content = new byte[contentLength];
            httpExchange.getRequestBody().read(content);
            String contentString = new String(content);
            logger.trace("content {}", contentString);
            StatusRecord status = new StatusRecordParser(cert.getCertKey()).parse(contentString);
            status.setOrgDomain(cert.getOrgDomain());
            logger.trace("content lines {}: {}", status.getLineList().size(),
                    Strings.formatFirst(status.getLineList()));
            logger.debug("status {}", status);
            new TopicTransaction().handle(app, cert.getCertKey(), status.getTopicString());
            if (status.getSubscribers() != null) {
                if (status.getSubscribers().length > 0) {
                    for (String subscriber : status.getSubscribers()) {
                        new SubscribeTransaction().handle(app, status.getOrgDomain(), subscriber);
                        new EnrollTransaction().handle(app, status.getOrgDomain(), subscriber);
                    }
                }
            }
            for (StatusCheck check : status.getChecks()) {
                status.getLineList().add(check.check());
            }
            app.putRecord(status);
            sendPlainResponse(httpExchange, "ok");
        } catch (CertificateException | StorageException | NumberFormatException | IOException e) {
            logger.warn(e.getMessage(), e);
            sendPlainResponse(httpExchange, "error: %s: %s", e.getClass(), e.getMessage());
        }
    }

    public static void sendPlainResponse(HttpExchange httpExchange, String responseString,
            Object... args) throws IOException {
        responseString = String.format(responseString, args) + "\n";
        byte[] responseBytes = responseString.getBytes();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR,
                responseBytes.length);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        httpExchange.getResponseBody().write(responseBytes);
        httpExchange.close();
    }
}
