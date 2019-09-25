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
import java.io.FileInputStream;
import java.util.Properties;

public class Configurations {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String BASE_DIRECTORY = System.getProperty("user.dir");

    public static String getProperty(String property) {

        try {
            Properties properties = new Properties();
            FileInputStream file = new FileInputStream(BASE_DIRECTORY + "/conf/EI_Monitor_Configurations.properties");
            properties.load(file);
            return properties.getProperty(property);

        }catch (Exception e){
            LOGGER.error("Failed to read configurations !!! " + e.getMessage());
        }
        return null;
    }
}
