package io.hops.cli.config;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsworksAPIConfig {

  private String apiKey;
  private String apiUrl;
  private String projectName;

  private URL url;

  private final String baseUrl = "/hopsworks-api/api";

  private final String DEFAULT_PATH_FILE_UPLOAD = "/project/{id}/dataset/{fileName}";
  private String pathFileUpload;

  public HopsworksAPIConfig( String apiKey, String apiUrl, String projectName) {
    this.apiKey = apiKey;
    this.apiUrl = apiUrl;

    this.projectName = projectName;

    try {
      this.url = new URL(this.apiUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    this.pathFileUpload = this.DEFAULT_PATH_FILE_UPLOAD;

  }

  public String getPathFileUpload() {
    return pathFileUpload;
  }

  public void setPathFileUpload(String pathFileUpload) {
    this.pathFileUpload = pathFileUpload;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public int getPort() {
    int port = this.url.getPort();
    if (port == -1) {
      return 443; //no specific port in the url means standard port 80
    }
    return port;
  }

  public String getHost() {
    return this.url.getHost();
  }

  public String getPath() {
    return this.url.getPath();
  }

  public String getProtocol() {
    return this.url.getProtocol();
  }

  public String getProjectName() {
    return projectName;
  }

  public String getProjectUrl() {
      return  getApiUrl() + getBaseUrl() + "/project/";
  }
  
  public String getProjectNameUrl() {
      return  getProjectUrl() + "getProjectInfo/" + projectName;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
