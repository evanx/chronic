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

import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.jx.JMap;
import vellum.type.ComparableTuple;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class StatusRecord {

    static Logger logger = LoggerFactory.getLogger(StatusRecord.class);
    public final static Pattern subjectCronPattern = 
            Pattern.compile("^Subject: Cron <(\\S+)@(\\S+)> (.*)"); 
    public final static Pattern nagiosStatusPattern = 
            Pattern.compile("^(\\S+) (OK|WARNING|CRITICAL|UNKNOWN) - (.*)$");
    static Pattern headPattern = Pattern.compile("^[a-zA-Z]+: .*$");
    List<String> lineList = new ArrayList();
    AlertType alertType = AlertType.NONE;
    AlertFormatType alertFormatType;
    String topic;
    StatusType statusType = StatusType.UNKNOWN;
    long timestamp = System.currentTimeMillis();
    long periodMillis;
    String fromLine;
    String subjectLine;
    String contentTypeLine;
    String contentType;
    String from;
    String subject;
    String username;
    String hostname;
    String service;
    String period;

    public StatusRecord() {
    }

    public StatusRecord(StatusRecord record) {
        this.alertType = record.alertType;
        this.alertFormatType = record.alertFormatType;
        this.topic = record.topic;
        this.periodMillis = record.periodMillis;
        this.from = record.from;
        this.subject = record.subject;
        this.hostname = record.hostname;
        this.username = record.username;
        this.service = record.service;
    }

    public ComparableTuple getKey() {
        return ComparableTuple.create(username, hostname, service);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setFromLine(String fromLine) {
        this.fromLine = fromLine;
        String fromCronPattern = "^From: ([a-z]+) \\(Cron Daemon\\)$";
        username = fromLine.replaceAll(fromCronPattern, "$1");
        from = username;
    }

    public String getFrom() {
        return from;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void parseSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
        Matcher matcher = subjectCronPattern.matcher(subjectLine);
        if (matcher.find()) {
            username = matcher.group(1);
            hostname = matcher.group(2);
            service = matcher.group(3);
            from = username + '@' + hostname;
        } else {
            subject = subjectLine.substring(9).trim();
        }
    }

    public boolean parseNagiosStatus(String line) {
        Matcher matcher = nagiosStatusPattern.matcher(line);
        if (matcher.find()) {
            service = matcher.group(1);
            parseStatusType(matcher.group(2));
            subject = line;
            logger.debug("parseNagiosStatus {} {}", statusType, service);
            return true;
        }
        return false;
    }

    public void parseContentTypeLine(String contentTypeLine) {
        this.contentTypeLine = contentTypeLine;
        int index = contentTypeLine.indexOf(";");
        if (index > 14) {
            contentType = contentTypeLine.substring(14, index);
        } else {
            contentType = contentTypeLine.substring(14);
        }
    }

    public String getSubject() {
        return subject;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public String getTopic() {
        return topic;
    }

    public AlertFormatType getAlertFormatType() {
        return alertFormatType;
    }
    
    public long getPeriodMillis() {
        return periodMillis;
    }

    public List<String> getLineList() {
        return lineList;
    }

    public boolean isLinesChanged(StatusRecord other) {
        if (lineList.size() != other.lineList.size()) {
            return true;
        }
        for (int i = 0; i < lineList.size(); i++) {
            Matcher matcher = nagiosStatusPattern.matcher(lineList.get(i));
            if (matcher.find()) {
                String nagiosService = matcher.group(1);
                String nagiosStatus = matcher.group(2);
                matcher = nagiosStatusPattern.matcher(other.lineList.get(i));
                if (matcher.find()) {
                    if (!nagiosService.equals(matcher.group(1))
                            || !nagiosStatus.equals(matcher.group(2))) {
                        return true;
                    }
                }

            } else if (!headPattern.matcher(lineList.get(i)).find()
                    && !lineList.get(i).equals(other.lineList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public String formatAlertTypeLabel() {
        if (statusType != null && statusType.isAlertable()) {
            return statusType.getLabel();
        } else if (alertType == null && statusType != null) {
            return statusType.getLabel();
        } else if (alertType != null) {
            return alertType.getLabel();
        } else {
            return StatusType.UNKNOWN.getLabel();
        }
    }
    
    @Override
    public String toString() {
        return Arrays.toString(new Object[]{getSource(), alertType, topic, statusType});
    }

    public String buildContent() {
        StringBuilder builder = new StringBuilder();
        for (String line : lineList) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString().trim();
    }

    public static StatusRecord parse(String text) throws IOException {
        logger.info("parse: {}", text);
        StatusRecord record = new StatusRecord();
        boolean inHeader = true;
        String[] lines = text.split("\n");
        boolean nagiosStatus = true;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("From: ")) {
                record.setFromLine(line);
            } else if (line.startsWith("Subject: ")) {
                record.parseSubjectLine(line);
            } else if (line.startsWith("Content-Type: ")) {
                record.parseContentTypeLine(line);
            } else if (line.startsWith("Status: ")) {
                nagiosStatus = false;
                record.parseStatusType(line.substring(8));
            } else if (line.startsWith("Service: ")) {
                record.setService(line.substring(9));
                nagiosStatus = false;
            } else if (line.startsWith("Alert: ")) {
                record.parseAlertType(line.substring(7));
            } else if (line.startsWith("AlertFormat: ")) {
                record.parseAlertFormatType(line.substring(13));
            } else if (line.startsWith("Period: ")) {
                record.parsePeriod(line.substring(8));
            } else if (!inHeader) {
                if (nagiosStatus) {
                    record.parseNagiosStatus(line);
                }
                record.getLineList().add(line);
            } else if (line.length() == 0) {
                inHeader = false;
            }
        }
        record.normalize();
        return record;
    }

    private void normalize() {
        if (service != null) {
            int index = service.lastIndexOf("/");
            if (index >= 0) {
                service = service.substring(index + 1);
            }
            index = service.lastIndexOf(".");
            if (index > 0) {
                service = service.substring(0, index);
            }
        }
        if (subject == null && username != null && username != null && service != null) {            
            subject = getSource();
        }
    }

    private void parseStatusType(String string) {
        try {
            statusType = StatusType.valueOf(string);
        } catch (Exception e) {
            logger.warn("parseStatusType {}: {}", string, e.getMessage());
        }
    }

    private void parsePeriod(String string) {
        periodMillis = Millis.parse(string);
    }

    private void parseAlertFormatType(String string) {
        try {
            alertFormatType = AlertFormatType.valueOf(string);
        } catch (Exception e) {
            logger.warn("parseAlertFormatType {}: {}", string, e.getMessage());
        }
    }
    
    private void parseAlertType(String string) {
        int index = string.indexOf(" ");
        if (index > 0) {
            topic = string.substring(index + 1);
            string = string.substring(0, index);
        }
        try {
            alertType = AlertType.valueOf(string);
        } catch (Exception e) {
            logger.warn("parseAlertType {}: {}", string, e.getMessage());
        }
    }
    
    public boolean isAlertable() {
        return statusType != null && statusType.isAlertable();
    }

    public boolean isAlertable(StatusRecord previousStatus,
            StatusRecord previousAlert) {
        if (previousAlert == null) {
        } else {
            if (previousAlert.getStatusType() == StatusType.ELAPSED
                    && statusType != StatusType.ELAPSED) {
                return true;
            }
        }
        if (alertType == AlertType.ALWAYS) {
            return true;
        }
        if (alertType == AlertType.PATTERN) {
        } else if (alertType == AlertType.ERROR) {
        }
        if (previousStatus == null) {
            return false;
        }
        if (alertType == AlertType.CONTENT_CHANGED) {
            if (isLinesChanged(previousStatus)) {
                statusType = StatusType.CONTENT_CHANGED;
                return true;
            }
        } else if (alertType == AlertType.STATUS_CHANGED) {
            if (statusType == previousStatus.statusType) {
                return statusType.isAlertable() && previousAlert != null
                        && statusType != previousAlert.getStatusType();
            }
        } else {
        }
        return false;
    }

    public Map getAlertMap() {
        Map map = new HashMap();
        map.put("from", from);
        map.put("username", username);
        map.put("hostname", hostname);
        map.put("service", service);
        map.put("statusType", statusType);
        map.put("alertType", alertType);
        map.put("alertTypeLabel", formatAlertTypeLabel());
        map.put("alertTopic", topic);
        map.put("timestamp", Millis.format(timestamp));
        map.put("source", getSource());
        map.put("subject", formatSubject());
        return map;
    }

    public String formatSubject() {
        if (isAlertable()) {
            if (statusType == StatusType.ELAPSED || subject == null) {
                return getSource() + ' ' + statusType.getLabel();
            } else if (!subject.contains(statusType.name())) {
                return subject + ' ' + statusType.getLabel();
            } else {
                return subject;
            }
        }
        if (subject == null) {
            return getSource();
        }
        return subject;
    }

    public String getSource() {
        if (username != null && hostname != null && service != null) {
            if (service.matches("\\s")) {
                return String.format("%s@%s::(%s)", username, hostname, service);                
            } else {
                return String.format("%s@%s::%s", username, hostname, service);
            }
        }
        if (username != null && hostname != null) {
            return String.format("%s@%s", username, hostname);
        }
        if (username == null && hostname == null && service == null) {
            return "//";
        }
        return Strings.joinNotNullArgs("/", username, hostname, service);
    }
}
