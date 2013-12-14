/*
 * Source https://github.com/evanx by @evanxsummers

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

import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class AlertBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertBuilder.class);
    StringBuilder builder = new StringBuilder();
    AlertRecord alert;
    
    public String build(AlertRecord alert) {
        this.alert = alert;
        logger.info("build {}", alert.status);
        builder.append("<pre>\n");
        if (alert.status.getAlertType() == AlertType.CONTENT_CHANGED) {
            appendHeading(alert.status);
            builder.append("\n<b>Changed:</b>\n\n");
            builder.append(Strings.join("\n", alert.status.buildChanged(alert.previousStatus)));
            if (alert.previousStatus.getTimestamp() != alert.status.getTimestamp()) {
                appendPrevious(alert.previousStatus);
            }
            builder.append('\n');
        } else if (alert.status.getAlertType() == AlertType.STATUS_CHANGED) {
            append(alert.status);
            appendPrevious(alert.previousStatus);
        } else {            
            append(alert.status);
        }
        builder.append("<hr><img src='cid:image'/>");
        return builder.toString();
    }

    private void append(StatusRecord status) {
        appendHeading(status);
        appendContent(status);
    }

    private void appendHeading(StatusRecord status) {
        builder.append(String.format("<b>%s</b>\n", formatSubject(status)));
        builder.append(String.format("<i>%s</i>\n\n", Millis.formatTime(status.getTimestamp())));
    }

    private void appendContent(StatusRecord status) {
        builder.append(buildContent(status));
    }
    
    private void appendPrevious(StatusRecord status) {
        builder.append("\n<hr><b>Previously:</b>\n");
        builder.append(String.format("<b><i>%s</i></b>\n", formatSubject(status)));
        builder.append(String.format("<i>%s</i>\n\n",
                Millis.formatTime(status.getTimestamp())));
        builder.append(buildContent(status));
    }
    
    public static String buildContent(StatusRecord status) {
        StringBuilder builder = new StringBuilder();
        for (String line : Strings.trimLines(status.getLineList())) {
            if (line.trim().isEmpty() && builder.length() == 0) {
                continue;
            }
            if (line.equals(status.getSubject())) {
                continue;
            }
            if (status.getAlertFormatType() == AlertFormatType.MINIMAL) {
                Matcher matcher = StatusRecordParser.nagiosStatusPattern.matcher(line);
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

    public String formatSubject(StatusRecord status) {
        logger.info("formatSubject {} {}", status.getStatusType(),
                status.getStatusType().getLabel());
        if (status.isAlertable()) {
            if (status.getStatusType() == StatusType.ELAPSED || status.getSubject() == null) {
                return status.getTopicString()
                        + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else if (!status.getSubject().contains(status.getStatusType().name())) {
                return status.getSubject()
                        + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else {
                return status.getSubject();
            }
        }
        if (status.getSubject() == null) {
            return status.getTopicString();
        }
        return status.getSubject();
    }
    
}
