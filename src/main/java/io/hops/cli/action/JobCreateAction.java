package io.hops.cli.action;

import com.google.common.base.Strings;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class JobCreateAction extends HopsworksAction {
  
  private static final Logger logger = LoggerFactory.getLogger(JobStartAction.class);
  
  private int projectId;
  private JsonObject payload;
  
  public JobCreateAction(String apiEndpoint, int port, boolean authentication, String path, int projectId,
    JsonObject payload) {
    super(apiEndpoint, port, authentication, path);
    this.projectId = projectId;
    this.payload = payload;
  }
  
  @Override
  public int execute() throws Exception {
    HttpContext localContext = null;
  
    if (getHttpServer().isAuthentication()) {
      List<Cookie> cookies;
      cookies = auth();
      localContext = generateContextWithCookies(cookies);
    }
  
//    HttpClient getClient = HttpClientBuilder.create().build();
    HttpClient getClient = getClient();
    String uri = getHttpServer().getAPIUrl() + "/project/" + projectId + "/jobs/spark";
    HttpPost request = new HttpPost(uri);
    request.addHeader("User-Agent", USER_AGENT);
    request.addHeader("ApiKey", getAuthData().getApiKey());

//    //Set "Authorization" only for JWT. Not needed in Hopsworks 0.6
//    if (!Strings.isNullOrEmpty(getAuthData().getJwt())){
//      request.addHeader("Authorization", "Bearer " + getAuthData().getJwt());
//    }
    StringEntity entity = new StringEntity(payload.toString());
    request.setEntity(entity);
    request.setHeader("Accept", "application/json");
    request.setHeader("Content-type", "application/json");
    
    HttpResponse response = getClient.execute(request, localContext);
    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder result = new StringBuilder();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    JsonObject body = Json.createReader(new StringReader(result.toString())).readObject();
    logger.info("create job: " + body.toString());
    return response.getStatusLine().getStatusCode();
  }
}
