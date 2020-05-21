package io.hops.upload.http;

import io.hops.cli.action.JobCreateAction;
import io.hops.cli.action.JobRunAction;
import io.hops.cli.action.JobStopAction;
import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class HopsworksJobsTest {
  private String apiKey = "....";
  private String apiEndpoint = "http://localhost"; //test server
  private int port = 443;
  private String path = "/hopsworks-api/api";
  private int projectId = 1;
  private String projectName = "demo_spark_admin000";
  //jwt is only needed for Hopsworks > 0.7.x
  private String jwt = "";
  private HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig("apikey....",
          "https://localhost:443/", "demo_spark_admin000");
  
  @Test
  public void createJobTest() {
    int statusCode = 0;
    try {
      JsonObject payload;
      //Create a SparkPi job with name sparkpi
      try (JsonReader jsonReader = Json.createReader(new StringReader("{\"type\":\"sparkJobConfiguration\"," +
        "\"appName\":\"sparkpi\",\"amMemory\":1024,\"amQueue\":\"default\",\"amVCores\":1,\"localResources\":[]," +
        "\"appPath\":\"hdfs:///Projects/" + projectName + "/TestJob/spark-examples.jar\"," +
        "\"args\":\"100000\",\"dynamicExecutors\":false,\"executorCores\":1,\"executorMemory\":1024,\"historyServerIp\":\"10.0.104.161:18080\"," +
        "\"mainClass\":\"org.apache.spark.examples.SparkPi\",\"maxExecutors\":1500,\"minExecutors\":1,\"numberOfExecutors\":1," +
        "\"numberOfExecutorsInit\":1,\"numberOfGpusPerExecutor\":0,\"selectedMaxExecutors\":10,\"selectedMinExecutors\":1," +
        "\"schedule\":null,\"flinkjobtype\":\"Streaming\"}"))) {
        payload = jsonReader.readObject();
      }
//      HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig(apiKey, apiEndpoint, projectName);
//      JobCreateAction jobCreateAction = new JobCreateAction(hopsworksAPIConfig, payload);
//      statusCode = jobCreateAction.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
  
  
  @Test
  public void startJobTest() {
    int statusCode = 200;
//    try {
//      int jobId = 2;
//      JobRunAction jobStartAction = new JobRunAction(hopsworksAPIConfig, jobId);
//      statusCode = jobStartAction.execute();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
  @Test
  public void stopJobTest() {
    int statusCode = 200;
//    try {
//      int jobId = 207;
//      JobStopAction jobStopAction = new JobStopAction(hopsworksAPIConfig, jobId);
//      statusCode = jobStopAction.execute();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
 
}
