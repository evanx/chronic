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
import chronic.type.EventType;
import chronic.type.StatusType;
import chronic.type.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class TopicEventChecker {

    static Logger logger = LoggerFactory.getLogger(TopicEventChecker.class);

    ChronicApp app;

    public TopicEventChecker(ChronicApp app) {
        this.app = app;
    }

    public TopicEvent check(TopicMessage message, TopicMessage previousMessage, TopicEvent previousEvent) {
        assert message.alertType != null;
        assert previousMessage != null;
        logger.info("check: {}", message);
        if (previousMessage.statusType == StatusType.ELAPSED) {
            message.statusType = StatusType.RESUMED;
            return new TopicEvent(message, previousMessage);
        } else if (message.alertType == AlertType.ALWAYS) {
            return new TopicEvent(message);
        } else if (message.alertType == AlertType.NEVER) {
            return new TopicEvent(message);
        } else if (message.alertType == AlertType.PATTERN) {
        } else if (message.alertType == AlertType.ERROR) {
        } else if (message.alertType == AlertType.CONTENT_CHANGED) {
            if (!TopicMessageMatcher.matches(message, previousMessage)) {
                message.statusType = StatusType.CONTENT_CHANGED;
                return new TopicEvent(message, previousMessage);
            }
        } else if (message.getStatusType() == StatusType.CONTENT_ERROR) {
            if (previousEvent == null || previousEvent.getMessage().getStatusType() != StatusType.CONTENT_ERROR) {
                return new TopicEvent(message, previousMessage);
            }
        } else if (message.alertType == AlertType.STATUS_CHANGED) {
            if (message.isStatus() && message.statusType == previousMessage.statusType) {
                if (previousEvent == null) {
                    logger.info("initial {}", message);
                    return new TopicEvent(previousMessage, EventType.INITIAL);
                } else if (message.isStatusChanged(previousEvent.getMessage().getStatusType())) {
                    logger.info("status changed {}", previousMessage);
                    return new TopicEvent(previousMessage, previousEvent.getMessage());
                }
            }
        } else {
            logger.warn("alertType {}", message.getAlertType());
        }
        return null;
    }
}
