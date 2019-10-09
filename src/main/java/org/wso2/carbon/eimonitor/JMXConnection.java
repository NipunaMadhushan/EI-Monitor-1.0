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

package org.wso2.carbon.eimonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

public class JMXConnection {

    private static final Log log = LogFactory.getLog(JMXConnection.class);

    private final String URL_STRING = "service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi";

    /**
     * This method returns the JMX connection for the WSO2 EI server
     * @return JMX connection
     */
    public MBeanServerConnection getJMXConnection() {

        MBeanServerConnection mbeanServerConnection = null;

        try {
            JMXServiceURL url = new JMXServiceURL(URL_STRING);

            //Pass credentials for password
            Map<String, String[]> env = new HashMap<>();
            String[] credentials = {"admin", "admin"};
            env.put(JMXConnector.CREDENTIALS, credentials);

            JMXConnector jmxConnector = JMXConnectorFactory.connect(url,env);
            mbeanServerConnection = jmxConnector.getMBeanServerConnection();
            log.info("Successfully connected to the URL :- " + URL_STRING);

        } catch (Exception e) {
            log.error("JMX Connection Failed !!! " + e.getMessage());
        }
        return mbeanServerConnection;
    }
}
