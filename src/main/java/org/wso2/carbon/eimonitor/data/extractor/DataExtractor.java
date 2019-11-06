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

import javax.management.MBeanServerConnection;

/**
 * This class is used to extract the data.
 */
public class DataExtractor {
    private int dataExtractCount;
    private MBeanServerConnection beanServerConnection;

    public DataExtractor(int dataExtractCount, MBeanServerConnection beanServerConnection) {
        this.dataExtractCount = dataExtractCount;
        this.beanServerConnection = beanServerConnection;
    }
    /**
     * This method stores the data of heap dump generator,thread dump generator and the network load generator.
     */
    public void storeData() {
        HeapDumpGenerator heapDumpGenerator = new HeapDumpGenerator(dataExtractCount);
        heapDumpGenerator.generateHeapDump();
        ThreadDumpGenerator threadDumpGenerator = new ThreadDumpGenerator(dataExtractCount);
        threadDumpGenerator.generateThreadDump();
        NetworkLoadGenerator networkLoadGenerator = new NetworkLoadGenerator(beanServerConnection);
        networkLoadGenerator.generateNetworkLoad();
        LogExtractor logExtractor = new LogExtractor();
        logExtractor.run();
        logExtractor.logWriter(logExtractor.getLogs());
        logExtractor.stop();
    }
}
