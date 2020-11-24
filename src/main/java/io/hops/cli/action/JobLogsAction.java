package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobLogsAction extends JobAction {

    private static final Logger logger = LoggerFactory.getLogger(JobLogsAction.class);
    private String stdOut;
    private String pathToStdOut;
    private String stdErr;
    private String pathToStdErr;
    private boolean isGetStdout = true;
    private boolean isGetStderr = true;
    private int executionId;

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

    public String getStdErr() {  return stdErr; }

    public String getPathToStdErr() {
        return pathToStdErr;
    }

    public JobLogsAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig, jobName);
    }

    public JobLogsAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName, boolean isGetStdout,
                         boolean isGetStderr) {

        super(hopsworksAPIConfig, jobName);
        this.isGetStderr = isGetStderr;
        this.isGetStdout = isGetStdout;
    }


    public JobLogsAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName, String execId) {
        super(hopsworksAPIConfig, jobName);

        try{
            this.executionId=Integer.parseInt(execId);
        } catch (NumberFormatException ex){
            logger.error("Not a valid number for execution Id="+execId +" | Skipped");
        }
    }

    @Override
    public int execute() throws Exception {

        int status = 200;
        if(this.executionId==0){
            this.executionId = getLatestExecution();    //"id/log/out|err"
        }

        CloseableHttpClient logsClient = getClient();
        if (isGetStdout) {
            HttpGet logsRequest = getJobGet("/executions/" + this.executionId + "/log/out");
            CloseableHttpResponse logsResponse = logsClient.execute(logsRequest);
            status = readJsonResponse(logsResponse);
            this.stdOut = this.getJsonResult().getString("log");
            this.pathToStdOut = this.getJsonResult().getString("path");
            logsResponse.close();
        }
        if (isGetStderr) {
            HttpGet logsRequest = getJobGet("/executions/" + this.executionId + "/log/err");
            CloseableHttpResponse logsResponse = logsClient.execute(logsRequest);
            status = readJsonResponse(logsResponse);
            this.stdErr = this.getJsonResult().getString("log");
            this.pathToStdErr = this.getJsonResult().getString("path");
            logsResponse.close();
        }
        logsClient.close();
        System.out.println(this.stdOut);
        System.out.println(this.stdErr);
        return status;
    }

}
