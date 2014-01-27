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
import chronic.check.TcpChecker;
import chronic.bundle.Bundle;
import chronic.check.ClockChecker;
import chronic.check.HttpsChecker;
import chronic.check.NtpChecker;
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.enumtype.DelimiterType;
import vellum.exception.ParseException;
import vellum.util.Lists;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class TopicMessageParser {

    static Logger logger = LoggerFactory.getLogger(TopicMessageParser.class);

    ChronicApp app;
    TopicMessage topicMessage;

    public TopicMessageParser(ChronicApp app, TopicMessage topicMessage) {
        this.app = app;
        this.topicMessage = topicMessage;
    }

    public TopicMessage parse(Headers headers, String text) throws IOException, ParseException {
        logger.trace("parse: {}", text);
        for (String key : headers.keySet()) {
            try {
                parseHeader(key, headers.get(key));
            } catch (Throwable e) {
                logger.error("parse {} {}", key, headers.get(key));
            }
        }
        return parse(text);
    }

    public TopicMessage parse(Collection<String> lines) throws IOException, ParseException {
        return parse(Lists.array(lines));
    }

    public TopicMessage parse(String text) throws IOException, ParseException {
        return parse(text.split("\n"));
    }

    public TopicMessage parse(String[] lines) throws IOException, ParseException {
        boolean addendum = false;
        for (String line : lines) {
            line = line.trim();
            Matcher matcher = TopicMessagePatterns.LOG_ADDENDUM.matcher(line);
            if (matcher.find()) {
                addendum = true;
                topicMessage.getLineList().add(line);
                continue;
            }
            if (addendum) {
                topicMessage.getLineList().add(line);
                continue;
            }
            if (line.startsWith("X-Cron")) {
                continue;
            }
            matcher = TopicMessagePatterns.FROM_CRON.matcher(line);
            if (matcher.find()) {
                topicMessage.setUsername(matcher.group(1));
                topicMessage.setFrom(matcher.group(1));
                continue;
            }
            matcher = TopicMessagePatterns.CRON_SUBJECT.matcher(line);
            if (matcher.find()) {
                parseCronSubject(matcher.group(1), matcher.group(2), matcher.group(3));
                continue;
            }
            if (!HtmlChecker.sanitary(line)) {
                logger.warn("omit not sanitary: {}", line);
                continue;
            }
            matcher = TopicMessagePatterns.HEADER.matcher(line);
            if (matcher.find()) {
                if (parseHeader(matcher.group(1), matcher.group(2))) {
                    continue;
                }
                logger.warn("header: {}", line);
            } else {
            }
            matcher = TopicMessagePatterns.SERVICE_STATUS.matcher(line);
            if (matcher.find()) {
                if (topicMessage.alertType == null) {
                    topicMessage.alertType = AlertType.STATUS_CHANGED;
                }
                parseServiceStatus(matcher.group(1), matcher.group(2), matcher.group(3));
                topicMessage.getLineList().add(line);
                continue;
            }
            matcher = TopicMessagePatterns.STATUS.matcher(line);
            if (matcher.find()) {
                parseStatusInfo(matcher.group(1), matcher.group(2));
                topicMessage.getLineList().add(line);
                continue;
            }
            topicMessage.getLineList().add(line);
        }
        normalize();
        return topicMessage;
    }

    private void parseHeader(String header, List<String> strings) throws ParseException {
        logger.trace("parseHeader {} {}", header, strings.toString());
        for (String string : strings) {
            parseHeader(header, string);
        }
    }

    private boolean parseHeader(String header, String string) throws ParseException, 
            NumberFormatException {
        if (header.equals("Alert")) {
            topicMessage.setAlertType(AlertType.valueOf(string));
        } else if (header.equals("AlertFormat")) {
            topicMessage.setAlertFormatType(AlertFormatType.valueOf(string));
        } else if (header.equals("AlertPush")) {
            topicMessage.setAlertPushUrl(string);
        } else if (header.equals("Check-tcp")) {
            topicMessage.getChecks().add(TcpChecker.parse(string));
        } else if (header.equals("Check-https")) {
            topicMessage.getChecks().add(HttpsChecker.parse(string));
        } else if (header.equals("Clock")) {
            topicMessage.getChecks().add(ClockChecker.parse(string));
        } else if (header.equals("Content-Type")) {
            parseContentType(string);
        } else if (header.equals("From")) {
            topicMessage.setFrom(string);
        } else if (header.equals("NtpOffsetSec")) {            
            topicMessage.getChecks().add(NtpChecker.parse(string));
        } else if (header.equals("Period")) {
            topicMessage.setPeriodMillis(Millis.parse(string));
        } else if (header.equals("Service")) {
            topicMessage.setServiceLabel(string);
        } else if (header.equals("Status")) {
            topicMessage.setStatusType(StatusType.valueOf(string));
        } else if (header.equals("StatusPeriod")) {
            topicMessage.setStatusPeriodMillis(Millis.parse(string));
        } else if (header.equals("Subject")) {
            topicMessage.subject = string;
        } else if (header.equals("Subscribe")) {
            parseSubscribe(string);
        } else if (header.equals("Topic")) {
            topicMessage.topicLabel = string;
        } else if (header.equals("Metric")) {
            parseMetric(string);
        } else if (header.equals("Metrics")) {
            parseMetrics(string);
        } else if (header.equals("Value")) {
            parseMetricValue(string);
        } else {
            return false;
        }
        return true;
    }

    private void parseContentType(String contentType) {
        int index = contentType.indexOf(";");
        if (index > 0) {
            topicMessage.setContentType(contentType.substring(0, index));
        } else {
            topicMessage.setContentType(contentType);
        }
    }

    private void parseStatusInfo(String status, String info) {
        logger.debug("parseStatusInfo {} {}", status, info);
        StatusType statusType = StatusType.valueOf(status);
        if (topicMessage.statusType == null || statusType.ordinal() > topicMessage.statusType.ordinal()) {
            topicMessage.statusType = statusType;
        }
    }

    private void parseServiceStatus(String serviceLabel, String status, String info) {
        logger.debug("parseServiceStatus {} {}", serviceLabel, status);
        if (topicMessage.getMetricMap().containsKey(serviceLabel)) {
            Matcher metricValueMatcher = TopicMessagePatterns.SERVICE_METRIC_VALUE.matcher(info);
            if (!metricValueMatcher.find()) {
                logger.warn("metric {} {} ", serviceLabel, info);
            } else {
                float value = Float.parseFloat(metricValueMatcher.group(1));
                logger.trace("metric {} {} ", serviceLabel, value);
                topicMessage.getMetricMap().get(serviceLabel).setValue(value);
            }
        }
        StatusType statusType = StatusType.valueOf(status);
        ServiceStatus serviceStatus = new ServiceStatus(topicMessage.getCert(), 
                serviceLabel, statusType, info);
        topicMessage.statusList.add(serviceStatus);
    }

    private void parseMetric(String name) {
        topicMessage.add(new MetricValue(name));
    }

    private void parseMetrics(String string) {
        for (String name : Strings.split(string, DelimiterType.COMMA_OR_SPACE)) {
            topicMessage.add(new MetricValue(name));
        }
    }

    private void parseMetricValue(String string) {
        Matcher matcher = TopicMessagePatterns.HEADER_METRIC_VALUE.matcher(string);
        if (!matcher.find()) {
            logger.warn("parseMetricValue {}", string);
        } else {
            String name = matcher.group(1);
            String valueString = matcher.group(2).trim();
            logger.info("parseMetricValue {} {}", name, valueString);
            int index = valueString.lastIndexOf(',');
            if (index > 0 && index == valueString.length() - 2) {
                valueString = valueString.substring(0, index) + "." + valueString.substring(index + 1);
            }
            float value = Float.parseFloat(valueString);
            MetricValue metricValue = topicMessage.getMetricMap().get(name);
            if (metricValue == null) {
                metricValue = new MetricValue(name, value);
                topicMessage.add(metricValue);
            } else {
                metricValue.setValue(value);
            }
            logger.info("parseMetricValue {}", metricValue);
        }
    }

    private void parseCronSubject(String username, String hostname, String serviceLabel) {
        serviceLabel = normaliseCronServiceLabel(serviceLabel);
        topicMessage.setUsername(username);
        topicMessage.setHostname(hostname);
        topicMessage.setServiceLabel(serviceLabel);
        topicMessage.setFrom(username + '@' + hostname);
        topicMessage.setTopicLabel(username + '@' + hostname + " " + serviceLabel);
    }

    private String normaliseCronServiceLabel(String serviceLabel) {
        int index = serviceLabel.lastIndexOf("/");
        if (index >= 0) {
            serviceLabel = serviceLabel.substring(index + 1);
        }
        index = serviceLabel.lastIndexOf(".");
        if (index > 0) {
            serviceLabel = serviceLabel.substring(0, index);
        }
        return serviceLabel;
    }

    private void parseSubscribe(String string) {
        topicMessage.getSubscribers().addAll(Arrays.asList(
                Strings.split(string, DelimiterType.COMMA_OR_SPACE)));
    }

    private void normalize() {
        if (topicMessage.alertType == null) {
            topicMessage.alertType = AlertType.CONTENT_CHANGED;
        }
        if (topicMessage.statusPeriodMillis == 0) {
            topicMessage.statusPeriodMillis = app.getProperties().getStatusPeriod();
        }
        String serviceLabel = null;
        if (topicMessage.statusList.size() == 1) {
            ServiceStatus status = topicMessage.statusList.get(0);
            serviceLabel = status.getServiceLabel();
        }
        for (ServiceStatus status : topicMessage.statusList) {
            StatusType statusType = status.getStatusType();
            if (topicMessage.statusType == null || statusType.ordinal() > topicMessage.statusType.ordinal()) {
                topicMessage.statusType = statusType;
                serviceLabel = status.getServiceLabel();
            }
            logger.info("normalize single status {} {}", topicMessage.statusType, status);
        }
        if (topicMessage.serviceLabel == null && serviceLabel != null) {
            topicMessage.serviceLabel = serviceLabel;
        }
        if (topicMessage.statusType == null) {
            topicMessage.statusType = StatusType.UNKNOWN;
        }
        if (topicMessage.topicLabel == null) {
            topicMessage.topicLabel = createTopicLabel();
        }
        logger.info("topicLabel {}", topicMessage.topicLabel);
    }

    private String createTopicLabel() {
        if (topicMessage.username != null && topicMessage.hostname != null && topicMessage.serviceLabel != null) {
            return String.format("%s@%s %s", topicMessage.username, topicMessage.hostname, topicMessage.serviceLabel);
        }
        if (topicMessage.username != null && topicMessage.hostname != null) {
            return String.format("%s@%s", topicMessage.username, topicMessage.hostname);
        }
        if (topicMessage.hostname != null) {
            return topicMessage.hostname;
        }
        if (topicMessage.serviceLabel != null) {
            return topicMessage.serviceLabel;
        }
        if (topicMessage.subject != null) {
            return topicMessage.subject;
        }
        return Bundle.get("undetermined_topic");
    }
}
