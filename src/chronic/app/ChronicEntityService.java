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

import chronic.alert.TopicMessage;
import chronic.entity.Cert;
import chronic.entity.IssuedCert;
import chronic.entity.Org;
import chronic.entity.OrgRole;
import chronic.entity.Person;
import chronic.entity.Subscription;
import chronic.entity.Topic;
import chronic.entitykey.CertKey;
import chronic.entitykey.TopicKey;
import chronic.entitykey.OrgRoleKey;
import chronic.entitykey.PersonKey;
import chronic.entitytype.OrgRoleType;
import chronic.handler.app.CertInfo;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.security.Certificates;
import vellum.security.DnameType;
import vellum.security.X509Certificates;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class ChronicEntityService implements AutoCloseable {

    static Logger logger = LoggerFactory.getLogger(ChronicEntityService.class);

    ChronicApp app;
    EntityManagerFactory emf;
    EntityManager em;

    public ChronicEntityService(ChronicApp app) {
        this.app = app;
    }

    public ChronicEntityService(ChronicApp app, EntityManagerFactory emf) {
        this.app = app;
        this.emf = emf;
    }

    public void begin() {
        if (em != null && em.isOpen()) {
            em.close();
            throw new PersistenceException("entity manager is open");
        }
        if (emf == null) {
            emf = app.emf;
        }
        em = emf.createEntityManager();
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

    public Cert findCert(Long certId) {
        return em.find(Cert.class, certId);
    }

    public Topic findTopic(Long topicId) {
        return em.find(Topic.class, topicId);
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

    public Org findOrg(String orgDomain) throws StorageException {
        List<Org> list = selectOrg(orgDomain);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Person.class, orgDomain);
        }
        return list.get(0);
    }

    public Cert findCert(CertKey key) throws StorageException {
        List<Cert> list = selectCert(key);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, Cert.class, key);
        }
        return list.get(0);
    }

    public IssuedCert findIssuedCert(CertKey key, boolean enabled) throws StorageException {
        List<IssuedCert> list = selectIssuedCert(key, enabled);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, IssuedCert.class, key);
        }
        return list.get(0);
    }

    public void remove(Object entity) throws StorageException {
        em.remove(entity);
    }

    public OrgRole findOrgRole(OrgRoleKey key) throws StorageException {
        logger.info("findOrgRole {}", key);
        List<OrgRole> list = selectOrgRole(key);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, OrgRole.class, key);
        }
        return list.get(0);
    }

    public Topic find(TopicKey key) throws StorageException {
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
        return selectSubscription(topic, email).isEmpty();
    }

    public boolean isSubscription(Topic topic, Person person) throws StorageException {
        return selectSubscription(topic, person).isEmpty();
    }

    public List<Org> listOrg(String email) {
        return em.createQuery("select o from Org o inner join OrgRole r"
                + " where o.orgDomain = r.orgDomain"
                + " and r.email = :email").
                setParameter("email", email).
                getResultList();
    }

    public List<Org> listOrg(String email, boolean enabled) {
        return em.createQuery("select o from Org o inner join OrgRole r"
                + " where o.orgDomain = r.orgDomain"
                + " and r.email = :email"
                + " and o.enabled = :enabled"
                + " and r.enabled = :enabled").
                setParameter("email", email).
                setParameter("enabled", enabled).
                getResultList();
    }

    public List<Cert> listCerts(Org org) {
        return em.createQuery("select c from Cert c"
                + " where c.org = :org").
                setParameter("org", org).
                getResultList();
    }

    public List<Cert> listCerts(String email) {
        List<Cert> list = new ArrayList();
        for (Org org : listOrg(email)) {
            list.addAll(listCerts(org));
        }
        return list;
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
                + " where s.email = :email").
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

    public List<Subscription> listSubscriptions(Topic topic) {
        assert topic != null;
        return em.createQuery("select s from Subscription s"
                + " where s.topic = :topic"
                + " and s.enabled = true").
                setParameter("topic", topic).
                getResultList();
    }

    public List<String> listSubscriptionEmails(Topic topic) {
        return em.createQuery("select s.email from Subscription s"
                + " where s.topicId = :topicId"
                + " and s.enabled = true").
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
        return em.createQuery("select count(1) from OrgRole r"
                + " where r.orgDomain = :orgDomain"
                + " and r.email = :email").
                setParameter("orgDomain", roleKey.getOrgDomain()).
                setParameter("email", roleKey.getEmail()).
                getSingleResult().equals(1);
    }

    private List<Org> selectOrg(String orgDomain) {
        return em.createQuery("select o from Org o"
                + " where o.orgDomain = :orgDomain").
                setParameter("orgDomain", orgDomain).
                getResultList();
    }

    public List<IssuedCert> selectIssuedCert(CertKey certKey) {
        return em.createQuery("select c from IssuedCert c"
                + " where c.commonName = :commonName"
                + " and c.orgUnit = :orgUnit"
                + " and c.orgDomain = :orgDomain",
                IssuedCert.class).
                setParameter("commonName", certKey.getCommonName()).
                setParameter("orgUnit", certKey.getOrgUnit()).
                setParameter("orgDomain", certKey.getOrgDomain()).
                getResultList();
    }

    private List<IssuedCert> selectIssuedCert(CertKey certKey, boolean enabled) {
        return em.createQuery("select c from IssuedCert c"
                + " where c.commonName = :commonName"
                + " and c.orgUnit = :orgUnit"
                + " and c.orgDomain = :orgDomain"
                + " and c.enabled = :enabled",
                IssuedCert.class).
                setParameter("commonName", certKey.getCommonName()).
                setParameter("orgUnit", certKey.getOrgUnit()).
                setParameter("orgDomain", certKey.getOrgDomain()).
                setParameter("enabled", enabled).
                getResultList();
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

    private List<OrgRole> selectOrgRole(OrgRoleKey key) {
        return selectOrgRole(key.getOrgDomain(), key.getEmail(), key.getRoleType());
    }

    private List<OrgRole> selectOrgRole(String orgDomain, String email, OrgRoleType roleType) {
        logger.info("selectOrgRole {} {}", orgDomain, email);
        return em.createQuery("select r from OrgRole r"
                + " where r.orgDomain = :orgDomain"
                + " and r.email = :email"
                + " and r.roleType = :roleType").
                setParameter("orgDomain", orgDomain).
                setParameter("email", email).
                setParameter("roleType", roleType).
                getResultList();
    }

    private List<Subscription> selectSubscription(Topic topic, String email) {
        return em.createQuery("select s from Subscription s"
                + " where s.topic = :topic"
                + " and s.email = :email").
                setParameter("topic", topic).
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

    public Org persistOrg(String orgDomain) throws StorageException {
        Org org = em.find(Org.class, orgDomain);
        if (org == null) {
            org = new Org(orgDomain, app.getProperties().getAllocateServer());
            em.persist(org);
            logger.info("persist org {}", org);
        }
        return org;
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
        String commonName = Certificates.get(DnameType.CN, certificate.getSubjectDN());
        String orgDomain = Certificates.get(DnameType.O, certificate.getSubjectDN());
        String orgUnit = Certificates.get(DnameType.OU, certificate.getSubjectDN());
        if (!app.getProperties().isAllowedDomain(orgDomain)) {
            throw new CertificateException("org not allowed: " + orgDomain);
        } else if (!app.getProperties().isAllowedAddress(remoteHostAddress)) {
            throw new CertificateException("address not allowed: " + remoteHostAddress);
        }
        return persistCert(orgDomain, orgUnit, commonName, remoteHostAddress, encoded);
    }

    public Cert persistCert(CertInfo certInfo) throws StorageException, CertificateException {
        return persistCert(certInfo.getOrgDomain(), certInfo.getOrgUnit(), certInfo.getCommonName(),
                certInfo.getRemoteHostAddress(), certInfo.getEncoded());
    }

    public Cert persistCert(String orgDomain, String orgUnit, String commonName,
            String remoteHostAddress, String encoded)
            throws StorageException, CertificateException {
        boolean enabled = false;
        Org org = em.find(Org.class, orgDomain);
        if (org == null) {
            enabled = true;
            org = new Org(orgDomain, app.getProperties().getAllocateServer());
            em.persist(org);
            logger.info("persist org {}", org);
        }
        CertKey certKey = new CertKey(orgDomain, orgUnit, commonName);
        Cert cert = findCert(certKey);
        if (cert == null) {
            if (!org.isEnroll()) {
                throw new CertificateException("enroll disabled");
            }
            if (org.getEnrollCommonName() != null && !orgDomain.equals(org.getEnrollCommonName())) {
                throw new CertificateException("invalid enroll certificate common name");
            }
            cert = new Cert(certKey);
            cert.setEncoded(encoded);
            cert.setAcquired(Calendar.getInstance());
            cert.setAddress(remoteHostAddress);
            cert.setEnabled(enabled);
            em.persist(cert);
            logger.info("certificate {}", certKey);
        } else {
            assert cert.getEncoded() != null;
            if (cert.getRevoked() != null) {
                throw new CertificateException("certificate revoked");
            } else if (!cert.getEncoded().equals(encoded)) {
                if (cert.isEnabled()) {
                    throw new CertificateException("invalid duplicate certificate");
                }
                cert.setEncoded(encoded);
                cert.setAcquired(Calendar.getInstance());
                cert.setAddress(remoteHostAddress);
                logger.warn("updated certificate {}", certKey);
            } else if (!cert.isEnabled()) {
                logger.warn("duplicate cert {}", certKey);
                if (!cert.getAddress().equals(remoteHostAddress)) {
                    logger.warn("different host address {}", remoteHostAddress);
                }
            }
        }
        cert.setOrg(org);
        return cert;
    }

    public Person persistPerson(String email) throws StorageException {
        logger.info("persistPerson {}", email);
        Person person = em.find(Person.class, email);
        if (person == null) {
            person = new Person(email);
            em.persist(person);
        }
        return person;
    }

    public void persistCertSubscription(Cert cert, String email)
            throws StorageException {
        logger.info("persistCertSubscription {} {}", cert, email);
        Person person = em.find(Person.class, email);
        if (person == null) {
            person = new Person(email);
            em.persist(person);
        }
        for (Topic topic : listTopic(cert)) {
            persistTopicSubscription(topic, person);
        }

    }

    public OrgRole persistOrgRole(Org org, String email, OrgRoleType roleType)
            throws StorageException {
        Person person = persistPerson(email);
        logger.info("persistOrgRole {} {}", org, person);
        OrgRoleKey key = new OrgRoleKey(org.getOrgDomain(), email, roleType);
        OrgRole orgRole = findOrgRole(key);
        if (orgRole == null) {
            orgRole = new OrgRole(key);
            orgRole.setEnabled(true);
            orgRole.setOrg(org);
            orgRole.setPerson(person);
            em.persist(orgRole);
            logger.info("persistOrgRole {}", orgRole);
        }
        orgRole.setPerson(person);
        orgRole.setOrg(org);
        return orgRole;
    }

    public Topic persistTopic(Cert cert, TopicMessage message)
            throws StorageException {
        logger.info("persistTopic {} {}", cert, message);
        assert (cert.getId() != null);
        TopicKey key = new TopicKey(cert.getId(), message.getTopicLabel());
        Topic topic = find(key);
        if (topic == null) {
            topic = new Topic(key);
            em.persist(topic);
        }
        topic.setCert(cert);
        topic.setAlertType(message.getAlertType());
        if (message.getPeriodMillis() > 0) {
            topic.setPeriodSeconds(message.getPeriodMillis() / 1000);
        }
        if (message.getStatusPeriodMillis() > 0) {
            topic.setStatusPeriodSeconds(message.getStatusPeriodMillis() / 1000);
        } else {
            topic.setStatusPeriodSeconds(app.getProperties().getStatusPeriod() / 1000);
        }
        return topic;
    }

    public Subscription persistTopicSubscription(Topic topic, Person person)
            throws StorageException {
        Subscription subscription = findSubscription(topic, person);
        if (subscription == null) {
            subscription = new Subscription(topic, person);
            em.persist(subscription);
        }
        return subscription;
    }

}
