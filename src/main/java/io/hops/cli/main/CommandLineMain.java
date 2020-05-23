package io.hops.cli.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.hops.cli.action.*;
import io.hops.cli.config.HopsworksAPIConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * *
 * <p>
 * Resources:
 * * https://starkandwayne.com/blog/working-with-hdfs/
 * * https://github.com/Microsoft/hdfs-mount
 * <p>
 * <p>
 */
public class CommandLineMain {

    private static final String HOPSWORKS_PROJECT = "HOPSWORKS_PROJECT";
    private static final String HOPSWORKS_URL = "HOPSWORKS_URL";
    private static final String HOPSWORKS_APIKEY = "HOPSWORKS_APIKEY";
    /**
     * *
     * These properties come either from environment variables or from
     * properties set in the conf/hops.properties file.
     * Environment variables take precedence over values in hops.properties.
     */
    private static final Properties props = new Properties();
    public static String project;
    public static String hopsworksUrl;
    public static String hopsworksApiKey;

    /**
     * There are 2 levels of commands.
     * 1. The top level commands (Args) - fs, jobs
     * 2. Sub-commands for each of the top-level commands (FsArgs, JobsArgs).
     */

    public static class Args {
        @Parameter(names = "-conf",
                description = "Location of hops.properties config file",
                order = 1)
        public String conf = "conf/hops.properties";

        @Parameter(names = "-proj",
                description = "Name of the project",
                order = 2)
        public String project;

        @Parameter(names = "-help",
                description = "Usage of hops-cli",
                help = true,
                order = 3)
        public boolean help;

        @Parameter(names = "-cp",
                arity = 2,
                description = "Copy a file from the local filesystem to the remote",
                order = 4)
        public List<String> cp;

        @Parameter(names = "-add-job",
                description = "Create a Job with a python file (.py), notebook (.ipynb) or a jar file (.jar).",
                order = 5)
        public String addJob;

        @Parameter(names = "-start-job",
                description = "Run a job with this name",
                order = 6)
        public String execJob;

        @Parameter(names = "-rm-job",
                description = "Remove a job with given name.",
                order = 7)
        public String rmJob;

        @Parameter(names = "-logs-job",
                description = "Download the latest logs for a job with given name.",
                order = 8)
        public String logsJob;

        @Parameter(names = "-stop-job",
                description = "Stops a job with this name and the latest execution id",
                order = 9)
        public String stopJob;


        @Parameter(names = "--user-args",
                description = "Job run arguments",
                order = 10)
        public String userArgs = "";

        /** Job run parameters **/
        @Parameter(names = "--driver-memory",
                description = "Driver memory",
                order = 11)
        public String driverMemory = "1024M";

        @Parameter(names = "--driver-cores",
                description = "Driver number of CPU cores ",
                order = 12)
        public String driverCores = "1";

        @Parameter(names = "--num-executors",
                description = "Number of executors",
                order = 13)
        public String numExecutors = "1";

        @Parameter(names = "--spark-properties",
                description = "Spark conf propertie",
                order = 14)
        public String sparkProps = "";

        @Parameter(names = "--executor-memory",
                description = "Executor memory",
                order = 15)
        public String executorMemory = "2048M";

        @Parameter(names = "--executor-cores",
                description = "Number of CPU cores per executor",
                order = 16)
        public String executorCores = "1";

        @Parameter(names = "--executor-gpus",
                description = "Number of GPUs per executor",
                order = 17)
        public String executorGpus = "0";

    }


    private static String getProperty(String variable, String env) throws Exception {
        if (variable == null) {
            variable = props.getProperty(env);
        }
        return variable;

    }


    public static void main(String[] args) {

        System.setProperty("java.net.preferIPv4Stack", "true");

        // Priority for configuration variables/args (1 is highest, 3 is lowest):
        // 1. Command Line > 2. Environment Variables > 3. conf/hops.properties
        project = System.getenv().get(HOPSWORKS_PROJECT);
        hopsworksUrl = System.getenv().get(HOPSWORKS_URL);
        hopsworksApiKey = System.getenv().get(HOPSWORKS_APIKEY);

        Args a = new Args();
        JCommander jc = JCommander.newBuilder()
                .addObject(a)
                .build();
        jc.setProgramName("hops-cli");

        if (args.length == 0) {
            jc.usage();
            System.exit(0);
        }

        try {
            jc.parse(args);
        } catch (ParameterException ex) {
            jc.usage();
            System.exit(0);

        }

        if (a.help) {
            jc.usage();
            System.exit(1);
        }

        String configFile = a.conf;

        try {
            props.load(new FileInputStream(configFile));
            project = CommandLineMain.getProperty(project, HOPSWORKS_PROJECT);
            hopsworksUrl = CommandLineMain.getProperty(hopsworksUrl, HOPSWORKS_URL);
            hopsworksApiKey = CommandLineMain.getProperty(hopsworksApiKey, HOPSWORKS_APIKEY);
        } catch (IOException ex) {
            System.err.println("Problem reading/parsing the conf/hops.properties file.");
            System.exit(-1);
        } catch (Exception ex) {
            Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        if (a.project != null) {
            project = a.project;
        }

        HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig(hopsworksApiKey, hopsworksUrl, project);

        try {
            HopsworksAction action=null;
            if (a.cp != null) {
                String relativePath = a.cp.get(0);
//          String absolutePath = FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
                String datasetPath = a.cp.get(1);
                action = new FileUploadAction(hopsworksAPIConfig, datasetPath, relativePath);
            } else if (a.addJob != null) {
                action = new JobCreateAction(hopsworksAPIConfig, a.addJob);
            } else if (a.rmJob != null) {
                action = new JobRemoveAction(hopsworksAPIConfig, a.rmJob);
            } else if (a.logsJob != null) {
                action = new JobLogsAction(hopsworksAPIConfig, a.logsJob);
            } else if (a.execJob != null) {
                action = new JobRunAction(hopsworksAPIConfig, a.execJob, a.userArgs);
            } else if (a.stopJob != null) {
                action = new JobStopAction(hopsworksAPIConfig, a.stopJob);
            }
            action.execute();
        } catch (Exception ex) {
            Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
            jc.usage();
            ex.printStackTrace();
            System.exit(1);
        }


    }

}
