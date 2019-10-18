package org.wso2.carbon.eimonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.incident.handler.IncidentHandler;
import org.wso2.carbon.eimonitor.monitor.Monitor;
import java.util.List;

public class MainThread extends Thread {
    private static final Log log = LogFactory.getLog(MainThread.class);
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean incidentHandlerState = false;
    public static int dataExtractCount = 0;

    private FileCleaner fileCleaner = new FileCleaner();
    private FileGenerator fileGenerator = new FileGenerator();
    private IncidentHandler incidentHandler = new IncidentHandler();

    /**
     * This method includes the main logic of the EI Monitor.
     */
    @Override
    public void run() {
        while(true) {
            //Clean the file directories of data
            fileCleaner.cleanDirectory(Constants.DirectoryNames.BASE_DIRECTORY +"/Data");
            //Generate the file directories of data
            fileGenerator.generateAllDirectories();

            dataExtractCount = 0;
            //Monitor the WSO2 EI server and checking whether there is an incident is happening or not
            while (!incidentHandlerState) {
                //Get monitor details
                List<Float> monitorValues = new Monitor().getMonitorDetails();

                log.debug("Heap Memory Percentage : " + monitorValues.get(0) * 100 +
                        "% , CPU Memory Percentage : " + monitorValues.get(1) * 100 + "% , Load Average : " +
                        monitorValues.get(2) + " , Average Maximum Blocked Time : " + monitorValues.get(3));

                //Check whether there is an incident is happening or not
                incidentHandlerState = incidentHandler.handleAll(monitorValues);

                try {
                    MainThread.sleep(Constants.MONITORING_TIME_PERIOD);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }

            //Check whether that the incident captured is a real issue or not
            boolean issueState = incidentHandler.handleIncidentTimePeriod();

            if (issueState) {
                log.info("An issue has occurred in the system !!!");
                while(true){
                    dataExtractCount += 1;
                    DataExtractor dataExtractor = new DataExtractor();
                    dataExtractor.storeData();
                }
            }
            else {
                log.info("Previous incident is not an issue !!!");
            }

            try {
                MainThread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
