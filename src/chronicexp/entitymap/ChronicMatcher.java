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
package chronicexp.entitymap;

import chronic.entity.Org;
import chronic.entitykey.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import vellum.data.ComparableTuple;
import vellum.storage.VellumEntity;
import vellum.storage.EntityMatcher;

/**
 *
 * @author evan.summers
 */
public class ChronicMatcher<E extends VellumEntity> implements EntityMatcher<E> {
        
    @Override
    public Collection<E> matches(Collection<E> entities, Comparable key) {
        if (key instanceof Org) {
            key = new OrgKey(((Org) key).getOrgDomain());
        }
        Collection<E> list = new LinkedList();
        for (E entity : entities) {
            if (matches(key, entity)) {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public boolean matches(Comparable key, E entity) {
        if (key instanceof PersonKey) {
            if (entity instanceof PersonKeyed)  {
                return matches((PersonKeyed) entity, (PersonKey) key);
            }
        }
        if (key instanceof CertTopicKey) {
            if (entity instanceof CertTopicKeyed)  {
                return matches((CertTopicKeyed) entity, (CertTopicKey) key);
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
        if (entity.getId() instanceof ComparableTuple) {
            return ((ComparableTuple) entity.getId()).contains(key);
        }
        return entity.getId().equals(key);
    }

    @Override
    public Collection<Comparable> getKeys(E entity) {
        List<Comparable> list = new LinkedList();
        if (entity instanceof PersonKeyed) {
            list.add(((PersonKeyed) entity).getPersonKey());
        }
        if (entity instanceof OrgKeyed) {
            list.add(((OrgKeyed) entity).getOrgKey());
        }
        if (entity instanceof CertTopicKeyed) {
            list.add(((CertTopicKeyed) entity).getCertTopicKey());
        }
        if (entity instanceof OrgTopicKeyed) {
            list.add(((OrgTopicKeyed) entity).getOrgTopicKey());
        }
        if (entity instanceof SubscriberKeyed) {
            list.add(((SubscriberKeyed) entity).getSubscriberKey());
        }
        if (entity instanceof CertKeyed) {
            list.add(((CertKeyed) entity).getCertKey());
        }
        return list;
    }
    
    private boolean matches(CertKeyed keyed, CertKey key) {
        return keyed.getCertKey().matches(key);        
    }

    private boolean matches(PersonKeyed keyed, PersonKey key) {
        return keyed.getPersonKey().matches(key);        
    }
    
    private boolean matches(OrgKeyed keyed, OrgKey key) {
        return keyed.getOrgKey().matches(key);        
    }
    
    private boolean matches(OrgTopicKeyed keyed, OrgTopicKey key) {
        return keyed.getOrgTopicKey().matches(key);        
    }

    private boolean matches(CertTopicKeyed keyed, CertTopicKey key) {
        return keyed.getCertTopicKey().matches(key);        
    }
    
    private boolean matches(SubscriberKeyed keyed, SubscriberKey key) {
        return keyed.getSubscriberKey().matches(key);
    }
    
}
