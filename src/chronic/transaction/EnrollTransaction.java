/*
 * Source https://github.com/evanx by @evanxsummers
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
        OrgRole orgRole = app.store().roles().
            select(Comparables.tuple(orgUrl, email, OrgRoleType.ADMIN));
        if (orgRole == null) {
            boolean enabled = app.getProperties().isAdmin(email);
            User user = app.store().users().select(email);
            if (user == null) {
                user = new User(email);
                user.setEnabled(enabled);
                app.store().users().insert(user);
            }
            Org org = app.store().orgs().select(orgUrl);
            if (org == null) {
                org = new Org(orgUrl);
                app.store().orgs().insert(org);
                enabled = true;
            } else if (!enabled) {
                enabled = !app.store().isOrgRoleType(orgUrl, OrgRoleType.ADMIN);
            }
            orgRole = new OrgRole(org, user, OrgRoleType.ADMIN);
            orgRole.setEnabled(enabled);
            app.store().roles().insert(orgRole);
        }
    }
}
