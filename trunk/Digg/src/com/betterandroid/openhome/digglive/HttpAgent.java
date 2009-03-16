package com.betterandroid.openhome.digglive;

import java.io.InputStream;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParamBean;

import android.util.Config;
import android.util.Log;

public class HttpAgent{
  
    private HttpClient cliet = new DefaultHttpClient();

    public HttpAgent(){
      HttpConnectionParamBean bean = new HttpConnectionParamBean(cliet.getParams());
      bean.setSoTimeout(5000);
      bean.setConnectionTimeout(5000);
    }
    
    public InputStream getUrl(String url) throws HttpException{
      
      HttpGet get = new HttpGet(url);
      get.setHeader("Accept-Language", "en_US");
      get.setHeader("Accept", "application/xml");
            
      HttpResponse res;
      try {
        if(Config.DEBUG){
          Log.d(HttpAgent.class.toString(), url);          
        }        
        res = cliet.execute(get);
        return res.getEntity().getContent();
      } catch (Exception e) {
        get.abort();
        throw new HttpException("Http request failed", e);
      }
    }
    
}