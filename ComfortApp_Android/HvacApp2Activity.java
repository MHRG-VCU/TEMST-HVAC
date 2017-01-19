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

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TabHost;

public class HvacApp2Activity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getPreferences(0);

        if(prefs.getBoolean("Accepted", false))
        {
          create_normal();
        }
        else
        {
          WebView browser;
          
          Button buttonAccept;
          Button buttonCancel;
          
          setContentView(R.layout.main2);
          
          browser = (WebView)findViewById(R.id.webViewMain);
          browser.loadData("<html><head><title>Agreement</title></head><body><h2>Project Title:</h2><p>Targeted Energy Management Toolset, Human Comfort Evaluation</p><p>The Idaho National Laboratory Institutional Review Board has approved this project.</p><br /><h2>Why have you been invited to participate?</h2><p>You are being asked to participate in a study designed to explore building design and operation practices. This study will evaluate indoor environmental quality and energy use in a selection of old, new and renovated buildings. The information on this page is intended to help you understand exactly what we are asking of you so that you can decide whether or not you want to participate in this study. Please read this consent form carefully before deciding whether or not to participate or not in this study. Please take whatever time you want before reaching a decision. Your participation in this study is entirely voluntary, and a decision not to participate will not in any way be used against you.</p> <h2>Project team and sponsors:</h2> <p>The project is led by Dr. Craig Rieger, Idaho National Laboratory. Other team investigators include Prof. Milos Manic of the University of Idaho, Dr. Kevin Van Den Wymelenberg of the University of Idaho's Integrated Design Laboratory.</p> <h2>Why is the study being done?</h2> <p>With increasing attention being paid to environmental sustainability, various building design and operation strategies have been adopted to try to reduce building energy consumption while maintaining or improving indoor environmental conditions. This study will compare the performance of a wide variety of office buildings to assess which of these strategies is most effective.</p> <h2>What will you be asked to do?</h2> <p>You are invited to participate in a SmartPhone feedback survey that is part of this project. The survey includes questions about your satisfaction with your office and your work. Participation allows for ongoing feedback of the conditions of your work space, which may also include direct measurement of conditions. Participation in this research is voluntary and whether you choose to participate or not is entirely your decision.</p> <h2>Potential harms / inconveniences / benefits</h2> <p>There are no known harms associated with your participation in this research. You will not benefit directly from your participation in this study, but you will contribute to the development of knowledge about how to better design and operate buildings. You will receive a report at the end of the study.</p> <h2>Privacy and confidentiality</h2> <p>All data will be transmitted by a secure, encrypted internet connection and stored on a server at the University of Idaho. Only authorized project team personnel will have access to the raw data. All information gathered from you will be confidential. Unless required by law, no information that might directly or indirectly reveal your identity will be released or published without your specific consent to the disclosure. University of Idaho and Idaho National Laboratory Institutional Review Board will have access to the individual data, for monitoring purposes. Your employer will not be given access to the individual responses.</p> <h2>You have the right to change your mind:</h2> <p>Your participation is entirely voluntary. Should you decide to participate in this research, you always have the right to end your participation at any time and for any reason. You may do so by deleting the application from your SmartPhone and returning any measurement device that was provided.</p> <h2>Who to contact if you have any further concerns or questions?</h2> <p>Should you have any concerns or questions please contact Craig Rieger at 208-526-4136 (craig.rieger@inl.gov) or Milos Manic at 208-533-8122 (misko@uidaho.edu).</p> <h2>Ethics review</h2> <p>This study has been reviewed and approved by the Idaho National Laboratory Institutional Review Board, as protocol INL-12-008. Any questions or concerns about the ethics of this study may be directed to Dena Tomchak, 208-526-1590 (dena.tomchak@inl.gov).</p> <h2>How to participate</h2> <p>If you agree to participate in this survey, please select the \"Accept\" option below.</p> </body> </html>", "text/html", null);
          buttonAccept = (Button)findViewById(R.id.buttonAccept);
          buttonAccept.setOnClickListener(new OnClickListener()
          {
            
            public void onClick(View v)
            {
              SharedPreferences prefs = getPreferences(0);
              SharedPreferences.Editor editor = prefs.edit();
              editor.putBoolean("Accepted", true);
              editor.commit();
              create_normal();
            }
          });
          buttonCancel = (Button)findViewById(R.id.buttonCancel);
          buttonCancel.setOnClickListener(new OnClickListener()
          {
            
            public void onClick(View v)
            {
              finish();
              System.exit(0);
            }
          });
        }
    }
    private void create_normal()
    {
      setContentView(R.layout.main);

      Resources res = getResources(); // Resource object to get Drawables
      TabHost tabHost = getTabHost();  // The activity TabHost
      TabHost.TabSpec spec;  // Reusable TabSpec for each tab
      Intent intent;  // Reusable Intent for each tab

      // Create an Intent to launch an Activity for the tab (to be reused)
      intent = new Intent().setClass(this, InfoActivity.class);

      // Initialize a TabSpec for each tab and add it to the TabHost
      spec = tabHost.newTabSpec("info").setIndicator("App Info",
                        res.getDrawable(R.drawable.info))
                    .setContent(intent);
      tabHost.addTab(spec);

      // Do the same for the other tabs
      intent = new Intent().setClass(this, ComfortActivity.class);
      spec = tabHost.newTabSpec("comf").setIndicator("Comfort",
                        res.getDrawable(R.drawable.comf))
                    .setContent(intent);
      tabHost.addTab(spec);

      intent = new Intent().setClass(this, FailActivity.class);
      spec = tabHost.newTabSpec("fail").setIndicator("Failure",
                        res.getDrawable(R.drawable.fail))
                    .setContent(intent);
      tabHost.addTab(spec);
      
      intent = new Intent().setClass(this, ReminderActivity.class);
      spec = tabHost.newTabSpec("reminder").setIndicator("Schedule Reminders")
                    .setContent(intent);
      tabHost.addTab(spec);
      
      intent = new Intent().setClass(this, ManageActivity.class);
      spec = tabHost.newTabSpec("manage").setIndicator("Manage",
                        res.getDrawable(R.drawable.manage))
                    .setContent(intent);
      tabHost.addTab(spec);
      

      tabHost.setCurrentTab(0);
    }
}