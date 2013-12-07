/*
 * Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic;

import chronic.mail.Mailer;
import java.io.IOException;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.system.Executor;

/**
 *
 * @author evan.summers
 */
public class ChronicMessenger {
    Logger logger = LoggerFactory.getLogger(getClass());
    ChronicApp app;
    Mailer mailer;
    
    public ChronicMessenger(ChronicApp app) {
        this.app = app;
        if (app.getProperties().getMailer().isEnabled()) {
            mailer = new Mailer(app.getProperties().getMailer());
        }
    }

    public void init() throws Exception {
        logger.info("initialized");
    }

    public synchronized void alert(AlertRecord alert) {
        logger.info("alert {}", alert.toString());
        try {
            if (app.getProperties().getAlertScript() != null) {
                Executor executor = new Executor();
                executor.exec(app.getProperties().getAlertScript(),
                        new AlertBuilder().build(alert).getBytes(),
                        alert.getAlertMap(true));
                if (executor.getExitCode() != 0 || !executor.getError().isEmpty()) {
                    logger.warn("process {}: {}", executor.getExitCode(), executor.getError());
                }
            }
            if (mailer != null) {
                for (String email : app.getStorage().getEmails(alert)) {
                    mailer.sendEmail(email,
                            alert.getStatus().getSubject(), new AlertBuilder().build(alert));
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
