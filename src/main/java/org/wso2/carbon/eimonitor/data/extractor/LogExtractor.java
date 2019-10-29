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
import org.wso2.carbon.eimonitor.MainThread;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;

/**
 * This class is used to extract logs from the wso2carbon.log file and write the logs into a text file.
 */
class LogExtractor {
    private static final Log log = LogFactory.getLog(LogExtractor.class);

    private Callable<Boolean> tailState = () -> true;
    private CarbonLogTailer carbonLogTailer = new CarbonLogTailer();
    private int sleep = 500;
    private String baseDirectory = System.getProperty("user.dir");
    private File carbonLogFile =
            new File(baseDirectory + "/repository/logs/wso2carbon.log");
    private Tailer tailer = Tailer.create(carbonLogFile, carbonLogTailer, sleep, false);

    /**
     * This method is used to write the log stream we created to a log file in a configurable file directory.
     * @param logStream A string which contains the logs we want to write
     */
    void logWriter(String logStream) {
        try {
            FileWriter outputStream = new FileWriter(Constants.DirectoryNames.LOG_FILE, true);
            outputStream.write(logStream);
            outputStream.write("\n");
            outputStream.close();

        } catch (Exception e) {
            log.error("log file generation failed !!! " + e.getMessage());
        }
    }

    void run() {
        try {
            MainThread.sleep(Constants.DataExtractThresholdValues.DATA_EXTRACTING_TIME_PERIOD);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private Callable<Boolean> tailIsAlive() {
        return tailState;
    }

    String getLogs() {
        return carbonLogTailer.getCarbonLogs();
    }

    void stop() {
        tailer.stop();
        tailState = () -> false;
    }

    /**
     * This class is used to build the string which contains the logs we need to write.
     */
    private class CarbonLogTailer extends TailerListenerAdapter {
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
