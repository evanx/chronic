
pass=123456

rm -f ~/tmp/test.jks ~/tmp/ca.pem ~/tmp/signed.pem

curl -s -k https://localhost:8443/sample/ca.pem -o ~/tmp/ca.pem

openssl x509 -text -in ~/tmp/ca.pem | grep 'CN='

keytool -keystore ~/tmp/test.jks -storepass $pass -importcert -alias ca -file ~/tmp/ca.pem -noprompt

keytool -keystore ~/tmp/test.jks -storepass $pass -keypass $pass -genkeypair -keyalg rsa \
  -alias clientx -dname 'CN=clientx, O=test.org, OU=mygroup' 

keytool -keystore ~/tmp/test.jks -storepass $pass -certreq -alias clientx | 
  curl -s -k --data-binary @- --cacert ~/.chronica/etc/server.pem --data-binary @- \
    -H 'Content-Type: text/plain' https://localhost:8443/sign > ~/tmp/signed.pem 

openssl x509 -text -in ~/tmp/signed.pem | grep 'CN='

keytool -keystore ~/tmp/test.jks -storepass $pass -importcert -alias clientx -file ~/tmp/signed.pem -noprompt
