
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
import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Person;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;
import chronic.entitytype.OrgRoleType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

    static ChronicApp app = new ChronicApp();

    String orgUnit = "test";
    String address = "127.0.0.1";    
    String encoded = "encoded";
    Props p1 = new Props("chronica.co", "root", "minutely", "evan.summers@gmail.com");
    Props p2 = new Props("test.org", "chronica", "hourly", "evanx@chronica.co");
    
    class Props {

        String commonName;
        String orgDomain;
        String topicLabel;
        String email;
        CertKey certKey;
        Org org;
        OrgRole orgRole;
        Cert cert;
        Topic topic;
        Person person;
        
        public Props(String orgDomain, String commonName, String topicLabel, String email) {
            this.orgDomain = orgDomain;
            this.commonName = commonName;
            this.topicLabel = topicLabel;
            this.email = email;
            certKey = new CertKey(orgDomain, orgUnit, commonName);
        }
    }
    
    public ChronicPersistenceTest() throws Exception {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        app.init();
        app.ensureInitialized();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEntityManager() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        p1.org = new Org(p1.orgDomain);
        em.persist(p1.org);
        Assert.assertNotNull(em.find(Org.class, p1.orgDomain));
        p1.cert = new Cert(p1.orgDomain, orgUnit, p1.commonName);
        p1.cert.setAddress(address);
        p1.cert.setEncoded(encoded);
        em.persist(p1.cert);
        Assert.assertNotNull(p1.cert.getId());
        Assert.assertNotNull(em.find(Cert.class, p1.cert.getId()));
        p1.person = new Person(p1.email);
        em.persist(p1.person);
        Assert.assertNotNull(em.find(Person.class, p1.email));
        p1.orgRole = new OrgRole(p1.orgDomain, p1.email, OrgRoleType.ADMIN);
        em.persist(p1.orgRole);
        Assert.assertNotNull(p1.orgRole.getId());
        Assert.assertNotNull(em.find(OrgRole.class, p1.orgRole.getId()));
        Assert.assertEquals(1, em.createQuery("select r from OrgRole r").getResultList().size());
        Assert.assertEquals(1, em.createQuery("select r from OrgRole r join Org o").getResultList().size());
        assertSize(1, em.createQuery("select r from OrgRole r join Org o"
                + " where o.orgDomain = :orgDomain"
                + "")
                .setParameter("orgDomain", p1.orgDomain)
                .getResultList());
        assertSize(1, em.createQuery("select r from OrgRole r join Org o join Cert c where r.email = :email")
                .setParameter("email", p1.email)
                .getResultList());
        p2.org = new Org(p2.orgDomain);
        em.persist(p2.org);
        assertSize(1, em.createQuery("select r from OrgRole r"
                + " join Org o on (o.orgDomain = r.orgDomain)"
                + " join Cert c on (c.orgDomain = r.orgDomain)"
                + " where r.email = :email")
                .setParameter("email", p1.email)
                .getResultList());
        assertSize(1, em.createQuery("select r from OrgRole r join Org o where o.orgDomain = :orgDomain")
                .setParameter("orgDomain", p1.orgDomain)
                .getResultList());
        Assert.assertNull(em.find(Person.class, ""));
        em.getTransaction().commit();
        em.close();
    }
    
    private static void assertSize(int size, List list) throws Exception {
          if (list.size() != size) {
              logger.error("expected {}, got {}", size, list.size());
              int index = 0;
              for (Object element : list) {
                  logger.info("list {} {}: " + element, index++, element.getClass().getSimpleName());
              }
              throw new Exception("list size " + list.size());
          }
    }
    
    @Test
    public void testEntityService() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
        ChronicEntityService es = new ChronicEntityService(app, emf);
        es.begin();
        p1.person = es.persistPerson(p1.email);
        p2.person = es.persistPerson(p2.email);
        p1.cert = es.persistCert(p1.orgDomain, orgUnit, p1.commonName, address, "encoded");
        p2.cert = es.persistCert(p2.orgDomain, orgUnit, p2.commonName, address, "encoded");
        p2.cert = es.persistCert(p2.orgDomain, orgUnit, p1.commonName, address, "encoded");
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
