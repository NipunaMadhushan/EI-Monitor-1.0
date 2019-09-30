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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.IncidentHandlerThresholdValues;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.monitor.CPUMemory;
import org.wso2.carbon.eimonitor.monitor.HeapMemory;
import org.wso2.carbon.eimonitor.monitor.LoadAverage;
import org.wso2.carbon.eimonitor.monitor.ThreadStatus;
import java.util.List;
import static org.wso2.carbon.eimonitor.Activator.*;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.IncidentHandlerThresholdValues.*;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.DataExtractThresholdValues.*;

public class IncidentHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * This method handles all the incidents given below which can occur in the EI server.
     *      Heap Memory goes over threshold value
     *      CPU Memory goes over threshold value
     *      System Load Average goes over threshold value
     *      Maximum Average Block Time goes over threshold value
     * Note That the parameters should be inserted according to the order of the given above.
     * Otherwise Incident Handler will not work properly and may occur errors.
     * @param monitorValues Monitor values of handling features
     * @return True if there is an incident. False if there is no an incident.
     */
    public static boolean handleAll(List<Float> monitorValues) {
        boolean state = false;

        List<Float> thresholdValues = IncidentHandlerThresholdValues.getAllThresholdValues();

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
            LOGGER.error("List sizes of threshold values and monitors are not equal !!!");
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
    public static void handleIncidentTimePeriod() throws InterruptedException {
        float totalIncidentHeapMRatio = 0;
        float totalIncidentCpuMRatio = 0;
        float totalIncidentLoadAverage = 0;
        float totalIncidentMaxBlockTime = 0;

        //adding the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0; dataExtractNumber < DATA_EXTRACTING_COUNT_THRESHOLD; dataExtractNumber++) {
            totalIncidentHeapMRatio += HeapMemory.getHeapMemoryUsage();
            totalIncidentCpuMRatio += CPUMemory.getCPUMemoryUsage();
            totalIncidentLoadAverage += LoadAverage.getSystemLoadAverage();
            int maxBlockTime = ThreadStatus.getThreadStatusDetails();
            long currentTime = System.currentTimeMillis();
            totalIncidentMaxBlockTime += (float)maxBlockTime/(float)(currentTime- START_TIME);

            dataExtractCount += 1;
            DataExtractor.storeData();
            Thread.sleep(DATA_EXTRACTING_TIME_PERIOD);
        }

        //taking the average values of total monitoring values
        float avgIncidentHeapMRatio = totalIncidentHeapMRatio / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentCpuMRatio = totalIncidentCpuMRatio / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentLoadAverage = totalIncidentLoadAverage / DATA_EXTRACTING_COUNT_THRESHOLD;
        float avgIncidentMaxBlockTime = totalIncidentMaxBlockTime / DATA_EXTRACTING_COUNT_THRESHOLD;

        //check whether it is a real issue or not
        if ((avgIncidentHeapMRatio > HEAP_RATIO_THRESHOLD) | (avgIncidentCpuMRatio > CPU_RATIO_THRESHOLD) |
                (avgIncidentLoadAverage > LOAD_AVERAGE_THRESHOLD) | (avgIncidentMaxBlockTime > BLOCK_TIME_THRESHOLD)) {
            LOGGER.info("An issue has occured in the system !!!");
            while(true){
                dataExtractCount += 1;
                DataExtractor.storeData();
            }
        }
        else {
            LOGGER.info("Previous incident is not an issue !!!");
            incidentHandlerState = false;
        }
    }
}
