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
import chronic.storage.temporary.TemporaryAdminUserStorage;
import chronic.storage.temporary.TemporaryNetworkStorage;
import chronic.storage.temporary.TemporaryOrgRoleStorage;
import chronic.storage.temporary.TemporaryOrgStorage;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.h2.tools.Server;

/**
 *
 * @author evan.summers
 */
public class TemporaryChronicStorage extends LogicalChronicStorage {
    Server h2Server;
    AdminUserStorage adminUserStorage = new TemporaryAdminUserStorage();
    OrgStorage orgStorage = new TemporaryOrgStorage();
    OrgRoleStorage orgRoleStorage = new TemporaryOrgRoleStorage();
    NetworkStorage networkStorage = new TemporaryNetworkStorage();
    Map<String, Connection> connectionMap = new HashMap();

    public TemporaryChronicStorage(ChronicApp app) {
        super(app);
    }
        
    @Override
    public void init() throws Exception {
        h2Server = Server.createTcpServer().start();
    }

    @Override
    public void shutdown() {
        if (h2Server != null) {
            h2Server.stop();
        }
    }

    @Override
    public AdminUserStorage getAdminUserStorage() {
        return adminUserStorage;
    }

    @Override
    public OrgStorage getOrgStorage() {
        return orgStorage;
    }
    
    @Override
    public OrgRoleStorage getOrgRoleStorage() {
        return orgRoleStorage;
    }

    @Override
    public NetworkStorage getNetworkStorage() {
        return networkStorage;
    }
}
