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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.DirectoryNames;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.MonitorConstants;
import org.wso2.carbon.eimonitor.incident.handler.IncidentHandler;
import org.wso2.carbon.eimonitor.monitor.*;
import java.util.*;

public class Activator implements BundleActivator {

    private static BundleContext bundleContext;

    private static final Log log = LogFactory.getLog(Activator.class);
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean incidentHandlerState = false;
    public static int dataExtractCount = 0;

    private DirectoryNames directoryNames = new DirectoryNames();
    private FileCleaner fileCleaner = new FileCleaner();
    private FileGenerator fileGenerator = new FileGenerator();
    private IncidentHandler incidentHandler = new IncidentHandler();
    private MonitorConstants monitorConstants = new MonitorConstants();

    /**
     * This method starts the bundle activator and the EI Monitor.
     * @param bundleContext is equal to the bundle contest of the class
     * @throws InterruptedException
     */
    public void start(BundleContext bundleContext) throws InterruptedException {

        Activator.bundleContext = bundleContext;

        Thread.sleep(20000);

        while (true) {
            //Clean the file directories of data
            fileCleaner.cleanDirectory(directoryNames.BASE_DIRECTORY +"/Data");
            //Generate the file directories of data
            fileGenerator.generateAllDirectories();

            dataExtractCount = 0;
            //Monitor the WSO2 EI server and checking whether there is an incident is happening or not
            while (!incidentHandlerState) {
                //Get monitor details
                List<Float> monitorValues = new Monitor().getMonitorDetails();

                log.info("Heap Memory Percentage : " + monitorValues.get(0) * 100 +
                        "% , CPU Memory Percentage : " + monitorValues.get(1) * 100 + "% , Load Average : " +
                        monitorValues.get(2) + " , Average Maximum Blocked Time : " + monitorValues.get(3));


                //Check whether there is an incident is happening or not
                incidentHandlerState = incidentHandler.handleAll(monitorValues);
                Thread.sleep(monitorConstants.MONITORING_TIME_PERIOD);
            }

            if (incidentHandlerState) {
                //Check whether that the incident captured is a real issue or not
                incidentHandler.handleIncidentTimePeriod();
            }
        }
    }

    /**
     * This method stops the bundle activator and the EI Monitor.
     * @param bundleContext is equal to null
     * @throws InterruptedException
     */
    public void stop(BundleContext bundleContext) throws InterruptedException {
        Activator.bundleContext = null;
    }
}
