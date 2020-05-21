package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class JobCreateAction extends JobAction {


  @XmlRootElement
  public static class Args {
    private int numExecutors=1;
    private int cpusPerExecutor=1;
    private int cpusPerDriver=1;
    private int driverMemInMbs = 2048;
    private int executorMemInMbs = 2048;
    private int gpusPerExecutor = 0;
    private String commandArgs = "";
    private String jvmArgs = "";
    private String[] files = {};
    private String[] jars = {};
    private String[] archives = {};
    private String[] sparkConf = {};
    public Args() {}

    public int getNumExecutors() {
      return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
      this.numExecutors = numExecutors;
    }

    public int getCpusPerExecutor() {
      return cpusPerExecutor;
    }

    public void setCpusPerExecutor(int cpusPerExecutor) {
      this.cpusPerExecutor = cpusPerExecutor;
    }

    public int getCpusPerDriver() {
      return cpusPerDriver;
    }

    public void setCpusPerDriver(int cpusPerDriver) {
      this.cpusPerDriver = cpusPerDriver;
    }

    public int getDriverMemInMbs() {
      return driverMemInMbs;
    }

    public void setDriverMemInMbs(int driverMemInMbs) {
      this.driverMemInMbs = driverMemInMbs;
    }

    public int getExecutorMemInMbs() {
      return executorMemInMbs;
    }

    public void setExecutorMemInMbs(int executorMemInMbs) {
      this.executorMemInMbs = executorMemInMbs;
    }

    public int getGpusPerExecutor() {
      return gpusPerExecutor;
    }

    public void setGpusPerExecutor(int gpusPerExecutor) {
      this.gpusPerExecutor = gpusPerExecutor;
    }

    public String getCommandArgs() {
      return commandArgs;
    }

    public void setCommandArgs(String commandArgs) {
      this.commandArgs = commandArgs;
    }

    public String getJvmArgs() {
      return jvmArgs;
    }

    public void setJvmArgs(String jvmArgs) {
      this.jvmArgs = jvmArgs;
    }

    public String[] getFiles() {
      return files;
    }

    public void setFiles(String[] files) {
      this.files = files;
    }

    public String[] getJars() {
      return jars;
    }

    public void setJars(String[] jars) {
      this.jars = jars;
    }

    public String[] getArchives() {
      return archives;
    }

    public void setArchives(String[] archives) {
      this.archives = archives;
    }

    public String[] getSparkConf() {
      return sparkConf;
    }

    public void setSparkConf(String[] sparkConf) {
      this.sparkConf = sparkConf;
    }
  }

  
  private static final Logger logger = LoggerFactory.getLogger(JobCreateAction.class);
  
  private JsonObject payload;
  
  public JobCreateAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
    super(hopsworksAPIConfig, jobName);

    Args args = new Args();
    JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
            .add("numExecutors", args.getNumExecutors())
            .add("cpusPerExecutor", args.getCpusPerExecutor());
//            .add("birthdate", new SimpleDateFormat("DD/MM/YYYY")
//                    .format(args.getBirthdate()));

    this.payload = objectBuilder.build();
  }
  
  @Override
  public int execute() throws Exception {
    CloseableHttpClient getClient = getClient();
    HttpPut request = new HttpPut(getJobUrl());
    request.addHeader("User-Agent", USER_AGENT);
    request.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");

    StringEntity entity = new StringEntity(payload.toString());
    request.setEntity(entity);

    CloseableHttpResponse response = getClient.execute(request);
    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder result = new StringBuilder();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    JsonObject body = Json.createReader(new StringReader(result.toString())).readObject();
    logger.info("create job: " + body.toString());
    response.close();
    return response.getStatusLine().getStatusCode();
  }
}
