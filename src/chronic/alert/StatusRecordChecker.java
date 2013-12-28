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

import chronic.alert.AlertRecord;
import chronic.type.StatusType;
import chronic.type.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class StatusRecordChecker {

    static Logger logger = LoggerFactory.getLogger(StatusRecordChecker.class);
    
    StatusRecord status;

    public StatusRecordChecker(StatusRecord status) {
        this.status = status;
    }    

    public boolean isAlertable(StatusRecord previous, AlertRecord alert) {
        logger.info("isAlertable {}", Args.format(status.topicLabel, status.alertType, 
                status.statusType, previous.statusType, status.matches(previous),
                alert.getStatus().getStatusType()));
        if (status.alertType == AlertType.NEVER) {
            return false;
        }
        if (status.alertType == AlertType.ALWAYS) {
            return true;
        }
        if (previous.statusType == StatusType.ELAPSED) {
            status.statusType = StatusType.RESUMED;
            return true;
        }
        if (status.alertType == AlertType.PATTERN) {
        } else if (status.alertType == AlertType.ERROR) {
        }
        if (status.alertType == AlertType.CONTENT_CHANGED) {
            if (!status.matches(previous)) {
                status.statusType = StatusType.CONTENT_CHANGED;
                return true;
            }
        }
        if (status.alertType == AlertType.STATUS_CHANGED) {
           if (status.isStatusAlertable() && status.statusType == previous.statusType
                    && status.statusType != alert.getStatus().getStatusType()) {
               if (alert.getStatus().getAlertType() == AlertType.INITIAL && 
                       !alert.getStatus().isStatusAlertable()) {
                   status.alertType = AlertType.INITIAL;
               }
               return true;
           }
        }
        return false;
    }
    
    public boolean matches(StatusRecord other) {
        return new StatusRecordMatcher(status).matches(other);
    }
    
}
