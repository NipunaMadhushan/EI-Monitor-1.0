package org.wso2.carbon.eimonitor.data.extractor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to call all the data extractor classes.
 */
public class DataExtractorFactory {

    public DataExtractorFactory() {}

    private static final DataExtractorFactory DATA_EXTRACTOR_FACTORY;

    //static block initialization for exception handling
    static {
        try {
            DATA_EXTRACTOR_FACTORY = new DataExtractorFactory();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static DataExtractorFactory getInstance() {
        return DATA_EXTRACTOR_FACTORY;
    }

    public List<DataExtractor> getDataExtractors() {

        return new ArrayList<DataExtractor>() {
            {
                add(HeapDumpGenerator.getInstance());
                add(ThreadDumpGenerator.getInstance());
                add(NetworkLoadGenerator.getInstance());
                add(LogExtractor.getInstance());
            }
        };
    }
}
