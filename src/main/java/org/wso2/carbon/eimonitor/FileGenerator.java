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

package org.wso2.carbon.eimonitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.configurations.Properties;
import org.wso2.carbon.eimonitor.configurations.configuredvalues.Constants;
import java.io.File;
import java.io.IOException;

/**
 * This class is used to create file directories and files.
 */
public class FileGenerator {

    private static final Log log = LogFactory.getLog(FileGenerator.class);

    /**
     * This method generates a new directory in a given file directory.
     * The given file directory can be defined by the user.
     * Directory name can also be defined by the user.
     * @param baseDirectory Directory path where the user want to to create the new directory
     * @param directoryName Name of the directory which the user is going to create
     */
    private void generateDirectory(String baseDirectory, String directoryName) {
        File file = new File(baseDirectory + "/" + directoryName);
        file.mkdir();
    }

    /**
     * This method generates a text file in a given file directory.
     * File directory can be defined by the user.
     * File name can also be defined by the user.
     * @param directory Directory path where the user want to create new text file
     * @param filename Name of the text file which the user is going to create
     */
    private void generateTextFile(String directory, String filename) {
        try {
            String fileDirectory = directory + "/" + filename;
            File file = new File(fileDirectory);
            file.createNewFile();
        } catch (IOException e) {
            log.error("File Generation failed!!! " + e.getMessage());
        }
    }

    /**
     * This method generates all the file directories related to the EI_Monitor.
     */
    public void generateAllDirectories() {
        FileGenerator fileGenerator = new FileGenerator();
        fileGenerator.generateDirectory(Constants.DirectoryNames.BASE_DIRECTORY, "Data");
        fileGenerator.generateDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + "/Data", "Heap Dumps");
        fileGenerator.generateDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + "/Data", "Thread Dumps");
        fileGenerator.generateDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + "/Data", "Network Load");
        fileGenerator.generateDirectory(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + "/Data", "Logs");
        fileGenerator.generateTextFile(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + Constants.DirectoryNames.NETWORK_LOAD_FILE_DIRECTORY, "networkLoad.txt");
        fileGenerator.generateTextFile(Properties.getProperty(Constants.DirectoryNames.BASE_DIRECTORY)
                + Constants.DirectoryNames.NETWORK_LOAD_FILE_DIRECTORY, "carbon.log");
    }
}
