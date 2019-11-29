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

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.lang.management.ManagementFactory;

/**
 * This class is used to read CPUMemory.
 */
public class CPUMemoryMonitor implements Monitor {

    private static final Log log = LogFactory.getLog(CPUMemoryMonitor.class);

    private static final Monitor MONITOR;

    private CPUMemoryMonitor(){}

    //static block initialization for exception handling
    static {
        try {
            MONITOR = new CPUMemoryMonitor();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static Monitor getInstance() {
        return MONITOR;
    }


    /**
     * This method sets the CPU Memory Ratio as a ratio of used CPU memory to total CPU memory at an instance time.
     * @return ratio of used cpu memory to the total cpu memory
     */
    public float getMonitorValue() {
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //Calculate the CPU memory ratio
        long totalMemory = operatingSystemMXBean.getTotalPhysicalMemorySize();
        long freeMemory = operatingSystemMXBean.getFreePhysicalMemorySize();

        return  (float) freeMemory / (float) totalMemory;
    }

    public float getThresholdValue() {
        return (float) Properties.getProperty(Constants.Threshold.CPU_RATIO_THRESHOLD, Float.class.getName());
    }

    public boolean isMonitorValueHealthy() {
        float monitorValue = getMonitorValue();
        float thresholdValue = getThresholdValue();

        if (monitorValue >= thresholdValue) {
            log.error("An incident has been captured... CPU Memory has gone over threshold value..");
            return true;
        } else {
            return false;
        }
    }
}
