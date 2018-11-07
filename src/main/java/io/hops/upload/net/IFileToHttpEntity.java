package io.hops.upload.net;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.net.URI;

public interface IFileToHttpEntity {

    void init(URI filePath,String targetFileName) throws IOException;
    HttpEntity next() throws IOException;
    boolean hasNext();
}
