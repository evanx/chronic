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
import chronic.entitytype.UserRoleType;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.TopicSubscriber;
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
    
    public abstract Storage<TopicSubscriber> getSubscriberStorage();
    
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
        for (TopicSubscriber topicSubscriber : getSubscriberStorage().selectCollection(null)) {
            logger.info("listTopics topicSubscriber {}", topicSubscriber);
            if (topicSubscriber.getEmail().equals(email)) {
                topics.add(getTopicStorage().find(Comparables.tuple(
                        topicSubscriber.getOrgUrl(), topicSubscriber.getTopicString())));
            }            
        }
        return topics;
    }
    
    public Map<UserRoleType, OrgRole> mapOrgRole(String url, String email) {
        Map map = new HashMap();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                map.put(orgRole.getRole(), orgRole);
            }
        }        
        return map;
    }

    public Collection<UserRoleType> listOrgRoleType(String url, String email) {
        List<UserRoleType> roleTypes = new LinkedList();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                roleTypes.add(orgRole.getRole());
            }
        }        
        return roleTypes;
    }

    public Iterable<User> listUsers(String email) {
        List list = new LinkedList();
        return list;
    }

    public Iterable<TopicSubscriber> listSubscribers(String email) {
        List list = new LinkedList();
        for (TopicSubscriber subscriber : getSubscriberStorage().selectCollection(null)) {
            logger.info("listTopics topicSubscriber {}", subscriber);
            if (subscriber.getEmail().equals(email)) {
                list.add(subscriber);
            }            
        }
        return list;
    }

    public Iterable<OrgRole> listRoles(String email) {
        List list = new LinkedList();
        for (OrgRole orgRole : getOrgRoleStorage().selectCollection(null)) {
            if (orgRole.getEmail().equals(email)) {
                list.add(orgRole);
            }
        }        
        return list;
    }        
}
