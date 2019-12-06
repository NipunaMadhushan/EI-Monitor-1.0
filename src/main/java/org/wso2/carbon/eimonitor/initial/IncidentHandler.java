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
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractorFactory;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import org.wso2.carbon.eimonitor.monitor.MonitorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is used to check whether the captured incident is a real issue or not.
 */
public class IncidentHandler extends Thread {

    private static final Log log = LogFactory.getLog(IncidentHandler.class);

    private int incidentTimeCountThreshold = (int) Properties.getProperty(Constants.TimePeriod
            .INCIDENT_TIME_MONITORING_COUNT, Integer.class.getName());

    private List<Monitor> monitors = MonitorFactory.getInstance().getMonitors();

    private List<Float> avgMonitorValues = new ArrayList<Float>() {
        {
            for (Monitor monitor: monitors) {
                add(monitor.getMonitorValue());
            }
        }
    };

    private int monitoringCount = 0;
    private ScheduledExecutorService service;

    public IncidentHandler(ScheduledExecutorService service) {
        log.info("IncidentHandler has been started..");
        this.service = service;
    }

    @Override
    public void run() {
        try {
            List<Float> newAvgMonitorValues = new ArrayList<>();
            for (int i = 0; i < monitors.size(); i++) {
                float avgMonitorValue = calculateAvgMonitorValue(avgMonitorValues.get(i), monitors.get(i)
                        .getMonitorValue());
                newAvgMonitorValues.add(avgMonitorValue);
            }
            this.avgMonitorValues = newAvgMonitorValues;

            extractData();

            this.monitoringCount += 1;
            if (this.monitoringCount >= incidentTimeCountThreshold) {

                if (isIncidentTimePeriodHealthy()) {
                    log.info("Previous captured incident is not an issue..");
                    ScheduleManager scheduleManager = new ScheduleManager();
                    scheduleManager.startRunTimeHandler();
                } else {
                    log.warn("Previous captured incident is an issue..");
                    ScheduleManager scheduleManager = new ScheduleManager();
                    scheduleManager.startDataExtractorHandler();
                }
                service.shutdown();
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    private float calculateAvgMonitorValue(float avgMonitorValue, float newMonitorValue) {
        return (avgMonitorValue + newMonitorValue) / 2;
    }

    private boolean isIncidentTimePeriodHealthy() {
        boolean state = true;
        for (int i = 0; i < monitors.size(); i++) {
            if (avgMonitorValues.get(i) > monitors.get(i).getThresholdValue()) {
                state = false;
            }
        }

        return state;
    }

    private boolean checkAvgMonitorValueState(float avgMonitorValue, float thresholdValue) {
        try {
            return avgMonitorValue >= thresholdValue;
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return Boolean.parseBoolean(null);
        }
    }

    private void extractData() {
        List<DataExtractor> dataExtractors = DataExtractorFactory.getInstance().getDataExtractors();

        for (DataExtractor dataExtractor: dataExtractors) {
            dataExtractor.extractData();
        }
        log.info("Data has been extracted successfully");
    }
}
