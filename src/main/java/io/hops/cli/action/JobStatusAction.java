package io.hops.cli.action;

import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.json.JsonArray;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * read job status which can be accessed via getter method
 */
public class JobStatusAction extends JobAction {

    Logger logger = Logger.getLogger(JobStatusAction.class.getName());
    protected  String[] jobStatusArr=new String[2];
    private Integer executionId;

    public String[] getJobStatusArr() {
        return jobStatusArr;
    }

    private void setJobStatusArr(String[] arr) {
        this.jobStatusArr = arr;
    }

    public Integer getExecutionId() {
        return executionId;
    }

    public JobStatusAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName) {
        super(hopsworksAPIConfig, jobName);
    }

    public JobStatusAction(HopsworksAPIConfig hopsworksAPIConfig, String jobName, String executionId) {
        super(hopsworksAPIConfig, jobName);
        if(!executionId.equals("")){
            try{
                this.executionId=Integer.parseInt(executionId);
            } catch (NumberFormatException ex){
                logger.log(Level.INFO,"Not a valid number execution id; Skipped");
            }

        }

    }

    @Override
    public int execute() throws Exception {

        String[] statusArr = new String[2];
        CloseableHttpClient client = getClient();
        HttpGet request;
        if(executionId==null || executionId==0){
            this.executionId = getLatestExecution();
            JsonArray array = this.getJsonResult().getJsonArray("items");
            if(array!=null){
                statusArr[0]=array.get(0).asJsonObject().get("state").toString();
                statusArr[1]=array.get(0).asJsonObject().get("finalStatus").toString();
            }
        }else {
            getExecutionById(executionId);
            statusArr[0]=this.getJsonResult().getString("state");
            statusArr[1]=this.getJsonResult().getString("finalStatus");
        }

        if (statusArr!=null){
            setJobStatusArr(statusArr);
            return 0;
        }return 1;


    }

}
