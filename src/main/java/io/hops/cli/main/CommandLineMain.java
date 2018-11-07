package io.hops.cli.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.hops.cli.config.HopsworksAPIConfig;
import io.hops.upload.net.HTTPFileUpload;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * *
 * <p>
 * Resources:
 *  * https://starkandwayne.com/blog/working-with-hdfs/
 *  * https://github.com/Microsoft/hdfs-mount
 * <p>
 * <p>
 */
public class CommandLineMain {

  private static final String JOBS = "jobs";
  private static final String FS = "fs";
  private static final String PROJECTS = "proj";
  private static final String DATASETS = "ds";

  private static final String HADOOP_HOME = "HADOOP_HOME";
  private static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
  private static final String HDFS_USER_NAME = "HDFS_USER_NAME";
  private static final String HOPSWORKS_PROJECT = "HOPSWORKS_PROJECT";
  private static final String HOPSWORKS_EMAIL = "HOPSWORKS_EMAIL";
  private static final String HOPSWORKS_PASSWORD = "HOPSWORKS_PASSWORD";
  private static final String HOPSWORKS_CERT = "HOPSWORKS_CERT";
  private static final String HOPSWORKS_URL = "HOPSWORKS_URL";
  private static final String STAGING_DIR = "STAGING_DIR";

  private static final String path = "/hopsworks-api/api";
  private static final String authPath = "/auth/login";

  /**
   * *
   * These properties come either from environment variables or from
   * properties set in the conf/hops.properties file.
   * Environment variables take precedence over values in hops.properties.
   */
  private static final Properties props = new Properties();
  public static String hadoopHome;
  public static String hadoopConfDir;
  public static String hdfsUsername;
  public static String project;
  public static String email;
  public static String password;
  public static String hopsCert;
  public static String hopsworksUrl;
  public static String stagingDir;
  public static HTTPFileUpload httpFileUpload = null;

  ;
  
  /**
   * There are 2 levels of commands.
   * 1. The top level commands (Args) - fs, jobs
   * 2. Sub-commands for each of the top-level commands (FsArgs, JobsArgs).
   */
  
  public static class Args {

    @Parameter(description = "")
    public List<String> mainArgs;

    @Parameter(names = FS,
        description = "Filesystem commands",
        order = 0)
    public String fsArgs;

//    @Parameter(names = JOBS,
//        description = "Job commands",
//        order = 1)
//    public String jobsArgs;

    @Parameter(names = "-conf",
        description = "Location of hops.properties config file",
        order = 2)
    public String conf = "conf/hops.properties";

    @Parameter(names = "-proj",
        description = "Name of the project",
        order = 3)
    public String project;

    @Parameter(names = "-help",
        description = "Usage of hops-cli",
        help = true,
        order = 4)
    public boolean help;
  }

  public static class JobsArgs {

    @Parameter(names = "submit",
        description = "Submit a python file (.py) or a jar file (.jar) for execution.")
    public String submit;

    @Parameter(names = "remove",
        description = "Remove a job with given id.")
    public int removeJobId;

    @Parameter(names = "logs",
        description = "Download the latest logs for a job with given id.")
    public int logsJobId;

    @Parameter(names = "-name",
        description = "Job ")
    public String jobName = "hops-cli";
    @Parameter(names = "--driver-memory",
        description = "Job ")
    public String driverMemory = "1024M";
    @Parameter(names = "--driver-cores",
        description = "Job ")
    public String driverCores = "1";
    @Parameter(names = "--num-executors",
        description = "")
    public String numExecutors = "1";
    @Parameter(names = "--spark-properties",
        description = "")
    public String SparkProps = "";
    @Parameter(names = "--executor-memory",
        description = "")
    public String executorMemory = "2048M";
    @Parameter(names = "--executor-cores",
        description = "")
    public String executorCores = "4";
    @Parameter(names = "--executor-gpus",
        description = "")
    public String executorGpus = "0";

  }

  public static class FsArgs {

//    @Parameter(names = "-ls",
//        description = "List files/dirs in the given path")
//    public String lsPath;
//
//    @Parameter(names = "-rm",
//        description = "Remove a files in the given path")
//    public String rmPath;
//
//    @Parameter(names = "-mkdir",
//        description = "Make a directory in the given path")
//    public String mkdirPath;

    @Parameter(names = "-copyFromLocal",
        arity = 2,
        description = "Copy a file from the local filesystem to the remote")
    public List<String> copyFromLocal;

//    @Parameter(names = "-copyToLocal",
//        arity = 2,
//        description = "Copy a file from the local filesystem to the remote")
//    public List<String> copyToLocal;
//
//    @Parameter(names = "-copyFromHdfs",
//        arity = 2,
//        description = "Copy a file from a local HDFS/HopsFS cluster to a remote Hops cluster")
//    public List<String> copyFromHdfs;

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
    hadoopHome = System.getenv().get(HADOOP_HOME);
    hadoopConfDir = System.getenv().get(HADOOP_CONF_DIR);
    if (hadoopConfDir == null && hadoopHome != null) {
      hadoopConfDir = hadoopHome + "/etc/hadoop";
    }
    hdfsUsername = System.getenv().get(HDFS_USER_NAME);
    project = System.getenv().get(HOPSWORKS_PROJECT);
    email = System.getenv().get(HOPSWORKS_EMAIL);
    password = System.getenv().get(HOPSWORKS_PASSWORD);
    hopsCert = System.getenv().get(HOPSWORKS_CERT);
    hopsworksUrl = System.getenv().get(HOPSWORKS_URL);
    stagingDir = System.getenv().get(STAGING_DIR);

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
      hadoopHome = CommandLineMain.getProperty(hadoopHome, HADOOP_HOME);
      hadoopConfDir = CommandLineMain.getProperty(hadoopConfDir, HADOOP_CONF_DIR);
      if ((hadoopConfDir == null || hadoopConfDir.isEmpty() == true) && hadoopHome != null) {
        hadoopConfDir = hadoopHome + "/etc/hadoop";
      }
      hdfsUsername = CommandLineMain.getProperty(hdfsUsername, HDFS_USER_NAME);
      project = CommandLineMain.getProperty(project, HOPSWORKS_PROJECT);
      email = CommandLineMain.getProperty(email, HOPSWORKS_EMAIL);
      password = CommandLineMain.getProperty(password, HOPSWORKS_PASSWORD);
      hopsCert = CommandLineMain.getProperty(hopsCert, HOPSWORKS_CERT);
      hopsworksUrl = CommandLineMain.getProperty(hopsworksUrl, HOPSWORKS_URL);
      stagingDir = CommandLineMain.getProperty(stagingDir, STAGING_DIR);
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
    // Only one of the top-level commands should be active
//    if (a.jobsArgs != null && a.fsArgs != null) {
//      jc.usage();
//      System.exit(1);
//    }

    JobsArgs jobsArgs = new JobsArgs();
    FsArgs fsArgs = new FsArgs();

    JCommander jcJob = JCommander.newBuilder()
        .addObject(jobsArgs)
        .build();
    jcJob.setProgramName("hops-cli " + JOBS);

    JCommander jcFs = JCommander.newBuilder()
        .addObject(fsArgs)
        .build();
    jcFs.setProgramName("hops-cli " + FS);

    
    try {
//      if (a.jobsArgs != null) {
//        JCommander jcJobs = JCommander.newBuilder()
//            .addObject(jobsArgs)
//            .build();
//        jcJobs.setProgramName("jobs");
//
//        if (a.mainArgs == null) {
//          jcJob.usage();
//          System.exit(2);
//        }
//        a.mainArgs.add(0, a.jobsArgs);
//        String[] commandArgs = a.mainArgs.toArray(new String[0]);
//        jcJobs.parse(commandArgs);
//
//      } else if (a.fsArgs != null) {
      if (a.fsArgs != null) {
        if (a.mainArgs == null) {
          jcFs.usage();
          System.exit(2);
        }
        a.mainArgs.add(0, a.fsArgs);
        String[] commandArgs = a.mainArgs.toArray(new String[0]);
        jcFs.parse(commandArgs);

        String authPath = "/auth/login";
        URL url = null;
        InetAddress address = null;
        try {
          url = new URL(hopsworksUrl);
          address = InetAddress.getByName(url.getHost());
        } catch (MalformedURLException ex) {
          Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
          jc.usage();
          System.exit(1);
        } catch (UnknownHostException ex) {
          Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
          jc.usage();
          System.exit(1);
        }
        String hostname = address.getHostName();
        int port = url.getPort();

        if (fsArgs.copyFromLocal != null) {
          String relativePath = fsArgs.copyFromLocal.get(0);
          String absolutePath = FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
          String datasetPath = fsArgs.copyFromLocal.get(1);

          httpFileUpload = new HTTPFileUpload("http://" + hostname, port, true, path);
          httpFileUpload.activateAuth(email, password, authPath);
          try {
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig(email, password, hopsworksUrl, project);
            httpFileUpload.uploadFile(absolutePath, datasetPath, hopsworksAPIConfig);
          } catch (IOException ex) {
            Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
            jc.usage();
            System.exit(1);
          } catch (URISyntaxException ex) {
            Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
            jc.usage();
            System.exit(1);
          }
//        } else if (fsArgs.copyFromHdfs != null) {
//          String hdfsPath = fsArgs.copyFromHdfs.get(0);
//          String uploadPath = fsArgs.copyFromHdfs.get(1);

        }

      } else {
        jc.usage();
        System.exit(1);
      }
    } catch (ParameterException ex) {
      jc.usage();
      System.exit(2);
    }

    URI uri = null;
    try {
      uri = new URI(hopsworksUrl);
    } catch (URISyntaxException ex) {
      Logger.getLogger(CommandLineMain.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(-2);
    }

  }

}
