package io.hops.upload.net;

import java.io.BufferedReader;
import java.io.EOFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsFileReader {

  private final FSDataInputStream in;
  private final long fileSize;
  private long position;

  private static final Logger logger = LoggerFactory.getLogger(HdfsFileReader.class);

  public HdfsFileReader(FileSystem fs, Path filePath) throws IOException {
    in = fs.open(filePath);
    fileSize = fs.getFileStatus(filePath).getLen();
    logger.info("File Size: " + fileSize);
    this.position = 0l;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void close() throws IOException {
    in.close();
  }

  public long bytesRemaining() {
    return this.fileSize - this.position;
  }

  public long getPosition() {
    return position;
  }

  public byte[] readChunk(int numberOfBytes) throws IOException {
    byte[] buffer = new byte[(int) numberOfBytes];
    try {
      in.readFully(position, buffer);
      position = position + numberOfBytes;
    } catch (EOFException eof) {
      position = this.fileSize;
    }

    return buffer;
  }

}
