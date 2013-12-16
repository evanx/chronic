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
import chronic.entitykey.OrgRoleTypeKey;
import chronic.entitykey.OrgUserKey;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.UserKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
import vellum.storage.EntityStore;
import vellum.storage.StorageException;

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

    public abstract EntityStore<User> user();

    public abstract EntityStore<Org> org();

    public abstract EntityStore<OrgRole> role();

    public abstract EntityStore<Network> net();

    public abstract EntityStore<Topic> topic();

    public abstract EntityStore<Subscriber> sub();

    public abstract EntityStore<Cert> cert();

    public static ChronicStorage create(ChronicApp app) {
        return new MockChronicStorage(app);
    }

    public Iterable<User> listUsers(String email) {
        List list = new LinkedList();
        return list;
    }

    public Iterable<OrgRole> listRoles(String email) {
        List list = new LinkedList();
        for (OrgRole orgRole : role().list(new UserKey(email))) {
            if (orgRole.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(orgRole);
            }
        }
        return list;
    }

    public Iterable<String> listOrgUrls(String email, OrgRoleType roleType) {
        List list = new LinkedList();
        for (OrgRole orgRole : role().list(new UserKey(email))) {
            if (orgRole.getEmail().equals(email)) {
                if (roleType == null || orgRole.getRoleType() == roleType) {
                    list.add(orgRole.getOrgUrl());
                }
            }
        }
        return list;
    }

    public Map<OrgRoleType, OrgRole> mapOrgRole(String orgUrl, String email) {
        Map map = new HashMap();
        for (OrgRole orgRole : role().list(new OrgUserKey(orgUrl, email))) {
            map.put(orgRole.getRoleType(), orgRole);
        }
        return map;
    }

    public Collection<OrgRoleType> listOrgRoleType(String orgUrl, String email) {
        List<OrgRoleType> roleTypes = new LinkedList();
        for (OrgRole orgRole : role().list(new OrgUserKey(orgUrl, email))) {
            roleTypes.add(orgRole.getRoleType());
        }
        return roleTypes;
    }

    public boolean isOrgRoleType(String orgUrl, OrgRoleType roleType) {
        return !role().list(new OrgRoleTypeKey(orgUrl, roleType)).isEmpty();
    }

    public Iterable<OrgRole> listOrgRole(String orgUrl) {
        return role().list(new OrgKey(orgUrl));
    }

    public Iterable<Topic> listTopics(String email) throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (Subscriber subscriber : sub().list(new UserKey(email))) {
            logger.info("listTopics subscriber {}", subscriber);
            topics.add(topic().find(subscriber.getTopicKey()));
        }
        return topics;
    }

    public Iterable<String> listSubscriberEmails(AlertRecord alert) {
        Set<String> set = new TreeSet();
        for (Subscriber subscriber : sub().list(alert.getStatus().getOrgTopicKey())) {
            set.add(subscriber.getEmail());
        }
        logger.info("listSubscriberEmails {} {}", alert.getStatus().getTopicString(), set);
        set.clear();
        set.add(app.getProperties().getAdminEmails().iterator().next());
        logger.info("listSubscriberEmails {} {}", alert.getStatus().getTopicString(), set);
        return set;
    }

    public Iterable<Subscriber> listSubscribers(String email) {
        Set set = new TreeSet();
        set.addAll(sub().list(new UserKey(email)));
        logger.info("listSubscriber {}", set);
        if (app.getProperties().isAdmin(email)) {
            set.addAll(sub().list());
        }
        return set;
    }


    public boolean isSubscriber(String email, AlertRecord alert) {
        return isSubscriber(email, alert.getStatus());
    }

    public boolean isSubscriber(String email, StatusRecord status) {
        SubscriberKey key = new SubscriberKey(status.getTopicKey(), email);
        return sub().containsKey(key);
    }

    public Iterable<Cert> listCerts(String email) {
        Set set = new TreeSet(TimestampedComparator.reverse());
        for (String orgUrl : listOrgUrls(email, OrgRoleType.ADMIN)) {
            logger.info("listCerts {}", orgUrl);
            set.addAll(cert().list(new OrgKey(orgUrl)));
        }
        if (app.getProperties().isAdmin(email)) {
            set.addAll(cert().list());
        }
        return set;
    }

}
