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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * This class is used to generate a text file which includes the data related to the network load.
 */
public class NetworkLoadGenerator implements DataExtractor {
    private static final Log log = LogFactory.getLog(NetworkLoadGenerator.class);
    private static MBeanServerConnection beanServerConnection;
    private String networkLoadFile = Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY, String.class
            .getName()) + Constants.DirectoryNames.NETWORK_LOAD_FILE_DIRECTORY + "/networkLoad.txt";

    public NetworkLoadGenerator(MBeanServerConnection beanServerConnection) {
        NetworkLoadGenerator.beanServerConnection = beanServerConnection;
    }

    private static final DataExtractor DATA_EXTRACTOR;

    //static block initialization for exception handling
    static {
        try {
            DATA_EXTRACTOR = new NetworkLoadGenerator(beanServerConnection);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static DataExtractor getInstance() {
        return DATA_EXTRACTOR;
    }

    /**
     * This method connects to the JMX connection and read the the data related to the synapse transport.
     * Then the data will be stored into a text file.
     */
    public void extractData() {
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
                    networkLoadFile);
            networkLoadWriter(Calendar.getInstance().getTime() + "  Receiver : " + receiverNetworkLoad,
                    networkLoadFile);

            log.info("Network Load file is generated !!!");

        } catch (MalformedObjectNameException | ReflectionException | IOException | InstanceNotFoundException |
                MBeanException | AttributeNotFoundException e) {
            log.error("Network Load file generation failed !!! " + e.getMessage());
        }
    }

    private void networkLoadWriter(String networkLoad, String fileName) {
        try {
            FileWriter outputStream = new FileWriter(fileName, true);
            outputStream.write(networkLoad);
            outputStream.write("\n");
            outputStream.close();

        } catch (Exception e) {
            log.error("Network Load file generation failed !!! " + e.getMessage());
        }
    }
}
