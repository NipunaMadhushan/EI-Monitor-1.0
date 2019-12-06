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

package org.wso2.carbon.eimonitor.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class is used to read the System Load Average.
 */
public class LoadAverageMonitor implements Monitor {

    private static final Log log = LogFactory.getLog(LoadAverageMonitor.class);

    private static final Monitor MONITOR;
    //private static final String JSON_FILE_DIRECTORY
    //        = System.getProperty("user.dir") + "/src/main/resources/report/loadAverageData.json";
    private static final String JSON_FILE_DIRECTORY = Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY,
            String.class.getName()) + Constants.DirectoryNames.MONITOR_VALUES_DIRECTORY + "/loadAverageData.json";

    private LoadAverageMonitor(){}

    //static block initialization for exception handling
    static {
        try {
            MONITOR = new LoadAverageMonitor();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static Monitor getInstance() {
        return MONITOR;
    }


    /**
     *This method sets the System Load Average.
     * @return system load average
     */
    public float getMonitorValue() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

        //create the json object from the monitor value with the time and write it to a json file
        JSONObject jsonObject = createJSONObject((float) systemLoadAverage, getThresholdValue());
        writeJSONObject(jsonObject);

        return (float) systemLoadAverage;
    }

    public float getThresholdValue() {
        return (float) Properties.getProperty(Constants.Threshold.LOAD_AVERAGE_THRESHOLD, Float.class.getName());
    }

    public boolean isMonitorValueHealthy() {
        float monitorValue = getMonitorValue();
        float thresholdValue = getThresholdValue();

        if (monitorValue >= thresholdValue) {
            log.error("An incident has been captured... System Load Average has gone over threshold value..");
            return true;
        } else {
            return false;
        }
    }

    private JSONObject createJSONObject(float monitorValue, float thresholdValue) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", timeStamp);
        jsonObject.put("value", monitorValue);
        jsonObject.put("threshold", thresholdValue);

        return jsonObject;
    }

    private void writeJSONObject(JSONObject jsonObject) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader(LoadAverageMonitor.JSON_FILE_DIRECTORY));
            jsonArray.add(jsonObject);
            Files.write(Paths.get(LoadAverageMonitor.JSON_FILE_DIRECTORY), jsonArray.toJSONString().getBytes());
        } catch (ParseException | IOException e) {
            log.error(e.getMessage() + "cannot write the json object to a json file..");
        }
    }
}
