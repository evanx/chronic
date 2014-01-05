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
public class AlertMailBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertMailBuilder.class);
    StringBuilder builder = new StringBuilder();
    ChronicApp app;
    TopicEvent alert;
    TimeZone timeZone;

    public AlertMailBuilder(ChronicApp app) {
        this.app = app;
    }

    public String build(TopicEvent alert, TimeZone timeZone) {
        this.alert = alert;
        this.timeZone = timeZone;
        logger.info("build {}", alert.message);
        builder.append("<pre>\n");
        if (alert.message.getAlertType() == AlertType.CONTENT_CHANGED) {
            appendHeader(alert.message);
            builder.append("\n<br><b>Changed lines:</b>\n");
            builder.append(Strings.join("\n", alert.message.buildChanged(alert.previousMessage)));
            if (alert.previousMessage != null) {
                if (alert.previousMessage.getTimestamp() != alert.message.getTimestamp()) {
                    appendPrevious(alert.previousMessage);
                }
            }
            builder.append('\n');
        } else if (alert.message.getAlertType() == AlertType.STATUS_CHANGED) {
            append(alert.message);
            if (alert.previousMessage != null) {
                appendPrevious(alert.previousMessage);
            }
        } else {
            append(alert.message);
        }
        builder.append(formatFooter(app.getProperties().getSiteUrl()));
        return builder.toString();
    }

    private void appendPrevious(TopicMessage status) {
        builder.append("\n<br><hr><b>Previously:</b>\n");
        appendHeader(status);
        appendContent(status);
    }

    private void append(TopicMessage status) {
        appendHeader(status);
        appendContent(status);
    }

    private void appendHeader(TopicMessage status) {
        builder.append(String.format("<i>%s</i>", CalendarFormats.timestampFormat.format(timeZone, status.getTimestamp())));
        builder.append(String.format(" <b>%s</b>", formatHeading(status)));
        String alertTypeStyle = "font-color: gray";
        builder.append(String.format(" <i style='%s'>(Alert on %s)</i>\n\n", alertTypeStyle,
                status.getAlertType().getLabel()));
    }

    private void appendContent(TopicMessage status) {
        builder.append(buildContent(status));
    }

    public static String buildContent(TopicMessage status) {
        StringBuilder builder = new StringBuilder();
        for (String line : Strings.trimLines(status.getLineList())) {
            if (line.trim().isEmpty() && builder.length() == 0) {
                continue;
            }
            if (line.equals(status.getSubject())) {
                continue;
            }
            if (status.getAlertFormatType() == AlertFormatType.MINIMAL) {
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

    private void appendln(String string) {
        builder.append(string);
        builder.append('\n');
    }

    private void appendf(String string, Object... args) {
        builder.append(String.format(string, args));
    }

    public static String formatHeading(TopicMessage status) {
        logger.info("formatSubject {} {}", status.getStatusType());
        if (status.isStatusKnown()) {
            if (status.getStatusType() == StatusType.ELAPSED || status.getSubject() == null) {
                return status.getTopicLabel()
                        + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else if (!status.getSubject().contains(status.getStatusType().name())) {
                return status.getSubject()
                        + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else {
                return status.getSubject();
            }
        }
        if (status.getSubject() == null) {
            return status.getTopicLabel();
        }
        return status.getSubject();
    }

    public static String formatFooter(String siteUrl) {
        String style = "font-size: 12px; font-color: gray";
        return String.format("<hr><a style='%s' href='%s'><img src='cid:image'/></a>", style,
                siteUrl, siteUrl);
    }

}
