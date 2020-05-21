package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class GetJobExecutionAction extends JobAction {
  private static final Logger logger = LoggerFactory.getLogger(GetJobExecutionAction.class);

  public GetJobExecutionAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
    super(hopsworksAPIConfig, jobName);
  }

  
  @Override
  public int execute() throws IOException {
    CloseableHttpClient getClient = getClient();
    HttpGet request = new HttpGet(getJobUrl() + "/executions");

    request.addHeader("User-Agent", USER_AGENT);
    request.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
    CloseableHttpResponse response = getClient.execute(request);    // , localContext
    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder result = new StringBuilder();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    JsonReader jsonReader = Json.createReader(new StringReader(result.toString()));
    JsonObject body = jsonReader.readObject();
    jsonReader.close();
    System.out.println("started job: " + jobName);
    response.close();
    return response.getStatusLine().getStatusCode();
  }
}
