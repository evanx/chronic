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
package chronic.alert;

import chronic.entitykey.TopicStatusKey;
import chronic.entitykey.TopicStatusKeyed;
import chronic.type.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class TopicStatus implements TopicStatusKeyed {

    static Logger logger = LoggerFactory.getLogger(TopicStatus.class);
    long topicId;
    StatusType statusType;
    long timestamp = System.currentTimeMillis();
        
    public TopicStatus(long topicId, StatusType statusType) {
        this.topicId = topicId;
        this.statusType = statusType;
    }

    public long getTopicId() {
        return topicId;
    }
    
    public StatusType getStatusType() {
        return statusType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public TopicStatusKey getTopicStatusKey() {
        return new TopicStatusKey(topicId, statusType);
    }
    
}
