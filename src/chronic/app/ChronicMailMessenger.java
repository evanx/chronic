/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic.app;

import vellum.mail.Mailer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.storage.StorageException;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class ChronicMailMessenger {

    static Logger logger = LoggerFactory.getLogger(ChronicMailMessenger.class);
    ChronicApp app;
    Mailer mailer;
    Map<String, AlertRecord> alertMap = new HashMap();

    public ChronicMailMessenger(ChronicApp app) {
        this.app = app;
    }

    public void init() throws Exception {
        logger.info("initialized");
        mailer = new Mailer(app.getProperties().getMailerProperties());
    }

    public synchronized void alert(ChronicDatabase db, AlertRecord alert) {
        logger.warn("alert {}", alert.toString());
        try {
            for (String email : db.listSubscriberEmails(alert.getStatus().getTopic())) {
                AlertRecord previous = alertMap.put(email, alert);
                if (previous != null) {
                    long elapsed = alert.getTimestamp() - previous.getTimestamp();
                    if (elapsed < app.getProperties().getAlertPeriod() &&
                            alert.getStatus().getKey().equals(previous.status.getKey())) {
                        logger.warn("elapsed {}", Args.format(email, Millis.formatPeriod(elapsed),
                                alert.getStatus().getKey()));
                    }
                }
                alert.setAlertedStatus(alert.getStatus());
                mailer.sendEmail(email,
                        alert.getStatus().getTopicLabel(),
                        new AlertMailBuilder(app).build(alert));
            }
        } catch (StorageException e) {
            logger.warn("{} {}", e.getMessage(), alert);
        }
    }

    public void alertAdmins(String subject) {
        alertAdmins(subject, null);
    }
    
    public void alertAdmins(String subject, String content) {
        if (content == null) {
            content = "";
        }
        content += new AlertMailBuilder(app).formatFooter();
        for (String email : app.getProperties().getAdminEmails()) {
            logger.info("alertAdmins email {}", email);
            mailer.sendEmail(email, subject, content);
        }
    }
    
}
