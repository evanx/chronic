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
package chronic.app;

import chronic.check.OpenPortChecker;
import chronic.bundle.Bundle;
import chronic.entitykey.CertKey;
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.enumtype.DelimiterType;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class StatusRecordParser {

    static Logger logger = LoggerFactory.getLogger(StatusRecordParser.class);

    StatusRecord record;

    public StatusRecordParser(CertKey certKey) {
        record = new StatusRecord(certKey);
    }

    private void parseAlertFormatType(String string) {
        try {
            record.setAlertFormatType(AlertFormatType.valueOf(string));
        } catch (Exception e) {
            logger.warn("parseAlertFormatType {}: {}", string, e.getMessage());
        }
    }

    private void parseAlertType(String string) {
        logger.trace("parseAlertType {}", string);
        try {
            record.setAlertType(AlertType.valueOf(string));
        } catch (Exception e) {
            logger.warn("parseAlertType {}: {}", string, e.getMessage());
        }
    }

    public void parseFromLine(String fromLine) {
        Matcher matcher = StatusRecordPatterns.FROM_CRON.matcher(fromLine);
        if (matcher.find()) {
            record.setUsername(matcher.group(1));
            record.setFrom(matcher.group(1));
        }
    }

    public void parseSubjectLine(String subjectLine) {
        Matcher matcher = StatusRecordPatterns.CRON_SUBJECT.matcher(subjectLine);
        if (matcher.find()) {
            record.setUsername(matcher.group(1));
            record.setHostname(matcher.group(2));
            record.setService(matcher.group(3));
            record.setFrom(record.username + '@' + record.hostname);
        } else {
            record.setSubject(subjectLine.substring(9).trim());
        }
    }

    public boolean parseNagiosStatus(String line) {
        Matcher matcher = StatusRecordPatterns.NAGIOS.matcher(line);
        if (matcher.find()) {
            String service = matcher.group(1).trim();
            if (!service.isEmpty()) {
                record.setService(service);
            }
            parseStatusType(matcher.group(2));
            logger.debug("parseNagiosStatus {} {}", matcher.group(1), matcher.group(2));
            return true;
        }
        return false;
    }

    public void parseContentTypeLine(String contentTypeLine) {
        int index = contentTypeLine.indexOf(";");
        if (index > 14) {
            record.setContentType(contentTypeLine.substring(14, index));
        } else {
            record.setContentType(contentTypeLine.substring(14));
        }
    }

    private void parsePeriod(String string) {
        record.setPeriodMillis(Millis.parse(string));
    }

    private void parseStatusType(String string) {
        try {
            record.setStatusType(StatusType.valueOf(string));
        } catch (Exception e) {
            logger.warn("parseStatusType {}: {}", string, e.getMessage());
        }
    }

    private void parseSubscribe(String string) {
        record.setSubcribers(Strings.split(string, DelimiterType.COMMA_OR_SPACE));
    }

    private void parsePort(String string) {
        try {
            record.getChecks().add(OpenPortChecker.parse(string));
        } catch (Exception e) {
            logger.warn("parsePort {}: {}", string, e.getMessage());
        }
    }

    public StatusRecord parse(String text) throws IOException {
        logger.trace("parse: {}", text);
        String[] lines = text.split("\n");
        boolean inHeader = true;
        boolean nagiosStatus = true;
        for (int i = 0; i < lines.length; i++) {
            String line = Strings.trimEnd(lines[i]);
            if (line.startsWith("From: ")) {
                parseFromLine(line);
            } else if (line.startsWith("Subject: ")) {
                parseSubjectLine(line);
            } else if (line.startsWith("Content-Type: ")) {
                parseContentTypeLine(line);
            } else if (line.startsWith("AlertFormat: ")) {
                parseAlertFormatType(line.substring(13).trim());
            } else if (line.startsWith("Subscribe: ")) {
                parseSubscribe(line.substring(11).trim());
            } else if (line.startsWith("Service: ")) {
                record.setService(line.substring(9).trim());
                nagiosStatus = false;
            } else if (line.startsWith("Period: ")) {
                parsePeriod(line.substring(8).trim());
            } else if (line.startsWith("Status: ")) {
                nagiosStatus = false;
                parseStatusType(line.substring(8).trim());
            } else if (line.startsWith("Topic: ")) {
                record.setTopicString(line.substring(7).trim());
            } else if (line.startsWith("Alert: ")) {
                parseAlertType(line.substring(7).trim());
            } else if (line.startsWith("Port: ")) {
                parsePort(line.substring(6).trim());
            } else if (inHeader && line.matches("^\\S*: .*")) {
                logger.trace("header {}", line);
            } else if (inHeader && line.trim().isEmpty()) {
                inHeader = false;
            } else {
                inHeader = false;
                if (nagiosStatus) {
                    parseNagiosStatus(line);
                }
                if (HtmlChecker.sanitary(line)) {
                    record.getLineList().add(line);
                } else {
                    logger.warn("omit not sanitary: {}", line);
                }
            }
        }
        normalize();
        return record;
    }

    private void normalize() {
        if (record.alertType == null) {
            record.alertType = AlertType.CONTENT_CHANGED;
        }
        if (record.service != null) {
            int index = record.service.lastIndexOf("/");
            if (index >= 0) {
                record.service = record.service.substring(index + 1);
            }
            index = record.service.lastIndexOf(".");
            if (index > 0) {
                record.service = record.service.substring(0, index);
            }
        }
        if (record.topicString == null) {
            record.topicString = createTopicString();
        }
    }

    public String createTopicString() {
        if (record.username != null && record.hostname != null && record.service != null) {
            if (record.service.matches("\\s")) {
                return String.format("%s@%s '%s'", record.username, record.hostname, record.service);
            } else {
                return String.format("%s@%s %s", record.username, record.hostname, record.service);
            }
        }
        if (record.username != null && record.hostname != null) {
            return String.format("%s@%s", record.username, record.hostname);
        }
        if (record.service != null) {
            return record.service;
        }
        if (record.subject != null) {
            return record.subject;
        }
        if (record.username == null && record.hostname == null && record.service == null) {
            return Bundle.get("unknown");
        }
        return Strings.joinNotNullArgs("/", record.username, record.hostname, record.service);
    }
}