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
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.monitor.CpuMemory;
import org.wso2.carbon.eimonitor.monitor.HeapMemory;
import org.wso2.carbon.eimonitor.monitor.LoadAverage;
import org.wso2.carbon.eimonitor.monitor.ThreadStatus;
import java.util.List;
import static org.wso2.carbon.eimonitor.Activator.*;

public class IncidentHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean handleAll(List<Float> thresholdValues, List<Float> monitorValues) {

        boolean state = false;

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

    public static void handleIncidentTimePeriod() throws InterruptedException {

        float totalIncidentHeapMemoryRatio = 0;
        float totalIncidentCpuMemoryRatio = 0;
        float totalIncidentLoadAverage = 0;
        float totalIncidentAverageMaxBlockedTime = 0;

        //adding the monitoring values for a configurable number of times
        for (int dataExtractNumber = 0; dataExtractNumber < DATA_EXTRACTING_COUNT_THRESHOLD; dataExtractNumber++){
            totalIncidentHeapMemoryRatio += HeapMemory.getHeapMemoryUsage(BEAN_SERVER_CONNECTION);
            totalIncidentCpuMemoryRatio += CpuMemory.getCpuMemoryUsage(BEAN_SERVER_CONNECTION);
            totalIncidentLoadAverage += LoadAverage.getSystemLoadAverage(BEAN_SERVER_CONNECTION);
            int maxBlockedTime = ThreadStatus.getThreadStatusDetails();
            long currentTime = System.currentTimeMillis();
            totalIncidentAverageMaxBlockedTime += (float)maxBlockedTime/(float)(currentTime- START_TIME);

            dataExtractCount += 1;
            DataExtractor.storeData();
            Thread.sleep(DATA_EXTRACTING_TIME_PERIOD);

        }

        //taking the average values of total monitoring values
        float averageIncidentHeapMemoryRatio = totalIncidentHeapMemoryRatio/ DATA_EXTRACTING_COUNT_THRESHOLD;
        float averageIncidentCpuMemoryRatio = totalIncidentCpuMemoryRatio/ DATA_EXTRACTING_COUNT_THRESHOLD;
        float averageIncidentLoadAverage = totalIncidentLoadAverage/ DATA_EXTRACTING_COUNT_THRESHOLD;
        float averageIncidentAverageMaxBlockedTime = totalIncidentAverageMaxBlockedTime/ DATA_EXTRACTING_COUNT_THRESHOLD;

        //check whether it is a real issue or not
        if ((averageIncidentHeapMemoryRatio> HEAP_RATIO_THRESHOLD)|(averageIncidentCpuMemoryRatio>CPU_RATIO_THRESHOLD)|
                (averageIncidentLoadAverage> LOAD_AVERAGE_THRESHOLD)|(averageIncidentAverageMaxBlockedTime> BLOCK_TIME_THRESHOLD)){
            LOGGER.info("An issue has occured in the system !!!");
            while(true){
                dataExtractCount += 1;
                DataExtractor.storeData();
            }
        }
        else{
            LOGGER.info("Previous incident is not an issue !!!");
            incidentHandlerState = false;
        }
    }
}
