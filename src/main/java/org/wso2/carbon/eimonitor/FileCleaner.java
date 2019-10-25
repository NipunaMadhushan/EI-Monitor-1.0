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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * This class is used to clean files or a file directory.
 */
public class FileCleaner {

    private static final Log log = LogFactory.getLog(FileCleaner.class);

    /**
     * This method deletes all the files in a given file location but not the directory.
     * Folder will remain as an empty folder.
     * @param fileLocation File location where the files should be deleted
     */
    public void cleanFiles(String fileLocation) {
        try {
            final Stream<Path> walk = Files.walk(Paths.get(fileLocation));
            walk.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            log.error("Failed to clean the files !!! " + e.getMessage());
        }
    }

    /**
     * This method deletes the whole directory including the sub folders and the files inside the directory.
     * @param directory Directory which should be deleted
     */
    public void cleanDirectory(String directory) {
        try {
            FileUtils.deleteDirectory(new File(directory));
        } catch (IOException e) {
            log.error("Failed to delete the directory " + directory + e.getMessage());
        }
    }
}
