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

import chronic.bundle.Bundle;

/**
 *
 * @author evan.summers
 */
public class AlertFormatter {

    StatusRecord status;

    public AlertFormatter(StatusRecord status) {
        this.status = status;
    }

    public String formatAlertTypeLabel() {
        if (status.statusType != null && status.statusType.isStatusAlertable()) {
            return status.statusType.getLabel();
        } else if (status.statusType != null && status.alertType == null) {
            return status.statusType.getLabel();
        } else if (status.alertType != null) {
            return status.alertType.getLabel();
        } else {
            return Bundle.get("unknown");
        }
    }
}
