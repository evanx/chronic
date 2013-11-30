
cd ~/chronic/testclient

  openssl x509 -text -in cert.pem | grep CN

  echo | openssl s_client -connect localhost:8443 2>/dev/null | 
    sed -n -e '/BEGIN CERT/,/END CERT/p' > server.pem

  openssl x509 -text -in server.pem | grep 'CN='

  ls -l server.pem
