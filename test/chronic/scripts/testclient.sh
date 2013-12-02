
cd ~/chronic/testclient

c0curl() {
  curl --cacert server.pem --cert cert.pem --key key.pem --data-binary @- --key key.pem https://localhost:8444/post
}

cat cronmail.content.txt | c0curl
cat cronmail.content.txt | grep -v ^other | c0curl
cat cronmail.status.txt | grep -v OK | c0curl
cat cronmail.status.txt | grep -v OK | c0curl
cat cronmail.status.txt | grep -v CRIT | c0curl
cat cronmail.status.txt | grep -v CRIT | c0curl
