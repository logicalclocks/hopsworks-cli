package io.hops.upload.http;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Ignore
public class HopsworksFileUploadTest {

  String projectId;
  String folder;
  String cwd;
  HopsworksAPIConfig hopsworksAPIConfig;

  @Before
  public void setUp() {
    cwd = "file://" + System.getProperty("user.dir");
    String apiKey = "admin";
    String apiEndpoint = "https://bbc6.sics.se"; //test server
    int port = 443;
    String path = "/hopsworks-api/api";

    projectId = "1";
    folder = "Resources/jim";

    hopsworksAPIConfig = new HopsworksAPIConfig( apiKey, apiEndpoint + ":443" , "test");
  }

  @Test
  public void simpleFileUploadTest() {

    String testProjectUploadPath = folder;

    int statusCode = 0;
//    try {
//      statusCode = httpFileUpload.uploadFile(cwd + "/src/main/resources/cloudera-client-config.jpg", testProjectUploadPath, hopsworksAPIConfig);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }

  @Test
  public void mediumFileUploadTest() {

    String testProjectUploadPath = folder;

    int statusCode = 0;
//    try {
//      statusCode = httpFileUpload.uploadFile(cwd + "/src/main/resources/cloudera-setup-2.png",
//          testProjectUploadPath, hopsworksAPIConfig);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    assertEquals("statusCode should be 200 OK", statusCode, HttpStatus.SC_OK);
  }

}
