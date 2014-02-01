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
import chronic.app.ChronicProperties;
import java.io.IOException;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.mail.Mailer;

/**
 *
 * @author evan.summers
 */
public class ChronicMessenger {

    static Logger logger = LoggerFactory.getLogger(ChronicMessenger.class);
    ChronicProperties properties;
    Mailer mailer;

    public ChronicMessenger(ChronicApp app) {
        this.properties = app.getProperties();
        this.mailer = app.getMailer();
    }

    public void alert(Throwable t) {
        logger.warn("alert throwable", t);
        StringBuilder builder = new StringBuilder();
        builder.append("<pre>\n");
        builder.append(Exceptions.printStackTrace(t));
        builder.append("</pre>");
        builder.append(formatFooter(properties.getSiteUrl()));
        try {
            mailer.send(properties.getAdminEmail(), "Chronica exception",
                    builder.toString());
        } catch (MessagingException | IOException e) {
            logger.warn("alert throwable email", e);
        }
    }
}
