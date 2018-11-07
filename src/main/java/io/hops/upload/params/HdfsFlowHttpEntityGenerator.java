package io.hops.upload.params;

import io.hops.cli.main.CommandLineMain;
import io.hops.upload.net.IFileToHttpEntity;
import io.hops.upload.net.FileReader;
import io.hops.upload.net.HdfsFileReader;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsFlowHttpEntityGenerator implements IFileToHttpEntity {

  private FlowParameter parameter;
  HdfsFileReader fileReader;

  private final long FLOW_STANDARD_CHUNK_SIZE = 1024 * 1024;

  private long sizeLastChunk;
  
  private Configuration conf;

  private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

  private long calculateSizeLastChunk(long chunkSize, long fileSize) {

    long lastChunk = fileSize % chunkSize;
    /*
     * if(lastChunk == 0){
     * lastChunk = chunkSize;
     * }
     */
    return lastChunk;
  }

  private long calculateTotalChunks(long chunkSize, long fileSize) {

    long chunks = fileSize / chunkSize;
    if (chunks == 0) {
      chunks = 1;
    }
    return chunks;

  }

  public void init() throws IOException {
//      String path="/user/hdfs/example/hdfs/";
//      String fileName="hello.csv";
//      String fileContent="hello;world";
    // ====== Init HDFS File System Object
    final String HADOOP_CONF_DIR = CommandLineMain.hadoopConfDir;
    conf = new Configuration();

    conf.addResource(new Path(HADOOP_CONF_DIR, "/core-default.xml"));
    conf.addResource(new Path(HADOOP_CONF_DIR, "/hdfs-default.xml"));
    conf.addResource(new Path(HADOOP_CONF_DIR, "/mapred-default.xml"));
    conf.addResource(new Path(HADOOP_CONF_DIR, "/core-site.xml"));
    conf.addResource(new Path(HADOOP_CONF_DIR, "/hdfs-site.xml"));
    conf.addResource(new Path(HADOOP_CONF_DIR, "/mapred-site.xml"));


    // Set FileSystem URI. "hdfs://10.0.2.15:8020"
    // Because of Maven
    conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
    conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

    System.setProperty("HADOOP_USER_NAME", CommandLineMain.hdfsUsername);
//    System.setProperty("hadoop.home.dir", CommandLineMain.hadoopHome);
    
  }
  public void init(URI fileUri, String fileName) throws IOException {

    init();
    conf.set("fs.defaultFS", fileUri.toURL().getProtocol() + "://" + fileUri.getAuthority());
    logger.info("FileName: " + fileName);

    Path path = new Path(fileUri);
    FileSystem fs = path.getFileSystem(conf);

    this.fileReader = new HdfsFileReader(fs, path);
    long fileSize = this.fileReader.getFileSize();

    //Flow Parameter
    parameter = new FlowParameter();
    parameter.setFlowChunkNumber(1); //chunk starts with 1 not 0
    parameter.setFlowChunkSize(this.FLOW_STANDARD_CHUNK_SIZE);
    parameter.setFlowCurrentChunkSize(this.FLOW_STANDARD_CHUNK_SIZE);
    parameter.setFlowTotalSize(fileSize);

    String flowIdentifier = fileSize + "-" + fileName.replaceAll("[^0-9A-Za-z_-]", "");

    parameter.setFlowIdentifier(flowIdentifier);
    parameter.setFlowFilename(fileName);
    parameter.setFlowRelativePath(fileName);

    long totalChunks = calculateTotalChunks(this.FLOW_STANDARD_CHUNK_SIZE, fileSize);
    logger.info("Total File Chunks:" + totalChunks);
    this.sizeLastChunk = this.calculateSizeLastChunk(this.FLOW_STANDARD_CHUNK_SIZE, fileSize);
    logger.info("Last Chunk Size:" + this.sizeLastChunk);
    parameter.setFlowTotalChunks(totalChunks);

  }

  private void addFlowParameter(MultipartEntityBuilder builder) {

    builder.addTextBody("templateId", "-1" + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowChunkNumber", parameter.getFlowChunkNumber() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowChunkSize", parameter.getFlowChunkSize() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowCurrentChunkSize", parameter.getFlowCurrentChunkSize() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowTotalSize", parameter.getFlowTotalSize() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowIdentifier", parameter.getFlowIdentifier() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowFilename", parameter.getFlowFilename() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowRelativePath", parameter.getFlowRelativePath() + "", ContentType.TEXT_PLAIN);
    builder.addTextBody("flowTotalChunks", parameter.getFlowTotalChunks() + "", ContentType.TEXT_PLAIN);

  }

  private String toStringHttpEntity(HttpEntity entity) {

    ByteArrayOutputStream out = new ByteArrayOutputStream((int) entity.getContentLength());

    String entityAsString = "";
    try {
      entity.writeTo(out);
      entityAsString = out.toString();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return entityAsString;

  }

  private void increaseChunkNumber() {
    parameter.setFlowChunkNumber(parameter.getFlowChunkNumber() + 1);
  }

  public HttpEntity next() throws IOException, NoSuchElementException {

    logger.info("File Chunk Number: " + parameter.getFlowChunkNumber());
    if (parameter.getFlowChunkNumber() > parameter.getFlowTotalChunks()) {
      throw new NoSuchElementException();
    }

    int chunkSize = 0;
    chunkSize = (int) parameter.getFlowChunkSize();
    if (parameter.getFlowChunkNumber() == parameter.getFlowTotalChunks()) {
      //last chunk
      //in flow.js the last chunk is bigger (surprisingly)
      // example: file size 3.7 mb would result only in three chunks. 1 mb,1 mb and 1.7 mb for the last one
      // implementation should have the same behaviour as the official client
      chunkSize += (int) this.sizeLastChunk;

    }

    if (parameter.getFlowTotalSize() < parameter.getFlowChunkSize()) {
      //only one chunk for the complete file
      chunkSize = (int) this.sizeLastChunk;
    }

    byte[] chunk;

    chunk = fileReader.readChunk(chunkSize);
    parameter.setFlowCurrentChunkSize(chunkSize);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

    this.addFlowParameter(builder);

    builder.addBinaryBody("file", chunk, ContentType.DEFAULT_BINARY, parameter.getFlowFilename());

    HttpEntity entity = builder.build();

    //print http entity
    //logger.info(this.toStringHttpEntity(entity));
    this.increaseChunkNumber();
    if (parameter.getFlowChunkNumber() > parameter.getFlowTotalChunks()) {
      this.fileReader.close();
    }

    return entity;
  }

  public boolean hasNext() {

    if (parameter.getFlowChunkNumber() <= parameter.getFlowTotalChunks()) {
      return true;
    }
    return false;
  }
}
