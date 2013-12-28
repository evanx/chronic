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

import chronic.check.TcpChecker;
import chronic.bundle.Bundle;
import chronic.check.HttpsChecker;
import chronic.entity.Cert;
import chronic.type.StatusType;
import chronic.type.AlertFormatType;
import chronic.type.AlertType;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
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

    boolean inHeader = true;
    boolean nagiosStatus = true;

    public StatusRecordParser() {
    }

    public StatusRecord parse(Cert cert, Headers headers, String text) throws IOException {
        record = new StatusRecord(cert);
        logger.trace("parse: {}", text);
        for (String key : headers.keySet()) {
            parseHeader(key, headers.get(key));
        }
        for (String line : text.split("\n")) {
            Matcher matcher = StatusRecordPatterns.HEADER.matcher(line);
            if (matcher.find()) {
                if (parseHeader(matcher.group(1), matcher.group(2))) {
                    continue;
                }
            } else {
                inHeader = false;
            }
            parseLineNagiosStatus(line);
            if (!HtmlChecker.sanitary(line)) {
                logger.warn("omit not sanitary: {}", line);
            } else {
                record.getLineList().add(line);
            }
        }
        normalize();
        return record;
    }

    private void parseHeader(String header, List<String> strings) {
        logger.info("parseHeader {} {}", header, strings.toString());
        for (String string : strings) {
            parseHeader(header, string);
        }
    }

    private boolean parseHeader(String header, String value) {
        if (header.equals("Alert")) {
            parseAlertType(value);
        } else if (header.equals("AlertFormat")) {
            parseAlertFormatType(value);
        } else if (header.equals("Check-tcp")) {
            parseTcp(value);
        } else if (header.equals("Check-https")) {
            parseHttps(value);
        } else if (header.equals("Content-Type")) {
            parseContentType(value);
        } else if (header.equals("From")) {
            parseFrom(value);
        } else if (header.equals("Period")) {
            parsePeriod(value);
        } else if (header.equals("Service")) {
            parseService(value);
        } else if (header.equals("Status")) {
            parseStatusType(value);
        } else if (header.equals("Subject")) {
            parseSubject(value);
        } else if (header.equals("Subscribe")) {
            parseSubscribe(value);
        } else if (header.equals("Topic")) {
            parseTopic(value);
        } else if (header.equals("Metric")) {
            parseMetric(value);
        } else if (header.equals("Metrics")) {
            parseMetrics(value);
        } else {
            return false;
        }
        return true;
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

    private void parseContentType(String contentType) {
        int index = contentType.indexOf(";");
        if (index > 0) {
            record.setContentType(contentType.substring(0, index));
        } else {
            record.setContentType(contentType);
        }
    }
    
    private void parseFrom(String string) {
        Matcher matcher = StatusRecordPatterns.FROM_CRON.matcher(string);
        if (matcher.find()) {
            record.setUsername(matcher.group(1));
            record.setFrom(matcher.group(1));
        }
    }

    private void parseHttps(String string) {
        try {
            record.getChecks().add(HttpsChecker.parse(string));
        } catch (Exception e) {
            logger.warn("parseHttps {}: {}", string, e.getMessage());
        }
    }

    private boolean parseLineNagiosStatus(String line) {
        Matcher matcher = StatusRecordPatterns.NAGIOS.matcher(line);
        if (matcher.find()) {
            logger.debug("parseNagiosStatus {} {}", matcher.group(1), matcher.group(2));
            String service = matcher.group(1).trim();
            if (!service.isEmpty()) {
                record.setService(service);
                if (record.getMetricMap().containsKey(service)) {
                    String valueString = matcher.group(3);
                    Matcher metricValueMatcher = StatusRecordPatterns.METRIC_VALUE.matcher(valueString);
                    if (!metricValueMatcher.find()) {
                        logger.warn("metric {} {} ", service, valueString);
                    } else {
                        float value = Float.parseFloat(metricValueMatcher.group(1));
                        logger.info("metric {} {} ", service, value);
                        record.getMetricMap().get(service).setValue(value);
                    }
                }
            }
            if (nagiosStatus) {
                parseStatusType(matcher.group(2));
            }
            return true;
        }
        return false;
    }

    private void parseMetric(String string) {
        record.getMetricMap().put(string, new MetricValue());
    }

    private void parseMetrics(String string) {
        for (String name : Strings.split(string, DelimiterType.COMMA_OR_SPACE)) {
            record.getMetricMap().put(name, new MetricValue());
        }
    }
    
    private void parsePeriod(String string) {
        record.setPeriodMillis(Millis.parse(string));
    }

    private void parseService(String string) {
        nagiosStatus = false;
        record.setService(string);
    }

    private void parseStatusType(String string) {
        nagiosStatus = false;
        try {
            record.setStatusType(StatusType.valueOf(string));
        } catch (Exception e) {
            logger.warn("parseStatusType {}: {}", string, e.getMessage());
        }
    }

    private void parseSubject(String string) {
        Matcher matcher = StatusRecordPatterns.CRON_SUBJECT.matcher(string);
        if (matcher.find()) {
            record.setUsername(matcher.group(1));
            record.setHostname(matcher.group(2));
            record.setService(matcher.group(3));
            record.setFrom(record.username + '@' + record.hostname);
        } else {
            record.setSubject(string.substring(9).trim());
        }
    }

    private void parseSubscribe(String string) {
        record.getSubscribers().addAll(Arrays.asList(
                Strings.split(string, DelimiterType.COMMA_OR_SPACE)));
    }

    private void parseTcp(String string) {
        try {
            record.getChecks().add(TcpChecker.parse(string));
        } catch (Exception e) {
            logger.warn("parseTcp {}: {}", string, e.getMessage());
        }
    }

    private void parseTopic(String string) {
        record.setTopicLabel(string);
        nagiosStatus = false;
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
        if (record.topicLabel == null) {
            record.topicLabel = createTopicLabel();
        }
    }

    private String createTopicLabel() {
        if (record.username != null && record.hostname != null && record.service != null) {
            return String.format("%s@%s %s", record.username, record.hostname, record.service);
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
