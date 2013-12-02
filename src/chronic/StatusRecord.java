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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.type.ComparableTuple;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class StatusRecord {

    static Logger logger = LoggerFactory.getLogger(StatusRecord.class);

    List<String> lineList = new ArrayList();
    AlertType alertType = AlertType.NONE;
    AlertFormatType alertFormatType;
    String topic;
    StatusType statusType = StatusType.UNKNOWN;
    long timestamp = System.currentTimeMillis();
    long periodMillis;
    String contentType;
    String from;
    String subject;
    String username;
    String hostname;
    String service;
    String period;
    String orgName;
    String source;
    
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
        this.source = record.source;
    }

    public ComparableTuple getKey() {
        return ComparableTuple.create(username, hostname, service);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
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
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getTopic() {
        return topic;
    }

    public void setAlertFormatType(AlertFormatType alertFormatType) {
        this.alertFormatType = alertFormatType;
    }
    
    public AlertFormatType getAlertFormatType() {
        return alertFormatType;
    }

    public void setPeriodMillis(long periodMillis) {
        this.periodMillis = periodMillis;
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
            Matcher matcher = StatusRecordParser.nagiosStatusPattern.matcher(lineList.get(i));
            if (matcher.find()) {
                String nagiosService = matcher.group(1);
                String nagiosStatus = matcher.group(2);
                matcher = StatusRecordParser.nagiosStatusPattern.matcher(other.lineList.get(i));
                if (matcher.find()) {
                    if (!nagiosService.equals(matcher.group(1))
                            || !nagiosStatus.equals(matcher.group(2))) {
                        return true;
                    }
                }

            } else if (!StatusRecordParser.headPattern.matcher(lineList.get(i)).find()
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

    public String buildContent(String lineDelimiter) {
        StringBuilder builder = new StringBuilder();
        for (String line : lineList) {
            builder.append(line);
            builder.append(lineDelimiter);
        }
        return builder.toString().trim();
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

    public Map getAlertMap(boolean detail) {
        Map map = new TreeMap();
        map.put("from", from);
        map.put("username", username);
        map.put("hostname", hostname);
        map.put("service", service);
        map.put("statusType", statusType);
        map.put("alertType", alertType);
        map.put("alertTypeLabel", formatAlertTypeLabel());
        map.put("topic", topic);
        map.put("timestamp", timestamp);
        map.put("timestampLabel", Millis.formatTimestamp(timestamp));
        map.put("source", getSource());
        map.put("subject", formatSubject());
        map.put("subjectShort", Strings.truncate(formatSubject(), 48));
        if (detail) {
            map.put("content", buildContent("<br>\n"));
        }
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
    
    public boolean isAdmin(String email) {
        if (email != null) { 
            if (orgName != null && email.endsWith(orgName)) {
                return true;
            }
            if (topic != null && email.contains(topic)) {
                return true;
            }
        }
        return true;
    }
}
