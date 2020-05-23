package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class JobStopAction extends JobAction {
  private static final Logger logger = LoggerFactory.getLogger(JobStopAction.class);

  public JobStopAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
    super(hopsworksAPIConfig, jobName);
  }
  
  
  @Override
  public int execute() throws Exception {
    int executionId = getLatestExecution();
    CloseableHttpClient getClient = getClient();
    HttpPut request = getJobPut("/executions/" + executionId + "/status");
    CloseableHttpResponse response = getClient.execute(request);
    System.out.println("Stopping job " + this.jobName + " with execution id: " + executionId);
    return readJsonRepoonse(response);
  }
}
