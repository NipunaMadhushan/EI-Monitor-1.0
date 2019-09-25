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
import javax.management.ObjectName;
import java.io.*;
import java.util.Calendar;
import static org.wso2.carbon.eimonitor.Activator.*;

public class NetworkLoadGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void getNetworkLoad(MBeanServerConnection beanServerConnection){
        try {
            //for JMX connections
            ObjectName sendAttributeName = new ObjectName("org.apache.synapse:Type=Transport,Name=passthru-http-sender");
            Object senderMessagesSent = beanServerConnection.getAttribute(sendAttributeName, "MessagesSent");
            Object senderMessagesReceived = beanServerConnection.getAttribute(sendAttributeName, "MessagesReceived");
            Object senderBytesSent = beanServerConnection.getAttribute(sendAttributeName, "BytesSent");
            Object senderBytesReceived = beanServerConnection.getAttribute(sendAttributeName, "BytesReceived");

            ObjectName receivedAttributeName = new ObjectName("org.apache.synapse:Type=Transport,Name=passthru-http-receiver");
            Object receiverMessagesSent = beanServerConnection.getAttribute(receivedAttributeName, "MessagesSent");
            Object receiverMessagesReceived = beanServerConnection.getAttribute(receivedAttributeName, "MessagesReceived");
            Object receiverBytesSent = beanServerConnection.getAttribute(receivedAttributeName, "BytesSent");
            Object receiverBytesReceived = beanServerConnection.getAttribute(receivedAttributeName, "BytesReceived");

            String senderNetworkLoad = "Sender : MessagesSent=" + senderMessagesSent.toString() + " ,MessagesReceived=" +
                        senderMessagesReceived.toString() + " ,BytesSent=" + senderBytesSent + " ,BytesReceived=" + senderBytesReceived;
            String receiverNetworkLoad = "Receiver : MessagesSent=" + receiverMessagesSent.toString() + " ,MessagesReceived=" +
                        receiverMessagesReceived.toString() + " ,BytesSent=" + receiverBytesSent + " ,BytesReceived=" + receiverBytesReceived;

            //writing the network load into a text file
            networkLoadWriter(Calendar.getInstance().getTime() + "  " + senderNetworkLoad, NETWORK_LOAD_FILE_DIRECTORY +"/"+ NETWORK_LOAD_FILE_NAME);
            networkLoadWriter(Calendar.getInstance().getTime() + "  " + receiverNetworkLoad, NETWORK_LOAD_FILE_DIRECTORY +"/"+ NETWORK_LOAD_FILE_NAME);

            LOGGER.info("Network Load file is generated !!!");

        }catch (Exception e){
            LOGGER.error("Network Load file generation failed !!! " + e.getMessage());
        }
    }
    private static void networkLoadWriter(String networkLoad,String fileName){

        try {
            FileWriter outputStream = new FileWriter(fileName,true);
            outputStream.write(networkLoad);
            outputStream.write("\n");
            outputStream.close();

        } catch (Exception e) {
            LOGGER.error("Network Load file generation failed !!! " + e.getMessage());
        }
    }
}
