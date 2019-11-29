package org.wso2.carbon.eimonitor.initial;

import org.wso2.carbon.eimonitor.data.extractor.DataExtractor;
import org.wso2.carbon.eimonitor.data.extractor.DataExtractorFactory;
import java.util.List;

/**
 * This class extends a thread which extracts data periodically.
 */
public class DataExtractorHandler extends Thread {

    private List<DataExtractor> dataExtractors = DataExtractorFactory.getInstance().getDataExtractors();

    @Override
    public void run() {
        for (DataExtractor dataExtractor: dataExtractors) {
            dataExtractor.extractData();
        }
    }
}
