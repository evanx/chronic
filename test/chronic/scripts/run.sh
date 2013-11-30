

set -u

cd ~/chronic

echo $PATH

if pgrep -f chronic
then
  kill `pgrep -f chronic`
  sleep 1
fi

rm -f log
/usr/java/default/jre/bin/java -jar chronic.jar
echo $?
