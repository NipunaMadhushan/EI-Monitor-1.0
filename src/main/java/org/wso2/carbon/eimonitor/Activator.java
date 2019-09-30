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

package org.wso2.carbon.eimonitor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.wso2.carbon.eimonitor.incident.handler.IncidentHandler;
import org.wso2.carbon.eimonitor.monitor.*;
import java.util.*;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.MonitorConstants.MONITORING_TIME_PERIOD;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.DirectoryNames.*;

public class Activator implements BundleActivator {

    static BundleContext bundleContext;

    private static final Logger LOGGER = LogManager.getLogger();
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean incidentHandlerState = false;
    public static int dataExtractCount = 0;

    /**
     * This method starts the bundle activator and the EI Monitor.
     * @param bundleContext is equal to the bundle contest of the class
     * @throws Exception
     */
    public void start(BundleContext bundleContext) throws Exception {

        Activator.bundleContext = bundleContext;

        Thread.sleep(20000);

        while (true) {
            //cleaning the file directories of data
            FileCleaner.cleanDirectory(BASE_DIRECTORY +"/Data");
            //generating the file directories of data
            FileGenerator.generateAllDirectories();

            dataExtractCount = 0;

            //monitoring the WSO2 EI server and checking whether there is an incident is happening or not
            while (!incidentHandlerState) {
                //getting monitor details
                List<Float> monitorValues = Monitor.getMonitorDetails();

                LOGGER.info("Heap Memory Percentage : " + monitorValues.get(0) * 100 +
                        "% , CPU Memory Percentage : " + monitorValues.get(1) * 100 + "% , Load Average : " +
                        monitorValues.get(2) + " , Average Maximum Blocked Time : " + monitorValues.get(3));


                //check whether there is an incident is happening or not
                incidentHandlerState = IncidentHandler.handleAll(monitorValues);
                Thread.sleep(MONITORING_TIME_PERIOD);
            }

            if (incidentHandlerState) {
                //check whether that the incident captured is a real issue or not
                IncidentHandler.handleIncidentTimePeriod();
            }
        }
    }

    /**
     * This methos stops the bundle activator and the EI Monitor.
     * @param bundleContext is equal to null
     * @throws Exception
     */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.bundleContext = null;
    }
}
