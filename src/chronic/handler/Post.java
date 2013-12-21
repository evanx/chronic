/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.StatusRecord;
import chronic.app.StatusRecordParser;
import chronic.check.StatusCheck;
import chronic.entity.Cert;
import chronic.entity.Topic;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.storage.StorageException;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Post implements ChronicHttpxHandler {

    final static int contentLengthLimit = 4000;
    final static Logger logger = LoggerFactory.getLogger(Post.class);

    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        try {
            Cert cert = httpx.persistCert();
            int contentLength = Integer.parseInt(httpx.getRequestHeader("Content-length"));
            logger.trace("contentLength {}", contentLength);
            if (contentLength > contentLengthLimit) {
                throw new Exception("Content length limit exceeded");
            }
            byte[] content = new byte[contentLength];
            httpx.getDelegate().getRequestBody().read(content);
            String contentString = new String(content);
            logger.trace("content {}", contentString);
            StatusRecord status = new StatusRecordParser().parse(
                    cert, httpx.getRequestHeaders(), contentString);
            logger.trace("content lines {}: {}", status.getLineList().size(),
                    Strings.formatFirst(status.getLineList()));
            logger.debug("status {}", status);
            Topic topic = httpx.persistTopic(cert, status.getTopicLabel());
            status.setTopic(topic);
            if (status.getSubscribers() != null) {
                if (status.getSubscribers().size() > 0) {
                    for (String subscriber : status.getSubscribers()) {
                        httpx.persistTopicSubscriber(topic, subscriber);
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
                builder.append(String.format("OK: %s: %s\n", cert.getCommonName(), topic.getTopicLabel()));
            }
            httpx.app.getStatusQueue().add(status);
            return new JMap(builder.toString());
        } catch (StorageException se) {
            return new JMap(String.format("error: %s\n", se.getMessage()));
        } catch (CertificateException | NumberFormatException | IOException e) {
            logger.warn(e.getMessage(), e);
            return new JMap(String.format("error: %s: %s\n", 
                    e.getClass(), e.getMessage()));
        }
    }
}
