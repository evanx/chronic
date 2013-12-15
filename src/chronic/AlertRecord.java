package chronic;

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


import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.data.Timestamped;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class AlertRecord implements Timestamped {

    static Logger logger = LoggerFactory.getLogger(AlertRecord.class);
    StatusRecord status;
    StatusRecord previousStatus;
    AlertRecord previousAlert;
    List<String> changedLines;
    String htmlContent;
    String preContent;
    
    public AlertRecord(StatusRecord status) {
        this.status = status;
    }
    
    public AlertRecord(StatusRecord status, StatusRecord previous) {
        this.status = status;
        this.previousStatus = previous;
    }

    public AlertRecord(StatusRecord status, StatusRecord previous, AlertRecord previousAlert) {
        this.status = status;
        this.previousStatus = previous;
        this.previousAlert = previousAlert;
    }
    
    @Override
    public long getTimestamp() {
        return status.getTimestamp();
    }    
    
    public StatusRecord getStatus() {
        return status;
    }
    
    public void setPrevious(StatusRecord previous) {
        this.previousStatus = previous;
    }

    public Map getAlertMap(boolean detail) {
        AlertFormatter formatter = new AlertFormatter(status);
        JMap map = status.getTopicKey().getMap();
        map.put("from", status.from);
        map.put("statusType", status.statusType);
        map.put("alertType", status.alertType);
        map.put("alertTypeLabel", formatter.formatAlertTypeLabel());
        map.put("topicString", status.topicString);
        map.put("timestamp", status.timestamp);
        map.put("timestampLabel", Millis.formatTime(status.timestamp));
        map.put("message", formatter.formatAlertTypeLabel());
        if (detail) {
            new AlertWebContentBuilder().build(this);
            map.put("htmlContent", htmlContent);
            map.put("preContent", preContent);
        }
        logger.trace("map {}", map);
        return map;
    }

    @Override
    public String toString() {
        return status.toString();
    }        
}
