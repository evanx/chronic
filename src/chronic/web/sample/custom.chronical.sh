

### custom config 

subscribers=""
admins=""
orgUrl=""
orgUnit=""
cronMinute=""
cronHour=""


### default settings

loadWarningThreshold=4
loadCriticalThreshold=9

diskWarningThreshold=80
diskCriticalThreshold=90

sleepSeconds="58"


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
