package org.wso2.carbon.eimonitor.initial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class manages the threads.
 */
public class ScheduleManager {

    private static final Log log = LogFactory.getLog(ScheduleManager.class);

    private int monitoringTimePeriod = (int) Properties.getProperty(Constants.TimePeriod.MONITORING_TIME_PERIOD
            , Integer.class.getName());
    private int dataExtractingTimePeriod = (int) Properties.getProperty(Constants.TimePeriod
            .DATA_EXTRACTING_TIME_PERIOD, Integer.class.getName());
    private int incidentHandlerTimePeriod = (int) Properties.getProperty(Constants.TimePeriod
            .INCIDENT_HANDLER_TIME_PERIOD, Integer.class.getName());

    public void startRunTimeHandler() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        RunTimeHandler runTimeHandler = new RunTimeHandler(service);
        service.scheduleAtFixedRate(runTimeHandler, 10, monitoringTimePeriod, TimeUnit.MILLISECONDS);
    }

    public void startIncidentHandler() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        IncidentHandler incidentHandler = new IncidentHandler(service);
        service.scheduleAtFixedRate(incidentHandler, 10, incidentHandlerTimePeriod, TimeUnit.MILLISECONDS);
    }

    public void startDataExtractorHandler() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        DataExtractorHandler dataExtractorHandler = new DataExtractorHandler();
        service.scheduleAtFixedRate(dataExtractorHandler, 10, dataExtractingTimePeriod,
                TimeUnit.MILLISECONDS);
    }
}
