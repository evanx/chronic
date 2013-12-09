/*
 * Source https://code.google.com/p/vellum by @evanxsummers

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

import chronic.entity.User;
import chronic.entitytype.OrgRoleType;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.ChronicQueryType;
import vellum.storage.Storage;
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
    
    public abstract Storage<User> getUserStorage();
    
    public abstract Storage<Org> getOrgStorage();
    
    public abstract Storage<OrgRole> getOrgRoleStorage();

    public abstract Storage<Network> getNetworkStorage();

    public abstract Storage<Topic> getTopicStorage();
    
    public abstract Storage<Subscriber> getSubscriberStorage();
    
    public Iterable<String> getEmails(AlertRecord alert) {
        List<String> list = new ArrayList();
        list.add(app.getProperties().getAdminEmails().iterator().next());
        return list;
    }

    public static ChronicStorage create(ChronicApp app) {
        return new TemporaryChronicStorage(app);
    }

    public Iterable<Topic> listTopics(Org org) {
        Set<Topic> topics = new TreeSet();
        for (Topic topic : getTopicStorage().selectCollection(
                ChronicQueryType.TOPICS_org, org.getOrgUrl())) {
            if (topic.getOrgUrl().equals(org.getOrgUrl())) {
                topics.add(topic);
            }
        }
        return topics;
    }

    public Iterable<Topic> listTopics(String email) throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (Subscriber topicSubscriber : getSubscriberStorage().selectCollection(null)) {
            logger.info("listTopics topicSubscriber {}", topicSubscriber);
            if (topicSubscriber.getEmail().equals(email)) {
                topics.add(getTopicStorage().find(Comparables.tuple(
                        topicSubscriber.getOrgUrl(), topicSubscriber.getTopicString())));
            }            
        }
        return topics;
    }
    
    public Map<OrgRoleType, OrgRole> mapOrgRole(String url, String email) {
        Map map = new HashMap();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                map.put(orgRole.getRoleType(), orgRole);
            }
        }        
        return map;
    }

    public Collection<OrgRoleType> listOrgRoleType(String url, String email) {
        List<OrgRoleType> roleTypes = new LinkedList();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                roleTypes.add(orgRole.getRoleType());
            }
        }        
        return roleTypes;
    }

    public boolean isOrgRoleType(String orgUrl, OrgRoleType roleType) {
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl) && orgRole.getRoleType() == roleType) {
                return true;
            }
        }        
        return false;
    }
    
    public Iterable<OrgRole> listOrgRole(String orgUrl) {
        List list = new LinkedList();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(orgUrl)) {
                list.add(orgRole);
            }
        }        
        return list;
    }
    
    public Iterable<User> listUsers(String email) {
        List list = new LinkedList();
        return list;
    }

    public Iterable<Subscriber> listSubscribers(String email) {
        List list = new LinkedList();
        for (Subscriber subscriber : getSubscriberStorage().selectCollection(null)) {
            logger.info("listTopics topicSubscriber {}", subscriber);
            if (subscriber.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(subscriber);
            }            
        }
        return list;
    }

    public Iterable<OrgRole> listRoles(String email) {
        List list = new LinkedList();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(orgRole);
            }
        }        
        return list;
    }        

}
