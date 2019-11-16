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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.monitor.Monitor;

import java.util.Objects;

/**
 * This class checks whether there is an incident or not.
 */
public class HandleIncident implements IncidentHandler {
    private static final Log log = LogFactory.getLog(HandleIncident.class);

    private final float heapRatioThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.HEAP_RATIO_THRESHOLD)));
    private final float cpuRatioThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.CPU_RATIO_THRESHOLD)));
    private final float loadAverageThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.LOAD_AVERAGE_THRESHOLD)));
    private final float blockedTimeThreshold = Float.parseFloat(Objects.requireNonNull(Properties.getProperty(Constants.
            IncidentHandlerThValues.BLOCK_TIME_THRESHOLD)));

    private float heapRatio;
    private float cpuRatio;
    private float systemLoadAverage;
    private float avgMaxBlockedTime;

    public HandleIncident(Monitor monitor) {
        this.heapRatio = monitor.getMonitorValue("Heap Memory Ratio");
        this.cpuRatio = monitor.getMonitorValue("CPU Memory Ratio");
        this.systemLoadAverage = monitor.getMonitorValue("System Load Average");
        this.avgMaxBlockedTime = monitor.getMonitorValue("Avg Max Blocked Time");
    }

    /**
     * This method compares the monitored values with the threshold values.
     * @return the state as boolean which says there is an incident or not.
     */
    @Override
    public boolean getState() {
        if (heapRatio > heapRatioThreshold | cpuRatio > cpuRatioThreshold | systemLoadAverage > loadAverageThreshold |
                avgMaxBlockedTime > blockedTimeThreshold) {
            log.error("An Incident has been captured");
            return true;
        } else {
            return false;
        }
    }
}
