/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.persistence;

import chronic.api.ChronicHttpx;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class PersistCertSubscriber {
    
    static Logger logger = LoggerFactory.getLogger(PersistCertSubscriber.class);
    
    public void handle(ChronicHttpx httpx, Cert cert, String email) throws StorageException {
        logger.info("handle {} {}", cert, email);
        User user = httpx.db.user().find(email);
        if (user == null) {
            user = new User(email);
            httpx.db.user().add(user);
        }
        for (Topic topic : httpx.db.topic().list(cert.getKey())) {
            new PersistSubscriber().handle(httpx, topic, email);
        }
        
    }
}
