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
import chronic.app.ChronicEntityService;
import chronic.entity.Alert;
import chronic.entity.Subscription;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import javax.mail.MessagingException;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.util.Calendars;

/**
 *
 * @author evan.summers
 */
public class ChronicAlerter {

    static Logger logger = LoggerFactory.getLogger(ChronicAlerter.class);
    ChronicApp app;
    ChronicEntityService es;
    TopicEvent event;
    
    public void alert(ChronicApp app, TopicEvent event) {
        this.app = app;
        this.event = event;
        es = new ChronicEntityService(app);
        try {
            es.begin();
            List<Subscription> subscriptions = es.listSubscriptions(event.getMessage().getTopic());
            logger.info("subscriptions {}", subscriptions);
            es.close();
            for (Subscription subscription : subscriptions) {
                event.getPendingEmails().add(subscription.getEmail());
            }
            logger.info("alert {}: pending emails {}", event.toString(), event.getPendingEmails());
            for (Subscription subscription : subscriptions) {
                try {
                    if (event.getPendingEmails().contains(subscription.getEmail())) {
                        if (alert(subscription)) {
                            event.getPendingEmails().remove(subscription.getEmail());
                        }
                    }
                } catch (IOException | MessagingException e) {
                    logger.warn("{} {}", e.getMessage(), event);
                } finally {
                    es.close();
                }
            }
        } catch (PersistenceException e) {
            logger.error("alert", e);
        } catch (Exception e) {
            logger.error("alert", e);
        } finally {
            es.close();
        }
    }

    private boolean alert(Subscription subscription) throws MessagingException, IOException {
        String email = subscription.getEmail();
        TimeZone timeZone = subscription.getTimeZone();
        logger.info("alert {}: {}", subscription, event.getMessage());
        TopicEvent previousEvent = app.getSentMap().get(email);
        if (previousEvent != null) {
            long elapsed = System.currentTimeMillis() - previousEvent.getTimestamp();
            if (elapsed < app.getProperties().getAlertPeriod()
                    && event.getMessage().getKey().equals(previousEvent.message.getKey())) {
                logger.warn("elapsed {}", Millis.formatPeriod(elapsed));
                return false;
            }
        }
        Alert previousAlert = app.getAlertMap().get(subscription.getSubscriptionKey());
        if (previousAlert != null) {
            long elapsed = System.currentTimeMillis() - previousAlert.getAlerted().getTimeInMillis();
            if (elapsed < app.getProperties().getAlertPeriod()) {
                logger.warn("elapsed {}: {}", Millis.formatPeriod(elapsed), previousAlert);
            } else if (previousAlert.getStatusType().isStatusAlertable() &&
                    previousAlert.getStatusType() == event.getMessage().getStatusType()) {
                logger.error("status: {}", previousAlert);
                return true;
            }
        }        
        if (!app.getProperties().isAdmin(email)) {
            logger.warn("omit email alert: {}", email);
        } else {
            app.getMailer().send(email, event.getMessage().getTopicLabel(),
                    new TopicEventMailBuilder(app).build(event, timeZone));
            app.getSentMap().put(email, event);
            Alert alert = new Alert(event.getMessage().getTopic(),
                    event.getMessage().getStatusType(),
                    Calendars.newCalendar(timeZone, event.getMessage().getTimestamp()),
                    email);
            es.begin();
            es.persist(alert);
            es.commit();
            app.getAlertMap().put(alert.getSubscriptionKey(), alert);
        }
        return true;
    }

}
