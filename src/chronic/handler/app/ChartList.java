/*
 * Source https://github.com/evanx by @evanxsummers
 */
package chronic.handler.app;

import chronic.app.ChronicHttpx;
import chronic.api.ChronicHttpxHandler;
import chronic.alert.MetricSeries;
import chronic.app.ChronicApp;
import chronic.app.ChronicEntityService;
import chronic.entity.Cert;
import chronic.entity.Topic;
import chronic.entitykey.TopicMetricKey;
import chronic.entitykey.TopicMetricKeyOrderComparator;
import chronic.type.MetricType;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.format.CalendarFormats;
import vellum.jx.JMap;
import vellum.jx.JMaps;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class ChartList implements ChronicHttpxHandler {

    Logger logger = LoggerFactory.getLogger(ChartList.class);

    @Override
    public JMap handle(ChronicApp app, ChronicHttpx httpx, ChronicEntityService es)
            throws Exception {
        logger.info("handle {}", app.getSeriesMap().keySet());
        String email = httpx.getEmail();
        TimeZone timeZone = httpx.getTimeZone();
        String intervalString = httpx.parseJsonMap().getString("data");
        logger.info("string {}", intervalString);
        MetricType intervalType = MetricType.valueOf(intervalString);
        List metrics = new LinkedList();
        for (TopicMetricKey key : Lists.sortedSet(app.getSeriesMap().keySet(), new TopicMetricKeyOrderComparator())) {
            logger.info("");
            Topic topic = es.findTopic(key.getTopicId());
            if (httpx.getReferer().endsWith("/demo")) {
            } else if (app.getProperties().isAdmin(email)) {
            } else if (!es.isSubscription(topic, email)) {
                continue;
            }
            MetricSeries series = app.getSeriesMap().get(key);
            logger.info("series {}", series.toString());
            JMap map = series.getMap(timeZone, intervalType);
            map.put("metricLabel", key.getMetricLabel());
            map.put("topicLabel", topic.getTopicLabel());
            Cert cert = es.findCert(topic.getCertId());
            map.put("commonName", cert.getCommonName());
            map.put("orgUnit", cert.getOrgUnit());
            map.put("orgDomain", cert.getOrgDomain());
            if (System.currentTimeMillis() - series.getTimestamp() > Millis.fromSeconds(90)) {
                map.put("timestampLabel", CalendarFormats.timestampFormat.format(timeZone, series.getTimestamp()));
            }
            metrics.add(map);
        }
        logger.info("metrics {}", metrics);
        return JMaps.mapValue("metrics", metrics);
    }
}
