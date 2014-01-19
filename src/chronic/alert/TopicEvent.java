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


import chronic.type.TopicEventType;
import java.util.Calendar;
import java.util.LinkedList;
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
    List<String> changedLines;
    List<String> pendingEmails = new LinkedList();
    String htmlContent;
    String preContent;
    TopicEventType eventType;
    Calendar polled;
    long timestamp;
    long period;

    public TopicEvent(TopicMessage message) {
        this.message = message;
    }

    public TopicEvent(TopicMessage message, TopicEventType eventType) {
        this.message = message;
        this.eventType = eventType;
    }
        
    public TopicEvent(TopicMessage message, TopicMessage previousMessage) {
        this.message = message;
        this.previousMessage = previousMessage;
    }
    
    @Override
    public long getTimestamp() {
        return message.getTimestamp();
    }
    
    public void setAlertEventType(TopicEventType alertEventType) {
        this.eventType = alertEventType;
    }
    
    public TopicEventType getAlertEventType() {
        return eventType;
    }
        
    public TopicMessage getMessage() {
        return message;
    }
    
    public void setPreviousMessage(TopicMessage previousMessage) {
        this.previousMessage = previousMessage;
    }

    public TopicMessage getPreviousMessage() {
        return previousMessage;
    }
    
    public Calendar getPolled() {
        return polled;
    }

    public void setPolled(Calendar polled) {
        this.polled = polled;
    }

    public List<String> getPendingEmails() {
        return pendingEmails;
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
