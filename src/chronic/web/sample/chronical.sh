#!/bin/bash

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

### default settings

loadWarningThreshold=4
loadCriticalThreshold=9

diskWarningThreshold=80
diskCriticalThreshold=90

packetLossWarningThreshold=20
packetLossCriticalThreshold=60

server=secure.chronical.info:443

periodSeconds=60

pingCount=1
tcpTimeout=2
sslTimeout=3
httpTimeout=4
databaseTimeout=2

### init 

startTimestamp=`date '+%s'`

set -u 

cd `dirname $0`
custom=`pwd`/custom.chronical.sh

dir=~/.chronic
mkdir -p $dir
cd $dir

pwd | grep '/.chronic$' || exit 1


### debug 

debug=3 # 0 no debugging 1 log to stdout, 2 to sterr, 3 debug file only

rm -f debug

decho() {
  if [ $debug -eq 1 ]
  then
    echo "chronical: $*" 
  fi
  if [ $debug -eq 2 ]
  then
    echo "chronical: $*" >&2
  fi
  if [ $debug -ge 1 ]
  then
    echo "chronical: $*" >> debug
  fi
}

decho custom $custom

dcat() {
  if [ $debug -eq 1 ]
  then
    echo "chronical:" 
    cat "$1"
  fi
  if [ $debug -eq 2 ]
  then
    echo "chronical:" >&2
    cat "$1" >&2
  fi
  if [ $debug -ge 1 ]
  then
    echo "chronical:" >> debug
    cat "$1" >> debug
  fi
}


### pid 

previousPid=''
if [ -f pid ] 
then
  previousPid=`head -1 pid`
  decho "previous pid: $previousPid" 
fi

echo $$ > pid

### util 

bcat() {
  if [ -f $1 ]
  then
    if [ `cat $1 | wc -l` -gt 0 ]
    then
      echo "(`head -1 $1 | sed 's/\s*$//'`)"
    fi
  fi
}


### init custom

if [ ! -f $custom ]
then
  echo "No $custom"
  exit 1
fi

if ! grep -q '^[a-zA-Z]*=' $custom
then
  echo "Invalid $custom"
  exit 1
fi

if grep '^[a-zA-Z]*=[\s"]*$' $custom
then
  echo "Please edit $custom to configure the above settings"
  exit 1
fi

. $custom


### reviewable setup

commonName=`hostname`

c1topic() {
  echo "Topic: $commonName $1"
  echo "Subscribe: $subscribers"
}


### check util functions 

c1ping() {
  decho "ping -qc$pingCount $1"
  packetLoss=`ping -qc2 $1 | grep 'packet loss' | sed 's/.* \([0-9]*\)% packet loss.*/\1/'`
  if [ $packetLoss -lt $packetLossWarningThreshold ]
  then
    echo "OK - $1 pingable ($packetLoss% packet loss)"
  elif [ $packetLoss -lt $packetLossCriticalThreshold ]
  then
    echo "WARNING - $1 $packetLoss% packet loss"
  else
    echo "CRITICAL - $1 $packetLoss% packet loss"
  fi
}

c1noping() {
  decho "ping -qc$pingCount $1"
  packetLoss=`ping -qc2 $1 | grep 'packet loss' | sed 's/.* \([0-9]*\)% packet loss.*/\1/'`
  if [ $packetLoss -lt 100 ]
  then
    echo "CRITICAL - $1 $packetLoss% packet loss"
  else
    echo "OK - $1 $packetLoss% packet loss"
  fi
}

c2tcp() {
  decho "nc -w$tcpTimeout $1 $2"
  if nc -w$tcpTimeout $1 $2
  then
    echo "OK - $1 port $2 open"
  else
    echo "CRITICAL - $1 port $2 closed"
  fi
}

c2notcp() {
  decho "nc -w$tcpTimeout $1 $2"
  if nc -w$tcpTimeout $1 $2
  then
    echo "CRITICAL - $1 port $2 open"
  else
    echo "OK - $1 port $2 closed"
  fi
}

c2ssl() {
  decho "timeout $sslTimeout openssl s_client -connect $1:$2"
  if timeout $sslTimeout openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "OK - $1 port $2 has SSL"
  else
    echo "CRITICAL - $1 port $2 no SSL"
  fi
}

c2nossl() {
  decho "timeout $sslTimeout openssl s_client -connect $1:$2"
  if timeout $sslTimeout openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "CRITICAL - $1 port $2 has SSL"
  else
    echo "OK - $1 port $2 no SSL"
  fi
}

c2https() {
  decho "curl --connect-timeout $httpTimeout -k -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "OK - $1 port $2 https available" `bcat https`
  else 
    echo "CRITICAL - $1 port $2 https unavailable" `bcat https`
  fi
}

c2nohttps() {
  decho "curl --connect-timeout $httpTimeout -k -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "CRITICAL - $1 port $2 https available" `bcat https`
  else 
    echo "OK - $1 port $2 https unavailable" `bcat https`
  fi
}

c2http() {
  decho "curl --connect-timeout $httpTimeout -k -s -I http://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I http://$1:$2 | grep '^HTTP' | tee http | grep -q OK 
  then
    echo "OK - $1 port $2 http available" `bcat http`
  else 
    echo "CRITICAL - $1 port $2 http unavailable" `bcat http`
  fi
}

c2nohttp() {
  decho "curl --connect-timeout $httpTimeout -k -s -I http://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I http://$1:$2 | grep '^HTTP' | tee http | grep -q OK 
  then
    echo "CRITICAL - $1 (port $2) http available" `bcat http`
  else 
    echo "OK - $1 (port $2) http unavailable" `bcat http`
  fi
}

c2postgres() {
  if timeout $databaseTimeout psql -h $1 -p $2 -c 'select 1' 2>&1 | grep -q '^psql: FATAL:  role\| 1 \|^$' 
  then
    echo "OK - $1 (port $2) postgres server"
  else
    echo "CRITICAL - $1 (port $2) postgres server not running"
  fi
}


### typical checks

c0login() {
  echo "<br><b>login</b>"
  last | head | grep '^[a-z]' | sed 's/\(^reboot .*\) - *[0-9].*/\1/'
}

c0load() {
  loadStatus=OK
  load=`cat /proc/loadavg | cut -d. -f1`
  [ $load -gt $loadWarningThreshold ] && loadStatus=WARNING
  [ $load -gt $loadCriticalThreshold ] && loadStatus=CRITICAL
  echo "Load $loadStatus - `cat /proc/loadavg`"
}

c0diskspace() {
  diskStatus=OK
  diskUsage=`df -h | grep '[0-9]%' | sed 's/.* \([0-9]*\)% .*/\1/' | sort -nr | head -1`
  [ $diskUsage -gt $diskWarningThreshold ] && diskStatus=WARNING
  [ $diskUsage -gt $diskCriticalThreshold ] && diskStatus=CRITICAL
  echo "Diskspace $diskStatus - $diskUsage%"
}

c0mdstat() {
  echo "<br><b>mdstat</b>"
  cat /proc/mdstat 2>/dev/null | grep ^md -A1 | sed 's/.*\[\([U_]*\)\]/\1/' | sed '/^\s*$/d' | grep 'U\|^md'
}

c0sshAuthKeys() {
  echo "<br><b>sshAuthKeys</b>"
  md5sum `locate authorized_keys | /etc/ssh/sshd_config`
}


### standard functionality

c1curl() {
  tee curl.txt | curl -k --cacert server.pem --key key.pem --cert ./cert.pem \
    --data-binary @- -H 'Content-Type: text/plain' https://$server/$1 >curl.out 2> curl.err
}

c0enroll() {
  echo "$subscribers" | c1curl enroll 
}

c0ensureKey() {
  if [ ! -f key.pem ] 
  then
    rm -f cert.pem
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout key.pem -out cert.pem \
      -subj "/CN=$commonName/O=$orgUrl/OU=$orgUnit"
    openssl x509 -text -in cert.pem | grep CN
  fi
}

c0ensureCert() {
  if [ ! -f server.pem ]
  then
    if echo | openssl s_client -connect $server 2>/dev/null | grep -q 'BEGIN CERT'
    then
      openssl s_client -connect $server 2>/dev/null | 
        sed -n -e '/BEGIN CERT/,/END CERT/p' > server.pem
      openssl x509 -text -in server.pem | grep 'CN='
      ls -l server.pem
    fi
  fi
}

c0ensureKey
c0ensureCert

c0reset() {
  rm -f cert.pem key.pem server.pem
  c0ensureKey
  c0ensureCert
}

c0post() {
  c1curl post
}

c0hourlyPost() {
  c0hourly 2>&1 | tee hourly | c0post
  dcat hourly
}

c0minutelyPost() {
  c0minutely 2>&1 | tee minutely | c0post
  dcat minutely
}

c0dailyPost() {
  c0daily 2>&1 | tee daily | c0post
  dcat daily
}

c0hourlyCron() {
  c0hourlyPost
  if [ `date +%H` -eq $scheduledHour ] 
  then
    c0dailyPost
  fi
}

c0minutelyCron() {
  c0minutelyPost
  c0stopped
  if [ `date +%M` -eq $scheduledMinute ]
  then
    if [ -f hourly ]
    then
      if [ `stat -c %Z hourly` -gt `date -d '55 minutes ago' '+%s'` ]
      then
        decho "too soon for hourly" `stat -c %Z hourly` vs `date -d '55 minutes ago' '+%s'` 
        return
      fi
    fi
    c0hourlyCron
  else
    if [ -f hourly ]
    then
      if [ `stat -c %Z hourly` -lt `date -d '59 minutes ago' '+%s'` ]
      then
        c0hourlyCron
      fi
    fi
  fi
}

c1killall() {
  if pgrep -f "chronical.sh $1" | grep -v $$ 
  then
    kill `pgrep -f "chronical.sh $1" | grep -v $$`    
  else
    return 1
  fi
}

c0killall() {
  c0kill
  c1killall run
  c1killall start
}

c0kill() {
  if [ -n "$previousPid" ] 
  then
    if ps -p "$previousPid" >/dev/null
    then
      kill "$previousPid"
    fi
  fi
}

c0stop() {
  rm -f pid
}

c0stopped() {
  if [ ! -f pid ]
  then
    decho "cancelled (pid file removed)"
    exit 1
  elif [ `head -1 pid` -ne $$ ]
  then
    decho "cancelled (pid file changed)"
    exit 1
  fi
}

c0run() {
  debug=2
  c0enroll
  rm -f hourly minutely
  while [ 1 ]
  do
    time=`date +%s`
    periodTime=`date -d "$periodSeconds seconds" +%s`
    decho "periodTime $periodTime (current $time, period $periodSeconds seconds, pid $$)"
    c0minutelyCron
    c0stopped
    decho "periodTime $periodTime vs stat `stat -c %Z minutely`"
    decho "minute `date +%M` vs scheduledMinute $scheduledMinute"
    decho "`date '+%H:%M:%S'` time `date +%s` finish $periodTime for $periodSeconds seconds"
    time=`date +%s`
    if [ $periodTime -gt $time ]
    then
      sleepSeconds=`expr $periodTime - $time`
      decho "sleep $sleepSeconds seconds until periodTime $periodTime from time $time"
      sleep $sleepSeconds
    else
      periodSeconds=`expr $periodSeconds + 30`
      decho "extending periodSeconds to $periodSeconds"
    fi
  done
}

c0restart() {
  debug=0  
  c0killall
  c0run 2>run.err >run.out &
}

c0start() {
  c0restart
}

echo "previous pid $previousPid"
echo "current pid $$"
pgrep -f "chronical.sh" | grep -v $$ && echo "WARNING previous chronical running (pgrep)"
ps x | grep "chronical.sh" | grep -v "$$\|grep" && echo "WARNING previous chronical running"

if [ $# -gt 0 ]
then
  command=$1
  shift
  c$#$command $@  
else 
  c0minutely
  c0hourly
  c0daily
fi
