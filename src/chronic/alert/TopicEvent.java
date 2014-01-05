package chronic.alert;

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


import chronic.type.AlertEventType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Timestamped;

/**
 *
 * @author evan.summers
 */
public class TopicEvent implements Timestamped {

    static Logger logger = LoggerFactory.getLogger(TopicEvent.class);
    TopicMessage message;
    TopicMessage previousMessage;
    TopicEvent previousEvent;
    List<String> changedLines;
    String htmlContent;
    String preContent;
    int ignoreCount;
    TopicEvent ignoredEvent;
    TopicEvent alertEvent;
    TopicMessage alertedMessage;
    AlertEventType alertEventType;
    boolean polled;
    long notificationTimestamp;
    long polledTimestamp;
    
    public TopicEvent(TopicMessage message) {
        this.message = message;
    }
    
    public TopicEvent(TopicMessage message, TopicMessage previousMessage) {
        this.message = message;
        this.previousMessage = previousMessage;
    }

    public TopicEvent(TopicMessage message, TopicMessage previousMessage, TopicEvent previousEvent) {
        this.message = message;
        this.previousMessage = previousMessage;
        this.previousEvent = previousEvent;
    }

    @Override
    public long getTimestamp() {
        if (ignoredEvent != null) {
            return ignoredEvent.getTimestamp();
        } 
        return message.getTimestamp();
    }    

    public void setAlertEventType(AlertEventType alertEventType) {
        this.alertEventType = alertEventType;
    }
    
    public AlertEventType getAlertEventType() {
        return alertEventType;
    }
        
    public void setIgnoredEvent(TopicEvent ignoredAlert) {
        this.ignoredEvent = ignoredAlert;
        ignoreCount++;
    }
        
    public TopicMessage getMessage() {
        return message;
    }
    
    public void setPreviousMessage(TopicMessage previousMessage) {
        this.previousMessage = previousMessage;
    }

    public TopicEvent getPreviousEvent() {
        return previousEvent;
    }
    public void setAlertedMessage(TopicMessage alertedMessage) {
        this.alertedMessage = alertedMessage;
    }    

    public void setPolled(boolean polled) {
        this.polled = polled;
    }

    public boolean isPolled() {
        return polled;
    }

    @Override
    public String toString() {
        return message.toString();
    }    
}
