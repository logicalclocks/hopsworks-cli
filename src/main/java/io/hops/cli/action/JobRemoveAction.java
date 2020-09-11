package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JobRemoveAction extends JobAction {
    private static final Logger logger = LoggerFactory.getLogger(JobRemoveAction.class);

    public JobRemoveAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig, jobName);
    }

    @Override
    public int execute() throws IOException {
        CloseableHttpClient client = getClient();
        int status = -1;
        try {
            final HttpDelete delete = new HttpDelete(getJobUrl());
            delete.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
            CloseableHttpResponse response = client.execute(delete);
            status = readJsonRepoonse(response);
            response.close();
        } catch (Exception ex) {
            ; // ignore
        } finally {
            client.close();
        }
        return status;
    }

}
