
cd ~/chronic/testclient

domain=test.org
commonName=`hostname -s`.$domain
org=$domain
orgUnit=localnet

if [ ! -f key.pem ] 
then
  rm -f cert.pem
  openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout key.pem -out cert.pem \
    -subj "/CN=$commonName/OU=$orgUnit/O=$org"
  openssl x509 -text -in cert.pem | grep CN
fi
if [ ! -f server.pem ] 
  echo | openssl s_client -connect localhost:8443 2>/dev/null | 
    sed -n -e '/BEGIN CERT/,/END CERT/p' > server.pem
  openssl x509 -text -in server.pem | grep 'CN='
fi

tee ~/tmp/chronic.out | curl --cacert server.pem --key key.pem \
  --data-binary @- -H 'Content-Type: text/plain' \
  https://localhost:8443/post

