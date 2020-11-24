package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class JobRunAction extends JobAction {


  private static final Logger logger = LoggerFactory.getLogger(JobRunAction.class);
  private final String args;
  protected int execId;

  public int getExecId(){return execId;}

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
    int statusCode = readJsonResponse(response);
    // get execution id after readJsonResponse
    if (getJsonResult()!=null){
      this.execId=getJsonResult().getInt("id");
    }

    return statusCode;
  }





}
