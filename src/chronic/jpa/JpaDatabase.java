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
package chronic.jpa;

import chronic.app.*;
import chronic.entity.Cert;
import chronic.entity.User;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscriber;
import chronic.entitymap.ChronicMapEntityService;
import chronicexp.jdbc.CertService;
import chronicexp.jdbc.SubscriberService;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.EntityService;

/**
 *
 * @author evan.summers
 */
public class JpaDatabase extends ChronicDatabase {

    static Logger logger = LoggerFactory.getLogger(ChronicDatabase.class);

    protected ChronicApp app;
    protected EntityManager em;
    protected Connection connection;

    public EntityService<User> user;        

    public EntityService<Org> org;

    public EntityService<OrgRole> role;

    public EntityService<Topic> topic;

    public EntityService<Subscriber> sub;

    public EntityService<Cert> cert;
    
    public JpaDatabase(ChronicApp app, Connection connection, EntityManager em) {
        super(app);
        this.connection = connection;
        this.em = em;
        user = new ChronicMapEntityService(User.class);
        org = new ChronicMapEntityService(Org.class);
        role = new ChronicMapEntityService(OrgRole.class);
        topic = new ChronicMapEntityService(Topic.class);
        sub = new SubscriberService(connection);
        cert = new CertService(connection);
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
        em.close();
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
