#EI Monitor

##Introduction

EI Monitor is a tool that can be used to monitor an instance of Enterprise Integrator(EI) and find the reason for an issue occur in the EI.
When an issue occurs in a production server, to investigate the incident, we need to fetch several information from the server instance. 
At the moment we need to extract this information manually and sometimes we may not get the required information before the server goes to a catastrophic situation. 
This tool automatically extracts useful information from the server when there are potential issues. 

EI monitor has mainly 8 components.

    1)Configuration File
    2)Configuration Parser
    3)Monitor(Heap Memory Usage, CPU Memory Usage, System Load Average, Thread Status)
    4)Incident Handler
    5)Data Extractor(Heap Dump Generator, Thread Dump Generator, Network Load Generator, Log Extractor)
    6)Analyzer
    7)Report Generator
    8)report

##Build the project

EI Monitor has been written as a maven project. Use "mvn clean install" command to build the osgi bundle. 
Then **EI-Monitor_1.0.0.jar** file will be created in the "target" folder.

##Run the project

Download WSO2 Enterprise Integrator zip file by using the link https://wso2.com/integration/ and extract it to a directory(**<EI_HOME>**).
Copy the EI-Monitor_1.0.0.jar file to the **<EI_HOME>/dropins** directory.
Go to the **<EI_HOME>/bin** directory and run the following command line on the terminal.
    For Windows - integrator.bat
    For Linux - sh.integrator.sh
    
##Setting property values

Many options can be customized through changing values in **configurations.properties** file.
    
1)HEAP_RATIO_THRESHOLD = set the threshold value for ratio of heap memory to identify as an incident.
                (E.g. : **HEAP_RATIO_THRESHOLD=0.5** EI Monitor will catch an incident when heap memory ratio goes over 0.5)
2)CPU_RATIO_THRESHOLD = set the threshold value for ratio of CPU memory to identify as an incident.
3)LOAD_AVERAGE_THRESHOLD = set the threshold value for system load average to identify as an incident.
4)BLOCKED_TIME_THRESHOLD = set the threshold value for average maximum blocked time in threads to identify as an incident.
5)MONITORING_TIME_PERIOD = set the period value to monitor the EI. Value is taken in milliseconds.
                (E.g. : **MONITORING_TIME_PERIOD=1000** Monitor the EI for every second)
6)DATA_EXTRACTING_TIME_PERIOD = set the period value to extract data. Value is taken in milliseconds.
                (E.g. : **DATA_EXTRACTING_TIME_PERIOD=1000** Extract the data for every second when there is an incident which has occured) 
7)DATA_EXTRACTING_COUNT_THRESHOLD = set the threshold value to identify the incident is a real issue or not.
                (E.g. : **DATA_EXTRACTING_COUNT_THRESHOLD=50** will monitor the EI for 50 times and identify the incident which has occured is a real issue or not) 
8)BASE_DIRECTORY = set the directory path to store the the data extracted by the Data Extractor.
                (E.g. : **BASE_DIRECTORY=/home/nipuna/Desktop** Data will be saved in the /home/nipuna/Desktop directory in a folder named **Data**) 
