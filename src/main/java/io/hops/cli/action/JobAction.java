package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.apache.http.HttpHeaders.USER_AGENT;

public abstract class JobAction extends HopsworksAction {
    protected final String jobName;
    protected String result;
    protected JsonObject jsonResult;

    public JobAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig);
        this.jobName = jobName;
    }

    protected void addHeaders(HttpRequestBase request) {
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
    }

    protected HttpPost getJobPost(String path) throws IOException {
        HttpPost request = new HttpPost(getJobUrl() + "/" + path);
        addHeaders(request);
        return request;
    }

    protected HttpPut getJobPut(String path) throws IOException {
        HttpPut request = new HttpPut(getJobUrl() + "/" + path);
        addHeaders(request);
        return request;
    }

    protected HttpGet getJobGet(String path) throws IOException {
        HttpGet request = new HttpGet(getJobUrl() + "/" + path);
        addHeaders(request);
        return request;
    }
    public String getJobUrl()  throws IOException {
        return hopsworksAPIConfig.getProjectUrl() + this.getProjectId() + "/jobs/" + jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getResult() {
        return result;
    }

    protected void setResult(String result) {
        this.result = result;
    }

    public JsonObject getJsonResult() {
        return jsonResult;
    }

    protected void setJsonResult(JsonObject jsonResult) {
        this.jsonResult = jsonResult;
    }

    protected int readJsonRepoonse(CloseableHttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder res = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            res.append(line);
        }
        JsonReader jsonReader = Json.createReader(new StringReader(res.toString()));
        JsonObject body = jsonReader.readObject();
        jsonReader.close();
        response.close();
        setResult(res.toString());
        setJsonResult(body);
        return response.getStatusLine().getStatusCode();
    }

    /**
     *
     * @return id of latest execution of the current job
     * @throws IOException if problem getting the job's executions from Hopsworks
     */
    protected int getLatestExecution() throws IOException {
        // First get the most recent execution for this job. Then get the log/path for that execution.
        CloseableHttpClient client = getClient();
        HttpGet request = getJobGet("/executions?sort_by=appId:desc&limit=1");

        CloseableHttpResponse response = client.execute(request);
        int status = readJsonRepoonse(response);
        if (status != 200 && status != 201) {
            client.close();
            if (response != null) {
                response.close();
            }
            throw new IOException("Could not get latest execution: " + status);
        }
        JsonArray array = this.getJsonResult().getJsonArray("items");
        JsonObject execution = array.getJsonObject(0);
        client.close();
        response.close();
        return execution.getInt("id");
    }

}
