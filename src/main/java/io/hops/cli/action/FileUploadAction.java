package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import io.hops.upload.net.IFileToHttpEntity;
import io.hops.upload.params.FlowHttpEntityGenerator;
import org.apache.commons.lang.SystemUtils;
import org.apache.hadoop.fs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class FileUploadAction extends HopsworksAction {

  private String hopsworksFolder;
  private URI filePath;

  private static final Logger logger = LoggerFactory.getLogger(FileUploadAction.class);

  public FileUploadAction(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder, String filePath) throws URISyntaxException {
    super(hopsworksAPIConfig);
    this.init(hopsworksFolder, filePath);
  }

  public FileUploadAction(HopsworksAPIConfig hopsworksAPIConfig, String hopsworksFolder, URI filePath) {
    super(hopsworksAPIConfig);
    this.init(hopsworksFolder, filePath);
  }

  private void init(String hopsworksFolder, String filePath) throws URISyntaxException {
    URI path;
    // Default FS if not given, is the local FS (file://)
    if (SystemUtils.IS_OS_WINDOWS && filePath.startsWith("file://") == false) {
      path = new URI("file:///" + filePath);
    } else if (filePath.startsWith("/")) {
      path = new URI("file://" + filePath);
    } else {
      path = new URI(filePath);
    }
    init(hopsworksFolder, path);
  }

  private void init(String hopsworksFolder, URI path) {

    this.hopsworksFolder = hopsworksFolder;
    this.filePath = path;
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
      statusCode = uploadFile(this.filePath, this.hopsworksFolder, this.hopsworksAPIConfig);
    if (statusCode != HttpStatus.SC_OK) {
      throw new Exception("HTTP File Upload not successful");
    }
    return statusCode;
  }



  private int startUploadFile(URI fileUri, String datasetPath, HopsworksAPIConfig apiConfig, String targetFileName) throws
          IOException {

    String projectId = getProjectId(apiConfig);
    String apiUrl = apiConfig.getProjectUrl() + projectId  + "/dataset/upload/" + datasetPath;
    CloseableHttpClient client = getClient();
    final HttpPost post = new HttpPost(apiUrl);
    post.addHeader("Authorization", "ApiKey " + apiConfig.getApiKey());
    IFileToHttpEntity entityGenerator = new FlowHttpEntityGenerator();
    entityGenerator.init(fileUri, targetFileName);
    int statusCode = 0;
    long startTime = System.currentTimeMillis();
    while (entityGenerator.hasNext()) {
      statusCode = this.uploadChunk(entityGenerator, post, client);
      if (HttpStatus.SC_OK != statusCode) {
        return statusCode;
      }
    }
    long endTime = System.currentTimeMillis();
    logger.info("File Total Upload Time: " + (endTime - startTime) + " milliseconds");
    client.close();
    return statusCode;

  }

  private int uploadFile(URI uri, String datasetPath, HopsworksAPIConfig apiConfig) throws IOException {

    Path path = new Path(uri);
    String targetFileName = path.getName();

    return this.startUploadFile(uri, datasetPath, apiConfig, targetFileName);
  }

  private int uploadChunk(IFileToHttpEntity entityGenerator, HttpPost post, HttpClient client) throws IOException {
    int statusCode;
    HttpEntity entity = entityGenerator.next();
    post.setEntity(entity);
    logger.info(post.toString());
    HttpResponse response = client.execute(post);
    StatusLine statusLine = response.getStatusLine();
    statusCode = statusLine.getStatusCode();
    InputStream responseContent = response.getEntity().getContent();
    logger.info("API Response ==> " + convertStreamToString(responseContent));
    return statusCode;
  }

  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

}
