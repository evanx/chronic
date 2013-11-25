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
package chronicapp;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;

/**
 *
 * @author evan.summers
 */
public class AlertBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertBuilder.class);
    StringBuilder builder = new StringBuilder();

    public String build(StatusRecord status, StatusRecord previousStatus, StatusRecord previousAlert)
            throws IOException {
        logger.info("build {}", status);
        append(status);
        if (status.getAlertType() == AlertType.CONTENT_CHANGED
                && previousStatus != null && previousStatus.getTimestamp() != status.getTimestamp()) {
            appendPrevious(previousStatus);
        } else if (status.getAlertType() == AlertType.STATUS_CHANGED
                && previousAlert != null) {
            appendPrevious(previousAlert);
        }
        return builder.toString();
    }

    private void append(StatusRecord status) {
        builder.append(String.format("<b>%s</b>\n", formatHtmlSubject(status)));
        builder.append(buildContent(status));
    }

    private void appendPrevious(StatusRecord status) {
            builder.append("\n<hr>Previous:\n");
        builder.append(String.format("<b>%s</b>\n", formatHtmlSubject(status)));
        builder.append(String.format("<i>%s</i>\n", 
                Millis.formatTimestamp(status.getTimestamp())));
        builder.append(buildContent(status));
    }

    public static String buildContent(StatusRecord status) {
        StringBuilder builder = new StringBuilder();
        for (String line : status.getLineList()) {
            if (!line.equals(status.getSubject())) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
        }
        return builder.toString();
    }
    
    public String formatHtmlSubject(StatusRecord status) {
        logger.info("formatHtmlSubject {} {}", status.getStatusType(), status.getStatusType().getLabel());            
        if (status.isAlertable()) {
            if (status.getStatusType() == StatusType.ELAPSED || status.getSubject() == null) {
                return status.getSource() + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else if (!status.getSubject().contains(status.getStatusType().name()))   {
                return status.getSubject() + " <i>" + status.getStatusType().getLabel() + "</i>";
            } else {
                return status.getSubject();
            }
        }
        if (status.getSubject() == null) {
            return status.getSource();
        }
        return status.getSubject();
    }
}
