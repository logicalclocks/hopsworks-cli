package io.hops.upload.http;

import io.hops.upload.net.HTTPFileUpload;
import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HopsworksFileUploadTest {

  HTTPFileUpload httpFileUpload = null;
  String projectId;
  String folder;
  String cwd;
  HopsworksAPIConfig hopsworksAPIConfig;

  @Before
  public void setUp() {
    cwd = "file://" + System.getProperty("user.dir");
    String authPath = "/auth/login";
    String anAccount = "admin@hopsworks.ai";
    String theS3cr3t = "admin";
    String apiKey = "admin";
    String apiEndpoint = "http://bbc6.sics.se"; //test server
    int port = 8080;
    String path = "/hopsworks-api/api";

    projectId = "1";
    folder = "Resources/jim";

    httpFileUpload = new HTTPFileUpload(apiEndpoint, port, true, path);
//    httpFileUpload.activateAuth(anAccount, theS3cr3t, authPath);
    httpFileUpload.activateAuth(anAccount, apiKey);
    hopsworksAPIConfig = new HopsworksAPIConfig(apiKey, apiEndpoint + ":8080" , "test");
//    hopsworksAPIConfig = new HopsworksAPIConfig(anAccount, theS3cr3t, apiEndpoint + ":8080" , "test");

  }

  @Test
  public void simpleFileUploadTest() {

    String testProjectUploadPath = folder;

    int statusCode = 0;
    try {
      statusCode = httpFileUpload.uploadFile(cwd + "/src/main/resources/cloudera-client-config.jpg", testProjectUploadPath, hopsworksAPIConfig);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }

  @Test
  public void mediumFileUploadTest() {

    String testProjectUploadPath = folder;

    int statusCode = 0;
    try {
      statusCode = httpFileUpload.uploadFile(cwd + "/src/main/resources/cloudera-setup-2.png", 
          testProjectUploadPath, hopsworksAPIConfig);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }

}
