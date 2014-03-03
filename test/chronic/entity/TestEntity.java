
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
import chronic.type.StatusType;
import java.util.Calendar;
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
public class TestEntity {

    static Logger logger = LoggerFactory.getLogger(TestTimeZone.class);

    TestEntityInfo p1 = new TestEntityInfo("chronica.co", "root", "minutely", "evan.summers@gmail.com");
    TestEntityInfo p2 = new TestEntityInfo("test.org", "chronica", "hourly", "evanx@chronica.co");
    
    @Test
    public void topic() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        p1.org = new Org(p1.orgDomain, "localhost");
        em.persist(p1.org);
        Assert.assertNotNull(em.find(Org.class, p1.orgDomain));
        p1.cert = new Cert(p1.orgDomain, p1.orgUnit, p1.commonName);
        p1.cert.setAddress(p1.address);
        p1.cert.setEncoded(p1.encoded);
        em.persist(p1.cert);
        Assert.assertNotNull(p1.cert.getId());
        p1.person = new Person(p1.email);
        em.persist(p1.person);
        Assert.assertNotNull(em.find(Person.class, p1.email));
        p2.person = new Person(p2.email);
        em.persist(p2.person);
        Assert.assertNotNull(em.find(Person.class, p2.email));
        p1.topic = new Topic(p1.cert.getId(), p1.topicLabel);
        Assert.assertNull(p1.topic.getId());
        em.persist(p1.topic);
        Assert.assertNotNull(p1.topic.getId());
        Assert.assertNotNull(em.find(Topic.class, p1.topic.getId()));        
        p1.event = new Event(p1.topic, StatusType.OK, Calendar.getInstance());
        em.persist(p1.event);
        p1.event = new Event(p1.topic, StatusType.CRITICAL, Calendar.getInstance());
        em.persist(p1.event);
        p1.alert = new Alert(p1.topic, StatusType.OK, Calendar.getInstance(), p1.person.getEmail());
        em.persist(p1.alert);
        p2.alert = new Alert(p1.topic, StatusType.OK, Calendar.getInstance(), p2.person.getEmail());
        em.persist(p2.alert);
        TestEntityInfo.assertSize("alert", 2, em.createQuery("select a from Alert a")
                .getResultList());
        em.getTransaction().commit();
        em.close();
    }
    
    
}
