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
package chronic.entitymap;

import chronic.app.ChronicApp;
import chronic.app.ChronicDatabase;
import chronic.entity.Cert;
import chronic.entity.Person;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import vellum.storage.EntityMatcher;
import vellum.storage.EntityService;
import vellum.storage.MapEntityService;

/**
 *
 * @author evan.summers
 */
public class MockChronicStorage extends ChronicDatabase {
    EntityMatcher matcher = new ChronicMatcher();
    MapEntityService<Person> users = new MapEntityService(matcher);
    MapEntityService<Org> orgs = new MapEntityService(matcher);
    MapEntityService<OrgRole> orgRoles = new MapEntityService(matcher);
    MapEntityService<Topic> topics = new MapEntityService(matcher);
    MapEntityService<Subscriber> subscribers = new MapEntityService(matcher);
    MapEntityService<Cert> certs = new MapEntityService(matcher);

    public MockChronicStorage(ChronicApp app) {
        super(app);
    }
        
    @Override
    public void close() {
    }

    @Override
    public EntityService<Person> person() {
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
