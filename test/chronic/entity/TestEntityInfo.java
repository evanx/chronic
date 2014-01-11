
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
package chronic.entity;

import static chronic.entity.TestEntity.logger;
import chronic.entitykey.CertKey;
import java.util.List;

/**
 *
 * @author evans
 */
public class TestEntityInfo {

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
    Alert alert;

    public TestEntityInfo(String orgDomain, String commonName, String topicLabel, String email) {
        this.orgDomain = orgDomain;
        this.commonName = commonName;
        this.topicLabel = topicLabel;
        this.email = email;
        certKey = new CertKey(orgDomain, orgUnit, commonName);
    }

    static void assertSize(String message, int size, List list) throws Exception {
          if (list.size() != size) {
              int index = 0;
              for (Object element : list) {
                  logger.info("list {} {}: " + element, index++, element.getClass().getSimpleName());
              }
              throw new Exception(message + ": list size " + list.size() + "; expected " + size);
          }
    }
    
}
