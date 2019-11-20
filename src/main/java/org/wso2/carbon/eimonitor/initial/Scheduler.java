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
import org.wso2.carbon.eimonitor.JMXConnection;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.data.extractor.HeapDumpGenerator;
import org.wso2.carbon.eimonitor.data.extractor.LogExtractor;
import org.wso2.carbon.eimonitor.data.extractor.NetworkLoadGenerator;
import org.wso2.carbon.eimonitor.data.extractor.ThreadDumpGenerator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServerConnection;

/**
 * This class is used to run the whole process in a thread.
 */
public class Scheduler extends Thread {

    private static final Log log = LogFactory.getLog(Scheduler.class);

    private JMXConnection jmxConnection = new JMXConnection();
    private MBeanServerConnection beanServerConnection = jmxConnection.connectJMX();
    private int monitoringTimePeriod;
    private boolean incidentState = false;

    private DataExtractor heapDumpGenerator = new HeapDumpGenerator();
    private DataExtractor threadDumpGenerator = new ThreadDumpGenerator();
    private DataExtractor networkLoadGenerator = new NetworkLoadGenerator(beanServerConnection);
    private DataExtractor logExtractor = new LogExtractor();

    public Scheduler() {
        Object monitoringTimePeriod = Properties.getProperty(Constants.MONITORING_TIME_PERIOD);
        if (monitoringTimePeriod instanceof Integer) {
            this.monitoringTimePeriod = (int) monitoringTimePeriod;
        } else {
            log.error(Constants.MONITORING_TIME_PERIOD
                    + " property has been defined incorrectly in the property file");
        }
    }

    public void run() {
        if (incidentState) {
            extractData();
        } else {
            ScheduledExecutorService normalRunTimeService = Executors.newSingleThreadScheduledExecutor();
            NormalRunTime normalRunTime = new NormalRunTime(normalRunTimeService);
            normalRunTimeService.scheduleAtFixedRate(normalRunTime, 0, monitoringTimePeriod,
                    TimeUnit.MILLISECONDS);
            ScheduledExecutorService incidentTimeService = Executors.newSingleThreadScheduledExecutor();
            IncidentTime incidentTime = new IncidentTime(incidentTimeService);
            incidentTimeService.scheduleAtFixedRate(incidentTime, 0, 10, TimeUnit.MILLISECONDS);
            incidentState = incidentTime.getIncidentState();
        }
    }

    private void extractData() {
        heapDumpGenerator.generateData();
        threadDumpGenerator.generateData();
        networkLoadGenerator.generateData();
        logExtractor.generateData();
    }

}
