/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.Topic;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.TopicKey;
import chronic.entitytype.OrgRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class TopicAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicAction.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        String email = app.getEmail(httpx);
        TopicKey key = new TopicKey(httpx.parseJsonMap());
        OrgRoleKey roleKey = new OrgRoleKey(key.getOrgDomain(), email, OrgRoleType.ADMIN);
        if (!app.storage().role().containsKey(roleKey)) {
            return JMaps.create("errorMessage", "no role");
        } else {
            Topic topic = app.storage().topic().select(key);
            topic.setEnabled(true);
            app.storage().topic().update(topic);
            return JMaps.create("topic", topic.getMap());
        }
    }
    
}
