
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
package chronic;

import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entitytype.OrgRoleType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class EntityServiceTest {

    static Logger logger = LoggerFactory.getLogger(TimeZoneTest.class);

    static ChronicApp app = new ChronicApp();

    EntityInfo p1 = new EntityInfo("chronica.co", "root", "minutely", "evan.summers@gmail.com");
    EntityInfo p2 = new EntityInfo("test.org", "chronica", "hourly", "evanx@chronica.co");
    
    public EntityServiceTest() throws Exception {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        app.init();
        app.ensureInitialized();
    }
    
    @Test
    public void testEntityService() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
        ChronicEntityService es = new ChronicEntityService(app, emf);
        es.begin();
        p1.person = es.persistPerson(p1.email);
        p2.person = es.persistPerson(p2.email);
        p1.cert = es.persistCert(p1.orgDomain, p1.orgUnit, p1.commonName, p1.address, "encoded");
        p2.cert = es.persistCert(p2.orgDomain, p2.orgUnit, p2.commonName, p2.address, "encoded");
        p2.cert = es.persistCert(p2.orgDomain, p2.orgUnit, p1.commonName, p2.address, "encoded");
        p1.org = p1.cert.getOrg();
        p2.org = p2.cert.getOrg();
        assert p1.org != null;
        assert p2.org != null;
        p1.orgRole = es.persistOrgRole(p1.org, p1.email, OrgRoleType.ADMIN);
        es.persistOrgRole(p2.org, p1.email, OrgRoleType.ADMIN);
        p2.orgRole = es.persistOrgRole(p2.org, p2.email, OrgRoleType.ADMIN);
        logger.info("org {}", p1.org);
        p1.topic = es.persistTopic(p1.cert, p1.topicLabel);
        p2.topic = es.persistTopic(p2.cert, p2.topicLabel);
        logger.info("topic {}", p1.topic);
        logger.info("topic {}", p2.topic);
        es.commit();
        es = new ChronicEntityService(app);
        es.begin();        
        Assert.assertNotNull("org", es.findOrg(p1.orgDomain));
        Assert.assertNotNull("org", es.findOrg(p2.orgDomain));
        Assert.assertEquals("org role", 2, es.listOrg(p1.email).size());
        Assert.assertEquals("org role", 1, es.listOrg(p2.email).size());
        Assert.assertEquals("certs org size", 1, es.listCerts(p1.org).size());
        Assert.assertEquals("certs org size", 2, es.listCerts(p2.org).size());
        es.rollback();
    }
    
}
