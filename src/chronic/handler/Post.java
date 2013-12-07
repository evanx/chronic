/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.StatusRecord;
import chronic.StatusRecordParser;
import chronic.entity.Network;
import chronic.entity.Org;
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
            String orgName = Certificates.getOrg(certificate.getSubjectDN());
            String networkName = Certificates.getOrgUnit(certificate.getSubjectDN());
            logger.trace("certificate {} {}", hostName, orgName);
            if (!app.getStorage().getOrgStorage().containsKey(orgName)) {
                org = new Org(orgName, orgName);
                app.getStorage().getOrgStorage().insert(org);
            } else {
                org = app.getStorage().getOrgStorage().select(orgName);
            }
            if (!app.getStorage().getNetworkStorage().containsKey(networkName)) {
                network = new Network(orgName, networkName);
                network.setAddress(hostAddress);
                app.getStorage().getNetworkStorage().insert(network);
            } else {
                network = app.getStorage().getNetworkStorage().select(networkName);
                if (!network.getOrgName().equals(org.getOrgName())) {
                    logger.warn("network orgName {}, {}", network.getOrgName(), org.getOrgName());
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
            StatusRecord record = new StatusRecordParser().parse(contentString);
            record.setOrgName(orgName);
            logger.trace("content lines {}: {}", record.getLineList().size(),
                    Strings.formatFirst(record.getLineList()));
            logger.debug("record {} {}", record.getSource(), record.getStatusType());
            app.putRecord(record);
            sendPlainResponse(httpExchange, "ok");
        } catch (CertificateException | StorageException | NumberFormatException | IOException e) {
            logger.warn(e.getMessage());
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
