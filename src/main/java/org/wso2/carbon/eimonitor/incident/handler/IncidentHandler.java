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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to catch an Incident and handle another time period to check whether the incident is a real issue
 * or not.
 */
public class IncidentHandler {
    private static final Log log = LogFactory.getLog(IncidentHandler.class);

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

    private int dataExtractCount;
    private float heapRatio;
    private float cpuRatio;
    private float loadAverage;
    private float avgMaxBlockedTime;
    private boolean incidentState;

    public IncidentHandler(int dataExtractCount) {
        this.dataExtractCount = dataExtractCount;
    }

    /**
     * This method handles all the incidents given below which can occur in the EI server.
     *      Heap Memory goes over threshold value
     *      CPU Memory goes over threshold value
     *      System Load Average goes over threshold value
     *      Maximum Average Block Time goes over threshold value
     * Note that the parameters should be inserted according to the order of the given above.
     * Otherwise Incident Handler will not work properly and may occur errors.
     * @param monitorValues Monitor values of handling features
     */
    public void handleIncident(HashMap monitorValues) {
        this.heapRatio = (float) monitorValues.get("Heap Memory Ratio");
        this.cpuRatio = (float) monitorValues.get("CPU Memory Ratio");
        this.loadAverage = (float) monitorValues.get("System Load Average");
        this.avgMaxBlockedTime = (float) monitorValues.get("Avg Max Blocked Time");

        if (heapRatio > heapRatioThreshold | cpuRatio > cpuRatioThreshold | loadAverage > loadAverageThreshold |
                avgMaxBlockedTime > blockedTimeThreshold) {
            this.incidentState = true;
        } else {
            this.incidentState = false;
        }
    }
    public boolean getIncidentState() {
        return incidentState;
    }

    /**
     * This method handles the time period after an incident is captured.
     * It will monitor the WSO2 EI server for a configurable number of times and take the average value of each
     * monitored features.
     * Then it will compare the average values with the threshold values for monitoring and check whether the incident
     * captured is a real issue or not
     */
    public boolean handleIncidentTimePeriod(Monitor monitor) {
        float totalIncidentHeapMRatio = 0;
        float totalIncidentCpuMRatio = 0;
        float totalIncidentLoadAverage = 0;
        float totalIncidentMaxBlockTime = 0;
        //Add the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0; dataExtractNumber < incidentTimeMonitoringCount; dataExtractNumber++) {
            HashMap monitorValues = monitor.getMonitorValues();
            this.heapRatio = (float) monitorValues.get("Heap Memory Ratio");
            this.cpuRatio = (float) monitorValues.get("CPU Memory Ratio");
            this.loadAverage = (float) monitorValues.get("System Load Average");
            this.avgMaxBlockedTime = (float) monitorValues.get("Avg Max Blocked Time");

            dataExtractCount += 1;
            DataExtractor dataExtractor = new DataExtractor(dataExtractCount);
            dataExtractor.storeData();
        }

        //Take the average values of total monitoring values
        float avgIncidentHeapMRatio = totalIncidentHeapMRatio / incidentTimeMonitoringCount;
        float avgIncidentCpuMRatio = totalIncidentCpuMRatio / incidentTimeMonitoringCount;
        float avgIncidentLoadAverage = totalIncidentLoadAverage / incidentTimeMonitoringCount;
        float avgIncidentMaxBlockTime = totalIncidentMaxBlockTime / incidentTimeMonitoringCount;

        //Check whether it is a real issue or not
        return (avgIncidentHeapMRatio > heapRatioThreshold) | (avgIncidentCpuMRatio > cpuRatioThreshold) |
                (avgIncidentLoadAverage > loadAverageThreshold) | (avgIncidentMaxBlockTime > blockedTimeThreshold);
    }
}
