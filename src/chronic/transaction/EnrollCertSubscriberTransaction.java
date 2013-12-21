/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.transaction;

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
public class EnrollCertSubscriberTransaction {
    
    static Logger logger = LoggerFactory.getLogger(EnrollCertSubscriberTransaction.class);
    
    public void handle(ChronicHttpx httpx, Cert cert, String email) throws StorageException {
        logger.info("handle {} {}", cert, email);
        User user = httpx.db.user().find(email);
        if (user == null) {
            user = new User(email);
            httpx.db.user().add(user);
        }
        for (Topic topic : httpx.db.topic().list(cert.getKey())) {
            new EnrollSubscriberTransaction().handle(httpx, topic, email);
        }
        
    }
}
