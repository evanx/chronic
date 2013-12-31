/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.securehandler;

import chronic.app.ChronicHttpx;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.alert.StatusRecord;
import chronic.alert.StatusRecordParser;
import chronic.api.ChronicPlainHttpxHandler;
import chronic.check.StatusCheck;
import chronic.entity.Cert;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.type.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class Post implements ChronicPlainHttpxHandler {

    final static int contentLengthLimit = 4000;
    final static Logger logger = LoggerFactory.getLogger(Post.class);

    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
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
        status = new StatusRecordParser().parse(cert, httpx.getRequestHeaders(), contentString);
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
        es.commit();
        app.getStatusQueue().add(status);
        if (builder.length() == 0) {
            builder.append(String.format("OK: %s\n", topic.getTopicLabel()));
        }
        return builder.toString();
    }
}
