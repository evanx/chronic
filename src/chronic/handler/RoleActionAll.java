/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicApp;
import chronic.app.ChronicHttpxHandler;
import chronic.entity.OrgRole;
import chronic.entity.Subscriber;
import chronic.entity.Topic;
import chronic.entitykey.SubscriberKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class RoleActionAll implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(RoleActionAll.class);
  
    ChronicApp app;
    String email;
    
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        this.app = app;
        email = app.getEmail(httpx);
        List roles = new LinkedList();
        for (OrgRole role : app.storage().listRoles(app.getEmail(httpx))) {
            if (!role.isEnabled()) {
                role.setEnabled(true);
                app.storage().role().update(role);
            }
            roles.add(role.getMap());
        }
        return JMaps.mapValue("roles", roles);
    }
    
}
