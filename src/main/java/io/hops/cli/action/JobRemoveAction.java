package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JobRemoveAction extends JobAction {
    private static final Logger logger = LoggerFactory.getLogger(JobRemoveAction.class);

    public JobRemoveAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig, jobName);
    }

    @Override
    public int execute() throws IOException {
        CloseableHttpClient client = getClient();
        int status = -1;

        final HttpDelete delete = new HttpDelete(getJobUrl());
        delete.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
        CloseableHttpResponse response = client.execute(delete);
        //status = readJsonRepoonse(response);
        status=response.getStatusLine().getStatusCode();
        response.close();
        client.close();

        return status;
    }

}
