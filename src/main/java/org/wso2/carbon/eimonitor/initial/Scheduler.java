package org.wso2.carbon.eimonitor.initial;

import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler extends Thread {
    private int monitoringTimePeriod = Integer.parseInt(Objects.requireNonNull(Properties.getProperty(Constants.
            MONITORING_TIME_PERIOD)));

    public void run() {
        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor();
        Thread normalRunTime = new NormalRunTime(service1);
        service1.scheduleAtFixedRate(normalRunTime, 0, monitoringTimePeriod, TimeUnit.MILLISECONDS);
        if (service1.isShutdown()) {
            ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor();
        }

    }
}
