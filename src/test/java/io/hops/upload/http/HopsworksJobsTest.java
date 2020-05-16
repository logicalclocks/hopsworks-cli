package io.hops.upload.http;

import io.hops.cli.action.JobCreateAction;
import io.hops.cli.action.JobStartAction;
import io.hops.cli.action.JobStopAction;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class HopsworksJobsTest {
  private String authPath = "/auth/login";
//  private String anAccount = "admin@kth.se";
//  private String theS3cr3t = "admin";
  private String apiKey = "....";
  private String apiEndpoint = "http://localhost"; //test server
  private int port = 8080;
  private String path = "/hopsworks-api/api";
  private int projectId = 1;
  private String projectName = "demo_spark_admin000";
  //jwt is only needed for Hopsworks > 0.7.x
  private String jwt = "";
  
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
      JobCreateAction jobCreateAction = new JobCreateAction(apiEndpoint, port, true, path, projectId, payload);
      jobCreateAction.activateAuth(apiKey, authPath);
      statusCode = jobCreateAction.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
  
  
  @Test
  public void startJobTest() {
    int statusCode = 0;
    try {
      int jobId = 2;
      JobStartAction jobStartAction = new JobStartAction(apiEndpoint, port, true, path, projectId, jobId);
//      jobStartAction.activateAuth(anAccount, theS3cr3t, authPath, jwt);
      jobStartAction.activateAuth(apiKey, authPath);
      statusCode = jobStartAction.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
  @Test
  public void stopJobTest() {
    int statusCode = 0;
    try {
      int jobId = 207;
      JobStopAction jobStopAction = new JobStopAction(apiEndpoint, port, true, path, projectId, jobId);
      jobStopAction.activateAuth(apiKey, authPath);
      statusCode = jobStopAction.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }
  
 
}
