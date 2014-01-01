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

import chronic.check.StatusCheck;
import chronic.entity.Cert;
import chronic.entity.Topic;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.CertTopicKeyed;
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.ComparableTuple;
import vellum.data.Patterns;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class TopicMessage implements CertTopicKeyed, OrgKeyed {

    static Logger logger = LoggerFactory.getLogger(TopicMessage.class);
    List<String> lineList = new ArrayList();
    AlertType alertType;
    AlertFormatType alertFormatType;
    String topicLabel;
    StatusType statusType;
    long timestamp = System.currentTimeMillis();
    long periodMillis;
    String contentType;
    String from;
    String subject;
    String username;
    String hostname;
    String serviceLabel;
    String alertPushUrl;
    Set<String> subscribers = new HashSet();
    List<MetricValue> metricList = new ArrayList();
    Map<String, MetricValue> metricMap = new TreeMap();
    List<StatusCheck> checks = new LinkedList();
    List<ServiceStatus> statusList = new LinkedList();
    
    Cert cert;
    Topic topic;

    public TopicMessage() {
    }
        
    public TopicMessage(Cert cert) {
        this.cert = cert;
    }

    public ComparableTuple getKey() {
        return getCertTopicKey();
    }

    @Override
    public CertTopicKey getCertTopicKey() {
        return new CertTopicKey(cert.getId(), topicLabel);
    }
    
    @Override
    public OrgKey getOrgKey() {
        return new OrgKey(cert.getOrgDomain());
    }

    void add(MetricValue metricValue) {
        metricList.add(metricValue);
        metricMap.put(metricValue.getLabel(), metricValue);
    }

    public List<ServiceStatus> getStatusList() {
        return statusList;
    }
        
    public List<MetricValue> getMetricList() {
        return metricList;
    }
    
    public Map<String, MetricValue> getMetricMap() {
        return metricMap;
    }
    
    public Cert getCert() {
        return cert;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
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

    public String getFrom() {
        return from;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
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

    public void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }

    public String getTopicLabel() {
        return topicLabel;
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

    public void setAlertPushUrl(String alertPushUrl) {
        this.alertPushUrl = alertPushUrl;
    }

    public String getAlertPushUrl() {
        return alertPushUrl;
    }
    
    public List<String> getLineList() {
        return lineList;
    }

    public boolean isStatusKnown() {
        return statusType != null && statusType != StatusType.UNKNOWN;
    }
    
    public boolean isStatusAlertable() {
        return statusType != null && statusType.isStatusAlertable();
    }
    
    public boolean matches(TopicMessage other) {
        return TopicMessageMatcher.matches(this, other);
    }
    
    public boolean isHtmlContent() {
        for (String line : lineList) {
            if (Patterns.matchesTag(line)) {
                return true;
            }
        }
        return false;
    }
    
    public List<String> buildChanged(TopicMessage previous) {
        List<String> list = new ArrayList();
        if (previous != null) {
            for (String line : lineList) {
                if (!line.isEmpty() && !previous.contains(line)) {
                    list.add(line);
                }
            }
        }
        if (list.isEmpty()) {
            logger.warn("buildChanged empty");
            list.addAll(lineList);
        }
        return list;
    }
    
    public boolean contains(String matchLine) {
        for (String line : lineList) {
            if (TopicMessageMatcher.matches(matchLine, line)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getSubscribers() {
        return subscribers;
    }
    
    public Collection<StatusCheck> getChecks() {
        return checks;
    }
    
    @Override
    public String toString() {
        return Args.format(alertType, topicLabel, statusType, periodMillis);
    }
}
