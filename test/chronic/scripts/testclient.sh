
cd ~/chronic/testclient

cat cronmail.txt | grep -v OK | 
  curl --cacert server.pem --data-binary @- --key key.pem \
  https://localhost:8443/post

cat cronmail.txt | grep -v OK | 
  curl --cacert server.pem --data-binary @- --key key.pem \
  https://localhost:8443/post

cat cronmail.txt | grep -v CRIT | 
  curl --cacert server.pem --data-binary @- --key key.pem \
  https://localhost:8443/post

cat cronmail.txt | grep -v CRIT | 
  curl --cacert server.pem --data-binary @- --key key.pem \
  https://localhost:8443/post
