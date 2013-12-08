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

import static chronic.ChronicStorage.logger;
import chronic.entity.AdminUser;
import chronic.entity.AdminUserRoleType;
import chronic.entity.Network;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.TopicSubscriber;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.h2.tools.Server;
import vellum.storage.Storage;
import vellum.storage.StorageException;
import vellum.storage.TemporaryStorage;
import vellum.util.Comparables;

/**
 *
 * @author evan.summers
 */
public class TemporaryChronicStorage extends ChronicStorage {
    Server h2Server;
    TemporaryStorage<AdminUser> adminUserStorage = new TemporaryStorage();
    TemporaryStorage<Org> orgStorage = new TemporaryStorage();
    TemporaryStorage<OrgRole> orgRoleStorage = new TemporaryStorage();
    TemporaryStorage<Network> networkStorage = new TemporaryStorage();
    TemporaryStorage<Topic> topicStorage = new TemporaryStorage();
    TemporaryStorage<TopicSubscriber> topicSubscriberStorage = new TemporaryStorage();
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
    public Storage<AdminUser> getAdminUserStorage() {
        return adminUserStorage;
    }

    @Override
    public Storage<Org> getOrgStorage() {
        return orgStorage;
    }
    
    @Override
    public Storage<OrgRole> getOrgRoleStorage() {
        return orgRoleStorage;
    }

    @Override
    public Storage<Network> getNetworkStorage() {
        return networkStorage;
    }
    
    @Override
    public Storage<Topic> getTopicStorage() {
        return topicStorage;
    }

    @Override
    public Storage<TopicSubscriber> getTopicSubscriberStorage() {
        return topicSubscriberStorage;
    }

    @Override
    public Iterable<Topic> listTopics(String email) throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (TopicSubscriber topicSubscriber : topicSubscriberStorage.selectCollection(null)) {
            logger.info("listTopics topicSubscriber {}", topicSubscriber);
            if (topicSubscriber.getEmail().equals(email)) {
                topics.add(topicStorage.find(Comparables.tuple(
                        topicSubscriber.getOrgUrl(), topicSubscriber.getTopicString())));
            }            
        }
        return topics;
    }
    
    @Override
    public Map<AdminUserRoleType, OrgRole> mapOrgRole(String url, String email) {
        Map map = new HashMap();
        for (OrgRole orgRole : orgRoleStorage.selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                map.put(orgRole.getRole(), orgRole);
            }
        }        
        return map;
    }

    @Override
    public Collection<AdminUserRoleType> listOrgRoleType(String url, String email) {
        List<AdminUserRoleType> roleTypes = new LinkedList();
        for (OrgRole orgRole : orgRoleStorage.selectCollection(null)) {
            if (orgRole.getOrgUrl().equals(url) && 
                    orgRole.getEmail().equals(email)) {
                roleTypes.add(orgRole.getRole());
            }
        }        
        return roleTypes;
    }
    
}
