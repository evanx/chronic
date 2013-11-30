

set -u

cd ~/chronic

if pgrep -f chronic
then
  kill `pgrep -f chronic`
fi

/usr/java/default/jre/bin/java -jar chronic.jar > log 2>&1 &
