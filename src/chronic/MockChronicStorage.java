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
package chronic;

import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import org.h2.tools.Server;
import vellum.storage.EntityStore;
import vellum.storage.MapStore;

/**
 *
 * @author evan.summers
 */
public class MockChronicStorage extends ChronicStorage {
    Server h2Server;
    MapStore<User> users = new ChronicMapStore();
    MapStore<Org> orgs = new ChronicMapStore();
    MapStore<OrgRole> orgRoles = new ChronicMapStore();
    MapStore<Network> nets = new ChronicMapStore();
    MapStore<Topic> topics = new ChronicMapStore();
    MapStore<Subscriber> subscribers = new ChronicMapStore();
    MapStore<Cert> certs = new ChronicMapStore();

    public MockChronicStorage(ChronicApp app) {
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
    public EntityStore<User> user() {
        return users;
    }

    @Override
    public EntityStore<Org> org() {
        return orgs;
    }
    
    @Override
    public EntityStore<OrgRole> role() {
        return orgRoles;
    }

    @Override
    public EntityStore<Network> net() {
        return nets;
    }
    
    @Override
    public EntityStore<Topic> topic() {
        return topics;
    }

    @Override
    public EntityStore<Subscriber> sub() {
        return subscribers;
    }

    @Override
    public EntityStore<Cert> cert() {
        return certs;
    }
    
    
}
