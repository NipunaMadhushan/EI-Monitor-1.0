package org.wso2.carbon.eimonitor.initial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractorFactory;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class extends a thread which extracts data periodically.
 */
public class DataExtractorHandler extends Thread {

    private static final Log log = LogFactory.getLog(DataExtractorHandler.class);

    private ScheduledExecutorService service;

    public DataExtractorHandler() {
        log.info("DataExtractorHandler has been started..");
    }

    @Override
    public void run() {
        List<DataExtractor> dataExtractors = DataExtractorFactory.getInstance().getDataExtractors();
        for (DataExtractor dataExtractor: dataExtractors) {
            dataExtractor.extractData();
        }
        log.info("Data has been extracted successfully..");
    }
}
