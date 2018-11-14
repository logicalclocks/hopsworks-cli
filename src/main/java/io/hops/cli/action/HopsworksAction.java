package io.hops.cli.action;

import io.hops.upload.beans.AuthData;
import io.hops.upload.beans.Server;
import io.hops.upload.cookie.CookieAuth;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.List;

public abstract class HopsworksAction {
  
  private Server httpServer;
  private AuthData authData;
  
  public HopsworksAction(String apiEndpoint, int port, boolean authentication, String path){
    this.httpServer = new Server(apiEndpoint, port, authentication, path);
  }
  
  public void activateAuth(String email, String password, String authPath) {
    authData = new AuthData(email, password, authPath);
    httpServer.setAuthentication(true);
  }
  
  public void activateAuth(String email, String password, String authPath, String jwt) {
    authData = new AuthData(email, password, authPath, jwt);
    httpServer.setAuthentication(true);
  }
  
  public List<Cookie> auth() throws IOException {
    CookieAuth cookieAuth = new CookieAuth(httpServer.getAPIUrl() + authData.getAuthPath(),
      authData.getEmail(), authData.getPassword());
    return cookieAuth.auth();
  }
  
  public HttpContext generateContextWithCookies(List<Cookie> cookies) {
    CookieStore cookieStore = new BasicCookieStore();
    for (int i = 0; i < cookies.size(); i++) {
      cookieStore.addCookie(cookies.get(i));
    }
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    return localContext;
    
  }
  
  public Server getHttpServer() {
    return httpServer;
  }
  
  public AuthData getAuthData() {
    return authData;
  }
  
  /**
   * Send http request.
   *
   * @return http status code.
   */
  public abstract int execute() throws Exception;
}
