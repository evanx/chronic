/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.app;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.alert.TopicEvent;
import chronic.alert.TopicEventMapper;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.persona.PersonaException;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class TopicEventList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicEventList.class);
    
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        String email = httpx.getEmail();
        TimeZone timeZone = httpx.getTimeZone();
        List topicEvents = new LinkedList();
        for (TopicEvent topicEvent : Lists.sortedLinkedList(app.getEventMap().values(),
                TimestampedComparator.reverse())) {            
            TopicEventMapper mapper = new TopicEventMapper(topicEvent, timeZone);
            if (es.isSubscription(topicEvent.getMessage().getTopic(), email)) {
                topicEvents.add(mapper.getExtendedMap());
            } else if (httpx.getReferer().endsWith("/demo")) {
                if (topicEvent.getMessage().getStatusType().isKnown() && topicEvent.getMessage().getAlertType().isPollable()) {
                    topicEvents.add(mapper.getBasicMap());
                }
            } else if (app.getProperties().isAdmin(email)) {
                topicEvents.add(mapper.getExtendedMap());
            }
        }
        if (false) {
            Thread.sleep(2000);
            throw new PersonaException("test persona exception");
        }
        return JMaps.mapValue("topicEvents", topicEvents);
    }
}
