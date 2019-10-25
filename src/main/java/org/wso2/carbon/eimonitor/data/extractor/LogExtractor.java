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
import org.awaitility.Awaitility;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to extract logs from the wso2carbon.log file and write the logs into a text file.
 */
class LogExtractor {
    private static final Log log = LogFactory.getLog(LogExtractor.class);

    private Callable<Boolean> tailState = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return true;
        }
    };
    private CarbonLogTailer carbonLogTailer = new CarbonLogTailer();
    private int sleep = 500;
    private File carbonLogFile =
            new File("/home/nipuna/Desktop/wso2ei-6.5.0/repository/logs/wso2carbon.log");
    private Tailer tailer = Tailer.create(carbonLogFile, carbonLogTailer, sleep, true);

    void logWriter(String logStream, String fileName) {
        try {
            FileWriter outputStream = new FileWriter(fileName, true);
            outputStream.write(logStream);
            outputStream.write("\n");
            outputStream.close();

        } catch (Exception e) {
            log.error("log file generation failed !!! " + e.getMessage());
        }
    }

    void run() throws InterruptedException {
        Awaitility.await().pollInterval(10, TimeUnit.MILLISECONDS).
                atMost(5, TimeUnit.SECONDS).until(tailIsAlive());
    }

    private Callable<Boolean> tailIsAlive() {
        return tailState;
    }

    String getLogs() {
        return carbonLogTailer.getCarbonLogs();
    }

    void stop() {
        tailer.stop();
        tailState = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return false;
            }
        };
    }


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

        void clearLogs() {
            this.stringBuilder = new StringBuilder();
        }
    }
}
