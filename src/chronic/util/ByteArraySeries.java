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
package chronic.util;

/**
 *
 * @author evan.summers
 */
public class ByteArraySeries {

    int capacity;
    int size = 0;
    byte[] values;
    transient float ceiling;

    public ByteArraySeries(int capacity) {
        this.capacity = capacity;
        values = new byte[capacity];
    }

    public synchronized void add(float value) {
        if (size == 0) {
            setCeiling(Math.abs(value)*3/2);
        } else if (Math.abs(value) > ceiling) {
            setCeiling(Math.abs(value)*3/2);
        }
        for (int i = capacity - size; i < capacity - 1; i++) {
            values[i] = values[i+1];
        }
        values[capacity - 1] = getNormalizedValue(value);
        if (size < capacity) {
            size++;
        }
    }

    public synchronized int size() {
        return size;
    }

    private float getFloatValue(int value) {
        return value * ceiling / Byte.MAX_VALUE;
    }
    
    private byte getNormalizedValue(float floatValue) {
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

    public synchronized void setCeiling(float ceiling) {
        for (int i = 0; i < values.length; i++) {
            values[i] = (byte) (values[i] * this.ceiling / ceiling);
        }
        this.ceiling = ceiling;
    }

    public synchronized float average(int count) {
        int total = 0;
        if (count > size()) count = size;
        for (int i = 1; i <= size; i++) {
            total += values[capacity - i];
        }
        return total * ceiling / size() / Byte.MAX_VALUE;
    }
    
    public synchronized float average() {
        return average(size());
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getSize() {
        return size;
    }
    
    public synchronized Float[] getFloatArray() {
        Float[] floatArray = new Float[capacity];
        for (int i = 0; i < capacity; i++) {
            floatArray[i] = getFloatValue(values[i]);
        }
        return floatArray;
    }

    @Override
    public String toString() {
        return String.format("size %d, average %f", size(), average());
    }
}
