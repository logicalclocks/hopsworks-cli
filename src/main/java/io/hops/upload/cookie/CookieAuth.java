package io.hops.upload.cookie;

import io.hops.upload.beans.Server;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CookieAuth {

    private String authUrl;
    private String email;
//    private String password;
    private String apiKey;

    private Server server;
    private static final Logger logger = LoggerFactory.getLogger(CookieAuth.class);


//    public CookieAuth(String authUrl,String email,String password){
//        this.authUrl = authUrl;
//        this.email = email;
//        this.password = password;
//
//    }

    protected HttpClient getClient() {
        HttpClient getClient = null;
        try {
            getClient = HttpClients
                    .custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            TrustSelfSignedStrategy.INSTANCE).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return getClient;

    }


//    public CookieAuth(String authUrl,String email,String password){
    public CookieAuth(String authUrl,String email, String apiKey){
        this.authUrl = authUrl;
        this.email = email;
        this.apiKey = apiKey;

    }
    private String toStringHttpEntity(HttpEntity entity){

        ByteArrayOutputStream out = new ByteArrayOutputStream((int) entity.getContentLength());

        String entityAsString = "";
        try {
            entity.writeTo(out);
            entityAsString = out.toString();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entityAsString;

    }

    private void printCookies(List<Cookie> cookies) throws IOException {

        for (Iterator<Cookie> iter = cookies.listIterator(); iter.hasNext(); ) {
            Cookie cookie = iter.next();
            logger.info(cookie.toString());

        }

    }

    public List<Cookie> auth() throws IOException {
//            CloseableHttpClient client = HttpClients.createDefault();
            HttpClient client = getClient();
            HttpPost httpPost = new HttpPost(this.authUrl );

        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", this.email));
//            params.add(new BasicNameValuePair("password", this.password));
            params.add(new BasicNameValuePair("otp", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

        httpPost.addHeader("Accept" , "application/json, text/plain, */*");
        httpPost.addHeader("Accept-Language" , "en-US,en;q=0.5");
        httpPost.addHeader("Accept-Encoding" , "gzip, deflate");
        httpPost.addHeader("Content-Type" , "application/x-www-form-urlencoded");

        httpPost.addHeader("Authorization" , "ApiKey " + this.apiKey);

        httpPost.addHeader("DNT","1");
        httpPost.addHeader("Connection","keep-alive");
        this.toStringHttpEntity(httpPost.getEntity());


        HttpClientContext context = HttpClientContext.create();

//            CloseableHttpResponse response = client.execute(httpPost,context);
        HttpResponse response = client.execute(httpPost);
        List<Cookie> cookies;
//        try {
            CookieStore cookieStore = context.getCookieStore();

            cookies = cookieStore.getCookies();
//        } finally {
//            response.close();
//        }

        logger.info(" Cookie Auth @ "+ this.authUrl);
        this.printCookies(cookies);
        logger.info("Status Code: " + response.getStatusLine().getStatusCode() + "\n\n");


//        client.close();

        return cookies;

    }
}
