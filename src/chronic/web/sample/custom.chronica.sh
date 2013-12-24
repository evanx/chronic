# Source https://github.com/evanx by @evanxsummers
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file to
# you under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the
# License. You may obtain a copy of the License at:
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

commonName="" # name of this publisher
orgUnit="" # network or group
orgDomain="" # internet domain of your organisation 
admins="" # emails of chronica admins for this organisation
subscribers="" # emails of default subcribers


### customizable jobs

c0minutely() {
  c1topic minutely
  c0load
  c0login
  #c2tcp chronica.co 443
  #c2nossl chronica.co 80
  #c2nohttps chronica.co 80
  #c2notcp chronica 21
  #c2notcp chronica 25
  #c2https chronica.co 443 
  #c2https chronica.co 8444
  #c2postgres localhost 5432
}

c0hourly() {
  c1topic hourly
  c0diskspace
  #c0shaAuth
  #c0megaRaid
  #c0mdstat
}

c0daily() {
  c1topic daily
  #c0yumVerify
  #c0rpmVerify
  #c2certExpiry chronica.co 443
}
