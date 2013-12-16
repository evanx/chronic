
set -u 

alias decho='echo DEBUG'
alias dcat='cat'

### init 

decho "see https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/chronical.sh"
decho "see https://raw.github.com/evanx/chronic/master/src/chronic/web/sample/custom.chronical.sh"

decho pwd `pwd`
custom=`dirname $0`/custom.chronical.sh
decho "custom $custom"

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

dir=~/.chronic
mkdir -p $dir
cd $dir


### reviewable setup

commonName=`hostname`

c1topic() {
  echo "Topic: $commonName $1"
  echo "Subscribe: $subscribers"
}


### check util functions 

c2tcp() {
  if nc -w3 $1 $2
  then
    echo "OK - $1:$2 open"
  else
    echo "WARNING - $1:$2 closed"
  fi
}

c2notcp() {
  if nc -w3 $1 $2
  then
    echo "WARNING - $1:$2 open"
  else
    echo "OK - $1:$2 closed"
  fi
}

c2ssl() {
  if timeout 4 openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "OK - $1:$2 has SSL"
  else
    echo "WARNING - $1:$2 no SSL"
  fi
}

c2nossl() {
  if timeout 4 openssl s_client -connect $1:$2 2> /dev/null < /dev/null | grep '^subject=' 
  then
    echo "WARNING - $1:$2 has SSL"
  else
    echo "OK - $1:$2 no SSL"
  fi
}

c2https() {
  if curl --connect-timeout 8 -k -s -I https://$1:$2 | grep 'HTTP' 
  then
    echo "OK - $1:$2 https available"
  else 
    echo "WARNING - $1:$2 unavailable https"
  fi
}

c2nohttps() {
  if curl --connect-timeout 8 -k -s -I https://$1:$2 | grep 'HTTP'
  then
    echo "WARNING - $1:$2 https available"
  else 
    echo "OK - $1:$2 unavailable https"
  fi
}

c2postgres() {
  psql -h $1 -p $2 -c 'select 1' 2>&1 | grep -q '^psql: FATAL:  role\| 1 \|^$' || 
    echo "WARNING - $1:$2 postgres server not running"
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
    --data-binary @- -H 'Content-Type: text/plain' https://chronical.info:8444/$1 >curl.out 2> curl.err
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
    if echo | openssl s_client -connect chronical.info:8444 2>/dev/null | grep -q 'BEGIN CERT'
    then
      openssl s_client -connect chronical.info:8444 2>/dev/null | 
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

c0minutelyCron() {
  c0minutelyPost
  if [ `date +%M` -eq $cronMinute ] 
  then
    c0hourlyPost
    if [ `date +%H` -eq $cronHour ] 
    then
      c0dailyPost
    fi
  fi
}

c0killstart() {
  if pgrep -f 'chronical.sh start'
  then
    kill `pgrep -f 'chronical.sh start'`
  fi
}

c0kill() {
  if [ -f pid ] 
  then
    echo "previous pid:" `cat pid`
  fi
  if [ -f pid ] 
  then
    pid=`cat pid`
    if ps -p $pid | grep $pid
    then
      decho "kill $pid"
      kill $pid
    fi
  fi
  rm -f pid
}

c0run() {
  c0kill
  echo $$ > pid
  c0enroll
  c0hourlyPost
  c0dailyPost
  while [ 1 ]
  do
    c0minutelyCron
    echo `date '+%H:%M:%S'` sleeping for 60 seconds
    sleep 58 
    date
    if [ ! -f pid -o `cat $pid` -ne $$ ]
    then
      return
    fi
  done
}

c0start() {
  c0kill
  c0run 2>run.err >run.out &
}

if [ $# -gt 0 ]
then
  command=$1
  shift
  c$#$command $@  
else 
  c0hourly
fi
