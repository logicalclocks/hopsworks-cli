package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import io.hops.upload.net.HTTPFileUpload;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpStatus;

public class FileUploadAction extends HopsworksAction {

  private HopsworksAPIConfig hopsworksAPIConfig;
  private String hopsworksFolder;
  private URI filePath;
  private String targetFileName = null;

  private HTTPFileUpload httpFileUpload = null;
  
  public FileUploadAction(String apiEndpoint, int port, boolean authentication, String path) {
    super(apiEndpoint, port, authentication, path);
  }
  
  
  //  public FileUploadAction(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder,
//      URI filePath, String targetFileName) {
//
//    this.targetFileName = targetFileName; //optional parameter
//    this.init(hopsworksAPIConfig, hopsworksFolder, filePath);
//
//  }
//
//  public FileUploadAction(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder, String filePath) throws URISyntaxException {
//    this.init(hopsworksAPIConfig, hopsworksFolder, filePath);
//
//  }
//
//  public FileUploadAction(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder, URI filePath) {
//
//    this.init(hopsworksAPIConfig, hopsworksFolder, filePath);
//
//  }

  private void init(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder,
      String filePath) throws URISyntaxException {
    URI path;
    // Default FS if not given, is the local FS (file://)
    if (filePath.startsWith("/")) {
      path = new URI("file://" + filePath);
    } else {
      path = new URI(filePath);
    }

    init(hopsworksAPIConfig, hopsworksFolder, path);
  }

  private void init(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder, URI path) {

    this.hopsworksAPIConfig = hopsworksAPIConfig;
    this.hopsworksFolder = hopsworksFolder;
    this.filePath = path;

    String authPath = this.hopsworksAPIConfig.getPathLogin();
    String userName = this.hopsworksAPIConfig.getUserName();
    String password = this.hopsworksAPIConfig.getPassword();

    httpFileUpload = new HTTPFileUpload(this.hopsworksAPIConfig.getApiUrl(), this.hopsworksAPIConfig.getPort(),
        true, this.hopsworksAPIConfig.getPath());
    httpFileUpload.activateAuth(userName, password, authPath);
  }

//  private String generateUploadPath() {
//    String uploadPath = this.hopsworksAPIConfig.getPathFileUpload();
//    uploadPath = uploadPath.replace("{id}", this.hopsworksAPIConfig.getProjectId());
//    uploadPath = uploadPath.replace("{fileName}", this.hopsworksFolder);
//    return uploadPath;
//  }

  @Override
  public int execute() throws Exception {

    int statusCode;
//    String completeUploadPath = generateUploadPath();
    if (this.targetFileName == null) {
      statusCode = httpFileUpload.uploadFile(this.filePath, this.hopsworksFolder, this.hopsworksAPIConfig);
    } else {
      statusCode = httpFileUpload.uploadFile(this.filePath, this.hopsworksFolder, this.hopsworksAPIConfig, this.targetFileName);
    }

    if (statusCode != HttpStatus.SC_OK) {
      throw new Exception("HTTP File Upload not successful");
    }
    return statusCode;

  }
}
