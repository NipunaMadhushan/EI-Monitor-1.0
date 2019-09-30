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

import javax.management.*;
import java.io.*;
import java.util.Calendar;
import static org.wso2.carbon.eimonitor.configurations.configuredvalues.DirectoryNames.*;

public class NetworkLoadGenerator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void getNetworkLoad(MBeanServerConnection beanServerConnection) {
        try {
            ObjectName sndAttrName = new ObjectName("org.apache.synapse:Type=Transport,Name=passthru-http-sender");
            Object sndMsgsSent = beanServerConnection.getAttribute(sndAttrName, "MessagesSent");
            Object sndMsgsReceived = beanServerConnection.getAttribute(sndAttrName, "MessagesReceived");
            Object sndBytesSent = beanServerConnection.getAttribute(sndAttrName, "BytesSent");
            Object sndBytesReceived = beanServerConnection.getAttribute(sndAttrName, "BytesReceived");

            ObjectName rcvAttrName = new ObjectName("org.apache.synapse:Type=Transport,Name=passthru-http-receiver");
            Object rcvMsgsSent = beanServerConnection.getAttribute(rcvAttrName, "MessagesSent");
            Object rcvMsgsReceived = beanServerConnection.getAttribute(rcvAttrName, "MessagesReceived");
            Object rcvBytesSent = beanServerConnection.getAttribute(rcvAttrName, "BytesSent");
            Object rcvBytesReceived = beanServerConnection.getAttribute(rcvAttrName, "BytesReceived");

            String senderNetworkLoad = "MessagesSent=" + sndMsgsSent + " ,MessagesReceived=" +
                        sndMsgsReceived + " ,BytesSent=" + sndBytesSent + " ,BytesReceived=" + sndBytesReceived;
            String receiverNetworkLoad = "MessagesSent=" + rcvMsgsSent + " ,MessagesReceived=" +
                        rcvMsgsReceived + " ,BytesSent=" + rcvBytesSent + " ,BytesReceived=" + rcvBytesReceived;

            //writing the network load into a text file
            networkLoadWriter(Calendar.getInstance().getTime() + "  Sender : " + senderNetworkLoad,
                    NETWORK_LOAD_FILE);
            networkLoadWriter(Calendar.getInstance().getTime() + "  Receiver : " + receiverNetworkLoad,
                    NETWORK_LOAD_FILE);

            LOGGER.info("Network Load file is generated !!!");

        } catch (MalformedObjectNameException | ReflectionException | IOException | InstanceNotFoundException |
                MBeanException | AttributeNotFoundException e){
            LOGGER.error("Network Load file generation failed !!! " + e.getMessage());
        }
    }

    private static void networkLoadWriter(String networkLoad, String fileName){

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
