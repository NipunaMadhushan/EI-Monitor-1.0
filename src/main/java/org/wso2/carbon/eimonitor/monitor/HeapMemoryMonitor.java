/*
 *  * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */

package org.wso2.carbon.eimonitor.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * This class is used to read Heap Memory.
 */
public class HeapMemoryMonitor implements Monitor {

    private static final Log log = LogFactory.getLog(HeapMemoryMonitor.class);

    /**
     *This method sets the Heap Memory Ratio as a ratio of used heap memory to committed heap memory at an instance
     *time.
     * @return ratio of used heap memory to the committed heap memory
     */
    public float getMonitorValue() {
        MemoryMXBean memoryMXBeanProxy = ManagementFactory.getMemoryMXBean();

        //Calculate heap memory ratio
        float usedHeapMemory = (float) memoryMXBeanProxy.getHeapMemoryUsage().getUsed();
        float committedHeapMemory = (float) memoryMXBeanProxy.getHeapMemoryUsage().getCommitted();
        return usedHeapMemory / committedHeapMemory;
    }

    public float getThresholdValue() {
        Object heapRatioThreshold = Properties.getProperty(Constants.IncidentHandlerThValues.HEAP_RATIO_THRESHOLD);
        if (heapRatioThreshold instanceof Float) {
            return (float) heapRatioThreshold;
        } else {
            log.error(Constants.IncidentHandlerThValues.HEAP_RATIO_THRESHOLD
                    + " property has been defined incorrectly in the properties file.");
            return Float.parseFloat(null);
        }
    }

    public boolean checkMonitorValue() {
        float monitorValue = getMonitorValue();
        float thresholdValue = getThresholdValue();

        if (monitorValue >= thresholdValue) {
            log.error("An incident has been captured... Heap Memory has gone over threshold value..");
            return true;
        } else {
            return false;
        }
    }
}