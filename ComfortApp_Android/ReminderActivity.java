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

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReminderActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reminder);
    
alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    
    SharedPreferences prefs = getPreferences(0);
    
    startTime = (TimePicker)findViewById(R.id.timePicker1);
    // Set to Start time to 9 AM or saved time
    startTime.setCurrentHour(prefs.getInt("startHour",9));
    startTime.setCurrentMinute(prefs.getInt("startMinute", 0));
    
    endTime = (TimePicker)findViewById(R.id.timePicker2);
    // Set to End time to 5 PM or saved time
    endTime.setCurrentHour(prefs.getInt("endHour", 17));
    endTime.setCurrentMinute(prefs.getInt("endMinute", 0));
    
    checkSunday = (CheckBox)findViewById(R.id.checkBox1);
    checkMonday = (CheckBox)findViewById(R.id.checkBox2);
    checkTuesday = (CheckBox)findViewById(R.id.checkBox3);
    checkWednesday = (CheckBox)findViewById(R.id.checkBox4);
    checkThursday = (CheckBox)findViewById(R.id.checkBox5);
    checkFriday = (CheckBox)findViewById(R.id.checkBox6);
    checkSaturday = (CheckBox)findViewById(R.id.checkBox7);
    
    // Select Monday - Friday by default or saved days
    checkSunday.setChecked(prefs.getBoolean("sundayCheck", false));
    checkMonday.setChecked(prefs.getBoolean("mondayCheck", true));
    checkTuesday.setChecked(prefs.getBoolean("tuesdayCheck", true));
    checkWednesday.setChecked(prefs.getBoolean("wednesdayCheck", true));
    checkThursday.setChecked(prefs.getBoolean("thursdayCheck", true));
    checkFriday.setChecked(prefs.getBoolean("fridayCheck", true));
    checkSaturday.setChecked(prefs.getBoolean("saturdayCheck", false));
    
    checkUseNotifications = (CheckBox)findViewById(R.id.checkBox8);

    // Select to use notifications by default or saved option
    useNotifications = prefs.getBoolean("useNotifications", true);
    checkUseNotifications.setChecked(useNotifications);
    setIntent(useNotifications);
    
    spinnerFrequency = (Spinner) findViewById(R.id.spinner1);
    ArrayAdapter<CharSequence> adapterFrequency =  ArrayAdapter.createFromResource(this,
        R.array.timeIncrements, android.R.layout.simple_spinner_item);
    adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerFrequency.setAdapter(adapterFrequency);
    spinnerFrequency.setSelection(prefs.getInt("frequencyIndex", 0));
    
    startReminders = (Button) findViewById(R.id.button1);
    cancelReminders = (Button) findViewById(R.id.button2);
    
    reminderStatus = (TextView) findViewById(R.id.textView7);
    
    updateStatus();
    
    startReminders.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        Calendar startTemp = Calendar.getInstance();
        startTemp.set(1970, 0, 1, startTime.getCurrentHour(), startTime.getCurrentMinute(), 0);
        Calendar endTemp = Calendar.getInstance();
        endTemp.set(startTemp.get(Calendar.YEAR), startTemp.get(Calendar.MONTH), startTemp.get(Calendar.DAY_OF_MONTH), endTime.getCurrentHour(), endTime.getCurrentMinute(), 0);
        if(endTemp.before(startTemp))
        {
          // Invalid
          Toast.makeText(getApplicationContext(), "The end time must be after the start time", Toast.LENGTH_LONG).show();
          return;
        }
       
        SharedPreferences prefs = getPreferences(0);
        
        int numberPerDay = 0;
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(startTemp.getTime());
        int hoursToAdd = 0;
        switch(spinnerFrequency.getSelectedItemPosition())
        {
          case 0:
            hoursToAdd = 1;
            break;
          case 1:
            hoursToAdd = 2;
            break;
          case 2:
            hoursToAdd = 4;
          case 3:
            hoursToAdd = 8;
          default:
            hoursToAdd = 16;
        }
        
        int numberOfDays = 0;
        if(checkSunday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkMonday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkTuesday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkWednesday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkThursday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkFriday.isChecked())
        {
          ++numberOfDays;
        }
        if(checkSaturday.isChecked())
        {
          ++numberOfDays;
        }
        
        while(tempCal.before(endTemp))
        {
          ++numberPerDay;
          tempCal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
        }
        
        Calendar cal = Calendar.getInstance();
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH), currentDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.HOUR_OF_DAY, startTime.getCurrentHour());
        cal.set(Calendar.MINUTE, startTime.getCurrentMinute());
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_WEEK) - 1));
        Date sunday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          sunday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          sunday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date monday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          monday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          monday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tuesday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          tuesday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          tuesday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date wednesday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          wednesday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          wednesday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date thursday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          thursday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          thursday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date friday;
        if(cal.before(currentDay))
        {
          cal.add(Calendar.DAY_OF_MONTH, 7);
          friday = cal.getTime();
          cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        else
        {
          friday = cal.getTime();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        // Saturday can never be "before" current day, no need to check
        Date saturday = cal.getTime();
        
        // Cancel the old notifications
        cancelNotifications();
        
        useNotifications = checkUseNotifications.isChecked();
        
        int startId = 0;
        // Add new notifications
        if(checkSunday.isChecked())
        {
          setNotifications(sunday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        if(checkMonday.isChecked())
        {
          setNotifications(monday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;          
        }
        if(checkTuesday.isChecked())
        {
          setNotifications(tuesday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        if(checkWednesday.isChecked())
        {
          setNotifications(wednesday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        if(checkThursday.isChecked())
        {
          setNotifications(thursday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        if(checkFriday.isChecked())
        {
          setNotifications(friday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        if(checkSaturday.isChecked())
        {
          setNotifications(saturday, hoursToAdd, numberPerDay, startId);
          startId += numberPerDay;
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("HighestIndex", numberPerDay * numberOfDays);
        editor.putBoolean("sundayCheck", checkSunday.isChecked());
        editor.putBoolean("mondayCheck", checkMonday.isChecked());
        editor.putBoolean("tuesdayCheck", checkTuesday.isChecked());
        editor.putBoolean("wednesdayCheck", checkWednesday.isChecked());
        editor.putBoolean("thursdayCheck", checkThursday.isChecked());
        editor.putBoolean("fridayCheck", checkFriday.isChecked());
        editor.putBoolean("saturdayCheck", checkSaturday.isChecked());
        editor.putInt("frequencyIndex", spinnerFrequency.getSelectedItemPosition());
        editor.putBoolean("useNotifications", checkUseNotifications.isChecked());
        editor.putInt("startHour", startTime.getCurrentHour());
        editor.putInt("startMinute", startTime.getCurrentMinute());
        editor.putInt("endHour", endTime.getCurrentHour());
        editor.putInt("endMinute", endTime.getCurrentMinute());
        editor.commit();
        
        updateStatus();
      }
    });
  
    cancelReminders.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        cancelNotifications();
        updateStatus();
      }
    });
  }
  
  private void cancelNotifications()
  {
    // Cancel all current notifications
    SharedPreferences prefs = getPreferences(0);
    int oldHigh = prefs.getInt("HighestIndex", 0);
    for(int i = 0; i < oldHigh; ++i)
    {
      PendingIntent pendingIntent = getPendingIntent(i, PendingIntent.FLAG_CANCEL_CURRENT);
      alarmManager.cancel(pendingIntent);
      pendingIntent.cancel();
    }
    if(getPendingIntent(0, PendingIntent.FLAG_NO_CREATE) == null)
    {
      // Change the highest index to 0
      SharedPreferences.Editor editor = prefs.edit();
      editor.putInt("HighestIndex", 0);
      editor.commit();
    }
  }
  
  private void setNotifications(Date dayWithStartTime, int hourChange, int numberOfNotifications, int startId)
  {
    long week = AlarmManager.INTERVAL_DAY * 7;
    long triggerStartTime = dayWithStartTime.getTime();
    //DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    Date current = new Date();
    for(int i = 0; i < numberOfNotifications; ++i)
    {
      long currentStartTime = triggerStartTime +
                              i * hourChange * AlarmManager.INTERVAL_HOUR;
      if(currentStartTime < current.getTime())
      {
        // Happened in the past, increment by a week
        currentStartTime += week;
      }
      PendingIntent pendingIntent = getPendingIntent(startId + i, PendingIntent.FLAG_UPDATE_CURRENT);
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentStartTime, week, pendingIntent);
    }
  }
  
  private void updateStatus()
  {
    SharedPreferences prefs = getPreferences(0);
    int oldHigh = prefs.getInt("HighestIndex", 0);
    
    if(oldHigh < 1 || getPendingIntent(0, PendingIntent.FLAG_NO_CREATE) == null)
    {
      // Alarms deactivated
      reminderStatus.setText("No reminders are currently set in the system.");
      if(oldHigh > 0)
      {
        // Change the highest index to save canceling "dead" alarms
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("HighestIndex", 0);
        editor.commit();
      }
    }
    else
    {
      // Alarms active
      reminderStatus.setText("Reminders are currently set.");
    }
  }
  
  private void setIntent(boolean useNotifications)
  {
    if(useNotifications)
    {
      intent = new Intent(this, TimeAlarm.class);
    }
    else
    {
      intent = new Intent(this, HvacApp2Activity.class);
    }
  }
  
  private PendingIntent getPendingIntent(int id, int flags)
  {
    if(useNotifications)
    {
      return PendingIntent.getBroadcast(this, id, intent, flags);
    }
    else
    {
      return PendingIntent.getActivity(this, id, intent, flags);
    }
  }
  
  private TimePicker startTime;
  private TimePicker endTime;
  
  private CheckBox checkSunday;
  private CheckBox checkMonday;
  private CheckBox checkTuesday;
  private CheckBox checkWednesday;
  private CheckBox checkThursday;
  private CheckBox checkFriday;
  private CheckBox checkSaturday;
  
  private CheckBox checkUseNotifications;
  
  private Spinner spinnerFrequency;
  
  private Button startReminders;
  private Button cancelReminders;
  
  private AlarmManager alarmManager;
  private Intent intent;
  
  private boolean useNotifications;
  
  private TextView reminderStatus;

}
