/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.transaction;

import chronic.*;
import chronic.entity.AdminUser;
import chronic.entity.AdminUserRoleType;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.type.ComparableTuple;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class EnrollTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollTransaction.class);
    ChronicApp app;
    Org org;
    AdminUser user;
    
    public EnrollTransaction(ChronicApp app) {
        this.app = app;
    }
    
    public void handle(String orgUrl, String email) throws StorageException {
        logger.info("enroll {} {}", orgUrl, email);
        ComparableTuple key = Comparables.tuple(orgUrl, email);
        if (app.getStorage().getAdminUserStorage().containsKey(email)) {
            user = app.getStorage().getAdminUserStorage().select(email);
        } else {
            user = new AdminUser(email);
        }
        if (app.getStorage().getOrgStorage().containsKey(orgUrl)) {
            org = app.getStorage().getOrgStorage().select(orgUrl);
            if (app.getStorage().getOrgRoleStorage().containsKey(key)) {
                OrgRole orgRole = app.getStorage().getOrgRoleStorage().select(key);
                if (orgRole.getRole() != AdminUserRoleType.ADMIN) {
                    logger.warn("subscribe exists role {}", orgRole);
                }
            } else {
                OrgRole orgRole = new OrgRole(user, org, AdminUserRoleType.ADMIN);
                app.getStorage().getOrgRoleStorage().insert(orgRole);                
            }
        } else {
            org = new Org(orgUrl);
            OrgRole orgRole = new OrgRole(user, org, AdminUserRoleType.ADMIN);
            app.getStorage().getOrgStorage().insert(org);
            app.getStorage().getOrgRoleStorage().insert(orgRole);
        }
    }
        
}
