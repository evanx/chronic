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

import chronic.entity.Topic;
import chronic.entitykey.CertIdKey;
import chronic.entitykey.CertTopicKey;
import chronic.entitykey.UserKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityService;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class TopicService implements EntityService<Topic> {

    static Logger logger = LoggerFactory.getLogger(TopicService.class);
    static QueryMap queryMap = new QueryMap(TopicService.class);
    Connection connection;

    public TopicService(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement prepare(String queryKey,
            Object... parameters) throws SQLException {
        logger.trace("prepare {} {}", queryMap.get(queryKey), parameters);
        PreparedStatement statement = connection.prepareStatement(queryMap.get(queryKey));
        int index = 0;
        for (Object parameter : parameters) {
            assert(parameter != null);
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Topic create(ResultSet resultSet) throws SQLException {
        Topic topic = new Topic();
        topic.setId(resultSet.getLong("topic_id"));
        topic.setCertId(resultSet.getLong("cert_id"));
        topic.setTopicLabel(resultSet.getString("topic_label"));
        topic.setEnabled(resultSet.getBoolean("enabled"));
        return topic;
    }

    private Collection<Topic> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(Topic topic) throws StorageException {
        try (PreparedStatement statement = prepare("insert")) {
            statement.setLong(1, topic.getCertId());
            statement.setString(2, topic.getTopicLabel());
            statement.setBoolean(3, topic.isEnabled());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            topic.setId(generatedKeys.getLong(1));
            assert(topic.getId() != null);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, topic.getKey());
        }
    }

    @Override
    public void update(Topic topic) throws StorageException {
        updateEnabled(topic);
    }

    public void updateEnabled(Topic topic) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled", 
                topic.isEnabled(), topic.getId())) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, topic.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, topic.getKey());
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
    public Topic find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        } else if (key instanceof CertTopicKey) {
            return findKey((CertTopicKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }

    private Topic findId(Long id) throws StorageException {
        try (PreparedStatement statement = prepare("select id", id);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Topic topic = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return topic;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, id);
        }
    }

    private Topic findKey(CertTopicKey key) throws StorageException {
        logger.info("findKey {}", key);
        try (PreparedStatement statement = prepare("select key",
                key.getCertId(), key.getTopicLabel());
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Topic topic = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
            }
            return topic;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public Topic retrieve(Comparable key) throws StorageException {
        Topic topic = find(key);
        if (topic == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return topic;
    }

    @Override
    public Collection<Topic> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Topic> list(Comparable key) throws StorageException {
        if (key instanceof String) {
            return listUser((String) key);
        } else if (key instanceof Long) {
            return listCert((Long) key);
        } else if (key instanceof UserKey) {
            return listUser(((UserKey) key).getEmail());
        } else if (key instanceof CertIdKey) {
            return listCert(((CertIdKey) key).getId());
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Collection<Topic> listCert(Long certId) throws StorageException {
        try (PreparedStatement statement = prepare("list cert", certId);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }
    
    private Collection<Topic> listUser(String email) throws StorageException {
        try (PreparedStatement statement = prepare("list email", email);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

}
