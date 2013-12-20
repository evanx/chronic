/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
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
public class EnrollRoleTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollRoleTransaction.class);
    
    public OrgRole handle(ChronicApp app, Cert cert, String email, OrgRoleType roleType) 
            throws StorageException {
        logger.info("enroll {} {}", cert, email);
        User user = new EnrollUserTransaction().handle(app, email);        
        logger.info("user {}", user);
        OrgRoleKey orgRoleKey = new OrgRoleKey(cert.getOrgDomain(), email, roleType);
        OrgRole orgRole = app.storage().role().find(orgRoleKey);
        if (orgRole == null) {
            orgRole = new OrgRole(orgRoleKey);
            orgRole.setEnabled(true);
            app.storage().role().add(orgRole);
        }
        return orgRole;
    }
}
