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

import java.util.HashMap;

/**
 * This class is used to read all the monitoring values in this tool.
 */
public class Monitor {
    private HashMap<String, Float> monitorValues = new HashMap<>();
    private HeapMemory heapMemory = new HeapMemory();
    private CPUMemory cpuMemory = new CPUMemory();
    private LoadAverage loadAverage = new LoadAverage();
    private ThreadStatus threadStatus = new ThreadStatus();

    public void setMonitorValues() {
        monitorValues.clear();

        heapMemory.setHeapMemoryRatio();
        float heapMemoryRatio = heapMemory.getHeapMemoryRatio();
        monitorValues.put("Heap Memory Ratio", heapMemoryRatio);

        cpuMemory.setCpuMemoryRatio();
        float cpuMemoryRatio = cpuMemory.getCpuMemoryRatio();
        monitorValues.put("CPU Memory Ratio", cpuMemoryRatio);

        loadAverage.setSystemLoadAverage();
        float systemLoadAverage = loadAverage.getSystemLoadAverage();
        monitorValues.put("System Load Average", systemLoadAverage);

        threadStatus.setAvgMaxBlockedTime();
        float avgMaxBlockedTime = threadStatus.getAvgMaxBlockedTime();
        monitorValues.put("Avg Max Blocked Time", avgMaxBlockedTime);
    }
    public HashMap<String, Float> getMonitorValues() {
        return monitorValues;
    }

}
