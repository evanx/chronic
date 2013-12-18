/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.User;
import chronic.entitytype.OrgRoleType;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entitykey.OrgRoleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class EnrollTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollTransaction.class);
    
    public void handle(ChronicApp app, String orgDomain, String email) throws StorageException {
        logger.info("enroll {} {}", orgDomain, email);
        OrgRoleKey orgRoleKey = new OrgRoleKey(orgDomain, email, OrgRoleType.ADMIN);
        OrgRole orgRole = app.storage().role().select(orgRoleKey);
        if (orgRole == null) {
            boolean enabled = app.getProperties().isAdmin(email);
            User user = app.storage().user().select(email);
            if (user == null) {
                user = new User(email);
                user.setEnabled(enabled);
                app.storage().user().insert(user);
            }
            Org org = app.storage().org().select(orgDomain);
            if (org == null) {
                org = new Org(orgDomain);
                app.storage().org().insert(org);
                enabled = true;
            } else if (!enabled) {
                enabled = !app.storage().isOrgRoleType(orgDomain, OrgRoleType.ADMIN);
            }
            orgRole = new OrgRole(orgRoleKey);
            orgRole.setEnabled(enabled);
            app.storage().role().insert(orgRole);
        }
    }
}
