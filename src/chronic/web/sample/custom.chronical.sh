# Source https://github.com/evanx by @evanxsummers
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.  

### custom config 

subscribers=""
admins=""
orgUrl=""
orgUnit=""
scheduledMinute=""
scheduledHour=""


### default settings

loadWarningThreshold=4
loadCriticalThreshold=9

diskWarningThreshold=80
diskCriticalThreshold=90

packetLossWarningThreshold=20
packetLossCriticalThreshold=60

server=chronical.info:8444

periodSeconds=60

pingCount=1
sslTimeout=2
tcpTimeout=2
httpTimeout=2
databaseTimeout=2


### customizable jobs

c0minutely() {
  c1topic minutely
  c0load
  #c2tcp chronical.info 443
  #c2nossl chronical.info 80
  #c2nohttps chronical.info 80
  #c2notcp chronical 21
  #c2notcp chronical 25
  #c2https chronical.info 443 
  #c2https chronical.info 8444
  #c2postgres localhost 5432
}

c0hourly() {
  c1topic hourly
  c0diskspace
  #c0mdstat
  #c0sshAuthKeys
  #c0megaRaid
}

c0daily() {
  c1topic daily
  c0login
}
