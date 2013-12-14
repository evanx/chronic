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
public class ChronicMemoryStore extends ChronicStorage {
    Server h2Server;
    MapStore<User> userStorage = new ChronicMapStore();
    MapStore<Org> orgStorage = new ChronicMapStore();
    MapStore<OrgRole> orgRoleStorage = new ChronicMapStore();
    MapStore<Network> networkStorage = new ChronicMapStore();
    MapStore<Topic> topicStorage = new ChronicMapStore();
    MapStore<Subscriber> subscriberStorage = new ChronicMapStore();
    MapStore<Cert> certStorage = new ChronicMapStore();

    public ChronicMemoryStore(ChronicApp app) {
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
        return userStorage;
    }

    @Override
    public EntityStore<Org> org() {
        return orgStorage;
    }
    
    @Override
    public EntityStore<OrgRole> role() {
        return orgRoleStorage;
    }

    @Override
    public EntityStore<Network> network() {
        return networkStorage;
    }
    
    @Override
    public EntityStore<Topic> topic() {
        return topicStorage;
    }

    @Override
    public EntityStore<Subscriber> sub() {
        return subscriberStorage;
    }

    @Override
    public EntityStore<Cert> cert() {
        return certStorage;
    }
    
    
}
