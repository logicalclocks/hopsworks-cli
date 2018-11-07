package io.hops.upload.beans;

public class Server {

    private String apiEndpoint;
    private int port;
    boolean authentication;
    String path;

    public Server(String apiEndpoint,int port,boolean authentication,String path){

        this.apiEndpoint = apiEndpoint;
        this.port = port;
        this.authentication = authentication;
        this.path = path;

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public int getPort() {
        return port;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public void setIpAddress(String ipAddress) {
        this.apiEndpoint = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAPIUrl(){
        return this.apiEndpoint + ":"+this.port+this.path;
    }

    public String generateAPIUrl(String apiPath){
        return this.getAPIUrl() + apiPath;
    }

}
