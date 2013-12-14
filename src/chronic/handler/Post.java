/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.StatusRecord;
import chronic.StatusRecordParser;
import chronic.check.StatusCheck;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.transaction.EnrollTransaction;
import chronic.transaction.SubscribeTransaction;
import chronic.transaction.TopicTransaction;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.security.Certificates;
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

    Org org;
    Network network;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String hostAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
        if (!app.getProperties().getAllowedAddresses().contains(hostAddress)) {
            logger.warn("remote hostAddress {}", hostAddress);
            sendPlainResponse(httpExchange, "error: not allowed IP address: " + hostAddress);
            return;
        }
        try {
            X509Certificate certificate = ((HttpsExchange) httpExchange).getSSLSession().
                    getPeerCertificateChain()[0];
            String hostName = Certificates.getCommonName(certificate.getSubjectDN());
            String orgUrl = Certificates.getOrg(certificate.getSubjectDN());
            String networkName = Certificates.getOrgUnit(certificate.getSubjectDN());
            logger.trace("certificate {} {}", hostName, orgUrl);
            if (!app.getStorage().orgs().containsKey(orgUrl)) {
                org = new Org(orgUrl);
                app.getStorage().orgs().insert(org);
            } else {
                org = app.getStorage().orgs().find(orgUrl);
            }
            if (!app.getStorage().nets().containsKey(networkName)) {
                network = new Network(orgUrl, networkName);
                network.setAddress(hostAddress);
                app.getStorage().nets().insert(network);
            } else {
                network = app.getStorage().nets().find(networkName);
                if (!network.getOrgUrl().equals(org.getOrgUrl())) {
                    logger.warn("network orgName {}, {}", network.getOrgUrl(), org.getOrgUrl());
                }                
            }
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
            StatusRecord status = new StatusRecordParser(orgUrl).parse(contentString);
            status.setOrgUrl(orgUrl);
            logger.trace("content lines {}: {}", status.getLineList().size(),
                    Strings.formatFirst(status.getLineList()));
            logger.debug("status {}", status);
            new TopicTransaction().handle(app, status.getOrgUrl(), networkName, hostName, 
                    status.getTopicString());
            if (status.getSubscribers() != null) {
                if (status.getSubscribers().length > 0) {
                    for (String subscriber : status.getSubscribers()) {
                        new SubscribeTransaction().handle(app, status.getOrgUrl(), subscriber);
                        new EnrollTransaction().handle(app, orgUrl, subscriber);
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
            Object ... args) throws IOException {
        responseString = String.format(responseString, args) + "\n";
        byte[] responseBytes = responseString.getBytes();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR,
                responseBytes.length);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        httpExchange.getResponseBody().write(responseBytes);
        httpExchange.close();
    }
}
