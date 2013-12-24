#!/bin/bash

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

# see https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronica.sh

### default settings

loadWarningThreshold=4
loadCriticalThreshold=9

diskWarningThreshold=80
diskCriticalThreshold=90

packetLossWarningThreshold=20
packetLossCriticalThreshold=60

server=secure.chronica.co:443

periodSeconds=60

pingCount=1
tcpTimeout=2
sslTimeout=3
httpTimeout=4
databaseTimeout=2


### init 

startTimestamp=`date '+%s'`

set -u 

dir=~/.chronica
mkdir -p $dir/etc
cp -f $0 $dir/script

cd `dirname $0`
custom=`pwd`/custom.chronica.sh
script=`pwd`/chronica.sh

cd $dir

if ! pwd | grep -q '/.chronica$'
then 
  echo "Chronica CRITICAL - pwd sanity check failed:" `pwd`
  exit 1
fi


### debug 

debug=3 # 0 no debugging 1 log to stdout, 2 to sterr, 3 debug file only

rm -f debug

decho() {
  if [ $debug -eq 1 ]
  then
    echo "chronica: $*" 
  fi
  if [ $debug -eq 2 ]
  then
    echo "chronica: $*" >&2
  fi
  if [ $debug -ge 1 ]
  then
    echo "chronica: $*" >> debug
  fi
}

decho custom $custom

dcat() {
  if [ $debug -eq 1 ]
  then
    echo "chronica:" 
    cat "$1"
  fi
  if [ $debug -eq 2 ]
  then
    echo "chronica:" >&2
    cat "$1" >&2
  fi
  if [ $debug -ge 1 ]
  then
    echo "chronica:" >> debug
    cat "$1" >> debug
  fi
}


### pid 

previousPid=''
if [ -f pid ] 
then
  previousPid=`head -1 pid`
  decho "previousPid: $previousPid" 
fi


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

if ! set | grep -q '^scheduledHour='
then
  scheduledHour=`date +%H`
fi

if ! set | grep -q '^scheduledMinute='
then
  scheduledMinute=`date -d '1 minute' +%M`
fi


### reviewable setup

commonName=$USER@`hostname`

c1topic() {
  echo "Topic: $commonName $1"
  echo "Subscribe: $subscribers"
}


### tcp check functions 

c1ping() {
  decho "ping -qc$pingCount $1"
  packetLoss=`ping -qc2 $1 | grep 'packet loss' | sed 's/.* \([0-9]*\)% packet loss.*/\1/'`
  if [ $packetLoss -lt $packetLossWarningThreshold ]
  then
    echo "OK - $1 pingable ($packetLoss% packet loss)"
  elif [ $packetLoss -lt $packetLossCriticalThreshold ]
  then
    echo "WARNING - $1: $packetLoss% packet loss"
  else
    echo "CRITICAL - $1: $packetLoss% packet loss"
  fi
}

c1noping() {
  decho "ping -qc$pingCount $1"
  packetLoss=`ping -qc2 $1 | grep 'packet loss' | sed 's/.* \([0-9]*\)% packet loss.*/\1/'`
  if [ $packetLoss -lt 100 ]
  then
    echo "CRITICAL - $1: $packetLoss% packet loss"
  else
    echo "OK - $1: $packetLoss% packet loss"
  fi
}

c2tcp() {
  decho "nc -w$tcpTimeout $1 $2"
  if nc -w$tcpTimeout $1 $2
  then
    echo "OK - $1 port $2 is open"
  else
    echo "CRITICAL - $1 port $2 is not open"
  fi
}

c2notcp() {
  decho "nc -w$tcpTimeout $1 $2"
  if nc -w$tcpTimeout $1 $2
  then
    echo "CRITICAL - $1 port $2 is not closed"
  else
    echo "OK - $1 port $2 is closed"
  fi
}

c2ssl() {
  decho "timeout $sslTimeout openssl s_client -connect $1:$2"
  if timeout $sslTimeout openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "OK - $1 port $2 has SSL"
  else
    echo "CRITICAL - $1 port $2 does not have SSL"
  fi
}

c2nossl() {
  decho "timeout $sslTimeout openssl s_client -connect $1:$2"
  if timeout $sslTimeout openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "CRITICAL - $1 port $2 has SSL"
  else
    echo "OK - $1 port $2 is not SSL"
  fi
}

c2https() {
  decho "curl --connect-timeout $httpTimeout -k -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "OK - $1 port $2 has HTTPS" `bcat https`
  else 
    echo "CRITICAL - $1 port $2 HTTPS is unavailable" `bcat https`
  fi
}

c2nohttps() {
  decho "curl --connect-timeout $httpTimeout -k -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -k -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "CRITICAL - $1 port $2 has HTTPS available" `bcat https`
  else 
    echo "OK - $1 port $2 HTTPS is unavailable" `bcat https`
  fi
}

c2httpsAuth() {
  decho "curl --connect-timeout $httpTimeout -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "CRITICAL - $1 port $2 does not require HTTPS client auth" `bcat https`
  else 
    echo "OK - $1 port $2 unavailable for HTTPS without client auth" `bcat https`
  fi
}

c2nohttpsAuth() {
  decho "curl --connect-timeout $httpTimeout -s -I https://$1:$2"
  if curl --connect-timeout $httpTimeout -s -I https://$1:$2 | grep '^HTTP' | tee https | grep -q OK 
  then
    echo "OK - $1 port $2 is available for HTTPS without client auth" `bcat https`
  else 
    echo "CRITICAL - $1 port $2 is unavailable for HTTPS without client auth" `bcat https`
  fi
}

c2http() {
  decho "curl --connect-timeout $httpTimeout -s -I http://$1:$2"
  if curl --connect-timeout $httpTimeout -s -I http://$1:$2 | grep '^HTTP' | tee http | grep -q OK 
  then
    echo "OK - $1 port $2 has HTTP available" `bcat http`
  else 
    echo "CRITICAL - $1 port $2 HTTP is unavailable" `bcat http`
  fi
}

c2nohttp() {
  decho "curl --connect-timeout $httpTimeout -s -I http://$1:$2"
  if curl --connect-timeout $httpTimeout -s -I http://$1:$2 | grep '^HTTP' | tee http | grep -q OK 
  then
    echo "CRITICAL - $1 port $2 has HTTP available" `bcat http`
  else 
    echo "OK - $1 port $2 HTTP is unavailable" `bcat http`
  fi
}

c2nosshpass() {
  if ! which sshpass > /dev/null
  then
    echo "INFO no sshpass installed so unable to perform nosshpass check"
  else
    if sshpass -p "" ssh $1 -p $2 date 2>&1 | head -1 | tee sshpass |
        grep -q "Permission denied, please try again."
    then
      echo "CRITICAL - $1 port $2 ssh is prompting for an ssh password"
    else
      echo "OK - $1 port $2 ssh is not prompting for an ssh password"
    fi
  fi
}

c2sshkey() {
  if ! which sshpass > /dev/null
  then
    echo "INFO no sshpass installed so unable to perform sshkey check"
  else
    if sshpass -p "" ssh $1 -p $2 date 2>&1 | grep -q "Permission denied (publickey)."
    then
      echo "OK - $1 port $2 requires an ssh key"
    else
      echo "CRITICAL - $1 port $2 ssh is not permission denied"
    fi
  fi
}

c2postgres() {
  if timeout $databaseTimeout psql -h $1 -p $2 -c 'select 1' 2>&1 | grep -q '^psql: FATAL:  role\| 1 \|^$' 
  then
    echo "OK - PostgreSQL server is running on $1, port $2"
  else
    echo "CRITICAL - PostgreSQL server not running on $1, port $2"
  fi
}

c1sha1sum() {
  echo `curl -s $1 | sha1sum` "$1" 
}

c2certExpiry() {
  expiryDate=`openssl s_client -connect $1:$2 2> /dev/null < /dev/null | openssl x509 -text |
    grep '^ *Not After :' | sed 's/^ *Not After : \(\S*\) \(\S*\) \S* \(20[0-9]*\) \(.*\)/\1 \2 \3/'`
  expiryMonth=`echo "$expiryDate" | cut -f1`
  if date -d '1 month' | grep -q "$expiryMonth"
  then
    echo "WARNING - $1 cert expires next month: $expiryDate"  
  elif date | grep -q "$expiryMonth"
  then
    echo "WARNING - $1 cert expires this month: $expiryDate"  
  else 
    echo "OK - $1 cert expires: $expiryDate"  
  fi
}


### reverse checks

c2rhttps() {
  echo "Check-https: $1 $2"
}

c2rtcp() {
  echo "Check-tcp: $1 $2"
}


### other common checks

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

c0yumVerify() {
  sha1sum /usr/bin/yum
  /usr/bin/yum verify
}

c0rpmVerify() {
  sha1sum /bin/rpm
  /bin/rpm -Va
}

c0megaRaid() {
  echo "<br><b>megaRaid</b>"
  /usr/sbin/MegaCli -ldinfo -lall -aall | grep '^State'
  /usr/sbin/MegaCli -pdlist -aall | grep '^Firmware'
}

c0mdstat() {
  echo "<br><b>mdstat</b>"
  cat /proc/mdstat 2>/dev/null | grep ^md -A1 | sed 's/.*\[\([U_]*\)\]/\1/' |
    sed '/^\s*$/d' | grep 'U\|^md'
}

c0shaAuth() {
  echo "<br><b>shaAuth</b>"
  /usr/bin/sha1sum /usr/bin/sha1sum
  /usr/bin/sha1sum /etc/group
  /usr/bin/sha1sum /etc/passwd
  /usr/bin/sha1sum /etc/ssh/sshd_config
  [ -f /root/.ssh/authorized_keys ] && /usr/bin/sha1sum /root/.ssh/authorized_keys
  /usr/bin/sha1sum `locate authorized_keys | grep '^/home/[a-z]*/\.*ssh/authorized_keys$'`
}


### crypto 

c0ensurePubKey() {
  if [ ! -f etc/chronica.pub.pem ]
  then
    curl -s https://chronica.co/sample/chronica.pub.pem -o etc/chronica.pub.pem
    echo 'Fetched public key: https://chronica.co/sample/chronica.pub.pem'
    cat etc/chronica.pub.pem
    ls etc/chronica.pub.pem
  fi 
}

c0ensureKey() {
  if [ ! -f etc/key.pem ] 
  then
    rm -f etc/cert.pem
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout etc/key.pem -out etc/cert.pem \
      -subj "/CN=$commonName/O=$orgDomain/OU=$orgUnit"
    echo 'Generated client certficate:'
    openssl x509 -text -in etc/cert.pem | grep CN
    ls etc/cert.pem
  fi
}

c0ensureCert() {
  if [ ! -f etc/server.pem ]
  then
    if openssl s_client -connect $server 2>/dev/null </dev/null | grep -q 'BEGIN CERT'
    then
      openssl s_client -connect $server 2>/dev/null |
        sed -n -e '/BEGIN CERT/,/END CERT/p' > etc/server.pem
      echo "Fetched server certificate:"
      openssl x509 -text -in etc/server.pem | grep 'CN='
      ls etc/server.pem
    fi
  fi
}

c0ensurePubKey
c0ensureKey
c0ensureCert


### standard functionality

c1curl() {
  tee curl.txt | curl -s -k --cacert etc/server.pem --key etc/key.pem --cert etc/cert.pem \
    --data-binary @- -H 'Content-Type: text/plain' https://$server/$1 # > curl.out 2> curl.err
}

c0enroll() {
  echo "$admins" | c1curl enroll 
}

c0reset() {
  rm -f etc/cert.pem etc/key.pem etc/server.pem
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
  c0enroll
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


### update script

c0updateGit() {
  c0updateCheck
  echo 'Run the following commands to update your script:'
  echo 'curl -s https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronica.sh | sha1sum'
  echo "curl -s https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronica.sh -o $script"
  echo "sha1sum $script"
  echo 'WARNING: In this case you are trusting that our github.com repository is not compromised.'
}

c0updateInfo() {
  c0ensurePubKey
  echo 'Run the following commands to verify the digest and signature:'
  echo '('
  echo 'curl -s https://chronica.co/sample/chronica.sh | sha1sum'
  echo 'curl -s https://chronica.co/sample/chronica.sh.sha1.txt'
  echo 'curl -s https://chronica.co/sample/chronica.sh.sha1.sig.txt |'
  echo '  openssl base64 -d | openssl rsautl -verify -pubin -inkey ~/.chronica/etc/chronica.pub.pem'
  echo ')'
  echo "Then run the following command to update your script:"
  echo '('
  echo "curl -s https://chronica.co/sample/chronica.sh -o $script"
  echo ')'
}

c0updateCheck() {
  c0updateInfo
  echo 'Verifying https://chronica.co/sample/chronica.sh.sha1.sig.txt using' `pwd`/'chronica.pub.pem:'
  if curl -s https://chronica.co/sample/chronica.sh.sha1.sig.txt |
    openssl base64 -d | openssl rsautl -verify -pubin -inkey chronica.pub.pem 
  then
    echo 'OK: signature verified: https://chronica.co/sample/chronica.sh.sha1.sig.txt'
  else
    echo 'CRITICAL: verification failed: https://chronica.co/sample/chronica.sh.sha1.sig.txt'
  fi
  echo -n `curl -s https://chronica.co/sample/chronica.sh | sha1sum` 
  echo ' sha1sum https://chronica.co/sample/chronica.sh' 
  echo -n `curl -s https://chronica.co/sample/chronica.sh.sha1.txt` 
  echo ' - https://chronica.co/sample/chronica.sh.sha1.txt'
  echo `curl -s https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronica.sh.sha1.txt` '- chronica.sh.sha1.txt on github (may be different)'
}

c0update() {
  c0updateInfo
  echo 'Verifying https://chronica.co/sample/chronica.sh.sha1.sig.txt using' `pwd`/'chronica.pub.pem:'
  if ! curl -s https://chronica.co/sample/chronica.sh.sha1.sig.txt |
    openssl base64 -d | openssl rsautl -verify -pubin -inkey chronica.pub.pem |
    grep `curl -s https://chronica.co/sample/chronica.sh.sha1.txt | head -1`
  then
      echo "ERROR: failed check: https://chronica.co/sample/chronica.sh.sha1.sig.txt"
  else 
      echo "OK: verified: https://chronica.co/sample/chronica.sh.sha1.sig.txt"
    if ! curl -s https://chronica.co/sample/chronica.sh | sha1sum |
      grep `curl -s https://chronica.co/sample/chronica.sh.sha1.txt | head -1`
    then
      echo "ERROR: failed check: https://chronica.co/sample/chronica.sh.sha1.txt"
    else 
      echo "OK: matches: https://chronica.co/sample/chronica.sh.sha1.txt"
      echo "Running the following command to update your script:"
      echo "curl -s https://chronica.co/sample/chronica.sh -o $script"
      curl -s https://chronica.co/sample/chronica.sh -o $script
      exit $?
    fi
  fi
}


### post with headers 

c1postheaders() {
  tee curl.txt | curl -s -k --cacert etc/server.pem --key etc/key.pem --cert etc/cert.pem \
    --data-binary @- -H 'Content-Type: text/plain' -H "$1" https://$server/post 
}

c2postheaders() {
  tee curl.txt | curl -s -k --cacert etc/server.pem --key etc/key.pem --cert etc/cert.pem \
    --data-binary @- -H 'Content-Type: text/plain' -H "$1" -H "$2" https://$server/post 
}

c3postheaders() {
  tee curl.txt | curl -s -k --cacert etc/server.pem --key etc/key.pem --cert etc/cert.pem \
    --data-binary @- -H 'Content-Type: text/plain' -H "$1" -H "$2" -H "$3" https://$server/post 
}


### logging

c0log() {
  pwd
  ls -l
  cat run.out
}

### lifecycle

c0ps() {
  [ -n "$previousPid" ] && echo "pid file: $previousPid"
  echo "this pid: $$"
  echo "ps aux:"
  ps aux | grep -v grep | grep "chronica"
  echo "pgrep -f chronica.sh:"
  pgrep -f chronica.sh
}

c0killall() {
  c0ps
  pids=`pgrep -f chronica.sh | grep -v $$`
  for pid in $pids 
  do
    if ps x | grep $pid | grep -v grep | grep '[0-9]' 
    then
      kill "$pid"
      echo "killed $pid"
    fi
  done
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
    decho 'cancelled (pid file removed)'
    exit 1
  elif [ `head -1 pid` -ne $$ ]
  then
    decho 'cancelled (pid file changed)'
    exit 1
  fi
}

c0run() {
  echo $$ > pid
  debug=2
  c0enroll
  rm -f hourly minutely
  c0hourlyCron
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
  c0kill
  c0run 2> run.err > run.out < /dev/null & 
  sleep 1
  c0ps
}

c0start() {
  c0restart
}

c0help() {
  echo "commands and their required number of arguments:"
  cat script | grep '^c[0-9]\S*() {\S*$' | sed 's/^c\([0-9]\)\(\S*\)() {/\1: \2/'
}

if [ $# -gt 0 ]
then
  command=$1
  [ $command != "start" ] &&  trap 'rm -f pid' EXIT
  shift
  c$#$command "$@"
else 
  c0minutely
  c0hourly
  c0daily
fi

