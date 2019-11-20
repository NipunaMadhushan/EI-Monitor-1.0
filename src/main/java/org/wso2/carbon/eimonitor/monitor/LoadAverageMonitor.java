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
import java.lang.management.OperatingSystemMXBean;

/**
 * This class is used to read the System Load Average.
 */
public class LoadAverageMonitor implements Monitor {

    private static final Log log = LogFactory.getLog(LoadAverageMonitor.class);

    /**
     *This method sets the System Load Average.
     * @return system load average
     */
    public float getMonitorValue() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        Object systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

        return (float) systemLoadAverage;
    }

    public float getThresholdValue() {
        Object loadAverageThreshold = Properties.getProperty(Constants.IncidentHandlerThValues.LOAD_AVERAGE_THRESHOLD);
        if (loadAverageThreshold instanceof Float) {
            return (float) loadAverageThreshold;
        } else {
            log.error(Constants.IncidentHandlerThValues.LOAD_AVERAGE_THRESHOLD
                    + " property has been defined incorrectly in the properties file.");
            return Float.parseFloat(null);
        }
    }

    public boolean checkMonitorValue() {
        float monitorValue = getMonitorValue();
        float thresholdValue = getThresholdValue();

        if (monitorValue >= thresholdValue) {
            log.error("An incident has been captured... System Load Average has gone over threshold value..");
            return true;
        } else {
            return false;
        }
    }
}
