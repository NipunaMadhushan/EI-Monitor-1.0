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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServerConnection;
import java.lang.reflect.Method;
import static org.wso2.carbon.eimonitor.Activator.HEAP_DUMP_FILE_DIRECTORY;

public class HeapDumpGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void getHeapDump(int number,MBeanServerConnection beanServerConn) {

        String hotSpotBeanName = "com.sun.management:type=HotSpotDiagnostic";
        String fileName = "HeapDump-"+number+".hprof";

        boolean live = true;

        if (beanServerConn != null) {
            Class clazz = null;
            String dumpFile = HEAP_DUMP_FILE_DIRECTORY + "/" + fileName;
            try {
                clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
                Object hotSpotMBean = ManagementFactory.newPlatformMXBeanProxy(beanServerConn, hotSpotBeanName, clazz);
                Method method = clazz.getMethod("dumpHeap", new Class[]{String.class, boolean.class});
                method.setAccessible(true);
                method.invoke(hotSpotMBean, new Object[]{dumpFile, new Boolean(live)});
                LOGGER.info("Heap Dump is generated !!!");
            } catch (Exception e) {
                LOGGER.error("Heap dump generation failed !!! " + e.getMessage());
            } finally {
                clazz = null;
            }
        }
    }
}
