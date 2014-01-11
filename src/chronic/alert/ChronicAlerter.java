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
package chronic.alert;

import chronic.app.ChronicApp;
import static chronic.alert.TopicEventMailBuilder.formatFooter;
import chronic.app.ChronicEntityService;
import chronic.entity.Alert;
import chronic.entity.Subscription;
import java.io.IOException;
import vellum.mail.Mailer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.exception.Exceptions;
import vellum.util.Args;
import vellum.util.Calendars;

/**
 *
 * @author evan.summers
 */
public class ChronicAlerter {

    static Logger logger = LoggerFactory.getLogger(ChronicAlerter.class);
    ChronicApp app;
    Mailer mailer;
    Map<String, TopicEvent> alertMap = new HashMap();

    public ChronicAlerter(ChronicApp app) {
        this.app = app;
    }

    public void init() throws Exception {
        logger.info("initialized");
        mailer = new Mailer(app.getProperties().getMailerProperties());
    }

    public void alert(ChronicEntityService es, TopicEvent event) {
        List<Subscription> subscriptions =
                es.listSubscriptions(event.getMessage().getTopic());
         logger.warn("alert {}", event.toString());
        for (Subscription subscription : subscriptions) {
            String email = subscription.getEmail();
            TimeZone timeZone = subscription.getTimeZone();
            try {
                TopicEvent previous = alertMap.put(email, event);
                if (previous != null) {
                    long elapsed = event.getTimestamp() - previous.getTimestamp();
                    if (elapsed < app.getProperties().getAlertPeriod()
                            && event.getMessage().getKey().equals(previous.message.getKey())) {
                        logger.warn("elapsed {}", Args.format(email, Millis.formatPeriod(elapsed),
                                event.getMessage().getKey()));
                    }
                }
                if (app.getProperties().getAdminDomains().contains(email)) {
                    Alert alert = new Alert(event.getMessage().getTopic(),
                            event.getMessage().getStatusType(),
                            Calendars.newCalendar(timeZone, event.getMessage().getTimestamp()),
                            email);
                    mailer.send(email, event.getMessage().getTopicLabel(),
                            new TopicEventMailBuilder(app).build(event, timeZone));
                }
            } catch (IOException | MessagingException e) {
                logger.warn("{} {}", e.getMessage(), event);
            }
        }
    }
    
    public void alert(Throwable t) {
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
