/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronic.alert;

import chronic.type.MetricType;
import chronic.util.ByteArraySeries;
import java.util.Calendar;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.format.CalendarFormats;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class MetricSeries {
    static Logger logger = LoggerFactory.getLogger(MetricSeries.class);
    
    long timestamp;
    long hourTimestamp;
    ByteArraySeries minutelySeries;
    ByteArraySeries hourlyAverageSeries;
    ByteArraySeries hourlyMaximumSeries;

    public MetricSeries(int minutelyCapacity, int hourlyCapacity) {
        minutelySeries = new ByteArraySeries(minutelyCapacity);
        hourlyAverageSeries = new ByteArraySeries(hourlyCapacity);
        hourlyMaximumSeries = new ByteArraySeries(hourlyCapacity);
    }

    public synchronized void add(long timestamp, float value) {
        this.timestamp = timestamp;
        int minute = Millis.timestampMinute(timestamp);
        if (minutelySeries.size() >= 60) {
            if (minute == 0) {
                hourly();
            } else if (minute <= 2) {
                if (hourTimestamp == 0) {
                    hourly();
                } else if (timestamp - hourTimestamp > Millis.fromMinutes(62)) {
                    logger.warn("hour elapsed {}", Millis.formatPeriod(timestamp - hourTimestamp));
                    hourly();
                }
            }
        }
        minutelySeries.add(value);
    }

    private void hourly() {
        logger.info("hourly {}", Millis.formatDefaultTimeZone(timestamp));
        hourTimestamp = timestamp;
        hourlyAverageSeries.add(minutelySeries.average(60));
        hourlyMaximumSeries.add(minutelySeries.maximum(60));
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    public synchronized JMap getMap(TimeZone timeZone, MetricType intervalType) {
        if (intervalType == MetricType.MINUTELY) {
            return getMinutelyMap();
        } else if (intervalType == MetricType.HOURLY_AVERAGE) {
            return getHourlyMap(hourlyAverageSeries.getFloatArray(), timeZone);
        } else if (intervalType == MetricType.HOURLY_MAXIMUM) {
            return getHourlyMap(hourlyMaximumSeries.getFloatArray(), timeZone);
        }
        return getMinutelyMap();
    }

    public synchronized JMap getMinutelyMap() {
        JMap map = new JMap();
        Float[] floatValues = minutelySeries.getFloatArray();
        int size = floatValues.length;
        String[] labels = new String[size];
        int minute = Millis.timestampMinute(timestamp);
        for (int i = 1; i <= size; i++) {
            if (minute == 0) {
                labels[size - i] = "0\"";
            } else if (minute % 5 == 0) {
                labels[size - i] = String.format("%02d", minute);
            } else {
                labels[size - i] = "";
            }
            if (minute == 0) {
                minute = 59;
            } else {
                minute--;
            }
        }
        map.put("data", floatValues);
        map.put("labels", labels);
        return map;
    }

    public synchronized JMap getHourlyMap(Float[] floatValues, TimeZone timeZone) {
        JMap map = new JMap();
        int size = floatValues.length;
        String[] labels = new String[size];
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(hourTimestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            hour = 23;
        } else {
            hour--;
        }
        for (int i = 1; i <= size; i++) {
            if (hour == 0) {
                labels[size - i] = "0h";
            } else if (hour % 4 == 0) {
                labels[size - i] = String.format("%d", hour);
            } else {
                labels[size - i] = "";
            }
            if (hour == 0) {
                hour = 23;
            } else {
                hour--;
            }
        }
        map.put("data", floatValues);
        map.put("labels", labels);
        return map;
    }

    @Override
    public String toString() {
        return minutelySeries.toString();
    }

}
