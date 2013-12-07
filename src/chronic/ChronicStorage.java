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

import chronic.storage.app.AdminUserStorage;
import chronic.storage.app.NetworkStorage;
import chronic.storage.app.OrgRoleStorage;
import chronic.storage.app.OrgStorage;
import java.util.ArrayList;
import java.util.List;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public abstract class ChronicStorage {
    
    static Logger logger = LoggerFactory.getLogger(ChronicStorage.class);

    ChronicApp app;
    
    public ChronicStorage(ChronicApp app) {
        this.app = app;
    }
        
    public abstract void init() throws Exception;

    public abstract void shutdown();
    
    public abstract AdminUserStorage getAdminUserStorage();
    
    public abstract OrgStorage getOrgStorage();
    
    public abstract OrgRoleStorage getOrgRoleStorage();

    public abstract NetworkStorage getNetworkStorage();
    
    public Iterable<String> getEmails(AlertRecord alert) {
        List<String> list = new ArrayList();
        list.add(app.getProperties().getAdminEmails().iterator().next());
        return list;
    }

    public static ChronicStorage create(ChronicApp app) {
        return new TemporaryChronicStorage(app);
    }

    public void subscribe(String email, String orgName) {
        logger.info("addAdmin {} {}", email, orgName);
    }
    
}
