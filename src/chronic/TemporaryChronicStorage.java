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
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.h2.tools.Server;
import vellum.storage.Storage;
import vellum.storage.TemporaryStorage;

/**
 *
 * @author evan.summers
 */
public class TemporaryChronicStorage extends ChronicStorage {
    Server h2Server;
    TemporaryStorage<User> userStorage = new TemporaryStorage();
    TemporaryStorage<Org> orgStorage = new TemporaryStorage();
    TemporaryStorage<OrgRole> orgRoleStorage = new TemporaryStorage();
    TemporaryStorage<Network> networkStorage = new TemporaryStorage();
    TemporaryStorage<Topic> topicStorage = new TemporaryStorage();
    TemporaryStorage<Subscriber> subscriberStorage = new TemporaryStorage();
    TemporaryStorage<Cert> certStorage = new TemporaryStorage();
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
    public Storage<User> user() {
        return userStorage;
    }

    @Override
    public Storage<Org> org() {
        return orgStorage;
    }
    
    @Override
    public Storage<OrgRole> role() {
        return orgRoleStorage;
    }

    @Override
    public Storage<Network> network() {
        return networkStorage;
    }
    
    @Override
    public Storage<Topic> topic() {
        return topicStorage;
    }

    @Override
    public Storage<Subscriber> sub() {
        return subscriberStorage;
    }

    @Override
    public Storage<Cert> cert() {
        return certStorage;
    }
    
    
}
