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

import java.util.ArrayList;
import java.util.List;
import static org.wso2.carbon.eimonitor.Activator.START_TIME;

public class Monitor {

    /**
     * This method returns all the features which are monitored in the WSO2 EI.
     * There is an order in the return output of this method.
     *      1)Heap Memory Ratio
     *      2)CPU Memory Ratio
     *      3)System Load Average
     *      4)Average Maximum Blocked Time
     * @return all the monitored features as floats inside a List.
     */
    public static List<Float> getMonitorDetails() {
        //monitoring the WSO2 EI server
        long currentTime = System.currentTimeMillis();
        float heapMemoryRatio = HeapMemory.getHeapMemoryUsage();
        float cpuMemoryRatio = CPUMemory.getCPUMemoryUsage();
        float systemLoadAverage = (float)LoadAverage.getSystemLoadAverage();
        int maxBlockedTime = ThreadStatus.getThreadStatusDetails();
        float averageMaxBlockedTime = (float)maxBlockedTime / (float)(currentTime - START_TIME);

        //adding the monitor values to a list
        List<Float> monitorValues = new ArrayList<>();
        monitorValues.add(heapMemoryRatio);
        monitorValues.add(cpuMemoryRatio);
        monitorValues.add(systemLoadAverage);
        monitorValues.add(averageMaxBlockedTime);

        return monitorValues;
    }
}
