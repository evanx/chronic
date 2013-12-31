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

import vellum.data.Millis;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class MetricSeries {

    int capacity;
    int size = 0;
    byte[] values;
    long timestamp;
    transient float ceiling;

    public MetricSeries(int capacity) {
        this.capacity = (byte) capacity;
        values = new byte[capacity];
    }

    public synchronized void add(long timestamp, float value) {
        this.timestamp = timestamp;
        if (size == 0) {
            setCeiling(Math.abs(value)*3/2);
        } else if (Math.abs(value) > ceiling) {
            setCeiling(Math.abs(value)*3/2);
        }
        for (int i = 0; i < capacity - 1; i++) {
            values[i] = values[i+1];
        }
        values[capacity - 1] = getNormalizedValue(value);
        if (size < capacity) {
            size++;
        }
    }

    public int size() {
        return size;
    }

    public float getFloatValue(int value) {
        return value * ceiling / Byte.MAX_VALUE;
    }
    
    public byte getNormalizedValue(float floatValue) {
        if (floatValue > 0) {
            int intValue = (int) (floatValue * Byte.MAX_VALUE / ceiling);
            if (intValue > Byte.MAX_VALUE) {
                intValue = Byte.MAX_VALUE;
            }
            return (byte) intValue;
        } else if (floatValue < 0) {
            int intValue = (int) (floatValue * Byte.MAX_VALUE / ceiling);
            if (intValue < Byte.MIN_VALUE) {
                intValue = Byte.MIN_VALUE;
            }
            return (byte) intValue;
        } else {
            return (byte) 0;
        }
    }

    public void setCeiling(float ceiling) {
        for (int i = 0; i < values.length; i++) {
            values[i] = (byte) (values[i] * this.ceiling / ceiling);
        }
        this.ceiling = ceiling;
    }

    public float avg() {
        int total = 0;
        for (int i = 0; i < values.length; i++) {
            total += values[i];
        }
        return total * ceiling / size() / Byte.MAX_VALUE;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSize() {
        return size;
    }
    
    public synchronized JMap getMinutelyMap() {
        JMap map = new JMap();
        Float[] floatValues = new Float[size];
        String[] labels = new String[size];
        int minute = Millis.timestampMinute(timestamp);
        for (int i = 1; i <= size; i++) {
            floatValues[size - i] = getFloatValue(values[capacity - i]);
            if (minute  == 0) {
                labels[size - i] = "'";
            } else if (minute % 5 == 0) {
                labels[size - i] = String.format("%02d", minute);
            } else {
                labels[size - i] = "";                
            }
            minute = (59 + minute) % 60;
        }
        map.put("data", floatValues);
        map.put("labels", labels);
        return map;
    }

    @Override
    public String toString() {
        return String.format("size %d, average %f, timestamp minute %s", size(), avg(), Millis.timestampMinute(timestamp));
    }       
}
