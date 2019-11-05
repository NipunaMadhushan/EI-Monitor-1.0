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

package org.wso2.carbon.eimonitor.configurations.configuredvalues;

/**
 * Stores the constants which are configured in the EI_Monitor_Configurations.properties file inside the
 * ${EI_HOME}/conf.
 */
public final class Constants {
    public static final String MONITORING_TIME_PERIOD = "monitoring.time.period";

    /**
     * Stores the constants related to the Data Extractor.
     */
    public static class DataExtractThValues {
        public static final String DATA_EXTRACTING_TIME_PERIOD = "data.extracting.time.period";
    }


    /**
     * Stores the constants related to the Data Extractor.
     * Contains the directory names and the paths where the extracting data should be stored.
     */
    public static class DirectoryNames {
        public static final String BASE_DIRECTORY = "base.directory";
        public static final String HEAP_DUMP_FILE_DIRECTORY = "/Data/Heap Dumps";
        public static final String THREAD_DUMP_FILE_DIRECTORY = "/Data/Thread Dumps";
        public static final String NETWORK_LOAD_FILE_DIRECTORY = "/Data/Network Load";
        public static final String LOG_FILE_DIRECTORY = BASE_DIRECTORY + "/Data/Logs";
        public static final String NETWORK_LOAD_FILE_NAME = "networkLoad.txt";
        public static final String LOG_FILE_NAME = "carbon.log";;
    }

    /**
     * Stores the constants related to the Incident Handler.
     */
    public static class IncidentHandlerThValues {
        public static final String HEAP_RATIO_THRESHOLD = "heap.ratio.threshold";
        public static final String CPU_RATIO_THRESHOLD = "cpu.ratio.threshold";
        public static final String LOAD_AVERAGE_THRESHOLD = "load.average.threshold";
        public static final String BLOCK_TIME_THRESHOLD = "blocked.time.threshold";
        public static final String INCIDENT_TIME_MONITORING_COUNT = "data.extracting.count.threshold";
    }
}
