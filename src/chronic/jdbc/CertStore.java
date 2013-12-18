/*
 Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic.jdbc;

import chronic.entity.Cert;
import chronic.entitykey.CertKey;
import chronic.entitykey.OrgKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityStore;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class CertStore implements EntityStore<Cert> {

    static Logger logger = LoggerFactory.getLogger(CertStore.class);
    static QueryMap queryMap = new QueryMap(CertStore.class);
    DataSource dataSource;

    public CertStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private PreparedStatement prepare(Connection connection, String queryKey,
            Object... parameters) throws SQLException {
        PreparedStatement statement = prepare(connection, queryKey);
        int index = 0;
        for (Object parameter : parameters) {
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Cert create(ResultSet resultSet) throws SQLException {
        Cert cert = new Cert();
        cert.setId(resultSet.getLong("id"));
        cert.setOrgDomain(resultSet.getString("org_domain"));
        cert.setOrgUnit(resultSet.getString("org_unit"));
        cert.setCommonName(resultSet.getString("cn"));
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
    public void insert(Cert cert) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "insert")) {
            statement.setString(1, cert.getOrgDomain());
            statement.setString(2, cert.getOrgUnit());
            statement.setString(3, cert.getCommonName());
            statement.setString(4, cert.getEncoded());
            int insertCount = statement.executeUpdate();
            if (insertCount != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            cert.setId(generatedKeys.getLong((1)));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }
    }

    @Override
    public void update(Cert cert) throws StorageException {
        updateEncoded(cert);
    }

    public void updateEncoded(Cert cert) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "update encoded")) {
            statement.setLong(1, cert.getId());
            statement.setString(2, cert.getEncoded());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, cert.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, cert.getKey());
        }
    }

    @Override
    public void delete(Comparable key) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "delete")) {
            statement.setLong(1, (Long) key);
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_DELETED, key);
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    @Override
    public Cert select(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return selectId((Long) key);
        } else if (key instanceof CertKey) {
            return selectKey((CertKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Cert selectKey(CertKey key) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "select key")) {
            statement.setString(1, key.getOrgDomain());
            statement.setString(2, key.getOrgUnit());
            statement.setString(3, key.getCommonName());
            try (ResultSet resultSet = statement.getResultSet()) {
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

    private Cert selectId(Long id) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "select id", id);
                ResultSet resultSet = statement.getResultSet()) {
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
    public boolean containsKey(Comparable key) throws StorageException {
        return select(key) != null;
    }

    @Override
    public Cert find(Comparable key) throws StorageException {
        Cert cert = select(key);
        if (cert == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return cert;
    }

    @Override
    public Collection<Cert> list() throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Cert> list(Comparable key) throws StorageException {
        if (key instanceof OrgKey) {
            return listOrgKey((OrgKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Collection<Cert> listOrgKey(OrgKey key) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "list org", key.getOrgDomain());
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

}
