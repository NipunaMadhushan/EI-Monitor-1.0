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

package org.wso2.carbon.eimonitor.initial;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This class can be used to read the configured data in the EI_Monitor_Configurations.properties file in the
 * ${EI_HOME}/conf.
 */
public class Activator implements BundleActivator {

    /**
     * This method starts the bundle activator and the EI Monitor.
     * @param context is equal to the bundle contest of the class
     */
    public void start(BundleContext context) {
        ScheduleManager.getInstance().startRunTimeHandler();
    }

    /**
     * This method stops the bundle activator and the EI Monitor.
     * @param context is equal to null
     */
    public void stop(BundleContext context) {
    }
}
