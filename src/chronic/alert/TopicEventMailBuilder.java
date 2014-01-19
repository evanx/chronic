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
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.util.TimeZone;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.format.CalendarFormats;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class TopicEventMailBuilder {

    static Logger logger = LoggerFactory.getLogger(TopicEventMailBuilder.class);
    StringBuilder builder = new StringBuilder();
    ChronicApp app;
    TopicEvent alert;
    TimeZone timeZone;

    public TopicEventMailBuilder(ChronicApp app) {
        this.app = app;
    }

    public String build(TopicEvent event, TimeZone timeZone) {
        this.alert = event;
        this.timeZone = timeZone;
        logger.info("build {}", event.message);
        builder.append("<pre>\n");
        if (event.message.getAlertType() == AlertType.CONTENT_CHANGED) {
            appendHeader(event.message);
            builder.append("\n<br><b>Changed lines:</b>\n");
            builder.append(Strings.join("\n", event.message.buildChanged(event.previousMessage)));
            if (event.previousMessage != null) {
                if (event.previousMessage.getTimestamp() != event.message.getTimestamp()) {
                    appendPrevious(event.previousMessage);
                }
            }
            builder.append('\n');
        } else if (event.message.getAlertType() == AlertType.STATUS_CHANGED) {
            append(event.message);
            if (event.previousMessage != null) {
                appendPrevious(event.previousMessage);
            }
        } else {
            append(event.message);
        }
        builder.append(formatFooter(app.getProperties().getSiteUrl()));
        return builder.toString();
    }

    private void appendPrevious(TopicMessage message) {
        builder.append("\n<br><hr><b>Previously:</b>\n");
        appendHeader(message);
        appendContent(message);
    }

    private void append(TopicMessage message) {
        appendHeader(message);
        appendContent(message);
    }

    private void appendContent(TopicMessage message) {
        builder.append(buildContent(message));
    }

    public static String buildContent(TopicMessage message) {
        StringBuilder builder = new StringBuilder();
        for (String line : Strings.trimLines(message.getLineList())) {
            if (line.trim().isEmpty() && builder.length() == 0) {
                continue;
            }
            if (line.equals(message.getSubject())) {
                continue;
            }
            if (message.getAlertFormatType() == AlertFormatType.MINIMAL) {
                Matcher matcher = TopicMessagePatterns.SERVICE_STATUS.matcher(line);
                if (matcher.find()) {
                    int index = line.indexOf(" - ");
                    if (index > 0) {
                        line = line.substring(0, index);
                        builder.append(line);
                        builder.append('\n');
                    }
                }
            } else {
                builder.append(line);
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private void appendHeader(TopicMessage message) {
        logger.info("formatSubject {} {}", message.getStatusType());
        builder.append(String.format("<i>%s</i>", CalendarFormats.timestampFormat.format(timeZone, message.getTimestamp())));
        if (!message.topicLabel.contains(message.getCert().getCommonName())) {
            builder.append(String.format(" %s", message.getCert().getCommonName()));
        }
        builder.append(String.format(" <b>%s</b>", message.getTopic()));
        builder.append(String.format(" %s", message.getStatusType().getLabel()));
    }

    public static String formatFooter(String siteUrl) {
        String style = "font-size: 12px; font-color: gray";
        return String.format("<hr><a style='%s' href='%s'><img src='cid:image'/></a>", style,
                siteUrl, siteUrl);
    }
}
