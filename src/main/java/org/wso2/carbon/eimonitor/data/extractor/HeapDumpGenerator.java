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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.DirectoryNames.HEAP_DUMP_FILE_DIRECTORY;

public class HeapDumpGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * This method generates a heap dump into the file directory we have configured in the
     * EI_Monitor_Configuration.properties file.
     * Generated file is in .hprof type.
     * @param number Heap dump number
     */
    public static void getHeapDump(int number) {

        String fileName = "Heap Dump-" + number + ".hprof";
        String dumpFile = HEAP_DUMP_FILE_DIRECTORY + "/" + fileName;

        try {
            final String hotspotBeanName = "com.sun.management:type=HotSpotDiagnostic";
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory.newPlatformMXBeanProxy(server,
                    hotspotBeanName, HotSpotDiagnosticMXBean.class);

            hotSpotDiagnosticMXBean.dumpHeap(dumpFile,true);

        } catch (IOException e) {
            LOGGER.error("Heap Dump Generation failed !!!" + e.getMessage());
        }
    }
}
