package org.wso2.carbon.eimonitor.initial;

import org.wso2.carbon.eimonitor.JMXConnection;

import javax.management.MBeanServerConnection;

public class IncidentTime {
    private JMXConnection jmxConnection = new JMXConnection();
    private MBeanServerConnection beanServerConnection = jmxConnection.connectJMX();

    public void run() {

    }
}
