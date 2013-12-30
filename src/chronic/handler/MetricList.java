/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.alert.MetricSeries;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Topic;
import chronic.entitykey.TopicMetricKey;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.jx.JMap;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class MetricList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(MetricList.class);

    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        logger.info("handle {}", app.getSeriesMap().keySet());
        String email = httpx.getEmail();
        List metrics = new LinkedList();
        for (TopicMetricKey key : app.getSeriesMap().keySet()) {
            MetricSeries series = app.getSeriesMap().get(key);
            logger.info("series {}", series.toString());
            if (System.currentTimeMillis() - series.getTimestamp() < Millis.fromSeconds(90) && series.getSize() > 5) {
                JMap map = series.getMinutelyMap();
                Topic topic = es.findTopic(key.getTopicId());
                map.put("topicLabel", topic.getTopicLabel());
                map.put("metricLabel", key.getMetricLabel());
                if (es.isSubscription(key.getTopicId(), email)) {
                } else if (httpx.getReferer().endsWith("/demo")) {
                } else if (app.getProperties().isAdmin(email)) {
                }
                metrics.add(map);
            }
        }
        logger.info("metrics {}", metrics);
        return JMaps.mapValue("metrics", metrics);
    }
}
