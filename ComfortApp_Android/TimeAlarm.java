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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeAlarm extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    CharSequence from = "TEMST";
    CharSequence message = "Please fill out the Comfort Data form and submit it.";
    PendingIntent contentIntent = PendingIntent.getActivity(context,  0,  new Intent(context, HvacApp2Activity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notif = new Notification(R.drawable.ic_launcher, "TEMST", System.currentTimeMillis());
    notif.setLatestEventInfo(context, from, message, contentIntent);
    notif.flags |= Notification.FLAG_AUTO_CANCEL;
    notificationManager.notify(99999, notif);

  }

}
