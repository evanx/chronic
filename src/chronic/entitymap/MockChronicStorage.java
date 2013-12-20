/*
 * Source https://github.com/evanx by @evanxsummers

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
package chronic.entitymap;

import chronic.app.ChronicApp;
import chronic.app.ChronicStorage;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import vellum.storage.EntityService;
import vellum.storage.MapEntityService;

/**
 *
 * @author evan.summers
 */
public class MockChronicStorage extends ChronicStorage {
    MapEntityService<User> users = new ChronicMapEntityService();
    MapEntityService<Org> orgs = new ChronicMapEntityService();
    MapEntityService<OrgRole> orgRoles = new ChronicMapEntityService();
    MapEntityService<Topic> topics = new ChronicMapEntityService();
    MapEntityService<Subscriber> subscribers = new ChronicMapEntityService();
    MapEntityService<Cert> certs = new ChronicMapEntityService();

    public MockChronicStorage(ChronicApp app) {
        super(app);
    }
        
    @Override
    public void init() throws Exception {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public EntityService<User> user() {
        return users;
    }

    @Override
    public EntityService<Org> org() {
        return orgs;
    }
    
    @Override
    public EntityService<OrgRole> role() {
        return orgRoles;
    }

    @Override
    public EntityService<Topic> topic() {
        return topics;
    }

    @Override
    public EntityService<Subscriber> sub() {
        return subscribers;
    }

    @Override
    public EntityService<Cert> cert() {
        return certs;
    }
    
    
}
