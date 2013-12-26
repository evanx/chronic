/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.entity.Person;
import chronic.app.ChronicCookie;
import chronic.app.ChronicEntityService;
import chronic.persona.PersonaInfo;
import chronic.persona.PersonaVerifier;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class PersonaLogin implements ChronicHttpxHandler {

    static Logger logger = LoggerFactory.getLogger(PersonaLogin.class);
    String assertion;
    String timezoneOffset;
    ChronicCookie cookie;
    
    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es) 
            throws Exception {
        JMap map = httpx.parseJsonMap();
        timezoneOffset = map.getString("timezoneOffset");
        logger.trace("timezoneOffset {}", timezoneOffset);
        assertion = map.getString("assertion");
        if (ChronicCookie.matches(httpx.getCookieMap())) {
            cookie = new ChronicCookie(httpx.getCookieMap());
        }
        PersonaInfo userInfo = new PersonaVerifier(httpx.app, cookie).getPersonaInfo(
                httpx.getHostUrl(), assertion);
        logger.trace("persona {}", userInfo);
        String email = userInfo.getEmail();
        Person person = es.findPerson(email);
        if (person == null) {
            person = new Person(email);
            person.setEnabled(true);
            person.setLoginTime(new Date());
            es.persist(person);
            logger.info("insert user {}", email);
        } else {
            person.setEnabled(true);
            person.setLoginTime(new Date());
        }
        cookie = new ChronicCookie(person.getEmail(), person.getLabel(),
                person.getLoginTime().getTime(), assertion);
        JMap cookieMap = cookie.toMap();
        logger.trace("cookie {}", cookieMap);
        httpx.setCookie(cookieMap, ChronicCookie.MAX_AGE_MILLIS);
        cookieMap.put("demo", httpx.getReferer().endsWith("/demo"));
        return cookieMap;
    }
}
