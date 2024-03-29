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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class is used to generate thread dump as a text file.
 */
public class ThreadDumpGenerator implements DataExtractor {

    private static final Log log = LogFactory.getLog(ThreadDumpGenerator.class);
    private String threadDumpFileDirectory = Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY, String
            .class.getName()) + Constants.DirectoryNames.THREAD_DUMP_FILE_DIRECTORY;

    private static final DataExtractor DATA_EXTRACTOR;

    private ThreadDumpGenerator(){}

    //static block initialization for exception handling
    static {
        try {
            DATA_EXTRACTOR = new ThreadDumpGenerator();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static DataExtractor getInstance() {
        return DATA_EXTRACTOR;
    }
    /**
     * This method generates thread dump as string into a string builder.
     */
    public void extractData() {
        try {
            //Get the thread details of each threads
            StringBuilder dump = new StringBuilder();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

            //Write the thread infos into the string builder to create thread dump
            for (ThreadInfo threadInfo : threadInfos) {
                dump.append('"');
                dump.append(threadInfo.getThreadName());
                dump.append("\"");

                final Thread.State state = threadInfo.getThreadState();
                dump.append("\n java.lang.Thread.State: ");
                dump.append(state);

                final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

                for (final StackTraceElement stackTraceElement : stackTraceElements) {
                    dump.append("\n at ");
                    dump.append(stackTraceElement);
                }

                dump.append("\n\n");
            }
            //Write the thread dump into a text file
            threadDumpWriter(dump.toString());
        } catch (Exception e) {
            log.error("Thread dump generation failed !!! " + e.getMessage());
        }
    }

    /**
     * This method writes the thread dump into a text file in the file directory we have configured in the
     * EI_Monitor_Configuration.properties file.
     * @param threadDump Generated thread dump as a string
     */
    private void threadDumpWriter(String threadDump) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String fileName = "ThreadDump-" + timestamp + ".txt";
        String dumpFile = threadDumpFileDirectory + "/" + fileName;
        try {
            PrintWriter outputStream = new PrintWriter(dumpFile);
            outputStream.println(threadDump);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("Thread dump generation failed !!! " + e.getMessage());
        }
    }
}
