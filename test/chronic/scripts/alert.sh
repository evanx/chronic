
print() {
  echo "<pre>"
  cat
  echo -n "<hr><font color=gray>"
  set | grep '^[a-z]*=' | grep '^from\|^service\|^status\|^subject\|^alert'
  echo "</font>"
  echo "</pre>"
}

print

