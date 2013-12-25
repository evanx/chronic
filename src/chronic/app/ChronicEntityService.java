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
package chronic.app;

import chronic.entity.Cert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Person;
import chronic.entity.Subscription;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.PersonKey;
import chronic.entitytype.OrgRoleType;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.persistence.EntityManager;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;
import vellum.security.X509Certificates;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class ChronicEntityService {

    static Logger logger = LoggerFactory.getLogger(ChronicEntityService.class);

    ChronicApp app;
    EntityManager em;

    public ChronicEntityService(ChronicApp app) {
        this.app = app;
    }

    public void begin(EntityManager em) {
        this.em = em;
        em.getTransaction().begin();
    }

    public void commit() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    public void rollback() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    public void close() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public void persist(Object entity) {
        em.persist(entity);
    }

    public Person findPerson(String key) throws StorageException {
        List<Person> list = list(new PersonKey(key));
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Person.class, key);
        }
        return list.get(0);
    }

    public Cert find(CertKey key) throws StorageException {
        List<Cert> list = selectCert(key);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Cert.class, key);
        }
        return list.get(0);
    }

    public OrgRole find(OrgRoleKey key) throws StorageException {
        List<OrgRole> list = selectOrgRole(key.getOrgDomain(), key.getEmail());
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, OrgRole.class, key);
        }
        return list.get(0);
    }

    public Topic find(CertTopicKey key) throws StorageException {
        List<Topic> list = selectTopic(key.getCertId(), key.getTopicLabel());
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Topic.class, key);
        }
        return list.get(0);
    }

    public Subscription findSubscription(long topicId, String email) throws StorageException {
        Topic topic = em.find(Topic.class, topicId);
        Person person = em.find(Person.class, email);
        return findSubscription(topic, person);
    }
    
    public Subscription findSubscription(Topic topic, Person person) throws StorageException {
        List<Subscription> list = selectSubscription(topic, person);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Subscription.class, 
                    topic.getTopicLabel(), person.getEmail());
        }
        return list.get(0);
    }

    public boolean isSubscription(Topic topic, String email) throws StorageException {
        Person person = em.find(Person.class, email);
        return isSubscription(topic, person);
    }
    
    public boolean isSubscription(Topic topic, Person person) throws StorageException {
        return selectSubscription(topic, person).isEmpty();
    }

    public List<Org> listOrg(String email) {
        return em.createQuery("select o from Org o join OrgRole r"
                + " where r.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public List<Cert> listCerts(String email) {
        return em.createQuery("select c from Cert c join Org o join OrgRole r"
                + " where r.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public List<OrgRole> listRoles(String email) {
        return em.createQuery("select r from OrgRole r"
                + " where r.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public List<Topic> listTopic(Cert cert) {
        return em.createQuery("select t from Topic t"
                + " where t.certId = :certId").
                setParameter("certId", cert.getId()).
                getResultList();
    }

    public List<Subscription> listSubscription(long certId, String topicLabel) {
        return em.createQuery("select s from Topic t join Subscription s"
                + " where t.certId = :certId"
                + " and t.topicLabel = :topicLabel").
                setParameter("certId", certId).
                setParameter("topicLabel", topicLabel).
                getResultList();
    }

    public List<Topic> listTopic(String email) {
        return em.createQuery("select t from Topic t join Subscription s"
                + " where s.email = :email"
        ).
                setParameter("email", email).
                getResultList();
    }

    public List<Subscription> listSubscription(String email) {
        return em.createQuery("select s from Subscription s"
                + " where s.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public List<Subscription> listSubcriber() {
        return em.createQuery("select s from Subscription s").
                getResultList();
    }

    public List<String> listSubscriptionEmails(Topic topic) {
        return em.createQuery("select s.email from Subscription s"
                + " where s.topicId = :topicId").
                setParameter("topicId", topic.getId()).
                getResultList();
    }

    public boolean isAdmin(String orgDomain, String email) {
        return em.createQuery("select r.email from Org o join OrgRole r"
                + " where o.orgDomain = :orgDomain").
                setParameter("orgDomain", orgDomain).
                getResultList().contains(email);
    }

    public boolean isRole(OrgRoleKey roleKey) {
        return em.createQuery("select count(1) from r"
                + " where r.orgDomain = :orgDomain"
                + " and r.email = :email").
                setParameter("orgDomain", roleKey.getOrgDomain()).
                setParameter("email", roleKey.getEmail()).
                getSingleResult().equals(1);
    }

    private List<Cert> selectCert(CertKey certKey) {
        return em.createQuery("select c from Cert c"
                + " where c.commonName = :commonName"
                + " and c.orgUnit = :orgUnit"
                + " and c.orgDomain = :orgDomain", Cert.class).
                setParameter("commonName", certKey.getCommonName()).
                setParameter("orgUnit", certKey.getOrgUnit()).
                setParameter("orgDomain", certKey.getOrgDomain()).
                getResultList();
    }

    private List<Topic> selectTopic(long certId, String topicLabel) {
        return em.createQuery("select t from Topic t"
                + " where t.certId = :certId"
                + " and t.topicLabel = :topicLabel").
                setParameter("certId", certId).
                setParameter("topicLabel", topicLabel).
                getResultList();
    }

    private List<OrgRole> selectOrgRole(String orgDomain, String email) {
        return em.createQuery("select r from OrgRole r"
                + " where r.orgDomain = :orgDomain"
                + " and r.email = :email").
                setParameter("orgDomain", orgDomain).
                setParameter("email", email).
                getResultList();
    }

    private List<Subscription> selectSubscription(long topicId, String email) {
        return em.createQuery("select s from Subscription s"
                + " where s.topicId = :topicId"
                + " and s.email = :email").
                setParameter("topicId", topicId).
                setParameter("email", email).
                getResultList();
    }
        
    private List<Subscription> selectSubscription(Topic topic, Person person) {
        return em.createQuery("select s from Subscription s"
                + " where s.topic = :topic"
                + " and s.person = :person").
                setParameter("topic", topic).
                setParameter("person", person).
                getResultList();
    }

    private List<Person> list(PersonKey key) {
        return em.createQuery("select p from Person p"
                + " where p.email = :email").
                setParameter("email", key.getEmail()).
                getResultList();
    }

    public Cert persistCert(Httpx httpx) throws StorageException, CertificateException,
            SSLPeerUnverifiedException {
        X509Certificate certificate = httpx.getPeerCertficate();
        String remoteHostAddress = httpx.getRemoteHostAddress();
        return persistCert(certificate, remoteHostAddress);
    }

    public Cert persistCert(X509Certificate certificate, String remoteHostAddress)
            throws StorageException, CertificateException,
            SSLPeerUnverifiedException {
        String encoded = X509Certificates.getEncodedPublicKey(certificate);
        String commonName = Certificates.getCommonName(certificate.getSubjectDN());
        String orgDomain = Certificates.getOrg(certificate.getSubjectDN());
        String orgUnit = Certificates.getOrgUnit(certificate.getSubjectDN());
        if (!app.getProperties().getAllowedOrgDomains().contains(orgDomain)) {
            throw new CertificateException("org not allowed: " + orgDomain);
        } else if (!app.getProperties().getAllowedAddresses().contains(remoteHostAddress)) {
            logger.info("remote hostAddress {}", remoteHostAddress);
        }
        boolean enabled = false;
        Org org = em.find(Org.class, orgDomain);
        if (org == null) {
            enabled = true;
            org = new Org(orgDomain);
            em.persist(org);
            logger.info("persist org {}", org);
        }
        CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);
        Cert cert = find(certKey);
        if (cert == null) {
            cert = new Cert(certKey);
            cert.setEnabled(enabled);
            cert.setEncoded(encoded);
            cert.setAddress(remoteHostAddress);
            cert.setAcquired(Calendar.getInstance());
            cert.setAddress(remoteHostAddress);
            em.persist(cert);
            logger.info("certificate {}", certKey);
        } else if (!cert.getEncoded().equals(encoded)) {
            logger.warn("invalid public key {}", certKey);
        } else if (!cert.isEnabled()) {
            logger.warn("cert disabled {}", certKey);
        } else if (!cert.getAddress().equals(remoteHostAddress)) {
            logger.warn("host address {}", remoteHostAddress);
        }
        cert.setOrg(org);
        return cert;
    }

    public Person persistPerson(String email) throws StorageException {
        logger.info("handle {}", email);
        Person person = em.find(Person.class, email);
        if (person == null) {
            person = new Person(email);
            em.persist(person);
        }
        return person;
    }

    public void persistCertSubscription(Cert cert, String email)
            throws StorageException {
        logger.info("handle {} {}", cert, email);
        Person person = em.find(Person.class, email);
        if (person == null) {
            person = new Person(email);
            em.persist(person);
        }
        for (Topic topic : listTopic(cert)) {
            persistTopicSubscription(topic, email);
        }

    }

    public OrgRole persistOrgRole(Cert cert, String email, OrgRoleType roleType)
            throws StorageException {
        Person person = persistPerson(email);
        logger.info("persistOrgRole {} {}", cert, person);
        OrgRoleKey orgRoleKey = new OrgRoleKey(cert.getOrgDomain(), email, roleType);
        OrgRole orgRole = find(orgRoleKey);
        if (orgRole == null) {
            orgRole = new OrgRole(orgRoleKey);
            orgRole.setEnabled(true);
            em.persist(orgRole);
        }
        return orgRole;
    }

    public Topic persistTopic(Cert cert, String topicLabel)
            throws StorageException {
        logger.info("handle {} {}", topicLabel, cert);
        assert (cert.getId() != null);
        CertTopicKey key = new CertTopicKey(cert.getId(), topicLabel);
        Topic topic = find(key);
        if (topic == null) {
            topic = new Topic(key);
            em.persist(topic);
        }
        topic.setCert(cert);
        return topic;
    }

    public Subscription persistTopicSubscription(Topic topic, String email)
            throws StorageException {
        Person person = em.find(Person.class, email);
        Subscription subscription = findSubscription(topic, person);
        if (subscription == null) {
            subscription = new Subscription(topic, person);
            em.persist(subscription);
        }
        return subscription;
    }
}
