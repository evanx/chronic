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

import chronic.type.IntervalType;
import chronic.util.ByteArraySeries;
import vellum.data.Millis;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class MetricSeries {

    long timestamp;
    long hourTimestamp;
    ByteArraySeries minutelySeries;
    ByteArraySeries hourlySeries;

    public MetricSeries(int minutelyCapacity, int hourlyCapacity) {
        minutelySeries = new ByteArraySeries(minutelyCapacity);
        hourlySeries = new ByteArraySeries(hourlyCapacity);
    }

    public synchronized void add(long timestamp, float value) {
        this.timestamp = timestamp;
        int minute = Millis.timestampMinute(timestamp);
        if (minute == 0) {
            hourly();
        } else if (minute == 1 && hourTimestamp == 0) {
            hourly();
        }
        minutelySeries.add(value);
    }

    private void hourly() {
        hourTimestamp = timestamp;
        hourlySeries.add(minutelySeries.average(60));
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    public synchronized JMap getMap(IntervalType intervalType) {
        if (intervalType == IntervalType.MINUTE) {
            return getMinutelyMap();
        } else if (intervalType == IntervalType.HOUR) {
            return getHourlyMap();
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
                labels[size - i] = "\"";
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

    public synchronized JMap getHourlyMap() {
        JMap map = new JMap();
        Float[] floatValues = minutelySeries.getFloatArray();
        int size = floatValues.length;
        String[] labels = new String[size];
        int minute = Millis.timestampMinute(timestamp);
        for (int i = 1; i <= size; i++) {
            if (minute == 0) {
                labels[size - i] = "\"";
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

    @Override
    public String toString() {
        return minutelySeries.toString();
    }

}
