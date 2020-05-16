package io.hops.upload.beans;

public class AuthData {

//    private String email;
//    private String password;
    private String authPath;
//    private String jwt;
    private String apiKey;

//    public AuthData(String email,String password,String authPath){
//        this.email = email;
//        this.password = password;
//        this.authPath = authPath;
//    }
//    public AuthData(String email,String password,String authPath, String jwt){
//        this.email = email;
//        this.password = password;
//        this.authPath = authPath;
//        this.jwt = jwt;
//    }
    public AuthData(String apiKey,String authPath){
        this.apiKey = apiKey;
        this.authPath = authPath;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

    public String getAuthPath() {
        return authPath;
    }
    
//    public String getJwt() {
//        return jwt;
//    }
//
//    public void setJwt(String jwt) {
//        this.jwt = jwt;
//    }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
