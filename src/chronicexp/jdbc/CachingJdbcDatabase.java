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
package chronicexp.jdbc;

import chronic.app.*;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import chronic.entitymap.ChronicMatcher;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.CachingEntityService;
import vellum.storage.DelegatingEntityService;
import vellum.storage.EntityService;

/**
 *
 * @author evan.summers
 */
public class CachingJdbcDatabase extends ChronicDatabase {

    static Logger logger = LoggerFactory.getLogger(ChronicDatabase.class);

    static final ChronicMatcher matcher = new ChronicMatcher();
    static final CachingEntityService<Cert> certCache = new CachingEntityService(100, matcher);
    static final CachingEntityService<User> userCache = new CachingEntityService(100, matcher);
    static final CachingEntityService<Org> orgCache = new CachingEntityService(100, matcher);
    static final CachingEntityService<OrgRole> roleCache = new CachingEntityService(100, matcher);
    static final CachingEntityService<Topic> topicCache = new CachingEntityService(100, matcher);
    static final CachingEntityService<Subscriber> subCache = new CachingEntityService(100, matcher);
    
    private EntityManager em;
    private Connection connection;

    public EntityService<User> user;
    public EntityService<Org> org;
    public EntityService<OrgRole> role;
    public EntityService<Topic> topic;
    public EntityService<Subscriber> sub;
    public EntityService<Cert> cert;

    public CachingJdbcDatabase(ChronicApp app) {
        super(app);
    }
    
    public void open() throws SQLException {
        connection = app.getDataSource().getConnection();
        em = app.getEntityManagerFactory().createEntityManager();
        user = new DelegatingEntityService(userCache, new UserService(connection));
        org = new DelegatingEntityService(orgCache, new OrgService(connection));
        role = new DelegatingEntityService(roleCache, new OrgRoleService(connection));
        topic = new DelegatingEntityService(topicCache, new TopicService(connection));
        sub = new DelegatingEntityService(subCache, new SubscriberService(connection));
        cert = new DelegatingEntityService(certCache, new CertService(connection));
    }

    public void begin() throws SQLException {
        connection.setAutoCommit(false);
        em.getTransaction().begin();
    }
    
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.warn("rollback connection {}", e);
        }
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            logger.warn("commit connection {}", e);
        }
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.warn("close connection {}", e);
        }
        if (em != null) {
            em.close();
        }
    }

    @Override
    public EntityService<User> user() {
        return user;
    }

    @Override
    public EntityService<Org> org() {
        return org;
    }

    @Override
    public EntityService<OrgRole> role() {
        return role;
    }

    @Override
    public EntityService<Topic> topic() {
        return topic;
    }

    @Override
    public EntityService<Subscriber> sub() {
        return sub;
    }

    @Override
    public EntityService<Cert> cert() {
        return cert;
    }
}
