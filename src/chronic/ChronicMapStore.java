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
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgRoleTypeKeyed;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.SubscriberKeyed;
import chronic.entitykey.TopicOrgKey;
import chronic.entitykey.TopicOrgKeyed;
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
            if (matches(key, entity)) {
                list.add(entity);
            }
        }
        return list;
    }

    private boolean matches(Comparable key, E entity) {
        if (key instanceof UserKey) {
            if (entity instanceof UserKeyed)  {
                return matches((UserKeyed) entity, (UserKey) key);
            }
        }
        if (key instanceof OrgKey) {
            if (entity instanceof OrgKeyed)  {
                return matches((OrgKeyed) entity, (OrgKey) key);
            }
        }
        if (key instanceof OrgRoleKey) {
            if (entity instanceof OrgRoleKeyed)  {
                return matches((OrgRoleKeyed) entity, (OrgRoleKey) key);
            }
        }
        if (key instanceof OrgRoleTypeKey) {
            if (entity instanceof OrgRoleTypeKeyed)  {
                return matches((OrgRoleTypeKeyed) entity, (OrgRoleTypeKey) key);
            }
        }
        if (key instanceof TopicOrgKey) {
            if (entity instanceof TopicOrgKeyed)  {
                return matches((TopicOrgKeyed) entity, (TopicOrgKey) key);
            }
        }
        if (key instanceof SubscriberKey) {
            if (entity instanceof SubscriberKeyed)  {
                return matches((SubscriberKeyed) entity, (SubscriberKey) key);
            }
        }
        if (key instanceof CertKey) {
            if (entity instanceof CertKeyed)  {
                return matches((CertKeyed) entity, (CertKey) key);
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

    private boolean matches(OrgRoleTypeKeyed keyed, OrgRoleTypeKey key) {
        return keyed.getOrgRoleTypeKey().equals(key);        
    }
    
    private boolean matches(TopicOrgKeyed keyed, TopicOrgKey key) {
        return keyed.getTopicOrgKey().equals(key);        
    }

    private boolean matches(SubscriberKeyed keyed, SubscriberKey key) {
        return keyed.getSubscriberKey().equals(key);        
    }
    
}
