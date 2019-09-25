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
import org.wso2.carbon.eimonitor.monitor.CpuMemory;
import org.wso2.carbon.eimonitor.monitor.HeapMemory;
import org.wso2.carbon.eimonitor.monitor.LoadAverage;
import org.wso2.carbon.eimonitor.monitor.ThreadStatus;
import javax.management.MBeanServerConnection;
import java.util.*;

public class Activator implements BundleActivator{

    static BundleContext bundleContext;

    private static final Logger LOGGER = LogManager.getLogger();

    //define the configurations
    private static final int MONITORING_TIME_PERIOD = Integer.parseInt(Objects.requireNonNull(Configurations.getProperty("MONITORING_TIME_PERIOD")));
    public static final int DATA_EXTRACTING_TIME_PERIOD = Integer.parseInt(Objects.requireNonNull(Configurations.getProperty("DATA_EXTRACTING_TIME_PERIOD")));
    public static final int DATA_EXTRACTING_COUNT_THRESHOLD = Integer.parseInt(Objects.requireNonNull(Configurations.getProperty("DATA_EXTRACTING_COUNT_THRESHOLD")));
    public static final float HEAP_RATIO_THRESHOLD = Float.parseFloat(Objects.requireNonNull(Configurations.getProperty("HEAP_RATIO_THRESHOLD")));
    public static final float CPU_RATIO_THRESHOLD = Float.parseFloat(Objects.requireNonNull(Configurations.getProperty("CPU_RATIO_THRESHOLD")));
    public static final float LOAD_AVERAGE_THRESHOLD = Float.parseFloat(Objects.requireNonNull(Configurations.getProperty("LOAD_AVERAGE_THRESHOLD")));
    public static final float BLOCK_TIME_THRESHOLD = Float.parseFloat(Objects.requireNonNull(Configurations.getProperty("BLOCKED_TIME_THRESHOLD")));

    //connect to the JMX connector of WSO2 EI
    public static final MBeanServerConnection BEAN_SERVER_CONNECTION = JMXConnection.getJMXConnection();

    //define the directories
    public static final String DATABASE_DIRECTORY = Objects.requireNonNull(Configurations.getProperty("BASE_DIRECTORY"));
    public static final String HEAP_DUMP_FILE_DIRECTORY = DATABASE_DIRECTORY + "/Data/Heap Dumps";
    public static final String THREAD_DUMP_FILE_DIRECTORY = DATABASE_DIRECTORY + "/Data/Thread Dumps";
    public static final String NETWORK_LOAD_FILE_DIRECTORY = DATABASE_DIRECTORY + "/Data/Network Load";
    public static final String NETWORK_LOAD_FILE_NAME = "networkLoad.txt";

    public static final long START_TIME = System.currentTimeMillis();

    public static boolean incidentHandlerState = false;
    public static int dataExtractCount = 0;

    public void start(BundleContext bundleContext) throws Exception {

        Activator.bundleContext = bundleContext;

        while (true) {

            //cleaning the file directories of data
            FileCleaner.cleanDirectory(DATABASE_DIRECTORY +"/Data");

            //generating the file directories of data
            FileGenerator.generateDirectory(DATABASE_DIRECTORY +"/Data");
            FileGenerator.generateDirectory(HEAP_DUMP_FILE_DIRECTORY);
            FileGenerator.generateDirectory(THREAD_DUMP_FILE_DIRECTORY);
            FileGenerator.generateDirectory(NETWORK_LOAD_FILE_DIRECTORY);
            FileGenerator.generateFile(NETWORK_LOAD_FILE_DIRECTORY, NETWORK_LOAD_FILE_NAME);

            dataExtractCount = 0;

            while (!incidentHandlerState) {

                //get the monitoring details
                long currentTime = System.currentTimeMillis();
                float heapMemoryRatio = HeapMemory.getHeapMemoryUsage(BEAN_SERVER_CONNECTION);
                float cpuMemoryRatio = CpuMemory.getCpuMemoryUsage(BEAN_SERVER_CONNECTION);
                float loadAverage = (float) LoadAverage.getSystemLoadAverage(BEAN_SERVER_CONNECTION);
                int maxBlockedTime = ThreadStatus.getThreadStatusDetails();
                float averageMaxBlockedTime = (float) maxBlockedTime / (float) (currentTime - START_TIME);

                //LOGGER.info("Heap Memory Percentage : " + heapMemoryRatio * 100 + "% , CPU Memory Percentage : " + cpuMemoryRatio * 100 + "% , Load Average : "
                //        + loadAverage + " , Average Maximum Blocked Time : " + averageMaxBlockedTime);

                //adding the threshold values to list
                List<Float> thresholdValues = new ArrayList<>();
                thresholdValues.add(HEAP_RATIO_THRESHOLD);
                thresholdValues.add(CPU_RATIO_THRESHOLD);
                thresholdValues.add(LOAD_AVERAGE_THRESHOLD);
                thresholdValues.add(BLOCK_TIME_THRESHOLD);

                //adding the monitoring values to a list
                List<Float> monitorValues = new ArrayList<>();
                monitorValues.add(heapMemoryRatio);
                monitorValues.add(cpuMemoryRatio);
                monitorValues.add(loadAverage);
                monitorValues.add(averageMaxBlockedTime);

                //check whether there is an incident is happening or not
                incidentHandlerState = IncidentHandler.handleAll(thresholdValues, monitorValues);

                Thread.sleep(MONITORING_TIME_PERIOD);
            }

            if (incidentHandlerState) {
                //check whether that the incident captured is a real issue or not
                IncidentHandler.handleIncidentTimePeriod();
            }
        }
    }

    public void stop(BundleContext bundleContext) throws Exception {
        Activator.bundleContext = null;
    }
}
