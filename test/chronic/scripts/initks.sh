
set -u
cd ~/chronic

pass=2013chronic
cn=localhost

rm -i keystores/$cn.jks

keytool -keystore keystores/$cn.jks -storepass $pass -genkeypair -alias $cn -keypass $pass \
  -keyalg rsa -dname "CN=$cn, O=testorg"

keytool -keystore keystores/$cn.jks -storepass $pass -alias $cn -exportcert -rfc | 
  openssl x509 -text | grep 'CN='

keytool -keystore keystores/$cn.jks -storepass $pass -alias $cn -exportcert -rfc | 
  openssl x509 -text | 
  sed -n -e '/BEGIN CERT/,/END CERT/p'
