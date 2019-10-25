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

package org.wso2.carbon.eimonitor.data.extractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.MainThread;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import static org.wso2.carbon.eimonitor.MainThread.dataExtractCount;

/**
 * This class is used to extract the data.
 */
public class DataExtractor {
    private static final Log log = LogFactory.getLog(DataExtractor.class);
    /**
     * This method stores the data of heap dump generator,thread dump generator and the network load generator.
     */
    public void storeData() {
        HeapDumpGenerator heapDumpGenerator = new HeapDumpGenerator();
        heapDumpGenerator.getHeapDump(dataExtractCount);
        ThreadDumpGenerator threadDumpGenerator = new ThreadDumpGenerator();
        threadDumpGenerator.getThreadDump(dataExtractCount);
        LogExtractor logExtractor = new LogExtractor();
        try {
            logExtractor.run();
            MainThread.sleep(Constants.DataExtractThresholdValues.DATA_EXTRACTING_TIME_PERIOD);
            logExtractor.logWriter(logExtractor.getLogs(), "/home/nipuna/Desktop/Logs/carbon.log");
            logExtractor.stop();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
