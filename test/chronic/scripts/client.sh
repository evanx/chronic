
cd ~/chronic

cat mail.txt | curl -k --key key.pem --data-binary @- -H 'Content-Type: text/plain' \
  https://localhost:8443/post
