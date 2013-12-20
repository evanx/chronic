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
package enbased.app;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import vellum.data.Millis;
import vellum.jx.JMap;
import vellum.jx.JMapped;
import vellum.jx.JMaps;
import vellum.storage.AbstractIdEntity;

/**
 *
 * @author evan.summers
 */
@Entity
public class EnbaseRequest extends AbstractIdEntity implements Serializable, JMapped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @Column(name = "app")
    Long appId;

    @Column(name = "act")
    EnbaseActionType actionType; 
    
    @Column(name = "typ")
    String entityType;
    
    @Column(name = "eid")
    Long entityId;

    @Column(name = "dat")
    Serializable data;
    
    @Column(name = "prop")
    String property;
    
    @Column(name = "val")
    Serializable value;

    @Column(name = "tim")
    long timestamp;
    
    public EnbaseRequest() {
    }

    public EnbaseRequest(Long appId, String entityType, EnbaseActionType actionType) {
        this.appId = appId;
        this.actionType = actionType;
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }
    
    @Override
    public Comparable getKey() {
        return getId();
    }

    @Override
    public String toString() {
        return getMap().toString();
    }

    @Override
    public JMap getMap() {
        return new JMap(
                JMaps.entryValue("entityType", entityType),
                JMaps.entryValue("entityId", entityId),
                JMaps.entryValue("actionType", actionType),
                JMaps.entryValue("data", data),
                JMaps.entryValue("property", property),
                JMaps.entryValue("value", value),
                JMaps.entryValue("timestamp", timestamp),
                JMaps.entryValue("timestampLabel", Millis.formatTime(timestamp))
        );
    }
    
}
