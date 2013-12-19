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
package chronic.app;

import chronic.entitykey.CertKey;
import chronic.entitykey.CertKeyed;
import chronic.entity.Org;
import chronic.entitykey.OrgKey;
import chronic.entitykey.OrgKeyed;
import chronic.entitykey.OrgUserKey;
import chronic.entitykey.OrgUserKeyed;
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgRoleTypeKeyed;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.SubscriberKeyed;
import chronic.entitykey.OrgTopicKey;
import chronic.entitykey.OrgTopicKeyed;
import chronic.entitykey.OrgUnitKey;
import chronic.entitykey.OrgUnitKeyed;
import chronic.entitykey.TopicKey;
import chronic.entitykey.TopicKeyed;
import chronic.entitykey.UserKey;
import chronic.entitykey.UserKeyed;
import chronic.entitykey.UserRoleTypeKey;
import chronic.entitykey.UserRoleTypeKeyed;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.ComparableTuple;
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
            key = new OrgKey(((Org) key).getOrgDomain());
        }
        Collection list = new LinkedList();
        for (E entity : keyMap.values()) {
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
        if (key instanceof UserRoleTypeKey) {
            if (entity instanceof UserRoleTypeKeyed)  {
                return matches((UserRoleTypeKeyed) entity, (UserRoleTypeKey) key);
            }
        }
        if (key instanceof OrgKey) {
            if (entity instanceof OrgKeyed)  {
                return matches((OrgKeyed) entity, (OrgKey) key);
            }
        }
        if (key instanceof OrgUserKey) {
            if (entity instanceof OrgUserKeyed)  {
                return matches((OrgUserKeyed) entity, (OrgUserKey) key);
            }
        }
        if (key instanceof OrgUnitKey) {
            if (entity instanceof OrgUnitKeyed)  {
                return matches((OrgUnitKeyed) entity, (OrgUnitKey) key);
            }
        }
        if (key instanceof OrgRoleTypeKey) {
            if (entity instanceof OrgRoleTypeKeyed)  {
                return matches((OrgRoleTypeKeyed) entity, (OrgRoleTypeKey) key);
            }
        }
        if (key instanceof TopicKey) {
            if (entity instanceof TopicKeyed)  {
                return matches((TopicKeyed) entity, (TopicKey) key);
            }
        }
        if (key instanceof OrgTopicKey) {
            if (entity instanceof OrgTopicKeyed)  {
                return matches((OrgTopicKeyed) entity, (OrgTopicKey) key);
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
        if (entity.getKey() instanceof ComparableTuple) {
            return ((ComparableTuple) entity.getKey()).contains(key);
        }
        return entity.getKey().equals(key);
    }
    
    private boolean matches(CertKeyed keyed, CertKey key) {
        return keyed.getCertKey().matches(key);        
    }

    private boolean matches(UserKeyed keyed, UserKey key) {
        return keyed.getUserKey().matches(key);        
    }

    private boolean matches(UserRoleTypeKeyed keyed, UserRoleTypeKey key) {
        return keyed.getUserRoleTypeKey().matches(key);        
    }
    
    private boolean matches(OrgKeyed keyed, OrgKey key) {
        return keyed.getOrgKey().matches(key);        
    }
    
    private boolean matches(OrgUserKeyed keyed, OrgUserKey key) {
        return keyed.getOrgUserKey().matches(key);        
    }

    private boolean matches(OrgUnitKeyed keyed, OrgUnitKey key) {
        return keyed.getOrgUnitKey().matches(key);        
    }
    
    private boolean matches(OrgRoleTypeKeyed keyed, OrgRoleTypeKey key) {
        return keyed.getOrgRoleTypeKey().matches(key);        
    }
    
    private boolean matches(OrgTopicKeyed keyed, OrgTopicKey key) {
        return keyed.getOrgTopicKey().matches(key);        
    }

    private boolean matches(TopicKeyed keyed, TopicKey key) {
        return keyed.getTopicKey().matches(key);        
    }
    
    private boolean matches(SubscriberKeyed keyed, SubscriberKey key) {
        return keyed.getSubscriberKey().matches(key);
    }
    
}
