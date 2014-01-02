/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.type.MetricType;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class IntervalList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(IntervalList.class);

    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        List list = new LinkedList();
        for (MetricType type : MetricType.values()) {
            JMap map = new JMap();
            map.put("name", type.name());
            map.put("label", type.getLabel());
            list.add(map);
        }
        logger.info("list {}", list);
        return JMaps.mapValue("intervals", list);
    }
}
