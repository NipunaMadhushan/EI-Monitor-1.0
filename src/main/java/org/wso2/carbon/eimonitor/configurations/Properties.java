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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class is used to read the configured data in EI_Monitor_Configurations.properties file in the ${EI_HOME}/conf
 * directory.
 */
public final class Properties {

    private static final Log log = LogFactory.getLog(Properties.class);
    private static String baseDirectory = System.getProperty("user.dir");

    /**
     * This method returns the value of the property which has been configured in the
     * EI_Monitor_Configurations.properties file.
     * EI_Monitor_Configurations.properties file should be in the directory of ${carbon.home}/conf .
     * @param key Property name in the EI_Monitor_Configurations.properties which we need
     * @param dataType return data type of the property we need
     * @return propertyValue of the property we want to return
     */
    public static Object getProperty(String key, String dataType) {
        try {
            java.util.Properties properties = new java.util.Properties();
            String fileName = baseDirectory + "/conf/EI_Monitor_Configurations.properties";
            //String fileName = "/home/nipuna/EI-Monitor-1.0/src/main/resources/EI_Monitor_Configurations.properties";

            FileInputStream file = new FileInputStream(fileName);
            properties.load(file);

            String property = properties.getProperty(key);

            if (isInteger(dataType)) {
                return Integer.valueOf(property);
            } else if (isFloat(dataType)) {
                return Float.valueOf(property);
            } else if (isLong(dataType)) {
                return Long.valueOf(property);
            } else {
                return property;
            }
        } catch (IOException e) {
            log.error("Failed to read configurations !!! " + e.getMessage());
            return null;
        } catch (ClassCastException e) {
            log.error(key + " property has been configured incorrectly" + e.getMessage());
            return null;
        }
    }

    private static boolean isFloat(String dataType) {
        return dataType.equals(Float.class.getName());
    }

    private static boolean isInteger(String dataType) {
        return dataType.equals(Integer.class.getName());
    }

    private static boolean isLong(String dataType) {
        return dataType.equals(Long.class.getName());
    }
}
