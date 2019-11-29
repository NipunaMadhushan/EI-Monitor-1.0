package org.wso2.carbon.eimonitor.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to collect data from all the monitors and check the monitor values.
 */
public class MonitorFactory {

    public MonitorFactory() {}

    private static final MonitorFactory MONITOR_FACTORY;

    //static block initialization for exception handling
    static {
        try {
            MONITOR_FACTORY = new MonitorFactory();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static MonitorFactory getInstance() {
        return MONITOR_FACTORY;
    }

    public List<Monitor> getMonitors() {

        return new ArrayList<Monitor>() {
            {
                add(HeapMemoryMonitor.getInstance());
                add(CPUMemoryMonitor.getInstance());
                add(LoadAverageMonitor.getInstance());
                add(ThreadStatusMonitor.getInstance());
            }
        };
    }
}
