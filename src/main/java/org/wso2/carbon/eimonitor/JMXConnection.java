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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

public class JMXConnection {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String HOST = "localhost";
    private static final String SERVER_PORT = "11111";
    private static final String REGISTRY_PORT = "9999";
    private static final String URL_STRING = "service:jmx:rmi://" + HOST + ":" + SERVER_PORT + "/jndi/rmi://" + HOST + ":" + REGISTRY_PORT + "/jmxrmi";

    public static MBeanServerConnection getJMXConnection() {

        MBeanServerConnection mbeanServerConnection = null;

        try {
            JMXServiceURL url = new JMXServiceURL(URL_STRING);

            //passing credentials for password
            Map<String, String[]> env = new HashMap<>();
            String[] credentials = {"admin", "admin"};
            env.put(JMXConnector.CREDENTIALS, credentials);

            JMXConnector jmxConnector = JMXConnectorFactory.connect(url,env);
            mbeanServerConnection = jmxConnector.getMBeanServerConnection();
            LOGGER.info("Successfully connected to the URL :- " + URL_STRING);

        } catch (Exception e) {
            LOGGER.error("JMX Connection Failed !!! " + e.getMessage());
        }
        return mbeanServerConnection;
    }
}
