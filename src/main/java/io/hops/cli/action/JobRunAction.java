package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class JobRunAction extends JobAction {
  private static final Logger logger = LoggerFactory.getLogger(JobRunAction.class);

  private final String args;
  public JobRunAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName, String args) {
    super(hopsworksAPIConfig, jobName);
    this.args =  args;
  }

  
  @Override
  public int execute() throws IOException {
    CloseableHttpClient getClient = getClient();
    HttpPost request = getJobPost("/executions");
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("data", args));
    request.setEntity(new UrlEncodedFormEntity(params));
//    application/x-www-form-urlencoded


    CloseableHttpResponse response = getClient.execute(request);

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
