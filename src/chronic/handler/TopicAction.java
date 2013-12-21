/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.entity.Topic;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.CertTopicKey;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class TopicAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicAction.class);
  
    @Override
    public JMap handle(ChronicHttpx httpx) throws Exception {
        String email = httpx.getEmail();
        JMap topicMap = httpx.parseJsonMap().getMap("topic");
        CertTopicKey key = new CertTopicKey(topicMap);
        OrgRoleKey roleKey = new OrgRoleKey(topicMap.getString("orgDomain"), email, OrgRoleType.ADMIN);
        if (!httpx.db.role().containsKey(roleKey)) {
            return JMaps.mapValue("errorMessage", "no role");
        } else {
            Topic topic = httpx.db.topic().find(key);
            topic.setEnabled(!topic.isEnabled());
            httpx.db.topic().replace(topic);
            return JMaps.mapValue("topic", topic.getMap());
        }
    }
    
}
