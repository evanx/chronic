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

import chronic.check.StatusCheck;
import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgTopicKey;
import chronic.entitykey.OrgTopicKeyed;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.ComparableTuple;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class StatusRecord implements OrgKeyed, OrgTopicKeyed, TopicKeyed, CertKeyed {

    static Logger logger = LoggerFactory.getLogger(StatusRecord.class);
    List<String> lineList = new ArrayList();
    AlertType alertType;
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
    String orgDomain;
    String orgUnit;
    String commonName;
    String[] subscribers;
    List<String> changedLines;

    transient Collection<StatusCheck> checks = new LinkedList();
    
    public StatusRecord(CertKey certKey) {
        this.orgDomain = certKey.getOrgDomain();
        this.orgUnit = certKey.getOrgUnit();
        this.commonName = certKey.getCommonName();
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
        this.orgDomain = record.orgDomain;
        this.orgUnit = record.orgUnit;
        this.commonName = record.commonName;
    }

    public ComparableTuple getKey() {
        return getTopicKey();
    }

    @Override
    public TopicKey getTopicKey() {
        return new TopicKey(orgDomain, orgUnit, commonName, topicString);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(orgDomain);
    }
    
    @Override
    public OrgTopicKey getOrgTopicKey() {
        return new OrgTopicKey(orgDomain, topicString);
    }

    @Override
    public CertKey getCertKey() {
        return new CertKey(orgDomain, orgUnit, commonName);
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

    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    public String getOrgDomain() {
        return orgDomain;
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

    public boolean isStatusType() {
        return statusType != null && statusType != StatusType.UNKNOWN;
    }
    
    public boolean isStatusAlertable() {
        return statusType != null && statusType.isStatusAlertable();
    }
    
    public boolean matches(StatusRecord other) {
        return new StatusRecordMatcher(this).matches(other);
    }
    
    public List<String> buildChanged(StatusRecord previous) {
        List<String> list = new ArrayList();
        for (String line : lineList) {
            assert(previous != null);
            assert(line != null);
            if (!line.isEmpty() && !previous.contains(line)) {
                list.add(line);
            }
        }
        return list;
    }
    
    public boolean contains(String otherLine) {
        for (String line : lineList) {
            if (StatusRecordMatcher.matches(line, otherLine)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin(String email) {
        if (email != null) {
            if (orgDomain != null && email.endsWith(orgDomain)) {
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

    public Collection<StatusCheck> getChecks() {
        return checks;
    }
    
    @Override
    public String toString() {
        return Args.format(alertType, orgDomain, topicString, statusType, periodMillis);
    }
}
