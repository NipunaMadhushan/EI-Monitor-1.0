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
import java.lang.management.ManagementFactory;

/**
 * This class is used to read CPUMemory.
 */
class CPUMemory {

    /**
     * This method returns the CPU Memory Usage as a ratio of used CPU memory to total CPU memory at an instance time.
     * @return cpuUsage as a float
     */
    float getCPUMemoryUsage() {
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        //Calculate the CPU memory ratio
        long totalMemory = operatingSystemMXBean.getTotalPhysicalMemorySize();
        long freeMemory = operatingSystemMXBean.getFreePhysicalMemorySize();

        return (float) freeMemory / (float) totalMemory;
    }
}
