package io.hops.upload.beans;

public class AuthData {

    private String email;
    private String password;
    private String authPath;

    public AuthData(String email,String password,String authPath){
        this.email = email;
        this.password = password;
        this.authPath = authPath;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

    public String getAuthPath() {
        return authPath;
    }
}
