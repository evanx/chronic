
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
package chronic.scripts;

import chronic.*;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;
import chronic.entitytype.OrgRoleType;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class ChronicPersistenceTest {

    static Logger logger = LoggerFactory.getLogger(ChronicTest.class);

    ChronicApp app = new ChronicApp();

    String orgUnit = "test";
    String commonName = "root";
    String topicLabel1 = "minutely";
    String topicLabel2 = "hourly";
    String email1 = "evan.summers@gmail.com";
    String email2 = "evanx@chronica.co";
    String address = "127.0.0.1";    
    TestProperties p1 = new TestProperties("chronica.co");
    TestProperties p2 = new TestProperties("test.org");
    
    class TestProperties {

        String orgDomain;
        CertKey certKey;
        Org org;
        OrgRole orgRole;
        Cert cert;
        Topic topic;
        Person person;
        
        public TestProperties(String orgDomain) {
            this.orgDomain = orgDomain;
            certKey = new CertKey(orgDomain, orgUnit, commonName);
        }
    }
    
    public ChronicPersistenceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() throws Exception {
        app.init();
        app.ensureInitialized();
        ChronicEntityService es = new ChronicEntityService(app);
        es.begin();
        p1.person = es.persistPerson(email1);
        p2.person = es.persistPerson(email2);
        p1.cert = es.persistCert(p1.orgDomain, orgUnit, commonName, address, "encoded");
        p2.cert = es.persistCert(p2.orgDomain, orgUnit, commonName, address, "encoded");
        p1.org = p1.cert.getOrg();
        p2.org = p2.cert.getOrg();
        p1.orgRole = es.persistOrgRole(p1.org, email1, OrgRoleType.ADMIN);
        p2.orgRole = es.persistOrgRole(p2.org, email1, OrgRoleType.ADMIN);
        assert p1.org != null;
        assert p2.org != null;
        logger.info("org {}", p1.org);
        p1.topic = es.persistTopic(p1.cert, topicLabel1);
        p2.topic = es.persistTopic(p2.cert, topicLabel2);
        logger.info("topic {}", p1.topic);
        logger.info("topic {}", p2.topic);
        es.commit();
        es = new ChronicEntityService(app);
        es.begin();
        List<Cert> certs = es.listCerts(email1);
        logger.info("certs {}", certs);
        Assert.assertEquals("certs size", 2, certs.size());
        es.rollback();
    }
    
}
