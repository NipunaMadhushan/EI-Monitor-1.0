package org.wso2.carbon.eimonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.incident.handler.IncidentHandler;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class extends a thread to run the EI_Monitor.
 * This class combines all the sub work stations and run it in a single thread.
 */
public class MainThread extends Thread {
    private static final Log log = LogFactory.getLog(MainThread.class);

    private FileCleaner fileCleaner = new FileCleaner();
    private FileGenerator fileGenerator = new FileGenerator();
    private Monitor monitor = new Monitor();

    /**
     * This method includes the main logic of the EI Monitor.
     */
    @Override
    public void run() {
        try {
            MainThread.sleep(60000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        int dataExtractCount;
        while (true) {
            //Clean the file directories of data
            fileCleaner.cleanDirectory(Constants.DirectoryNames.BASE_DIRECTORY + "/Data");
            //Generate the file directories of data
            fileGenerator.generateAllDirectories();

            dataExtractCount = 0;
            boolean incidentHandlerState = false;
            IncidentHandler incidentHandler = new IncidentHandler(dataExtractCount);

            //Monitor the WSO2 EI server and checking whether there is an incident is happening or not
            while (!incidentHandlerState) {
                //Get monitor details
                monitor.setMonitorValues();
                HashMap monitorValues = monitor.getMonitorValues();
                log.info("Heap Memory Ratio : " + monitorValues.get("Heap Memory Ratio") + " , CPU Memory Ratio : "
                        + monitorValues.get("CPU Memory Ratio") + " , Load Average : " + monitorValues.
                        get("System Load Average") + " , Average Maximum Blocked Time : "
                        + monitorValues.get("Avg Max Blocked Time"));

                //Check whether there is an incident is happening or not
                incidentHandler.handleIncident(monitorValues);
                incidentHandlerState = incidentHandler.getIncidentState();

                try {
                    int monitoringTimePeriod = Integer.parseInt(Objects.requireNonNull(Properties.getProperty(Constants.
                            MONITORING_TIME_PERIOD)));
                    MainThread.sleep(monitoringTimePeriod);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }

            //Check whether that the incident captured is a real issue or not
            boolean issueState = incidentHandler.handleIncidentTimePeriod(monitor);
            int incidentTimeMonitoringCount = Integer.parseInt(Objects.requireNonNull(Properties.getProperty(Constants.
                    IncidentHandlerThValues.INCIDENT_TIME_MONITORING_COUNT)));
            dataExtractCount += incidentTimeMonitoringCount;
            if (issueState) {
                log.info("An issue has occurred in the system !!!");
                break;
            } else {
                log.info("Previous incident is not an issue !!!");
            }
        }
        //Extract the data after identifying an issue has occurred.
        while (true) {
            dataExtractCount += 1;
            DataExtractor dataExtractor = new DataExtractor(dataExtractCount);
            dataExtractor.storeData();
        }
    }
}
