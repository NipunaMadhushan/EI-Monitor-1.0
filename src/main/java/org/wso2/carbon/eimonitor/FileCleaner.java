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

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileCleaner {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void cleanFiles(String fileLocation){

        try {
            final Stream<Path> walk = Files.walk(Paths.get(fileLocation));
            walk.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
        }
        catch (IOException e) {
            LOGGER.error("Failed to clean the files !!! " + e.getMessage());
        }
    }

    public static void cleanDirectory(String directory) {

        try {
            FileUtils.deleteDirectory(new File(directory));
        }catch (Exception e){
            LOGGER.error("Failed to delete the directory " + directory + e.getMessage());
        }
    }
}
