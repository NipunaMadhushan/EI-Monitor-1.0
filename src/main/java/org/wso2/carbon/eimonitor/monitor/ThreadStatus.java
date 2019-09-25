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
import javax.management.ObjectName;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import static org.wso2.carbon.eimonitor.Activator.BEAN_SERVER_CONNECTION;

public class ThreadStatus {

    private static final Logger LOGGER = LogManager.getLogger();

    public static int getThreadStatusDetails() {

        try {
            //for JMX connections
            ObjectName name = new ObjectName("java.lang:type=Threading");
            ThreadMXBean threadMXBean = JMX.newMXBeanProxy(BEAN_SERVER_CONNECTION,name,ThreadMXBean.class);
            threadMXBean.setThreadContentionMonitoringEnabled(true);
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

            //for virtual machines
            //ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            //threadMXBean.setThreadContentionMonitoringEnabled(true);
            //ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

            int maxBlockedCount = 0;

            for (ThreadInfo threadInfo : threadInfos) {

                Object blockedTime = threadInfo.getBlockedTime();

                //finding the maximum blocked time among the threads
                if (Integer.parseInt(blockedTime.toString()) >maxBlockedCount){
                    maxBlockedCount = Integer.parseInt(blockedTime.toString());
                }
            }
            return maxBlockedCount;

        } catch (Exception e) {
            LOGGER.error("Failed to read the thread status !!! " + e.getMessage());
        }
        return Integer.parseInt(null);
    }
}