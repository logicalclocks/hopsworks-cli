package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
    StringEntity input = new StringEntity(args);
    input.setContentType("text/plain");
    request.setEntity(input);
    CloseableHttpResponse response = getClient.execute(request);
    return readJsonRepoonse(response);
  }

}
