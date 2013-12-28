/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.alert.StatusRecord;
import chronic.alert.StatusRecordParser;
import chronic.check.StatusCheck;
import chronic.entity.Cert;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.type.AlertType;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class Post implements ChronicHttpxHandler {

    final static int contentLengthLimit = 4000;
    final static Logger logger = LoggerFactory.getLogger(Post.class);

    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        try {
            Cert cert = es.persistCert(httpx);
            StatusRecord status = new StatusRecord(cert);
            int contentLength = Integer.parseInt(httpx.getRequestHeader("Content-length"));
            if (contentLength > contentLengthLimit) {
                logger.warn("contentLength {} {}", contentLength, cert);
                status.setAlertType(AlertType.ONCE);
                status.setTopicLabel("Chronica");
                status.getLineList().add("INFO: Content length limit exceeded");
                Topic topic = es.persistTopic(cert, status.getTopicLabel());
                status.setTopic(topic);
                app.getStatusQueue().add(status);
                cert.setEnabled(false);
                throw new Exception("Content length limit exceeded: " + cert);
            }
            byte[] content = new byte[contentLength];
            httpx.getDelegate().getRequestBody().read(content);
            String contentString = new String(content);
            logger.trace("content {}", contentString);
            status = new StatusRecordParser().parse(cert,
                    httpx.getRequestHeaders(),
                    contentString);
            logger.debug("status {}", status);
            Topic topic = es.persistTopic(cert, status.getTopicLabel());
            status.setTopic(topic);
            if (status.getSubscribers() != null) {
                if (status.getSubscribers().size() > 0) {
                    for (String email : status.getSubscribers()) {
                        Person person = es.persistPerson(email);
                        logger.info("subscribe {}", person);
                        es.persistTopicSubscription(topic, person);
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            for (StatusCheck check : status.getChecks()) {
                String result = check.check();
                logger.info("check {}: {}", check.getClass().getSimpleName(), result);
                status.getLineList().add(result);
                builder.append(result);
                builder.append("\n");
            }
            if (builder.length() == 0) {
                builder.append(String.format("OK: %s: %s\n", 
                        cert.getCommonName(), topic.getTopicLabel()));
            }
            es.commit();
            app.getStatusQueue().add(status);
            return new JMap(builder.toString());
        } catch (StorageException se) {
            se.printStackTrace(System.err);
            return new JMap(String.format("error: %s\n", se.getMessage()));
        } catch (CertificateException | NumberFormatException | IOException e) {
            logger.warn(e.getMessage(), e);
            return new JMap(String.format("error: %s: %s\n", 
                    e.getClass(), e.getMessage()));
        }
    }
}
