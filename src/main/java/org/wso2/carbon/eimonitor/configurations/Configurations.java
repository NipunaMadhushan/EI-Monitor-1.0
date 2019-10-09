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

package org.wso2.carbon.eimonitor.configurations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configurations {

    private static final Logger logger = LogManager.getLogger();
    private final String BASE_DIRECTORY = System.getProperty("user.dir");

    /**
     * This method returns the value of the property which has been configured in the
     * EI_Monitor_Configurations.properties file.
     * EI_Monitor_Configurations.properties file should be in the directory of ${carbon.home}/conf .
     * @param property Property name in the EI_Monitor_Configurations.properties which we need
     * @return propertyValue of the property we want to return
     */
    public String getProperty(String property) {
        try {
            Properties properties = new Properties();
            String fileName = BASE_DIRECTORY + "/conf/EI-Monitor-Configurations.properties";
            FileInputStream file = new FileInputStream(fileName);
            properties.load(file);
            String propertyValue = properties.getProperty(property);

            return propertyValue;

        } catch (IOException e) {
            logger.error("Failed to read configurations !!! " + e.getMessage());
        }
        return null;
    }
}
