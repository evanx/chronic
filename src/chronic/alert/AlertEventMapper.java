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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class AlertEventMapper {
    static Logger logger = LoggerFactory.getLogger(AlertEventMapper.class);

    AlertEvent alert;
    TopicMessage status;
    String timestampLabel;
    
    public AlertEventMapper(AlertEvent alert, TimeZone timeZone) {
        this.alert = alert;
        this.status = alert.message;        
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(timeZone);
        timestampLabel = format.format(alert.message.timestamp);
    }
    
    public JMap getExtendedMap() {
        JMap map = getBasicMap();
        putExtended(map);
        return map;
    }
        
    public JMap getBasicMap() {
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
        map.put("timestampLabel", timestampLabel);
        map.put("message", formatter.formatAlertTypeLabel());
        return map;
    }

    public void putExtended(JMap map) {
        new AlertWebContentBuilder().build(alert);
        map.put("htmlContent", alert.htmlContent);
        map.put("preContent", alert.preContent);
    }
}
