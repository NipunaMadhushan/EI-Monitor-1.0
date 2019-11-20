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

package org.wso2.carbon.eimonitor.data.extractor;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to extract logs from the wso2carbon.log file and write the logs into a text file.
 */
public class LogExtractor implements DataExtractor {
    private static final Log log = LogFactory.getLog(LogExtractor.class);

    private CarbonLogTailer carbonLogTailer = new CarbonLogTailer();
    private int sleep = 500;
    private String baseDirectory = System.getProperty("user.dir");
    private File carbonLogFile = new File(baseDirectory + "/repository/logs/wso2carbon.log");
    private Tailer tailer = Tailer.create(carbonLogFile, carbonLogTailer, sleep, true);
    private String logFile = Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY) + Constants.DirectoryNames.
            LOG_FILE_DIRECTORY + "/carbon.log";
    private int dataExtractingTimePeriod;

    public LogExtractor() {
        Object dataExtractingTimePeriod = Properties.getProperty(Constants.DataExtractThValues.
                DATA_EXTRACTING_TIME_PERIOD);
        if (dataExtractingTimePeriod instanceof Integer) {
            this.dataExtractingTimePeriod = (int) dataExtractingTimePeriod;
        } else {
            log.error(Constants.DataExtractThValues.DATA_EXTRACTING_TIME_PERIOD
                    + " property has been defined incorrectly");
        }
    }

    /**
     * This method is used to write the log stream we created to a log file in a configurable file directory.
     * @param logStream A string which contains the logs we want to write
     */
    private void logWriter(String logStream) {
        try {
            FileWriter outputStream = new FileWriter(logFile, true);
            outputStream.write(logStream);
            outputStream.write("\n");
            outputStream.close();

        } catch (Exception e) {
            log.error("log file generation failed !!! " + e.getMessage());
        }
    }

    /**
     * This method runs the tailer and write the logs into a log file.
     * Then stops the tailer.
     */
    @Override
    public void generateData() {
        run();
        logWriter(getLogs());
        stop();
    }

    private void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(dataExtractingTimePeriod);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private String getLogs() {
        return carbonLogTailer.getCarbonLogs();
    }

    private void stop() {
        tailer.stop();
    }

    /**
     * This class is used to build the string which contains the logs we need to write.
     */
    private static class CarbonLogTailer extends TailerListenerAdapter {
        private StringBuilder stringBuilder;

        private CarbonLogTailer() {
            super();
            stringBuilder = new StringBuilder();
        }

        @Override
        public void handle(String s) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }

        @Override
        public void fileNotFound() {
            log.error("The carbon log file is not present to read logs.");
        }

        @Override
        public void handle(Exception ex) {
            log.error("Exception occurred while reading the logs.", ex);
        }

        String getCarbonLogs() {
            return stringBuilder.toString();
        }
    }
}
