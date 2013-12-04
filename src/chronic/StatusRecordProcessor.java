/*
 * Source https://code.google.com/p/vellum by @evanxsummers

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
package chronic;

import chronic.type.AlertType;
import chronic.type.StatusType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class StatusRecordProcessor {

    static Logger logger = LoggerFactory.getLogger(StatusRecordProcessor.class);

    public StatusRecordProcessor() {
    }
        
    public boolean isAlertable(StatusRecord status, StatusRecord previousStatus,
            StatusRecord previousAlert) {
        if (previousAlert.getStatusType() == StatusType.ELAPSED
                && status.statusType != StatusType.ELAPSED) {
            return true;
        }
        if (isAlertable(status, previousAlert)) {
            return true;
        }
        if (status.alertType == AlertType.STATUS_CHANGED) {
            return status.isAlertable() &&
                    status.statusType == previousStatus.statusType &&
                    status.statusType != previousAlert.getStatusType();
            }
        return false;
    }

    
    public boolean isAlertable(StatusRecord status, StatusRecord previous) {
        if (status.alertType == AlertType.ALWAYS) {
            return true;
        }
        if (status.alertType == AlertType.PATTERN) {
        } else if (status.alertType == AlertType.ERROR) {
        }
        if (status.alertType == AlertType.CONTENT_CHANGED) {
            if (equals(status, previous)) {
                status.statusType = StatusType.CONTENT_CHANGED;
                return true;
            }
        }
        return false;
    }
    
    public List<String> getChanged(StatusRecord status, StatusRecord other) {
        List<String> list = new ArrayList();
        for (String line : status.lineList) {
            if (!contains(other.lineList, line)) {
                list.add(line);
            }
        }
        return list;
    }
    
    public boolean contains(List<String> lineList, String other) {
        for (String line : lineList) {
            if (equals(line, other)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean equals(StatusRecord status, StatusRecord other) {
        if (status.lineList.size() != other.lineList.size()) {
            return false;
        }
        for (int i = 0; i < status.lineList.size(); i++) {
            if (!equals(status.lineList.get(i), other.lineList.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(String line, String other) {
        Matcher matcher = StatusRecordParser.nagiosStatusPattern.matcher(line);
        if (matcher.find()) {
            Matcher otherMatcher = StatusRecordParser.nagiosStatusPattern.matcher(other);
            return otherMatcher.find() && otherMatcher.group(1).equals(matcher.group(1)) &&
                        otherMatcher.group(2).equals(matcher.group(2));

        } else if (StatusRecordParser.headPattern.matcher(line).find()) {
            return true;
        }
        return line.equals(other);
    }
    
    public boolean isAdmin(StatusRecord status, String email) {
        if (email != null) { 
            if (status.orgName != null && email.endsWith(status.orgName)) {
                return true;
            }
            if (status.topic != null && email.contains(status.topic)) {
                return true;
            }
        }
        return true;
    }
    
}
