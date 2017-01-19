/************************************************************************
*      __   __  _    _  _____   _____
*     /  | /  || |  | ||     \ /  ___|
*    /   |/   || |__| ||    _||  |  _
*   / /|   /| ||  __  || |\ \ |  |_| |
*  /_/ |_ / |_||_|  |_||_| \_\|______|
*    
* 
*   Written by Dumidu Wijayasekara, University of Idaho (2012)
*   Copyright (2012) Modern Heuristics Research Group (MHRG)
*	University of Idaho, Virginia Commonwealth University (VCU)
*   http://www.people.vcu.edu/~mmanic/
*   Do not redistribute without author's(s') consent
*  
*   Any opinions, findings, and conclusions or recommendations expressed 
*   in this material are those of the author's(s') and do not necessarily 
*   reflect the views of any other entity.
*  
************************************************************************/

// Use the appropriate package
package com.example.hvacapp2;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import android.webkit.*;

public class ManageActivity extends Activity {
	
	ListView lv;
	Button buttonGet;
	InputStream is = null;
	StringBuilder sb = null;
    String result = null;
    String[] data;
    
    JSONArray jArray;
    
    WebView browser;
    
    Button buttonRefresh;
    Button buttonLogout;
    
    CookieManager cookieManager;

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_layout);
        browser=(WebView)findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        browser.loadUrl("https://mhrg.if.uidaho.edu/TEMST/login.php");
        browser.setWebViewClient(new Callback());
        
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        
        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new OnClickListener()
        {
          
          public void onClick(View v)
          {
            CookieSyncManager.getInstance().sync();
            cookieManager.removeExpiredCookie();
            CookieSyncManager.getInstance().sync();
            // Avoids issue of cookies being saved even though cleared
            browser.loadUrl(browser.getUrl());
          }
        });
        
        buttonLogout = (Button) findViewById(R.id.logoutButton);
        buttonLogout.setOnClickListener(new OnClickListener()
        {
          
          public void onClick(View v)
          {
            browser.loadUrl("https://mhrg.if.uidaho.edu/TEMST/logout.php");
            CookieSyncManager.getInstance().sync();
            cookieManager.removeExpiredCookie();
            CookieSyncManager.getInstance().sync();
          }
        });

        /*lv = (ListView) findViewById(R.id.listData);
        buttonGet = (Button) findViewById(R.id.buttonData);
        
        data = new String[] {"No data"};
        
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, data));
        
        buttonGet.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	            	
                Toast.makeText(ManageActivity.this, "Getting Data", Toast.LENGTH_SHORT).show();
                
                // Obtain the data from the server
                
                try {
                  // Temporary Code
                  // The following code bypasses SSL Certificate checks and should be removed in a production environment
                  HttpClient httpclient;
                  try {
                  KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                  trustStore.load(null, null);

                  SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                  sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                  HttpParams params = new BasicHttpParams();
                  HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                  HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                  SchemeRegistry registry = new SchemeRegistry();
                  registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                  registry.register(new Scheme("https", sf, 443));

                  ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                  httpclient = new DefaultHttpClient(ccm, params);
                  }catch(Exception e){httpclient = new DefaultHttpClient();}
                  // End Temporary Code
                  // The following line should be uncommented once the code above is removed
                	   //HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("https://mhrg.if.uidaho.edu/TEMST/get.php");           	
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                        
                } catch (Exception e) {
                	Toast.makeText(ManageActivity.this, e.toString(), Toast.LENGTH_LONG).show();       
                }
                
                // Convert the response to string
                try{
                	//BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
                	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                	sb = new StringBuilder();                	
                	sb.append(reader.readLine() + "\n");
                	String line="0";
                	
                	while ((line = reader.readLine()) != null){                		
                		sb.append(line + "\n");                		
                	}
                	
                	is.close();
                	
                	result = sb.toString();                      
                	
                }catch (Exception e){
                	Toast.makeText(ManageActivity.this, e.toString(), Toast.LENGTH_LONG).show();    
                }
                 
                
                //paring the data
                int fd_building;
                int fd_floor;
                int fd_room;
                int fd_heat;
                int fd_light;
                int fd_air;
                
                String [] dataIn = null;
                
                try{
            		jArray = new JSONArray(result);
            		
            		dataIn = new String[jArray.length()];
            		
            		JSONObject json_data = null;
            		
            		for(int i = 0; i < jArray.length(); i++){
                		json_data = jArray.getJSONObject(i);
                		
                		fd_building = json_data.getInt("building");
                		fd_floor = json_data.getInt("floor");
                		fd_room = json_data.getInt("room");
                		fd_heat = json_data.getInt("heat");
                		fd_light = json_data.getInt("light");
                		fd_air = json_data.getInt("air");
                		
                		String str = getBuilding(fd_building);
                		str = str.concat(", ");
                		str = str.concat(getFloor(fd_building, fd_floor));
                		str = str.concat(", Room: ");
                		str = str.concat(Integer.toString(fd_room));
                		str = str.concat(", H: ");
                		str = str.concat(Integer.toString(fd_heat));
                		str = str.concat(", L: ");
                		str = str.concat(Integer.toString(fd_light));
                		str = str.concat(", V: ");
                		str = str.concat(Integer.toString(fd_air));
                		
                		dataIn[i] = str;
                		                		
            		}
            		
            		
            	}catch (JSONException e){
            		Toast.makeText(ManageActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            	}
                                        
                lv.setAdapter(new ArrayAdapter<String>(ManageActivity.this, R.layout.list_item, dataIn));                                                                    
                                                 
            }
        });*/
    }
    
    String getBuilding(int fd_building){
    	String buildingStr = " ";
    	if (fd_building == 1){
    		buildingStr = "CAES";
    	}
    	else if (fd_building == 2){
    		buildingStr = "Banner";    	
    	}    
    	
    	return buildingStr;
    }
    
    String getFloor(int fd_building, int fd_floor){
    	String floorStr = " ";
    	if (fd_building == 1){
    		if (fd_floor == 1){
    			floorStr = "1st Fl.";
    		}
    		else if (fd_floor == 2){;
    			floorStr = "2nd Fl.";
    		}
    	}
    	else if (fd_building == 2){
    		if (fd_floor == 1){
    			floorStr = "1st Fl.";
    		}
    		else if (fd_floor == 2){
    			floorStr = "2nd Fl.";
    		}
    		else if (fd_floor == 3){
    			floorStr = "3rd Fl.";
    		}
    		else if (fd_floor == 4){
    			floorStr = "4th Fl.";
    		}
    		else if (fd_floor == 5){
    			floorStr = "5th Fl.";
    		}
    		else if (fd_floor == 6){
    			floorStr = "6th Fl.";
    		}
    		else if (fd_floor == 7){
    			floorStr = "7th Fl.";
    		}
    		else if (fd_floor == 8){
    			floorStr = "8th Fl.";
    		}
    		else if (fd_floor == 9){
    			floorStr = "9th Fl.";
    		}
    		else if (fd_floor == 10){
    			floorStr = "10th Fl.";
    		}
    		else if (fd_floor == 11){
    			floorStr = "11th Fl.";
    		}
    	}    
    	
    	return floorStr;
    }
    
    private class Callback extends WebViewClient {
      /*@Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler,
          SslError error)
      {
        // TODO Auto-generated method stub
        // super.onReceivedSslError(view, handler, error);
        //Temporary Code
        handler.proceed();
      }*/
      
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url)
      {
        // TODO Auto-generated method stub
        view.loadUrl(url);
        return true;//super.shouldOverrideUrlLoading(view, url);
      }
    }
    @Override
    protected void onResume()
    {
      super.onResume();
      CookieSyncManager.getInstance().startSync();
    }
    @Override
    protected void onPause()
    {
      super.onPause();
      CookieSyncManager.getInstance().stopSync();
    }
}