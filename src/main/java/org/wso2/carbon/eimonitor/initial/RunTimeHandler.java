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
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is used to monitor the EI and capture the incidents.
 */
public class RunTimeHandler extends Thread {
    private static final Log log = LogFactory.getLog(RunTimeHandler.class);

    private ScheduledExecutorService service;

    public RunTimeHandler(ScheduledExecutorService service) {
        this.service = service;

        //Clean the file directories of data
        FileCleaner fileCleaner = new FileCleaner();
        fileCleaner.cleanDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY
                , String.class.getName()) + "/Data");

        //Generate the file directories of data
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.generateAllDirectories();

        try {
            RunTimeHandler.sleep(30000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        log.info("Heap Ratio: " + HeapMemoryMonitor.getInstance().getMonitorValue() + ", CPU Ratio: " +
                CPUMemoryMonitor.getInstance().getMonitorValue() + ", Load Average: " + LoadAverageMonitor.getInstance()
                .getMonitorValue() + ", Avg Max Block Time: " + ThreadStatusMonitor.getInstance().getMonitorValue());

        if (!isMonitorValuesHealthy()) {
            log.error("An incident has been captured!!!");
            ScheduleManager.getInstance().startIncidentHandler();
            service.shutdown();

        }
    }

    private static boolean isMonitorValuesHealthy() {
        boolean state = true;

        List<Monitor> monitors = MonitorFactory.getInstance().getMonitors();

        for (Monitor monitor: monitors) {
            if (monitor.isMonitorValueHealthy()) {
                state = false;
            }
        }

        return state;
    }
}
