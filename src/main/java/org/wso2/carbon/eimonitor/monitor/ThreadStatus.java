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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadStatus {

    /**
     * This method returns the Maximum Blocked Time among all threads.
     * It will check the block times of all the threads and find the maximum block time.
     * @return maxBlockedTime as an integer
     */

    public static int getThreadStatusDetails() {
        //getting the thread information or all threads
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.setThreadContentionMonitoringEnabled(true);
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

        int maxBlockedTime = 0;

        //finding the maximum blocked time among the threads
        for (ThreadInfo threadInfo : threadInfos) {
            Object blockedTime = threadInfo.getBlockedTime();

            if (Integer.parseInt(blockedTime.toString()) > maxBlockedTime) {
                maxBlockedTime = Integer.parseInt(blockedTime.toString());
            }
        }

        return maxBlockedTime;
    }
}