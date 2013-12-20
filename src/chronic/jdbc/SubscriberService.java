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

import chronic.entity.Subscriber;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.TopicIdKey;
import chronic.entitykey.UserKey;
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
import vellum.storage.EntityService;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class SubscriberService implements EntityService<Subscriber> {

    static Logger logger = LoggerFactory.getLogger(SubscriberService.class);
    static QueryMap queryMap = new QueryMap(SubscriberService.class);
    DataSource dataSource;

    public SubscriberService(DataSource dataSource) {
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

    private Subscriber create(ResultSet resultSet) throws SQLException {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(resultSet.getLong("topic_sub_id"));
        subscriber.setTopicId(resultSet.getLong("topic_id"));
        subscriber.setEmail(resultSet.getString("email"));
        subscriber.setEnabled(resultSet.getBoolean("enabled"));
        return subscriber;
    }

    private Collection<Subscriber> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void add(Subscriber subscriber) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "insert")) {
            statement.setLong(1, subscriber.getTopicId());
            statement.setString(2, subscriber.getEmail());
            statement.setBoolean(3, subscriber.isEnabled());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            subscriber.setId(generatedKeys.getLong(1));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, subscriber.getKey());
        }
    }

    @Override
    public void replace(Subscriber subscriber) throws StorageException {
        updateEnabled(subscriber);
    }

    public void updateEnabled(Subscriber subscriber) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "update enabled")) {
            statement.setBoolean(1, subscriber.isEnabled());
            statement.setLong(2, subscriber.getId());
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, subscriber.getKey());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, subscriber.getKey());
        }
    }

    @Override
    public void remove(Comparable key) throws StorageException {
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
    public Subscriber find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return selectId((Long) key);
        } else if (key instanceof SubscriberKey) {
            return selectKey((SubscriberKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Subscriber selectKey(SubscriberKey key) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "select key")) {
            statement.setLong(1, key.getTopicId());
            statement.setString(2, key.getEmail());
            try (ResultSet resultSet = statement.getResultSet()) {
                if (!resultSet.next()) {
                    return null;
                }
                Subscriber subscriber = create(resultSet);
                if (resultSet.next()) {
                    throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
                }
                return subscriber;
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }
    private Subscriber selectId(Long id) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "select id", id);
                ResultSet resultSet = statement.getResultSet()) {
            if (!resultSet.next()) {
                return null;
            }
            Subscriber subscriber = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return subscriber;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, id);
        }
    }

    @Override
    public boolean contains(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public Subscriber retrieve(Comparable key) throws StorageException {
        Subscriber subscriber = find(key);
        if (subscriber == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return subscriber;
    }

    @Override
    public Collection<Subscriber> list() throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Subscriber> list(Comparable key) throws StorageException {
        if (key instanceof String) {
            return listUser((String) key);
        } else if (key instanceof Long) {
            return listTopic((Long) key);
        } else if (key instanceof UserKey) {
            return listUser(((UserKey) key).getEmail());
        } else if (key instanceof TopicIdKey) {
            return listTopic(((TopicIdKey) key).getTopicId());
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Collection<Subscriber> listTopic(Long topicId) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "list topic", topicId);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }
    
    private Collection<Subscriber> listUser(String email) throws StorageException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = prepare(connection, "list email", email);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

}