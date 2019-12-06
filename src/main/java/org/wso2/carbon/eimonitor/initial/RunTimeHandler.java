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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.FileCleaner;
import org.wso2.carbon.eimonitor.FileGenerator;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.monitor.CPUMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.HeapMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.LoadAverageMonitor;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import org.wso2.carbon.eimonitor.monitor.MonitorFactory;
import org.wso2.carbon.eimonitor.monitor.ThreadStatusMonitor;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is used to monitor the EI and capture the incidents.
 */
public class RunTimeHandler extends Thread {
    private static final Log log = LogFactory.getLog(RunTimeHandler.class);

    private ScheduledExecutorService service;

    public RunTimeHandler(ScheduledExecutorService service) {
        log.info("RunTimeHandler has been started..");
        this.service = service;

        deleteAndGenerateDirectories();

        try {
            RunTimeHandler.sleep(15000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        log.info("Heap Memory Ratio: " + HeapMemoryMonitor.getInstance().getMonitorValue() + ", CPU Usage: " +
                CPUMemoryMonitor.getInstance().getMonitorValue() + ", Load Average: " + LoadAverageMonitor.getInstance()
                .getMonitorValue() + ", Avg Max Block Time: " + ThreadStatusMonitor.getInstance().getMonitorValue());

        if (!isMonitorValuesHealthy()) {
            log.error("An incident has been captured!!!");
            ScheduleManager scheduleManager = new ScheduleManager();
            scheduleManager.startIncidentHandler();
            service.shutdown();
        }
    }

    /**
     * This method checks each monitor value whether it has gone over the threshold value.
     * @return The output message as a boolean whether the monitor values are normal or has gone over the threshold
     * values.
     */
    private boolean isMonitorValuesHealthy() {
        boolean state = true;

        List<Monitor> monitors = MonitorFactory.getInstance().getMonitors();

        for (Monitor monitor: monitors) {
            if (monitor.isMonitorValueHealthy()) {
                state = false;
            }
        }

        return state;
    }

    /**
     * This method deletes the previous directory where the data had been stored and create a new directory at the same
     * file path.
     */
    private void deleteAndGenerateDirectories() {
        //Clean the file directories of data
        FileCleaner fileCleaner = new FileCleaner();
        fileCleaner.cleanDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + "/Data");

        //Generate the file directories of data
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.generateAllDirectories();
        fileGenerator.writeEmptyJSONArray(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + Constants.DirectoryNames.MONITOR_VALUES_DIRECTORY + "/heapMemoryData.json");
        fileGenerator.writeEmptyJSONArray(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + Constants.DirectoryNames.MONITOR_VALUES_DIRECTORY + "/cpuMemoryData.json");
        fileGenerator.writeEmptyJSONArray(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + Constants.DirectoryNames.MONITOR_VALUES_DIRECTORY
                + "/loadAverageData.json");
        fileGenerator.writeEmptyJSONArray(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + Constants.DirectoryNames.MONITOR_VALUES_DIRECTORY
                + "/threadStatusData.json");

        //Copy the report files to the folder where the data is being extracted
        File source = new File(System.getProperty("user.dir") + "/wso2/report");
        File dest = new File(Objects.requireNonNull(Properties.getProperty(Constants.DirectoryNames
                .BASE_DIRECTORY, String.class.getName())) + "/Data");
        fileGenerator.copyFileDirectory(source, dest);
    }
}

