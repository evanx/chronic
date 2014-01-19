
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
package chronic.entity;

import chronic.TestTimeZone;
import chronic.entitytype.OrgRoleType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class TestOrgRole {

    static Logger logger = LoggerFactory.getLogger(TestTimeZone.class);

    TestEntityInfo p1 = new TestEntityInfo("chronica.co", "root", "minutely", "evan.summers@gmail.com");
    TestEntityInfo p2 = new TestEntityInfo("test.org", "chronica", "hourly", "evanx@chronica.co");
    
    public TestOrgRole() throws Exception {
    }

    @Test
    public void orgRole() throws Exception {
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
        Assert.assertNotNull(em.find(TestOrgRole.class, p1.orgRole.getId()));
        TestEntityInfo.assertSize("org role", 1, em.createQuery("select r from OrgRole r join Org o"
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
        TestEntityInfo.assertSize("org role", 1, em.createQuery("select r from OrgRole r"
                + " join Org o on (o.orgDomain = r.orgDomain)"
                + " join Cert c on (c.orgDomain = r.orgDomain)"
                + " where r.email = :email")
                .setParameter("email", p1.email)
                .getResultList());
        TestEntityInfo.assertSize("org role", 1, em.createQuery(
                "select r from OrgRole r join Org o where o.orgDomain = :orgDomain")
                .setParameter("orgDomain", p1.orgDomain)
                .getResultList());
        Assert.assertNull(em.find(Person.class, ""));        
        p2.person = new Person(p2.email);
        em.persist(p2.person);
        Assert.assertNotNull(em.find(Person.class, p2.email));
        em.getTransaction().commit();
        em.close();
    }

    
}
