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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.management.MBeanServerConnection;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import static org.wso2.carbon.eimonitor.Activator.THREAD_DUMP_FILE_DIRECTORY;

public class ThreadDumpGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void getThreadDump(int number,MBeanServerConnection beanServerConnection){
        try {
            //for virtual machines
            StringBuilder dump = new StringBuilder();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(),1000);

            //for the JMX servers
            //ObjectName name = new ObjectName("java.lang:type=Threading");
            //StringBuilder dump = new StringBuilder();
            //ThreadMXBean threadMXBean = JMX.newMBeanProxy(beanServerConnection,name,ThreadMXBean.class);
            //threadMXBean.setThreadContentionMonitoringEnabled(true);
            //final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(),1000);

            //putting the thread infos into the string builder
            for (ThreadInfo threadInfo : threadInfos){
                dump.append('"');
                dump.append(threadInfo.getThreadName());
                dump.append("\"");

                final Thread.State state = threadInfo.getThreadState();
                dump.append("\n java.lang.Thread.State: ");
                dump.append(state);

                final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

                for (final StackTraceElement stackTraceElement : stackTraceElements){
                    dump.append("\n at ");
                    dump.append(stackTraceElement);
                }

                dump.append("\n\n");
            }

            //writing the thread dump into a text file
            threadDumpWriter(number,dump.toString());

            LOGGER.info("Thread Dump is generated !!!");
        }
        catch (Exception e){
            LOGGER.error("Thread dump generation failed !!! " + e.getMessage());
        }
    }

    private static void threadDumpWriter(int number, String threadDump) {

        String fileName = "ThreadDump-" + number + ".txt";
        String dumpFile = THREAD_DUMP_FILE_DIRECTORY + "/" + fileName;
        try {
            PrintWriter outputStream = new PrintWriter(dumpFile);
            outputStream.println(threadDump);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            LOGGER.error("Thread dump generation failed !!! " + e.getMessage());
        }
    }
}
