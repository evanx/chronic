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

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entity.Org;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.OrgRoleKeyed;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.SubscriberKeyed;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.entitykey.UserKey;
import chronic.entitykey.UserKeyed;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.AbstractEntity;
import vellum.storage.MapStore;

/**
 *
 * @author evan.summers
 */
public class ChronicMapStore<E extends AbstractEntity> extends MapStore<E> {
    Logger logger = LoggerFactory.getLogger(ChronicMapStore.class);
    
    @Override
    public Collection<E> list(Comparable key) {
        if (key instanceof Org) {
            key = new OrgKey(((Org) key).getOrgUrl());
        }
        Collection list = new LinkedList();
        for (E entity : list()) {
            if (matches(entity, key)) {
                list.add(entity);
            }
        }
        return list;
    }

    private boolean matches(E entity, Comparable key) {
        if (key instanceof OrgKey) {
            if (entity instanceof OrgKeyed)  {
                return matches((OrgKeyed) entity, (OrgKey) key);
            }
        }
        if (key instanceof OrgKey) {
            if (entity instanceof OrgKeyed)  {
                return matches((OrgKeyed) entity, (OrgKey) key);
            }
        }
        return false;
    }
    
    private boolean matches(CertKeyed keyed, CertKey key) {
        return keyed.getCertKey().equals(key);        
    }

    private boolean matches(OrgKeyed keyed, OrgKey key) {
        return keyed.getOrgKey().equals(key);        
    }

    private boolean matches(UserKeyed keyed, UserKey key) {
        return keyed.getUserKey().equals(key);        
    }
    
    private boolean matches(OrgRoleKeyed keyed, OrgRoleKey key) {
        return keyed.getOrgRoleKey().equals(key);        
    }
    
    private boolean matches(TopicKeyed keyed, TopicKey key) {
        return keyed.getTopicKey().equals(key);        
    }

    private boolean matches(SubscriberKeyed keyed, SubscriberKey key) {
        return keyed.getSubscriberKey().equals(key);        
    }
    
}
