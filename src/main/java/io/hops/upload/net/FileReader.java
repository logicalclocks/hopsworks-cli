package io.hops.upload.net;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReader {

    private RandomAccessFile raf;

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public long open(String filePath) throws IOException{
        raf = new RandomAccessFile(filePath, "r");
        long fileSize = raf.length();
        logger.info("File Size: "+ fileSize);
        return fileSize;

    }

    public void close() throws IOException {
        raf.close();
    }

    public byte[] readChunk(int numberOfBytes) throws IOException {
        byte[] buffer = new byte[(int) numberOfBytes];
        int bytesRead = raf.read(buffer);
        if (bytesRead != -1) {
            return buffer;
        }
        else
        {
            throw new IOException("Read File Failed");
        }

    }

}