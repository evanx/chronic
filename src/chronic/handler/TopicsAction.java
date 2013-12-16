/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.ChronicApp;
import chronic.ChronicHttpxHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.httpserver.Httpx;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class TopicsAction implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(TopicsAction.class);
  
    @Override
    public JMap handle(ChronicApp app, Httpx httpx) throws Exception {
        JMap topicMap = httpx.parseJsonMap();
        
        return topicMap;
    }
    
}
