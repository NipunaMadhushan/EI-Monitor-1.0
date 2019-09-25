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

package org.wso2.carbon.eimonitor.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.lang.management.MemoryMXBean;

public class HeapMemory {

    private static final Logger LOGGER = LogManager.getLogger();

    public static float getHeapMemoryUsage(MBeanServerConnection beanServerConnection){

        try{
            //for virtual machines
            //MemoryMXBean memoryMXBeanProxy = ManagementFactory.getMemoryMXBean();

            //for the JMX Connections
            ObjectName name = new ObjectName("java.lang:type=Memory");
            MemoryMXBean memoryMXBeanProxy = JMX.newMXBeanProxy(beanServerConnection,name,MemoryMXBean.class);

            float usedHeapMemory = (float)memoryMXBeanProxy.getHeapMemoryUsage().getUsed();
            float committedHeapMemory = (float) memoryMXBeanProxy.getHeapMemoryUsage().getCommitted();
            float heapMemoryRatio = usedHeapMemory/committedHeapMemory;

            return heapMemoryRatio;

        } catch (Exception e) {
            LOGGER.error("Failed to read the Heap Memory Usage!!! " + e.getMessage());
        }
        return Float.parseFloat(null);
    }
}