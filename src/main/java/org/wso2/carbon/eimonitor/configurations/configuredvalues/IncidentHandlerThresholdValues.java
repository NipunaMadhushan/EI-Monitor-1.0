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

package org.wso2.carbon.eimonitor.configurations.configuredvalues;

import org.wso2.carbon.eimonitor.configurations.Configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class provides threshold values for Incident Handler to catch an incident.
 */
public class IncidentHandlerThresholdValues {
    Configurations configurations = new Configurations();
    public final float HEAP_RATIO_THRESHOLD = Float.parseFloat(Objects.requireNonNull(configurations.getProperty("HEAP_RATIO_THRESHOLD")));
    public final float CPU_RATIO_THRESHOLD = Float.parseFloat(Objects.requireNonNull(configurations.getProperty("CPU_RATIO_THRESHOLD")));
    public final float LOAD_AVERAGE_THRESHOLD = Float.parseFloat(Objects.requireNonNull(configurations.getProperty("LOAD_AVERAGE_THRESHOLD")));
    public final float BLOCK_TIME_THRESHOLD = Float.parseFloat(Objects.requireNonNull(configurations.getProperty("BLOCKED_TIME_THRESHOLD")));

    /**
     * This method returns all the threshold values which are needed in the Incident Handler.
     * @return All the threshold values as a list of floats
     */
    public List<Float> getAllThresholdValues() {
        List<Float> thresholdValues = new ArrayList<>();
        thresholdValues.add(HEAP_RATIO_THRESHOLD);
        thresholdValues.add(CPU_RATIO_THRESHOLD);
        thresholdValues.add(LOAD_AVERAGE_THRESHOLD);
        thresholdValues.add(BLOCK_TIME_THRESHOLD);

        return thresholdValues;
    }
}
