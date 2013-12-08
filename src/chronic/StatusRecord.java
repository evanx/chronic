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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.type.ComparableTuple;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class StatusRecord {

    static Logger logger = LoggerFactory.getLogger(StatusRecord.class);
    List<String> lineList = new ArrayList();
    AlertType alertType = AlertType.NONE;
    AlertFormatType alertFormatType;
    String topicString;
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
    String orgUrl;
    String[] subscribers;
    List<String> changedLines;
    
    public StatusRecord() {
    }

    public StatusRecord(StatusRecord record) {
        this.alertType = record.alertType;
        this.alertFormatType = record.alertFormatType;
        this.topicString = record.topicString;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getOrgUrl() {
        return orgUrl;
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

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }

    public String getTopicString() {
        return topicString;
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

    public boolean isAlertable() {
        return statusType != null && statusType.isAlertable();
    }

    public boolean isAlertable(StatusRecord previous) {
        if (alertType == AlertType.ALWAYS) {
            return true;
        }
        if (alertType == AlertType.PATTERN) {
        } else if (alertType == AlertType.ERROR) {
        }
        if (alertType == AlertType.CONTENT_CHANGED) {
            if (!equals(previous)) {
                statusType = StatusType.CONTENT_CHANGED;
                return true;
            }
        }
        return false;
    }
    
    public boolean equals(StatusRecord other) {
        if (lineList.size() != other.lineList.size()) {
            return false;
        }
        for (int i = 0; i < lineList.size(); i++) {
            if (!equals(lineList.get(i), other.lineList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(String line, String other) {
        Matcher matcher = StatusRecordParser.nagiosStatusPattern.matcher(line);
        if (matcher.find()) {
            Matcher otherMatcher = StatusRecordParser.nagiosStatusPattern.matcher(other);
            return otherMatcher.find() && otherMatcher.group(1).equals(matcher.group(1)) &&
                        otherMatcher.group(2).equals(matcher.group(2));

        } else if (StatusRecordParser.headPattern.matcher(line).find()) {
            return true;
        }
        return line.equals(other);
    }
        
    public List<String> buildChanged(StatusRecord previous) {
        List<String> list = new ArrayList();
        for (String line : lineList) {
            if (!contains(line)) {
                list.add(line);
            }
        }
        return list;
    }
    
    public boolean contains(String other) {
        for (String line : lineList) {
            if (equals(line, other)) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isAdmin(String email) {
        if (email != null) { 
            if (orgUrl != null && email.endsWith(orgUrl)) {
                return true;
            }
        }
        return true;
    }    

    public void setSubcribers(String[] subscribers) {
        this.subscribers = subscribers;
    }

    public String[] getSubscribers() {
        return subscribers;
    }        
    
    @Override
    public String toString() {
        return Args.format(alertType, orgUrl, topicString, statusType);
    }
    
}
