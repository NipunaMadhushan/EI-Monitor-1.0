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

package org.wso2.carbon.eimonitor.initial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.JMXConnection;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.data.extractor.HeapDumpGenerator;
import org.wso2.carbon.eimonitor.data.extractor.LogExtractor;
import org.wso2.carbon.eimonitor.data.extractor.NetworkLoadGenerator;
import org.wso2.carbon.eimonitor.data.extractor.ThreadDumpGenerator;
import org.wso2.carbon.eimonitor.monitor.CPUMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.HeapMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.LoadAverageMonitor;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import org.wso2.carbon.eimonitor.monitor.ThreadStatusMonitor;
import java.util.concurrent.ScheduledExecutorService;
import javax.management.MBeanServerConnection;

/**
 * This class is used to check whether the captured incident is a real issue or not.
 */
public class IncidentTime extends Thread {

    private static final Log log = LogFactory.getLog(IncidentTime.class);

    private JMXConnection jmxConnection = new JMXConnection();
    private MBeanServerConnection beanServerConnection = jmxConnection.connectJMX();
    private int incidentTimeCountThreshold;

    private Monitor heapMemoryMonitor = new HeapMemoryMonitor();
    private Monitor cpuMemoryMonitor = new CPUMemoryMonitor();
    private Monitor loadAverageMonitor = new LoadAverageMonitor();
    private Monitor threadStatusMonitor = new ThreadStatusMonitor();

    private DataExtractor heapDumpGenerator = new HeapDumpGenerator();
    private DataExtractor threadDumpGenerator = new ThreadDumpGenerator();
    private DataExtractor networkLoadGenerator = new NetworkLoadGenerator(beanServerConnection);
    private DataExtractor logExtractor = new LogExtractor();

    private float avgIncidentHeapMRatio = heapMemoryMonitor.getThresholdValue();
    private float avgIncidentCpuMRatio = cpuMemoryMonitor.getThresholdValue();
    private float avgIncidentLoadAverage = loadAverageMonitor.getThresholdValue();
    private float avgIncidentMaxBlockTime = threadStatusMonitor.getThresholdValue();

    private int monitoringCount = 0;
    private ScheduledExecutorService service;

    public IncidentTime(ScheduledExecutorService service) {
        this.service = service;

        Object incidentTimeCountThreshold = Properties.getProperty(Constants.IncidentHandlerThValues.
                INCIDENT_TIME_MONITORING_COUNT);
        if (incidentTimeCountThreshold instanceof Integer) {
            this.incidentTimeCountThreshold = (int) incidentTimeCountThreshold;
        } else {
            log.error(Constants.IncidentHandlerThValues.INCIDENT_TIME_MONITORING_COUNT
                    + " property has been defined incorrectly in the property file..");
        }
    }


    public void run() {
        try {
            this.avgIncidentHeapMRatio = calculateAvgMonitorValue(avgIncidentHeapMRatio,
                    heapMemoryMonitor.getMonitorValue());
            this.avgIncidentCpuMRatio = calculateAvgMonitorValue(avgIncidentCpuMRatio,
                    cpuMemoryMonitor.getMonitorValue());
            this.avgIncidentLoadAverage = calculateAvgMonitorValue(avgIncidentLoadAverage,
                    loadAverageMonitor.getMonitorValue());
            this.avgIncidentMaxBlockTime = calculateAvgMonitorValue(avgIncidentMaxBlockTime,
                    threadStatusMonitor.getMonitorValue());

            extractData();

            this.monitoringCount += 1;
            if (this.monitoringCount >= incidentTimeCountThreshold) {
                service.shutdown();
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    private void extractData() {
        heapDumpGenerator.generateData();
        threadDumpGenerator.generateData();
        networkLoadGenerator.generateData();
        logExtractor.generateData();
    }

    private float calculateAvgMonitorValue(float avgMonitorValue, float newMonitorValue) {
        return (avgMonitorValue + newMonitorValue) / 2;
    }

    public boolean getIncidentState() {
        boolean heapMemoryMonitorState = checkAvgMonitorValueState(avgIncidentHeapMRatio, heapMemoryMonitor.
                getThresholdValue());
        boolean cpuMemoryMonitorState = checkAvgMonitorValueState(avgIncidentCpuMRatio, cpuMemoryMonitor.
                getThresholdValue());
        boolean loadAverageMonitorState = checkAvgMonitorValueState(avgIncidentLoadAverage, loadAverageMonitor.
                getThresholdValue());
        boolean threadStatusMonitorState = checkAvgMonitorValueState(avgIncidentMaxBlockTime, threadStatusMonitor.
                getThresholdValue());

        return heapMemoryMonitorState | cpuMemoryMonitorState | loadAverageMonitorState | threadStatusMonitorState;
    }

    private boolean checkAvgMonitorValueState(float avgMonitorValue, float thresholdValue) {
        try {
            return avgMonitorValue >= thresholdValue;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return Boolean.parseBoolean(null);
        }
    }
}
