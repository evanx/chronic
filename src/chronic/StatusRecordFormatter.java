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

import chronic.type.StatusType;

/**
 *
 * @author evan.summers
 */
public class StatusRecordFormatter {

    StatusRecord status = new StatusRecord();

    public StatusRecordFormatter(StatusRecord status) {
        this.status = status;
    }
    
    public String formatAlertTypeLabel() {
        if (status.statusType != null && status.statusType.isAlertable()) {
            return status.statusType.getLabel();
        } else if (status.alertType == null && status.statusType != null) {
            return status.statusType.getLabel();
        } else if (status.alertType != null) {
            return status.alertType.getLabel();
        } else {
            return StatusType.UNKNOWN.getLabel();
        }
    }

    public String formatSubject() {
        if (status.isAlertable()) {
            if (status.statusType == StatusType.ELAPSED || status.subject == null) {
                return status.getSource() + ' ' + status.statusType.getLabel();
            } else if (!status.subject.contains(status.statusType.name())) {
                return status.subject + ' ' + status.statusType.getLabel();
            } else {
                return status.subject;
            }
        }
        if (status.subject == null) {
            return status.getSource();
        }
        return status.subject;
    }
    
    
}
