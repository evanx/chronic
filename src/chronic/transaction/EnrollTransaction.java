/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.User;
import chronic.entitytype.OrgRoleType;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class EnrollTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollTransaction.class);
    
    public void handle(ChronicApp app, String orgUrl, String email) throws StorageException {
        logger.info("enroll {} {}", orgUrl, email);
        OrgRole orgRole = app.getStorage().getOrgRoleStorage().
            select(Comparables.tuple(orgUrl, email, OrgRoleType.ADMIN));
        if (orgRole == null) {
            boolean enabled = app.getProperties().isAdmin(email);
            User user = app.getStorage().getUserStorage().select(email);
            if (user == null) {
                user = new User(email);
                user.setEnabled(enabled);
                app.getStorage().getUserStorage().insert(user);
            }
            Org org = app.getStorage().getOrgStorage().select(orgUrl);
            if (org == null) {
                org = new Org(orgUrl);
                app.getStorage().getOrgStorage().insert(org);
                enabled = true;
            } else if (!enabled) {
                enabled = !app.getStorage().isOrgRoleType(orgUrl, OrgRoleType.ADMIN);
            }
            orgRole = new OrgRole(org, user, OrgRoleType.ADMIN);
            orgRole.setEnabled(enabled);
            app.getStorage().getOrgRoleStorage().insert(orgRole);
        }
    }
}
