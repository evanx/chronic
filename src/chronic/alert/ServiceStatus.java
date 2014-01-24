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

import chronic.entity.Cert;
import chronic.entitykey.ServiceStatusKey;
import chronic.entitykey.ServiceStatusKeyed;
import chronic.type.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class ServiceStatus implements ServiceStatusKeyed {

    static Logger logger = LoggerFactory.getLogger(ServiceStatus.class);
    String serviceLabel;
    StatusType statusType;
    String statusInfo;
    Cert cert;
        
    public ServiceStatus(Cert cert, String serviceLabel, StatusType statusType, String statusInfo) {
        this.cert = cert;
        this.serviceLabel = serviceLabel;
        this.statusType = statusType;
        this.statusInfo = statusInfo;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }
        
    public StatusType getStatusType() {
        return statusType;
    }

    public String getStatusInfo() {
        return statusInfo;
    }
    
    public boolean isStatusKnown() {
        return statusType != null && statusType != StatusType.UNKNOWN;
    }
    
    public boolean isStatusAlertable() {
        return statusType != null && statusType.isStatus();
    }
    
    @Override
    public String toString() {
        return Args.format(serviceLabel, statusType, statusInfo);
    }

    @Override
    public ServiceStatusKey getServiceStatusKey() {
        return new ServiceStatusKey(cert.getId(), serviceLabel, statusType);
    }
}
