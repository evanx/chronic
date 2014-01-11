
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
package chronic;

import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;

/**
 *
 * @author evans
 */
public class EntityInfo {

    String orgUnit = "test";
    String address = "127.0.0.1";    
    String encoded = "encoded";
    
    String commonName;
    String orgDomain;
    String topicLabel;
    String email;
    CertKey certKey;
    Org org;
    OrgRole orgRole;
    Cert cert;
    Topic topic;
    Person person;

    public EntityInfo(String orgDomain, String commonName, String topicLabel, String email) {
        this.orgDomain = orgDomain;
        this.commonName = commonName;
        this.topicLabel = topicLabel;
        this.email = email;
        certKey = new CertKey(orgDomain, orgUnit, commonName);
    }

}
