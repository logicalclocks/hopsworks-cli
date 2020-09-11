package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;

public class JobLogsAction extends JobAction {
    private static final Logger logger = LoggerFactory.getLogger(JobLogsAction.class);

    private String stdOut;
    private String pathToStdOut;
    private String stdErr;
    private String pathToStdErr;
    private boolean isGetStdout = true;
    private boolean isGetStderr = true;
    private int executionId;

    public JobLogsAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig, jobName);
    }

    public JobLogsAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName, boolean isGetStdout,
                         boolean isGetStderr) {
        super(hopsworksAPIConfig, jobName);
        this.isGetStderr = isGetStderr;
        this.isGetStdout = isGetStdout;
    }

    @Override
    public int execute() throws Exception {
        int status = 200;
        this.executionId = getLatestExecution();
//            "id/log/out|err"
        CloseableHttpClient logsClient = getClient();
        if (isGetStdout) {
            HttpGet logsRequest = getJobGet("/executions/" + this.executionId + "/log/out");
            CloseableHttpResponse logsResponse = logsClient.execute(logsRequest);
            status = readJsonRepoonse(logsResponse);
            this.stdOut = this.getJsonResult().getString("log");
            this.pathToStdOut = this.getJsonResult().getString("path");
            logsResponse.close();
        }
        if (isGetStderr) {
            HttpGet logsRequest = getJobGet("/executions/" + this.executionId + "/log/err");
            CloseableHttpResponse logsResponse = logsClient.execute(logsRequest);
            status = readJsonRepoonse(logsResponse);
            this.stdErr = this.getJsonResult().getString("log");
            this.pathToStdErr = this.getJsonResult().getString("path");
            logsResponse.close();
        }
        logsClient.close();
        System.out.println(this.stdOut);
        System.out.println(this.stdErr);
        return status;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getPathToStdOut() {
        return pathToStdOut;
    }

    public boolean isGetStderr() {
        return isGetStderr;
    }

    public boolean isGetStdout() {
        return isGetStdout;
    }

    public int getExecutionId() {
        return executionId;
    }
}
