/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.persistence;

import chronic.api.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entitytype.OrgRoleType;
import chronic.entity.OrgRole;
import chronic.entitykey.OrgRoleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class PersistOrgRole {
    
    static Logger logger = LoggerFactory.getLogger(PersistOrgRole.class);
    
    public OrgRole handle(ChronicHttpx httpx, Cert cert, String email, OrgRoleType roleType) 
            throws StorageException {
        logger.info("enroll {} {}", cert, email);
        User user = new PersistUser().handle(httpx, email);        
        logger.info("user {}", user);
        OrgRoleKey orgRoleKey = new OrgRoleKey(cert.getOrgDomain(), email, roleType);
        OrgRole orgRole = httpx.db.role().find(orgRoleKey);
        if (orgRole == null) {
            orgRole = new OrgRole(orgRoleKey);
            orgRole.setEnabled(true);
            httpx.db.role().add(orgRole);
        }
        return orgRole;
    }
}
