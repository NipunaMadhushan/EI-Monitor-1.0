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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * This class is used to read the thread status.
 */
public class ThreadStatusMonitor implements Monitor {

    private static final Log log = LogFactory.getLog(ThreadStatusMonitor.class);

    /**
     * This method returns the Maximum Blocked Time among all threads.
     * It will check the block times of all the threads and find the maximum block time.
     * @return ratio of maximum blocked time to the total time of thread among all threads.
     */
    public float getMonitorValue() {
        //Get the thread information or all threads
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.setThreadContentionMonitoringEnabled(true);
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

        float avgMaxBlockedTime = 0;

        //Find the average maximum blocked time among the threads
        for (ThreadInfo threadInfo : threadInfos) {
            long blockedTime = threadInfo.getBlockedTime();
            long threadId = threadInfo.getThreadId();
            long threadCpuTime = threadMXBean.getThreadCpuTime(threadId);

            if (((float) blockedTime / (float) threadCpuTime) > avgMaxBlockedTime) {
                avgMaxBlockedTime = (float) blockedTime / (float) threadCpuTime;
            }
        }

        return avgMaxBlockedTime;
    }

    public float getThresholdValue() {
        Object blockTimeThreshold = Properties.getProperty(Constants.IncidentHandlerThValues.BLOCK_TIME_THRESHOLD);
        if (blockTimeThreshold instanceof Float) {
            return (float) blockTimeThreshold;
        } else {
            log.error(Constants.IncidentHandlerThValues.BLOCK_TIME_THRESHOLD
                    + " property has been defined incorrectly in the properties file.");
            return Float.parseFloat(null);
        }
    }

    public boolean checkMonitorValue() {
        float monitorValue = getMonitorValue();
        float thresholdValue = getThresholdValue();

        if (monitorValue >= thresholdValue) {
            log.error("An incident has been captured... Average Maximum Block Time has gone over threshold value..");
            return true;
        } else {
            return false;
        }
    }
}
