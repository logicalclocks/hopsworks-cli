package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import static org.apache.http.HttpHeaders.USER_AGENT;

public abstract class JobAction extends HopsworksAction {
    protected final String jobName;
    protected final String jobUrl;

    public JobAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig);
        this.jobName = jobName;
        this.jobUrl = hopsworksAPIConfig.getApiUrl() + hopsworksAPIConfig.getProjectUrl() +
                "/jobs/" + jobName;
    }

    protected void addHeaders(HttpRequestBase request) {
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Authorization", "ApiKey " + hopsworksAPIConfig.getApiKey());
    }

    protected HttpPost getJobPost(String path) {
        HttpPost request = new HttpPost(getJobUrl() + "/" + path);
        addHeaders(request);
        return request;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getJobName() {
        return jobName;
    }

}
