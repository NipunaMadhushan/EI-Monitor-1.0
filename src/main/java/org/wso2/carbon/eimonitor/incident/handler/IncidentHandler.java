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
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import java.util.List;

/**
 * This class is used to catch an Incident and handle another time period to check whether the incident is a real issue
 * or not.
 */
public class IncidentHandler {
    private static final Log log = LogFactory.getLog(IncidentHandler.class);
    private List<Float> thresholdValues = Constants.IncidentHandlerThresholdValues.getAllThresholdValues();
    private int dataExtractCount;

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
     * @return True if there is an incident. False if there is no an incident.
     */
    public boolean handleAll(List<Float> monitorValues) {
        boolean state = false;

        if (thresholdValues.size() == monitorValues.size()) {
            for (int itemIndex = 0; itemIndex < thresholdValues.size(); itemIndex++) {
                if (monitorValues.get(itemIndex) > thresholdValues.get(itemIndex)) {
                    state = true;
                    break;
                }
            }
            return state;
        } else {
            log.error("List sizes of threshold values and monitors are not equal !!!");
            Boolean.parseBoolean(null);
            return false;
        }
    }

    /**
     * This method handles the time period after an incident is captured.
     * It will monitor the WSO2 EI server for a configurable number of times and take the average value of each
     * monitored features.
     * Then it will compare the average values with the threshold values for monitoring and check whether the incident
     * captured is a real issue or not
     */
    public boolean handleIncidentTimePeriod() {
        float totalIncidentHeapMRatio = 0;
        float totalIncidentCpuMRatio = 0;
        float totalIncidentLoadAverage = 0;
        float totalIncidentMaxBlockTime = 0;

        //Add the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0;
             dataExtractNumber < Constants.IncidentHandlerThresholdValues.INCIDENT_TIME_MONITORING_COUNT;
             dataExtractNumber++) {
            Monitor monitor = new Monitor();
            List<Float> monitorValues = monitor.getMonitorDetails();
            totalIncidentHeapMRatio += monitorValues.get(0);
            totalIncidentCpuMRatio += monitorValues.get(1);
            totalIncidentLoadAverage += monitorValues.get(2);
            totalIncidentMaxBlockTime += monitorValues.get(3);

            dataExtractCount += 1;
            DataExtractor dataExtractor = new DataExtractor(dataExtractCount);
            dataExtractor.storeData();
        }

        //Take the average values of total monitoring values
        float avgIncidentHeapMRatio =
                totalIncidentHeapMRatio / Constants.IncidentHandlerThresholdValues.INCIDENT_TIME_MONITORING_COUNT;
        float avgIncidentCpuMRatio =
                totalIncidentCpuMRatio / Constants.IncidentHandlerThresholdValues.INCIDENT_TIME_MONITORING_COUNT;
        float avgIncidentLoadAverage =
                totalIncidentLoadAverage / Constants.IncidentHandlerThresholdValues.INCIDENT_TIME_MONITORING_COUNT;
        float avgIncidentMaxBlockTime =
                totalIncidentMaxBlockTime / Constants.IncidentHandlerThresholdValues.INCIDENT_TIME_MONITORING_COUNT;

        //Check whether it is a real issue or not
        return (avgIncidentHeapMRatio > thresholdValues.get(0)) | (avgIncidentCpuMRatio > thresholdValues.get(1)) |
                (avgIncidentLoadAverage > thresholdValues.get(2)) | (avgIncidentMaxBlockTime > thresholdValues.get(3));
    }
}
