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

echo "see https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronical.sh"
echo "see https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/custom.chronical.sh"

if [ $# -gt 0 ]
then 
  if [ -d $1 ]
  then 
    cd $1
    rm -f chronical.sh 
    wget https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronical.sh
  fi
fi



