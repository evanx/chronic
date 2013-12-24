/*
 Source https://code.google.com/p/vellum by @evanxsummers

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

import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityService;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;
import vellum.util.Calendars;

/**
 *
 * @author evan.summers
 */
public class CertService implements EntityService<Cert> {

    static Logger logger = LoggerFactory.getLogger(CertService.class);
    static QueryMap queryMap = new QueryMap(CertService.class);
    Connection connection;
    
    public CertService(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement prepare(String queryKey,
            Object... parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(queryMap.get(queryKey));
        int index = 0;
        for (Object parameter : parameters) {
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Cert create(ResultSet resultSet) throws SQLException {
        Cert cert = new Cert();
        cert.setId(resultSet.getLong("cert_id"));
        cert.setOrgDomain(resultSet.getString("org_domain"));
        cert.setOrgUnit(resultSet.getString("org_unit"));
        cert.setCommonName(resultSet.getString("common_name"));
        cert.setAddress(resultSet.getString("address"));
        cert.setAcquired(Calendars.newCalendar(resultSet.getTimestamp("acquired").getTime()));
        cert.setEncoded(resultSet.getString("encoded"));
        cert.setEnabled(resultSet.getBoolean("enabled"));
        return cert;
    }

    private Collection<Cert> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(Cert cert) throws StorageException {
        try (PreparedStatement statement = prepare("insert")) {
            statement.setString(1, cert.getOrgDomain());
            statement.setString(2, cert.getOrgUnit());
            statement.setString(3, cert.getCommonName());
            statement.setString(4, cert.getEncoded());
            statement.setString(5, cert.getAddress());
            statement.setBoolean(6, cert.isEnabled());
            ResultSet generatedKeys = statement.executeQuery();
            if (!generatedKeys.next()) {
                throw new StorageException(StorageExceptionType.NOT_PERSISTED);
            }
            cert.setId(generatedKeys.getLong(1));
            assert(cert.getId() != null);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }
    }

    @Override
    public void update(Cert cert) throws StorageException {
        assert cert.getId() != null;
        updateEnabled(cert);
    }

    public void updateEnabled(Cert cert) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled")) {
            statement.setBoolean(1, cert.isEnabled());
            statement.setLong(2, cert.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, cert);
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }
    }
    public void updateEncoded(Cert cert) throws StorageException {
        try (PreparedStatement statement = prepare("update encoded")) {
            statement.setBoolean(1, cert.isEnabled());
            statement.setString(2, cert.getEncoded());
            statement.setLong(3, cert.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, cert.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }
    }

    @Override
    public void remove(Comparable key) throws StorageException {
        try (PreparedStatement statement = prepare("delete", key)) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_DELETED, key);
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    @Override
    public Cert find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        } else if (key instanceof CertKey) {
            return findKey((CertKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Cert findKey(CertKey key) throws StorageException {
        try (PreparedStatement statement = prepare("select key")) {
            statement.setString(1, key.getOrgDomain());
            statement.setString(2, key.getOrgUnit());
            statement.setString(3, key.getCommonName());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                Cert cert = create(resultSet);
                if (resultSet.next()) {
                    throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
                }
                return cert;
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    private Cert findId(Long id) throws StorageException {
        try (PreparedStatement statement = prepare("select id", id);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Cert cert = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return cert;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, id);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public Cert retrieve(Comparable key) throws StorageException {
        Cert cert = find(key);
        if (cert == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return cert;
    }

    @Override
    public Collection<Cert> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Cert> list(Comparable key) throws StorageException {
        if (key instanceof OrgKey) {
            return listOrg((OrgKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Collection<Cert> listOrg(OrgKey key) throws StorageException {
        try (PreparedStatement statement = prepare("list org", key.getOrgDomain());
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

}
