package chronic.app;

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


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.data.Timestamped;
import vellum.jx.JMap;
import vellum.jx.JMapped;

/**
 *
 * @author evan.summers
 */
public class AlertRecord implements Timestamped, JMapped {

    static Logger logger = LoggerFactory.getLogger(AlertRecord.class);
    StatusRecord status;
    StatusRecord previousStatus;
    AlertRecord previousAlert;
    List<String> changedLines;
    String htmlContent;
    String preContent;
    int ignoreCount;
    AlertRecord ignoredAlert;
    StatusRecord alertedStatus;
    
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
        if (ignoredAlert != null) {
            return ignoredAlert.getTimestamp();
        } 
        return status.getTimestamp();
    }    
    
    public StatusRecord getStatus() {
        return status;
    }
    
    public void setPrevious(StatusRecord previous) {
        this.previousStatus = previous;
    }

    @Override
    public JMap getMap() {
        JMap map = getPartialMap();
        putExtra(map);
        return map;
    }
        
    public JMap getPartialMap() {
        AlertFormatter formatter = new AlertFormatter(status);
        JMap map = new JMap();
        map.put("orgDomain", status.cert.getOrgDomain());
        map.put("orgUnit", status.cert.getOrgUnit());
        map.put("commonName", status.cert.getCommonName());
        map.put("from", status.from);
        map.put("statusType", status.statusType);
        map.put("alertType", status.alertType);
        map.put("alertTypeLabel", formatter.formatAlertTypeLabel());
        map.put("topicLabel", status.topicLabel);
        map.put("timestamp", status.timestamp);
        map.put("timestampLabel", Millis.formatTime(status.timestamp));
        map.put("message", formatter.formatAlertTypeLabel());
        logger.trace("map {}", map);
        return map;
    }

    public void putExtra(JMap map) {
        new AlertWebContentBuilder().build(this);
        map.put("htmlContent", htmlContent);
        map.put("preContent", preContent);
    }

    @Override
    public String toString() {
        return status.toString();
    }        

    public void setAlertedStatus(StatusRecord alertedStatus) {
        this.alertedStatus = alertedStatus;
    }    
}
