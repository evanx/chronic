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

/**
 *
 * @author evan.summers
 */
public class AlertBuilder {

    static Logger logger = LoggerFactory.getLogger(AlertBuilder.class);
    StringBuilder builder = new StringBuilder();

    public String build(StatusRecord status, StatusRecord previousStatus, AlertRecord previousAlert)
            throws IOException {
        logger.info("build {}", status);
        if (status.statusType == StatusType.ELAPSED) {
            builder.append(String.format("<b>%s</b>\n", status.formatSubject()));
            builder.append("\n<hr><b>Previous:</b>\n");
            append(status);
        } else {
            append(status);
            if (status.getAlertType() == AlertType.CONTENT_CHANGED
                    && previousStatus != null && previousStatus != status) {
                builder.append("\n<hr><b>Previous:</b>\n");
                append(previousStatus);
            } else if (status.getAlertType() == AlertType.STATUS_CHANGED
                    && previousAlert != null) {
                builder.append("\n<hr><b>Previous:</b>\n");
                append(previousAlert.getStatusRecord());
            }
        }
        builder.append("\n<hr>");
        return builder.toString();
    }

    private void append(StatusRecord status) {
        if (status.getSubject() != null) {
            if (status.isAlertable() && 
                    !status.getSubject().contains(status.getStatusType().name())) {
                builder.append(String.format("<b>%s <i>%s</i></b>\n", 
                        status.getSubject(), status.getStatusType().name()));
            } else {
                builder.append(String.format("<b>%s</b>\n", status.getSubject()));
            }
        }
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
}
