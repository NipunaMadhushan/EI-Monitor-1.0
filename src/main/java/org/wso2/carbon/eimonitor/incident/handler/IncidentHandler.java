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
import org.wso2.carbon.eimonitor.configurations.configuredvalues.DataExtractThresholdValues;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.IncidentHandlerThresholdValues;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.monitor.*;
import java.util.List;
import static org.wso2.carbon.eimonitor.Activator.*;

public class IncidentHandler {

    private static final Log log = LogFactory.getLog(IncidentHandler.class);

    private IncidentHandlerThresholdValues incidentHandlerThresholdValues = new IncidentHandlerThresholdValues();

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

        List<Float> thresholdValues = incidentHandlerThresholdValues.getAllThresholdValues();

        if (thresholdValues.size()== monitorValues.size()){
            for (int itemIndex = 0 ; itemIndex < thresholdValues.size() ; itemIndex++){
                if (monitorValues.get(itemIndex) > thresholdValues.get(itemIndex)){
                    state = true;
                    break;
                }
            }
            return state;
        }
        else {
            log.error("List sizes of threshold values and monitors are not equal !!!");
            return Boolean.parseBoolean(null);
        }
    }

    /**
     * This method handles the time period after an incident is captured.
     * It will monitor the WSO2 EI server for a configurable number of times and take the average value of each
     * monitored features.
     * Then it will compare the average values with the threshold values for monitoring and check whether the incident
     * captured is a real issue or not
     * @throws InterruptedException
     */
    public void handleIncidentTimePeriod() throws InterruptedException {

        List<Float> thresholdValues = incidentHandlerThresholdValues.getAllThresholdValues();

        DataExtractThresholdValues dataExtractThresholdValues = new DataExtractThresholdValues();
        final int DATA_EXTRACTING_COUNT_THRESHOLD = dataExtractThresholdValues.DATA_EXTRACTING_COUNT_THRESHOLD;
        final int DATA_EXTRACTING_TIME_PERIOD = dataExtractThresholdValues.DATA_EXTRACTING_TIME_PERIOD;

        float totalIncidentHeapMRatio = 0;
        float totalIncidentCpuMRatio = 0;
        float totalIncidentLoadAverage = 0;
        float totalIncidentMaxBlockTime = 0;

        Monitor monitor = new Monitor();
        //Add the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0; dataExtractNumber < DATA_EXTRACTING_COUNT_THRESHOLD; dataExtractNumber++) {
            List<Float> monitorValues = monitor.getMonitorDetails();
            totalIncidentHeapMRatio += monitorValues.get(0);
            totalIncidentCpuMRatio += monitorValues.get(1);
            totalIncidentLoadAverage += monitorValues.get(2);
            float maxBlockTime = monitorValues.get(3);
            long currentTime = System.currentTimeMillis();
            totalIncidentMaxBlockTime += maxBlockTime/(float)(currentTime- START_TIME);

            dataExtractCount += 1;
            DataExtractor dataExtractor = new DataExtractor();
            dataExtractor.storeData();
            Thread.sleep(DATA_EXTRACTING_TIME_PERIOD);
        }

        //Take the average values of total monitoring values
        float avgIncidentHeapMRatio = totalIncidentHeapMRatio / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentCpuMRatio = totalIncidentCpuMRatio / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentLoadAverage = totalIncidentLoadAverage / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentMaxBlockTime = totalIncidentMaxBlockTime / DATA_EXTRACTING_COUNT_THRESHOLD;

        //Check whether it is a real issue or not
        if ((avgIncidentHeapMRatio > thresholdValues.get(0)) | (avgIncidentCpuMRatio > thresholdValues.get(1)) |
                (avgIncidentLoadAverage > thresholdValues.get(2)) | (avgIncidentMaxBlockTime > thresholdValues.get(3)))
        {
            log.info("An issue has occurred in the system !!!");
            while(true){
                dataExtractCount += 1;
                DataExtractor dataExtractor = new DataExtractor();
                dataExtractor.storeData();
            }
        }
        else {
            log.info("Previous incident is not an issue !!!");
            incidentHandlerState = false;
        }
    }
}
