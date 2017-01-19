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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class InfoActivity extends Activity {
  WebView browser;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.info_layout);
        browser = (WebView)findViewById(R.id.webView2);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        browser.loadUrl("https://mhrg.if.uidaho.edu/EnergyDetail/TEMSTFAQ.shtml");
        browser.setWebViewClient(new Callback());
    }
    
    private class Callback extends WebViewClient {      
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url)
      {
        // TODO Auto-generated method stub
        view.loadUrl(url);
        return true;
      }
    }
}
