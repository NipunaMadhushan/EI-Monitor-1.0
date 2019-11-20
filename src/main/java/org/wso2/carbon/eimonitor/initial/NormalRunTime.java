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
import org.wso2.carbon.eimonitor.JMXConnection;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.monitor.CPUMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.HeapMemoryMonitor;
import org.wso2.carbon.eimonitor.monitor.LoadAverageMonitor;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import org.wso2.carbon.eimonitor.monitor.ThreadStatusMonitor;
import java.util.concurrent.ScheduledExecutorService;
import javax.management.MBeanServerConnection;

/**
 * This class is used to monitor the EI and capture the incidents.
 */
public class NormalRunTime extends Thread {
    private static final Log log = LogFactory.getLog(NormalRunTime.class);

    private FileCleaner fileCleaner = new FileCleaner();
    private FileGenerator fileGenerator = new FileGenerator();

    private JMXConnection jmxConnection = new JMXConnection();

    private ScheduledExecutorService service;

    public NormalRunTime(ScheduledExecutorService service) {
        this.service = service;
    }

    @Override
    public void run() {
        MBeanServerConnection beanServerConnection = null;
        while (beanServerConnection == null) {
            beanServerConnection = jmxConnection.connectJMX();
            try {
                NormalRunTime.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        //Clean the file directories of data
        fileCleaner.cleanDirectory(Constants.DirectoryNames.BASE_DIRECTORY + "/Data");
        //Generate the file directories of data
        fileGenerator.generateAllDirectories();

        //Set values of each monitor and add the monitor value states to a list
        Monitor heapMemoryMonitor = new HeapMemoryMonitor();
        Monitor cpuMemoryMonitor = new CPUMemoryMonitor();
        Monitor loadAverageMonitor = new LoadAverageMonitor();
        Monitor threadStatusMonitor = new ThreadStatusMonitor();

        boolean heapMemoryMonitorState = heapMemoryMonitor.checkMonitorValue();
        boolean cpuMemoryMonitorState = cpuMemoryMonitor.checkMonitorValue();
        boolean loadAverageMonitorState = loadAverageMonitor.checkMonitorValue();
        boolean threadStatusMonitorState = threadStatusMonitor.checkMonitorValue();

        //check monitor value states whether there is a monitor value has gone over the threshold value
        if (heapMemoryMonitorState | cpuMemoryMonitorState | loadAverageMonitorState | threadStatusMonitorState) {
            log.error("An incident has been captured..");
            service.shutdown();
        }
    }
}
