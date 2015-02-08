
This server app's entry point is <a href="https://github.com/evanx/chronic/blob/master/src/chronic/app/ChronicApp.java">ChronicApp</a>. It's a Java server using the JDK's builtin HttpServer and HttpsServer. It uses EntityManager to save events e.g. to PostgreSQL.

Machines use a <a href="https://github.com/evanx/chronic/tree/master/src/chronic/web/sample/chronica.sh">shell script</a> to HTTPS POST status data to the server, where they are auto-enrolled according to their client-side generated certificate. These posts have a specified "topic" e.g. disk, CPU, clock, file modifications, logins, and other status data and metrics. This script uses standard Linux programs like `df,` `netstat`, `openssl` etc.

There is also a custom <a href="https://github.com/evanx/chronic4j">log4j appender</a> project which can be used rather non-intrusively by Java apps to post status via log4j.

Administrators log into the centralized AngularJS app which uses Mozilla Persona to authenticate them. They are then able to view events on various topics from various participating machines in their organisations.

Commmon utilities were copied from <a href="https://github.com/evanx/vellum">github.com/evanx/vellum</a> to <a href="https://github.com/evanx/vellumcore">github.com/evanx/vellumcore</a>, as a dependency for other projects, including this.


For (un)related articles, see https://github.com/evanx/vellum/wiki
