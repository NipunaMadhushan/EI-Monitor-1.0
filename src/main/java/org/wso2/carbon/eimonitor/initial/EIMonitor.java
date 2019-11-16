package org.wso2.carbon.eimonitor.initial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.FileCleaner;
import org.wso2.carbon.eimonitor.FileGenerator;
import org.wso2.carbon.eimonitor.JMXConnection;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.*;
import org.wso2.carbon.eimonitor.incident.handler.HandleIncident;
import org.wso2.carbon.eimonitor.incident.handler.HandleIncidentTimePeriod;
import org.wso2.carbon.eimonitor.incident.handler.IncidentHandler;
import org.wso2.carbon.eimonitor.monitor.*;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.management.MBeanServerConnection;

/**
 * This class extends a thread to run the EI_Monitor.
 * This class combines all the sub work stations and run it in a single thread.
 */
public class EIMonitor extends Thread {
    private static final Log log = LogFactory.getLog(EIMonitor.class);

    private FileCleaner fileCleaner = new FileCleaner();
    private FileGenerator fileGenerator = new FileGenerator();
    private Monitor monitor = new Monitor();

    private JMXConnection jmxConnection = new JMXConnection();

    private ScheduledExecutorService service;

    public EIMonitor(ScheduledExecutorService service) {
        this.service = service;
    }
    /**
     * This method includes the main logic of the EI Monitor.
     */
    @Override
    public void run() {
        MBeanServerConnection beanServerConnection = null;
        while (beanServerConnection == null) {
            beanServerConnection = jmxConnection.connectJMX();
            try {
                EIMonitor.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }

        while (true) {
            //Clean the file directories of data
            fileCleaner.cleanDirectory(Constants.DirectoryNames.BASE_DIRECTORY + "/Data");
            //Generate the file directories of data
            fileGenerator.generateAllDirectories();

            boolean incidentHandlerState = false;

            //Monitor the WSO2 EI server and checking whether there is an incident is happening or not
            while (!incidentHandlerState) {
                //Set and get monitor details
                HeapMemory heapMemory = new HeapMemory();
                CPUMemory cpuMemory = new CPUMemory();
                LoadAverage loadAverage = new LoadAverage();
                ThreadStatus threadStatus = new ThreadStatus();

                monitor.setMonitorValue("Heap Memory Ratio", heapMemory.heapMemoryRatio());
                monitor.setMonitorValue("CPU Memory Ratio", cpuMemory.cpuMemoryRatio());
                monitor.setMonitorValue("System Load Average", loadAverage.systemLoadAverage());
                monitor.setMonitorValue("Avg Max Blocked Time", threadStatus.avgMaxBlockedTime());

                log.info("Heap Memory Ratio : " + monitor.getMonitorValue("Heap Memory Ratio")
                        + " , CPU Memory Ratio : " + monitor.getMonitorValue("CPU Memory Ratio")
                        + " , Load Average : " + monitor.getMonitorValue("System Load Average")
                        + " , Average Maximum Blocked Time : " + monitor.getMonitorValue("Avg Max Blocked Time"));

                //Check whether there is an incident is happening or not
                IncidentHandler incidentHandler = new HandleIncident(monitor);
                incidentHandlerState = incidentHandler.getState();

                try {
                    int monitoringTimePeriod = Integer.parseInt(Objects.requireNonNull(Properties.getProperty(Constants.
                            MONITORING_TIME_PERIOD)));
                    EIMonitor.sleep(monitoringTimePeriod);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }

            //Check whether that the incident captured is a real issue or not
            IncidentHandler incidentHandler = new HandleIncidentTimePeriod(beanServerConnection);
            boolean issueState = incidentHandler.getState();

            if (issueState) {
                log.info("An issue has occurred in the system !!!");
                break;
            } else {
                log.info("Previous incident is not an issue !!!");
            }
        }
        //Extract the data after identifying an issue has occurred.
        while (true) {
            DataExtractor dataExtractor = new HeapDumpGenerator();
            dataExtractor.generateData();
            dataExtractor = new ThreadDumpGenerator();
            dataExtractor.generateData();
            dataExtractor = new NetworkLoadGenerator(beanServerConnection);
            dataExtractor.generateData();
            dataExtractor = new LogExtractor();
            dataExtractor.generateData();
        }
    }
}
