package org.wso2.carbon.eimonitor.configurations.configuredvalues;

import org.wso2.carbon.eimonitor.configurations.Configurations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores the constants which are configured in the EI_Monitor_Configurations.properties file inside the
 * ${EI_HOME}/conf.
 */
public final class Constants {
    private static Configurations configurations = new Configurations();

    public static final int MONITORING_TIME_PERIOD =
            Integer.parseInt(Objects.requireNonNull(configurations.getProperty("MONITORING_TIME_PERIOD")));

    /**
     * Stores the constants related to the Data Extractor.
     */
    public static class DataExtractThresholdValues {
        public static final int DATA_EXTRACTING_TIME_PERIOD =
                Integer.parseInt(Objects.requireNonNull(configurations.getProperty("DATA_EXTRACTING_TIME_PERIOD")));
    }

    /**
     * Stores the constants related to the Data Extractor.
     * Contains the directory names and the paths where the extracting data should be stored.
     */
    public static class DirectoryNames {
        public static final String BASE_DIRECTORY =
                Objects.requireNonNull(configurations.getProperty("BASE_DIRECTORY"));
        public static final String HEAP_DUMP_FILE_DIRECTORY = BASE_DIRECTORY + "/Data/Heap Dumps";
        public static final String THREAD_DUMP_FILE_DIRECTORY = BASE_DIRECTORY + "/Data/Thread Dumps";
        public static final String NETWORK_LOAD_FILE_DIRECTORY = BASE_DIRECTORY + "/Data/Network Load";
        public static final String LOG_FILE_DIRECTORY = BASE_DIRECTORY + "/Data/Logs";
        public static final String NETWORK_LOAD_FILE_NAME = "networkLoad.txt";
        public static final String NETWORK_LOAD_FILE = NETWORK_LOAD_FILE_DIRECTORY + "/" + NETWORK_LOAD_FILE_NAME;
        public static final String LOG_FILE_NAME = "carbon.log";
        public static final String LOG_FILE = LOG_FILE_DIRECTORY + "/" + LOG_FILE_NAME;
    }

    /**
     * Stores the constants related to the Incident Handler.
     */
    public static class IncidentHandlerThresholdValues {
        public static final float HEAP_RATIO_THRESHOLD =
                Float.parseFloat(Objects.requireNonNull(configurations.getProperty("HEAP_RATIO_THRESHOLD")));
        public static final float CPU_RATIO_THRESHOLD =
                Float.parseFloat(Objects.requireNonNull(configurations.getProperty("CPU_RATIO_THRESHOLD")));
        public static final float LOAD_AVERAGE_THRESHOLD =
                Float.parseFloat(Objects.requireNonNull(configurations.getProperty("LOAD_AVERAGE_THRESHOLD")));
        public static final float BLOCK_TIME_THRESHOLD =
                Float.parseFloat(Objects.requireNonNull(configurations.getProperty("BLOCKED_TIME_THRESHOLD")));
        public static final int INCIDENT_TIME_MONITORING_COUNT =
                Integer.parseInt(Objects.requireNonNull(configurations.getProperty("DATA_EXTRACTING_COUNT_THRESHOLD")));

        /**
         * This method returns all the threshold values which are needed in the Incident Handler.
         * @return All the threshold values as a list of floats
         */
        public static List<Float> getAllThresholdValues() {
            List<Float> thresholdValues = new ArrayList<>();
            thresholdValues.add(HEAP_RATIO_THRESHOLD);
            thresholdValues.add(CPU_RATIO_THRESHOLD);
            thresholdValues.add(LOAD_AVERAGE_THRESHOLD);
            thresholdValues.add(BLOCK_TIME_THRESHOLD);

            return thresholdValues;
        }
    }
}
