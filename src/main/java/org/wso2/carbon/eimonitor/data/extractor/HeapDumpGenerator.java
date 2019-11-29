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

import com.sun.management.HotSpotDiagnosticMXBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.util.Date;
import javax.management.MBeanServer;

/**
 * This class is used to generate a heap dump.
 */
public class HeapDumpGenerator implements DataExtractor {

    private static final Log log = LogFactory.getLog(HeapDumpGenerator.class);
    private String heapDumpFileDirectory = Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY, String
            .class.getName()) + Constants.DirectoryNames.HEAP_DUMP_FILE_DIRECTORY;


    private static final DataExtractor DATA_EXTRACTOR;

    private HeapDumpGenerator(){}

    //static block initialization for exception handling
    static {
        try {
            DATA_EXTRACTOR = new HeapDumpGenerator();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static DataExtractor getInstance() {
        return DATA_EXTRACTOR;
    }


    /**
     * This method generates a heap dump into the file directory we have configured in the
     * EI_Monitor_Configuration.properties file.
     * Generated file is in .hprof type.
     */
    public void extractData() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String fileName = "Heap Dump-" + timestamp + ".hprof";
        String dumpFile = heapDumpFileDirectory + "/" + fileName;
        try {
            String hotspotBeanName = "com.sun.management:type=HotSpotDiagnostic";
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory.newPlatformMXBeanProxy(server,
                    hotspotBeanName, HotSpotDiagnosticMXBean.class);

            hotSpotDiagnosticMXBean.dumpHeap(dumpFile, true);

        } catch (IOException e) {
            log.error("Heap Dump Generation failed !!!" + e.getMessage());
        }
    }
}
