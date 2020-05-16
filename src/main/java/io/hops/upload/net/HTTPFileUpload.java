package io.hops.upload.net;

import io.hops.cli.config.HopsworksAPIConfig;
import io.hops.upload.cookie.CookieAuth;
import io.hops.upload.beans.AuthData;
import io.hops.upload.beans.Server;
import io.hops.upload.params.FlowHttpEntityGenerator;
import java.io.BufferedReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.hadoop.fs.Path;
import static org.apache.http.HttpHeaders.USER_AGENT;
import org.apache.http.client.methods.HttpGet;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonValue;

public class HTTPFileUpload {

  private Server httpServer;
  private AuthData authData;

  private static final Logger logger = LoggerFactory.getLogger(HTTPFileUpload.class);

//  public void activateAuth(String email, String password, String authPath) {
//    this.authData = new AuthData(email, password, authPath);
//    this.httpServer.setAuthentication(true);
//
//  }

  public void activateAuth(String apiKey, String authPath) {
    this.authData = new AuthData(apiKey, authPath);
    this.httpServer.setAuthentication(true);

  }


  public HTTPFileUpload(String apiEndpoint, int port, boolean authentication, String path) {
    this.httpServer = new Server(apiEndpoint, port, authentication, path);

  }

//  private List<Cookie> auth() throws IOException {
//    CookieAuth cookieAuth = new CookieAuth(this.httpServer.getAPIUrl() + this.authData.getAuthPath(),
//        this.authData.getEmail(), this.authData.getPassword());
//    return cookieAuth.auth();
//  }
  private List<Cookie> auth() throws IOException {
    CookieAuth cookieAuth = new CookieAuth(this.httpServer.getAPIUrl() + this.authData.getAuthPath(),
        this.authData.getApiKey());
//        this.authData.getEmail(), this.authData.getPassword());
    return cookieAuth.auth();
  }

  private HttpContext generateContextWithCookies(List<Cookie> cookies) {
    CookieStore cookieStore = new BasicCookieStore();
    for (int i = 0; i < cookies.size(); i++) {
      cookieStore.addCookie(cookies.get(i));
    }
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    return localContext;

  }

  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  private HttpClient getClient() {
    HttpClient getClient = null;
    try {
      getClient = HttpClients
              .custom()
              .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                      TrustSelfSignedStrategy.INSTANCE).build())
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
              .build();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    }
    return getClient;

  }


  private int startUploadFile(URI fileUri, String datasetPath, HopsworksAPIConfig apiConfig, String targetFileName) throws
      IOException {

    HttpContext localContext = null;

    if (this.httpServer.isAuthentication()) {
      List<Cookie> cookies;
      cookies = this.auth();
      localContext = this.generateContextWithCookies(cookies);
    }

//    HttpClient getClient = HttpClientBuilder.create().build();
    HttpClient getClient = getClient();

    HttpGet request = new HttpGet(apiConfig.getProjectNameUrl());
    request.addHeader("User-Agent", USER_AGENT);
    HttpResponse response = getClient.execute(request, localContext);
    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuilder result = new StringBuilder();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    JsonObject body = Json.createReader(new StringReader(result.toString())).readObject();
    JsonValue projectId = body.get("projectId");
    
    
    String apiUrl = apiConfig.getProjectUrl() + projectId.toString()  + "/dataset/upload/" + datasetPath;
//    final HttpClient client = HttpClientBuilder.create().build();
    HttpClient client = getClient();

    final HttpPost post = new HttpPost(apiUrl);
    
    IFileToHttpEntity entityGenerator = new FlowHttpEntityGenerator();

    entityGenerator.init(fileUri, targetFileName);

    int statusCode = 0;

    long startTime = System.currentTimeMillis();

    while (entityGenerator.hasNext()) {

      statusCode = this.uploadChunk(entityGenerator, post, client, localContext);
      if (HttpStatus.SC_OK != statusCode) {
        return statusCode;
      }

    }
    long endTime = System.currentTimeMillis();
    logger.info("File Total Upload Time: " + (endTime - startTime) + " milliseconds");
    return statusCode;

  }

  public int uploadFile(String filePath, String datasetPath, HopsworksAPIConfig apiConfig) throws IOException, URISyntaxException {
    return uploadFile(HTTPFileUpload.pathToUri(filePath), datasetPath, apiConfig);
  }

  public int uploadFile(URI uri, String datasetPath, HopsworksAPIConfig apiConfig) throws IOException {

    Path path = new Path(uri);
    String targetFileName = path.getName();

    return this.startUploadFile(uri, datasetPath, apiConfig, targetFileName);
  }

  public static URI pathToUri(String filePath) throws URISyntaxException {
    URI uri;
    // Default FS if not given, is the local FS (file://)
    if (filePath.startsWith("/")) {
      uri = new URI("file://" + filePath);
    } else {
      uri = new URI(filePath);
    }
    return uri;
  }

  public int uploadFile(String filePath, String apiPath, HopsworksAPIConfig apiConfig, String targetFileName) throws IOException, URISyntaxException {
    return this.startUploadFile(HTTPFileUpload.pathToUri(filePath), apiPath, apiConfig, targetFileName);
  }

  public int uploadFile(URI uri, String apiPath, HopsworksAPIConfig apiConfig,  String targetFileName) throws IOException, URISyntaxException {
    return this.startUploadFile(uri, apiPath, apiConfig, targetFileName);
  }

  private int uploadChunk(IFileToHttpEntity entityGenerator, HttpPost post, HttpClient client,
      HttpContext localContext) throws IOException {
    int statusCode;
    HttpEntity entity = entityGenerator.next();
    post.setEntity(entity);
    logger.info(post.toString());

    HttpResponse response;
    if (this.httpServer.isAuthentication()) {
      response = client.execute(post, localContext);
    } else {
      response = client.execute(post);
    }

    StatusLine statusLine = response.getStatusLine();

    statusCode = statusLine.getStatusCode();

    InputStream responseContent = response.getEntity().getContent();

    logger.info("API Response ==> " + convertStreamToString(responseContent));

    return statusCode;

  }

}
