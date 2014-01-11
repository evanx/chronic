
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
public class EntityTest {

    static Logger logger = LoggerFactory.getLogger(TimeZoneTest.class);

    static ChronicApp app = new ChronicApp();

    EntityInfo p1 = new EntityInfo("chronica.co", "root", "minutely", "evan.summers@gmail.com");
    EntityInfo p2 = new EntityInfo("test.org", "chronica", "hourly", "evanx@chronica.co");
    
    public EntityTest() throws Exception {
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
        p1.cert = new Cert(p1.orgDomain, p1.orgUnit, p1.commonName);
        p1.cert.setAddress(p1.address);
        p1.cert.setEncoded(p1.encoded);
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
        assertSize(1, em.createQuery("select r from OrgRole r join Org o"
                        + " where o.orgDomain = r.orgDomain"
                        + " and o.orgDomain = :orgDomain")
                .setParameter("orgDomain", p1.orgDomain)
                .getResultList());
        Assert.assertEquals(1, em.createQuery(
                "select r from OrgRole r join Org o").
                getResultList().size());
        Assert.assertEquals(1, em.createQuery(
                "select r from OrgRole r join Org o join Cert c where r.email = :email")
                .setParameter("email", p1.email)
                .getResultList().size());
        p2.org = new Org(p2.orgDomain);
        em.persist(p2.org);
        assertSize(1, em.createQuery("select r from OrgRole r"
                + " join Org o on (o.orgDomain = r.orgDomain)"
                + " join Cert c on (c.orgDomain = r.orgDomain)"
                + " where r.email = :email")
                .setParameter("email", p1.email)
                .getResultList());
        assertSize(1, em.createQuery(
                "select r from OrgRole r join Org o where o.orgDomain = :orgDomain")
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
    
}
