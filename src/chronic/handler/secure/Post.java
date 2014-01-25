/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.secure;

import chronic.app.ChronicHttpx;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.alert.TopicMessage;
import chronic.alert.TopicMessageParser;
import chronic.api.PlainHttpxHandler;
import chronic.alert.StatusCheck;
import chronic.entity.Cert;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.type.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class Post implements PlainHttpxHandler {

    final static int contentLengthLimit = 4000;
    final static Logger logger = LoggerFactory.getLogger(Post.class);

    @Override
    public String handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        Cert cert = es.persistCert(httpx);
        TopicMessage message = new TopicMessage(cert);
        int contentLength = Integer.parseInt(httpx.getRequestHeader("Content-length"));
        if (contentLength > contentLengthLimit) {
            logger.warn("contentLength {} {}", contentLength, cert);
            message.setStatusType(StatusType.CONTENT_ERROR);
            message.setTopicLabel("Chronica");
            message.getLineList().add("INFO: Content length limit exceeded");
            Topic topic = es.persistTopic(cert, message);
            message.setTopic(topic);
            app.getMessageQueue().add(message);
            cert.setEnabled(false);
            throw new Exception("Content length limit exceeded: " + cert);
        }
        logger.info("contentLength {} {}", contentLength, cert);
        byte[] content = Streams.readBytes(httpx.getDelegate().getRequestBody());
        String contentString = new String(content);
        logger.trace("content {}", contentString);
        new TopicMessageParser(app, message).parse(httpx.getRequestHeaders(), contentString);
        logger.debug("status {}", message);
        Topic topic = es.persistTopic(cert, message);
        message.setTopic(topic);
        if (message.getSubscribers() != null) {
            if (message.getSubscribers().size() > 0) {
                for (String email : message.getSubscribers()) {
                    Person person = es.persistPerson(email);
                    logger.info("subscribe {}", person);
                    es.persistTopicSubscription(topic, person);
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (StatusCheck check : message.getChecks()) {
            String result = check.check();
            logger.info("check {}: {}", check.getClass().getSimpleName(), result);
            message.getLineList().add(result);
            builder.append(result);
            builder.append("\n");
        }
        es.commit();
        app.getMessageQueue().add(message);
        if (builder.length() == 0) {
            builder.append(String.format("OK: %s\n", topic.getTopicLabel()));
        }
        return builder.toString();
    }
}
