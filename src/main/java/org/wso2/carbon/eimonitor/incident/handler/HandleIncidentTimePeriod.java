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

package org.wso2.carbon.eimonitor.incident.handler;

import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.*;
import org.wso2.carbon.eimonitor.monitor.*;
import javax.management.MBeanServerConnection;
import java.util.Objects;

/**
 * This method checks whether the captured incident is a real issue or not.
 */
public class HandleIncidentTimePeriod implements IncidentHandler {
    private final float heapRatioThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.HEAP_RATIO_THRESHOLD)));
    private final float cpuRatioThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.CPU_RATIO_THRESHOLD)));
    private final float loadAverageThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.LOAD_AVERAGE_THRESHOLD)));
    private final float blockedTimeThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.BLOCK_TIME_THRESHOLD)));
    private final int incidentTimeMonitoringCount = Integer.parseInt(Objects.requireNonNull(Properties.
            getProperty(Constants.IncidentHandlerThValues.INCIDENT_TIME_MONITORING_COUNT)));

    private float totalIncidentHeapMRatio = 0;
    private float totalIncidentCpuMRatio = 0;
    private float totalIncidentLoadAverage = 0;
    private float totalIncidentMaxBlockTime = 0;

    private float avgIncidentHeapMRatio;
    private float avgIncidentCpuMRatio;
    private float avgIncidentLoadAverage;
    private float avgIncidentMaxBlockTime;

    private Monitor monitor = new Monitor();

    private MBeanServerConnection beanServerConnection;
    private boolean issueState;

    public HandleIncidentTimePeriod(MBeanServerConnection beanServerConnection) {
        this.beanServerConnection = beanServerConnection;
    }

    /**
     * This method is used to add monitor values to the total values of them and calculate the average value of them.
     * Then checks the issue state and return the value.
     * @return issue state as boolean
     */
    @Override
    public boolean getState() {
        //Add the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0; dataExtractNumber < incidentTimeMonitoringCount; dataExtractNumber++) {
            addMonitorValues();
            extractData();
        }
        calculateAverageValues();
        checkIssueState();

        return issueState;
    }

    private void addMonitorValues() {
        HeapMemory heapMemory = new HeapMemory();
        CPUMemory cpuMemory = new CPUMemory();
        LoadAverage loadAverage = new LoadAverage();
        ThreadStatus threadStatus = new ThreadStatus();

        monitor.setMonitorValue("Heap Memory Ratio", heapMemory.heapMemoryRatio());
        monitor.setMonitorValue("CPU Memory Ratio", cpuMemory.cpuMemoryRatio());
        monitor.setMonitorValue("System Load Average", loadAverage.systemLoadAverage());
        monitor.setMonitorValue("Avg Max Blocked Time", threadStatus.avgMaxBlockedTime());

        this.totalIncidentHeapMRatio += monitor.getMonitorValue("Heap Memory Ratio");
        this.totalIncidentCpuMRatio += monitor.getMonitorValue("CPU Memory Ratio");
        this.totalIncidentLoadAverage += monitor.getMonitorValue("System Load Average");
        this.totalIncidentMaxBlockTime += monitor.getMonitorValue("Avg Max Blocked Time");
    }

    private void extractData() {
        DataExtractor dataExtractor = new HeapDumpGenerator();
        dataExtractor.generateData();
        dataExtractor = new ThreadDumpGenerator();
        dataExtractor.generateData();
        dataExtractor = new NetworkLoadGenerator(beanServerConnection);
        dataExtractor.generateData();
        dataExtractor = new LogExtractor();
        dataExtractor.generateData();
    }

    private void calculateAverageValues() {
        this.avgIncidentHeapMRatio = totalIncidentHeapMRatio / incidentTimeMonitoringCount;
        this.avgIncidentCpuMRatio = totalIncidentCpuMRatio / incidentTimeMonitoringCount;
        this.avgIncidentLoadAverage = totalIncidentLoadAverage / incidentTimeMonitoringCount;
        this.avgIncidentMaxBlockTime = totalIncidentMaxBlockTime / incidentTimeMonitoringCount;
    }

    /**
     * This method checks whether the captured incident is a real issue or not.
     */
    private void checkIssueState() {
         this.issueState = (avgIncidentHeapMRatio > heapRatioThreshold) | (avgIncidentCpuMRatio > cpuRatioThreshold) |
                (avgIncidentLoadAverage > loadAverageThreshold) | (avgIncidentMaxBlockTime > blockedTimeThreshold);
    }
}
