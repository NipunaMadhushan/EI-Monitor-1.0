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
import java.lang.management.OperatingSystemMXBean;

/**
 * This class is used to read the System Load Average.
 */
public class LoadAverage {
    private float systemLoadAverage;

    /**
     *This method sets the System Load Average.
     */
    public void setSystemLoadAverage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        Object systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

        this.systemLoadAverage = (float) systemLoadAverage;
    }
    public float getSystemLoadAverage() {
        return systemLoadAverage;
    }
}
