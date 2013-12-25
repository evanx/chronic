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

import static chronic.app.AlertMailBuilder.formatFooter;
import java.io.IOException;
import java.util.Collection;
import vellum.mail.Mailer;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.exception.Exceptions;
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

    public void alert(AlertRecord alert, Collection<String> emails) {
         logger.warn("alert {} {}", alert.toString(), emails);
        for (String email : emails) {
            try {
                AlertRecord previous = alertMap.put(email, alert);
                if (previous != null) {
                    long elapsed = alert.getTimestamp() - previous.getTimestamp();
                    if (elapsed < app.getProperties().getAlertPeriod()
                            && alert.getStatus().getKey().equals(previous.status.getKey())) {
                        logger.warn("elapsed {}", Args.format(email, Millis.formatPeriod(elapsed),
                                alert.getStatus().getKey()));
                    }
                }
                alert.setAlertedStatus(alert.getStatus());
                if (app.getProperties().getAdminDomains().contains(email)) {
                    mailer.send(email, alert.getStatus().getTopicLabel(),
                        new AlertMailBuilder(app).build(alert));
                }
            } catch (IOException | MessagingException e) {
                logger.warn("{} {}", e.getMessage(), alert);
            }
        }
    }

    void alert(Throwable t) {
        logger.warn("alert throwable", t);
        StringBuilder builder = new StringBuilder();
        builder.append("<pre>\n");
        builder.append(Exceptions.printStackTrace(t));
        builder.append("</pre>");
        builder.append(formatFooter(app.getProperties().getSiteUrl()));
        try {
            mailer.send(app.getProperties().getAdminEmail(), "Chronica exception",
                    builder.toString());
        } catch (MessagingException | IOException e) {
            logger.warn("alert throwable email", e);
        }
    }
}
