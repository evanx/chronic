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

import static chronic.ChronicStorage.logger;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entitytype.OrgRoleType;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.entitykey.OrgKey;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import chronic.entitykey.SubscriberKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.EntityStore;
import vellum.storage.StorageException;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public abstract class ChronicStorage {
    
    static Logger logger = LoggerFactory.getLogger(ChronicStorage.class);

    ChronicApp app;
    
    public ChronicStorage(ChronicApp app) {
        this.app = app;
    }
        
    public abstract void init() throws Exception;

    public abstract void shutdown();
    
    public abstract EntityStore<User> users();
    
    public abstract EntityStore<Org> orgs();
    
    public abstract EntityStore<OrgRole> roles();

    public abstract EntityStore<Network> nets();

    public abstract EntityStore<Topic> topics();
    
    public abstract EntityStore<Subscriber> subs();
    
    public abstract EntityStore<Cert> certs();
    
    public static ChronicStorage create(ChronicApp app) {
        return new ChronicMapStorage(app);
    }

    public Iterable<User> listUsers(String email) {
        List list = new LinkedList();
        return list;
    }

    
    public Iterable<OrgRole> listRoles(String email) {
        List list = new LinkedList();
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(orgRole);
            }
        }        
        return list;
    }        

    public Iterable<String> listOrgUrls(String email, OrgRoleType roleType) {
        List list = new LinkedList();
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getEmail().equals(email)) {
                if (roleType == null ||  orgRole.getRoleType() == roleType) {
                    list.add(orgRole.getOrgUrl());
                }
            }
        }        
        return list;
    }        
    
    public Map<OrgRoleType, OrgRole> mapOrgRole(String orgUrl, String email) {
        Map map = new HashMap();
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl) && 
                    orgRole.getEmail().equals(email)) {
                map.put(orgRole.getRoleType(), orgRole);
            }
        }        
        return map;
    }

    public Collection<OrgRoleType> listOrgRoleType(String orgUrl, String email) {
        List<OrgRoleType> roleTypes = new LinkedList();
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl) && 
                    orgRole.getEmail().equals(email)) {
                roleTypes.add(orgRole.getRoleType());
            }
        }        
        return roleTypes;
    }

    public boolean isOrgRoleType(String orgUrl, OrgRoleType roleType) {
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl) && orgRole.getRoleType() == roleType) {
                return true;
            }
        }        
        return false;
    }
    
    public Iterable<OrgRole> listOrgRole(String orgUrl) {
        List list = new LinkedList();
        for (OrgRole orgRole : roles().list(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl)) {
                list.add(orgRole);
            }
        }        
        return list;
    }
        
    public Iterable<Topic> listTopics(String email) throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (Subscriber subscriber : subs().list(null)) {
            logger.info("listTopics subscriber {}", subscriber);
            if (subscriber.getEmail().equals(email)) {
                topics.add(topics().find(Comparables.tuple(
                        subscriber.getOrgUrl(), subscriber.getTopicString())));
            }            
        }
        return topics;
    }

    public Iterable<String> listSubscriberEmails(AlertRecord alert) {
        Set<String> set = new HashSet();
        for (Subscriber subscriber : listSubscribers(alert.getStatus())) {
            set.add(subscriber.getEmail());
        }
        logger.warn("listSubscriberEmails {} {}", alert.getStatus().getTopicString(), set);
        set.clear();
        set.add(app.getProperties().getAdminEmails().iterator().next());
        logger.info("listSubscriberEmails {} {}", alert.getStatus().getTopicString(), set);
        return set;
    }
    
    public Iterable<Subscriber> listSubscribers(String email) {
        List list = new LinkedList();
        for (Subscriber subscriber : subs().list(null)) {
            logger.info("listSubscriber subscriber {}", subscriber);
            if (subscriber.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(subscriber);
            }            
        }
        return list;
    }

    public Iterable<Subscriber> listSubscribers(StatusRecord status) {
        List list = new LinkedList();
        for (Subscriber sub : listSubscribersOrg(status.getOrgUrl())) {
            if (sub.getTopicString().equals(status.getTopicString())) {
                list.add(sub);
            }
        }
        return list;
    }
    
    
    public Iterable<Subscriber> listSubscribersOrg(String orgUrl) {
        List list = new LinkedList();
        for (Subscriber subscriber : subs().list(null)) {
            logger.info("listSubscriberOrg subscriber {}", subscriber);
            if (subscriber.getOrgUrl().equals(orgUrl)) {
                list.add(subscriber);
            }            
        }
        return list;
    }

    public boolean isSubscriber(String email, AlertRecord alert) {
        return isSubscriber(email, alert.getStatus());
    }
    
    public boolean isSubscriber(String email, StatusRecord status) {
        SubscriberKey key = new SubscriberKey(status.getOrgUrl(), 
                status.getTopicString(), email);
        return subs().containsKey(key);
    }    

    public Iterable<Cert> listCerts(String email) {
        List list = new LinkedList();
        for (String orgUrl : listOrgUrls(email, OrgRoleType.ADMIN)) {
            list.addAll(certs().list(new OrgKey(orgUrl)));
        }
        return list;
    }
   
}
