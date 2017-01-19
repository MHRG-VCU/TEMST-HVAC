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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

//import javax.net.ssl.TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class ComfortActivity extends Activity
{

  public void updateRequestStatus(boolean error)
  {
    if(error)
    {
      if(!success)
      {
        Toast.makeText(getApplicationContext(), "An error occurred while getting the information.  Please try again.", Toast.LENGTH_LONG).show();
      }
    }
    else
    {
      Toast.makeText(getApplicationContext(), "Information Received", Toast.LENGTH_SHORT).show();
    }
  }
  
  public void updateRequestStatus()
  {
    updateRequestStatus(false);
  }
  void sendRequest()
  {
    if (comm != null)
    {
      try
      {
        Toast.makeText(getApplicationContext(), "Sending the request",
            Toast.LENGTH_SHORT).show();
        comm.write("r".getBytes("UTF-8"));
        ComfortActivity.success = false;
      } catch (Exception e)
      {
        Toast.makeText(getApplicationContext(),
            "An error occurred sending the request", Toast.LENGTH_LONG)
            .show();
      }
    } else
    {
      Toast.makeText(getApplicationContext(), "No device is connected",
          Toast.LENGTH_LONG).show();
    }
  }
  ArrayAdapter<CharSequence> adapterFloorEmpty;
  /*ArrayAdapter<CharSequence> adapterFloorCAES;
  ArrayAdapter<CharSequence> adapterFloorBanner;*/
  ArrayAdapter<CharSequence> adapterFloorUB4;
  ArrayAdapter<CharSequence> adapterRoomEmpty;
  /*ArrayAdapter<CharSequence> adapterRoomCAES1;
  ArrayAdapter<CharSequence> adapterRoomCAES2;
  ArrayAdapter<CharSequence> adapterRoomBanner1;
  ArrayAdapter<CharSequence> adapterRoomBanner2;
  ArrayAdapter<CharSequence> adapterRoomBanner3;
  ArrayAdapter<CharSequence> adapterRoomBanner4;
  ArrayAdapter<CharSequence> adapterRoomBanner5;
  ArrayAdapter<CharSequence> adapterRoomBanner6;
  ArrayAdapter<CharSequence> adapterRoomBanner7;
  ArrayAdapter<CharSequence> adapterRoomBanner8;
  ArrayAdapter<CharSequence> adapterRoomBanner9;
  ArrayAdapter<CharSequence> adapterRoomBanner10;
  ArrayAdapter<CharSequence> adapterRoomBanner11;*/
  ArrayAdapter<CharSequence> adapterRoomUB4_1;

  Spinner spinnerBuilding;
  Spinner spinnerFloor;
  Spinner spinnerRoom;

  Button buttonHome;
  Button buttonSetHome;
  Button buttonRes;
  Button buttonSub;
  Button buttonCurrentData;

  private int mTempState = 50;
  private SeekBar tempBar;

  private int mLightState = 50;
  private SeekBar lightBar;;

  private int mVentState = 50;
  private SeekBar ventBar;

  private String FILENAME = "hvacSaveComf";
  private String FILENAME2 = "hvacSaveHome";

  // home location
  int mHomeBuilding = 0;
  int mHomeFloor = 0;
  int mHomeRoom = 0;
  boolean mHomeSet = false;

  // Connection UUID
  private static final UUID MY_UUID = UUID
      .fromString("00001101-0000-1000-8000-00805F9B34FB");

  public BluetoothAdapter mBluetoothAdapter = null;

  EditText text_Out;
  TextView text_TM;
  TextView text_RH;
  TextView text_LT;

  String str = null;
  String str1 = null;
  String[] str_split = null;
  String str_TM = null;// " 22.0";//
  String str_RH = null;// " 62.2\0";//
  String str_LT = null;// " 326 ";//

  private Handler handler;

  ListView BTList;

  ArrayAdapter<String> mArrayAdapter;

  final int REQUEST_ENABLE_BT = 1;

  Set<BluetoothDevice> pairedDevices;

  boolean pairedDev = false;

  // Connection thread
  ConnectThread con;

  // Communication thread
  CommThread comm;

  // ------------------------- MENU ---------------------------
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.icon: // Cancel discovery because it will slow down the
                      // connection
        if (mBluetoothAdapter != null)
        {
          mBluetoothAdapter.cancelDiscovery();
          pairedDev = true;
          pairedDevices = mBluetoothAdapter.getBondedDevices();
          // If there are paired devices
          if (pairedDevices != null && pairedDevices.size() > 0)
          {
            // Loop through paired devices

            mArrayAdapter.clear();

            for (BluetoothDevice device : pairedDevices)
            {
              // Add the name and address to an array adapter to show in a
              // ListView
              mArrayAdapter.add(device.getName() + " : " + device.getAddress());
            }

            BTList.setAdapter(mArrayAdapter);

          }
        }
        break;

      case R.id.text:
        pairedDev = false;
        mArrayAdapter.clear();
        if (mBluetoothAdapter.startDiscovery())
        {
          Toast.makeText(ComfortActivity.this, "BT Dev. discovery initiated",
              Toast.LENGTH_SHORT).show();
        }

        if (mBluetoothAdapter.isDiscovering())
        {
          System.out.println("BT is discovering");
        } else
        {
          System.out.println("Nothing is happening");
        }
        break;

      case R.id.icontext:
        mArrayAdapter.clear();
        try
        {
          if(con != null && con.mmSocket != null)
          {
            con.mmSocket.close();
          }
        } catch (IOException e)
        {
          System.out.println("Error: " + e.getMessage());
        }

        break;
    }
    return true;
  }

  // ----------------------------------------------------------

  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {

    public void onReceive(Context context, Intent intent)
    {
      String action = intent.getAction();

      System.out.println("Something received");
      // When discovery finds a device
      if (BluetoothDevice.ACTION_FOUND.equals(action))
      {
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent
            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        // Add the name and address to an array adapter to show in a ListView
        mArrayAdapter.add(device.getName() + " : " + device.getAddress());

        System.out.println("Discovered! " + device.getName() + " : "
            + device.getAddress());

        BTList.setAdapter(mArrayAdapter);
      }
      if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
      {

        System.out.println("Discovery started!");

      }

      if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
      {

        System.out.println("Discovery stopped!");

      }
    }
  };

  /*
   * @Override protected void onResume(){ super.onResume(); }
   * 
   * @Override protected void onPause(){ super.onPause(); }
   */

  @Override
  protected void onDestroy()
  {
    if (this.mReceiver != null)
    {
      this.unregisterReceiver(mReceiver);
      this.mReceiver = null;
    }

    super.onDestroy();
  }

  // Manages the BT connection to the server
  public synchronized void manageComm(BluetoothSocket mmSocket)
  {
    comm = new CommThread(mmSocket);
    comm.start();
  }

  public void showToast(String str)
  {
    Toast.makeText(ComfortActivity.this, str, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent)
  {
    super.onActivityResult(requestCode, resultCode, intent);

    switch (requestCode)
    {
      case REQUEST_ENABLE_BT:
        if (resultCode == RESULT_OK)
        {
          Toast.makeText(ComfortActivity.this, "BT was enabled succesfully",
              Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED)
        {
          System.out.println("Enabling BT did not succeed. Exiting!");
          this.finish();
        }
    }
  }
  
  @Override
  public void finish()
  {
    comm.destroy();
    super.finish();
  }

  private class ConnectThread extends Thread
  {
    private final BluetoothSocket mmSocket;
    public final BluetoothDevice mmDevice;
    
    @Override
    public void destroy()
    {
      if(mmSocket != null)
      {
        try
        {
          mmSocket.close();
        }
        catch(Exception e)
        {
          
        }
      }
    }

    public ConnectThread(BluetoothDevice device)
    {
      // Use a temporary object that is later assigned to mmSocket,
      // because mmSocket is final
      BluetoothSocket tmp = null;
      mmDevice = device;

      // Toast.makeText(getApplicationContext(),
      // "Attempting to create socket with " + device.getName(),
      // Toast.LENGTH_LONG).show();
      // Get a BluetoothSocket to connect with the given BluetoothDevice
      try
      {

        // MY_UUID is the app's UUID string, also used by the server code
        tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        // Toast.makeText(getApplicationContext(), "Connection successful",
        // Toast.LENGTH_LONG).show();

      } catch (Exception e)
      {
        System.out.println("Creating socket failed: " + e.getMessage());
      }
      mmSocket = tmp;
    }

    public void run()
    {
      // Cancel discovery because it will slow down the connection
      mBluetoothAdapter.cancelDiscovery();

      System.out.println("Attempting connection.");
      try
      {
        // Connect the device through the socket. This will block
        // until it succeeds or throws an exception
        mmSocket.connect();
      } catch (IOException connectException)
      {
        // Unable to connect; close the socket and get out
        System.out.println("Unable to connect: "
            + connectException.getMessage());
        try
        {
          mmSocket.close();
        } catch (IOException closeException)
        {
          System.out.println("Error closing socket: "
              + closeException.getMessage());
        }
        return;
      }

      handler.post(new Runnable()
      {

        public void run()
        {
          showToast("Sucesfully connected");
        }
      });
      System.out.println("We are connected");
      // Do work to manage the connection (in a separate thread)
      // manageConnectedSocket(mmSocket);
      manageComm(mmSocket);
      try
      {
      sleep(2000);
      }
      catch(Exception e)
      {
        
      }
      handler.post(new Runnable()
      {

        public void run()
        {
          sendRequest();
        }
      });
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel()
    {
      try
      {
        mmSocket.close();
      } catch (IOException e)
      {
      }

      handler.post(new Runnable()
      {

        public void run()
        {
          showToast("Connection canceled");
        }
      });
    }
  }

  private class CommThread extends Thread
  {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    
    @Override
    public void destroy()
    {
      if(mmInStream != null)
      {
        try {
          mmInStream.close();
        }
        catch(Exception e)
        {
          
        }
      }
      
      if(mmOutStream != null)
      {
        try {
          mmOutStream.close();
        }
        catch(Exception e)
        {
          
        }
      }
      if(mmSocket != null)
      {
        try {
          mmSocket.close();
        }
        catch(Exception e)
        {
          
        }
      }
    }

    public CommThread(BluetoothSocket socket)
    {
      mmSocket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;

      // Get the input and output streams, using temp objects because
      // member streams are final
      try
      {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e)
      {
      }

      mmInStream = tmpIn;
      mmOutStream = tmpOut;
    }

    public void run()
    {
      int tempBytes = 0;
      StringBuilder builder = new StringBuilder();
      
      // Keep listening to the InputStream until an exception occurs
      while (true)
      {
        builder.setLength(0);
        
        tempBytes = 0;
        try
        {
          // Read from the InputStream
          try
          {
            Thread.sleep(750);
          } catch (InterruptedException e1)
          {
            e1.printStackTrace();
          }
          tempBytes = mmInStream.read();
          
          for(int i = 0; i < tempBytes; ++i)
          {
            builder.append((char)mmInStream.read());
          }
            
          str = builder.toString();
          handler.post(new Runnable()
          {

            public void run()
            {
              str_TM = str_RH = str_LT = "";
              try
              {

                str_split = str.split(",");
                str_TM = str_split[0];
                str_RH = str_split[1];
                str_LT = str_split[2];

                text_TM.setText(str_TM + "\u02DA" + "F");
                text_RH.setText(str_RH + "\u0025");
                text_LT.setText(str_LT + " lx");
                ComfortActivity.success = true;
                updateRequestStatus();

              } catch (Exception e)
              {
                updateRequestStatus(true);
              }
            }
          });
        } catch (IOException e)
        {
          break;
        }
      }
      /*//final byte[] buffer = new byte[1024]; // buffer store for the stream
      final char[] buffer = new char[1024];
      int bytes; // bytes returned from read()
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mmInStream));
      
      int tempBytes = 0;
      
      boolean read = true;//false;

      // Keep listening to the InputStream until an exception occurs
      while (true)
      {
        //read = !read;
        bytes = 0;
        try
        {
          // Read from the InputStream
          try
          {
            Thread.sleep(750);
          } catch (InterruptedException e1)
          {
            e1.printStackTrace();
          }
//          do {
//            tempBytes = mmInStream.read(buffer, bytes, buffer.length - bytes);
//            bytes += tempBytes;
//            //bytes = mmInStream.read(buffer);
          //} while(tempBytes > 0);
          bytes = bufferedReader.read(buffer);
          
          final int n = bytes - 1;
          // Send the obtained bytes to the UI activity
          // mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
          // buffer).sendToTarget();
            handler.post(new Runnable()
            {

              public void run()
              {
                str_TM = str_RH = str_LT = "";
                try
                {
                  //str = new String(buffer, "UTF-8");
                  str = new String(buffer);
                  str = str.substring(0, n);

                  str_split = str.split("\n");
                  str_TM = str_split[2];
                  str_RH = str_split[3];
                  str_LT = str_split[4];

                  str_split = str_TM.split("TM ");
                  str_TM = str_split[1];
                  str_split = str_RH.split("RH ");
                  str_RH = str_split[1];
                  if (str_RH.charAt(str_RH.length() - 1) == '\0')
                  {
                    str_RH = str_RH.substring(0, str_RH.length() - 1);
                  }
                  str_split = str_LT.split("LT ");
                  str_LT = str_split[1];

                  //text_TM.setText(str_TM + "\u02DA" + "F");
                  //text_RH.setText(str_RH + "\u0025");
                  //text_LT.setText(str_LT + " lx");
                  ComfortActivity.success = true;
                  updateRequestStatus();

                } catch (Exception e)
                {
                  System.out.println("Error encoding: " + e.getMessage());
                  System.out.println("Message Length: " + n);
                  System.out.println("Message: " + new String(buffer));
                  updateRequestStatus(true);
                }
              }
            });
        } catch (IOException e)
        {
          break;
        }
      }*/
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes)
    {
      try
      {
        mmOutStream.write(bytes);
      } catch (IOException e)
      {
      }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel()
    {
      try
      {
        mmSocket.close();
      } catch (IOException e)
      {
      }
    }
  }

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.comf_layout);

    // Obtain the identifiers of layout components
    tempBar = (SeekBar) findViewById(R.id.seekBarTemp);
    lightBar = (SeekBar) findViewById(R.id.seekBarLight);
    ventBar = (SeekBar) findViewById(R.id.seekBarVent);
    tempBar.setProgress(tempBar.getMax() / 2);
    lightBar.setProgress(lightBar.getMax() / 2);
    ventBar.setProgress(ventBar.getMax() / 2);
    mTempState = tempBar.getProgress();
    mLightState = lightBar.getProgress();
    mVentState = ventBar.getProgress();

    spinnerBuilding = (Spinner) findViewById(R.id.spinnerBuilding);
    spinnerFloor = (Spinner) findViewById(R.id.spinnerFloor);
    spinnerRoom = (Spinner) findViewById(R.id.spinnerRoom);

    buttonRes = (Button) findViewById(R.id.buttonReset1);
    buttonSub = (Button) findViewById(R.id.buttonSubmit1);
    buttonHome = (Button) findViewById(R.id.buttonHome);
    buttonSetHome = (Button) findViewById(R.id.buttonSetHome);
    buttonCurrentData = (Button) findViewById(R.id.buttonCurrentData);

    // Read the previous values
    initVal();

    /*tempBar.setProgress(mTempState);
    lightBar.setProgress(mLightState);
    ventBar.setProgress(mVentState);*/

    buttonRes.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        // Perform action on clicks
        Toast.makeText(ComfortActivity.this, "Reset", Toast.LENGTH_SHORT)
            .show();

        mTempState = 50;
        tempBar.setProgress(mTempState);

        mLightState = 50;
        lightBar.setProgress(mLightState);

        mVentState = 50;
        ventBar.setProgress(mVentState);
      }
    });

    buttonSub.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        // Perform action on clicks

        // Check if the building is selected
        if (spinnerBuilding.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Building.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        // Check if the floor is selected
        if (spinnerFloor.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Floor.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        // Check if the room is selected
        if (spinnerRoom.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Room.",
              Toast.LENGTH_SHORT).show();
          return;
        }
        
        // See if sensor data is available
        if (str_TM == null || str_TM.equals("") ||
            str_RH == null || str_RH.equals("") ||
            str_LT == null || str_LT.equals(""))
        {
          // Make sure the user really wants to submit partial/no data
          AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ComfortActivity.this);
          alertBuilder.setMessage(R.string.confirmMissingData);
          alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
          {
            
            public void onClick(DialogInterface dialog, int which)
            {
              submitData();              
            }
          });
          alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
          {
            
            public void onClick(DialogInterface dialog, int which)
            {
              // Do Nothing
            }
          });
          alertBuilder.setOnCancelListener(new OnCancelListener()
          {
            
            public void onCancel(DialogInterface dialog)
            {
              // Do Nothing
            }
          });
          AlertDialog alert = alertBuilder.create();
          alert.show();
        }
        else
        {
          submitData();
        }
      }
    });

    buttonCurrentData.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        if(comm == null && mBluetoothAdapter != null)
        {
          // Try automatic connection
          try
          {
            mBluetoothAdapter.cancelDiscovery();
            pairedDev = true;
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices != null && pairedDevices.size() > 0)
            {
              // Loop through paired devices

              for (BluetoothDevice device : pairedDevices)
              {
                // Add the name and address to an array adapter to show in a
                // ListView
                if(device.getName().startsWith("SmartBlock"))
                {
                  con = new ConnectThread(device);
                  con.start();
                  break;
                }
              }
            }
          }
          catch(Exception e)
          {
            Toast.makeText(getApplicationContext(), "Could not automatically connect to a device", Toast.LENGTH_LONG).show();
          }
        }
        else
        {
          sendRequest();
        }
      }
    });

    // Add functionality to the home button
    buttonHome.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        // Perform action on clicks
        // Read the stored data
        try
        {
          //
          DataInputStream in = new DataInputStream(openFileInput(FILENAME2));
          try
          {
            mHomeBuilding = Integer.parseInt(in.readUTF());
            mHomeFloor = Integer.parseInt(in.readUTF());
            mHomeRoom = Integer.parseInt(in.readUTF());

            mHomeSet = true;

            spinnerBuilding.setSelection(mHomeBuilding);

          } catch (EOFException e)
          {

          }
          in.close();
        } catch (IOException e)
        {
          Toast.makeText(ComfortActivity.this, "No Home Location Available",
              Toast.LENGTH_SHORT).show();
        }
      }
    });

    // Add functionality to the Set Home button
    buttonSetHome.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        // Perform action on clicks

        // Check if the building is selected
        if (spinnerBuilding.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Building.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        // Check if the floor is selected
        if (spinnerFloor.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Floor.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        // Check if the room is selected
        if (spinnerRoom.getSelectedItemPosition() == 0)
        {
          Toast.makeText(ComfortActivity.this, "Select Room.",
              Toast.LENGTH_SHORT).show();
          return;
        }

        // Store the user selection for the next time
        try
        {
          DataOutputStream out = new DataOutputStream(openFileOutput(FILENAME2,
              Context.MODE_PRIVATE));

          out.writeUTF(Integer.toString(spinnerBuilding
              .getSelectedItemPosition()));
          out.writeUTF(Integer.toString(spinnerFloor.getSelectedItemPosition()));
          out.writeUTF(Integer.toString(spinnerRoom.getSelectedItemPosition()));

          Toast.makeText(ComfortActivity.this, "Home Location Set.",
              Toast.LENGTH_SHORT).show();

          out.close();
        } catch (IOException e)
        {
          Toast.makeText(ComfortActivity.this, "I/O error", Toast.LENGTH_SHORT)
              .show();
        }
      }
    });

    // Add the functionality to the temp SeekBar
    tempBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {

      public void onStopTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        // Toast.makeText(ComfortActivity.this, "Set: " + mTempState,
        // Toast.LENGTH_SHORT).show();
      }

      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub

      }

      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser)
      {
        mTempState = tempBar.getProgress();

      }
    });

    // Add the functionality to the light SeekBar
    lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {

      public void onStopTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        // Toast.makeText(ComfortActivity.this, "Set: " + mLightState,
        // Toast.LENGTH_SHORT).show();
      }

      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub

      }

      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser)
      {
        mLightState = lightBar.getProgress();

      }
    });

    // Add the functionality to the light SeekBar
    ventBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {

      public void onStopTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        // Toast.makeText(ComfortActivity.this, "Set: " + mVentState,
        // Toast.LENGTH_SHORT).show();
      }

      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub

      }

      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser)
      {
        // TODO Auto-generated method stub
        mVentState = ventBar.getProgress();

      }
    });

    // Set the location selector
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.building_array, android.R.layout.simple_spinner_item);
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerBuilding.setAdapter(adapter);

    // Floor adapters
    adapterFloorEmpty = ArrayAdapter.createFromResource(this,
        R.array.floor_array_empty, android.R.layout.simple_spinner_item);
    /*adapterFloorCAES = ArrayAdapter.createFromResource(this,
        R.array.floor_array_caes, android.R.layout.simple_spinner_item);
    adapterFloorBanner = ArrayAdapter.createFromResource(this,
        R.array.floor_array_banner, android.R.layout.simple_spinner_item);*/
    adapterFloorUB4 = ArrayAdapter.createFromResource(this,R.array.floor_array_ub4,android.R.layout.simple_spinner_item);
    
    // Room Adapters
    adapterRoomEmpty = ArrayAdapter.createFromResource(this,
        R.array.room_array_empty, android.R.layout.simple_spinner_item);
    /*adapterRoomCAES1 = ArrayAdapter.createFromResource(this,
        R.array.room_array_caes1, android.R.layout.simple_spinner_item);
    adapterRoomCAES2 = ArrayAdapter.createFromResource(this,
        R.array.room_array_caes2, android.R.layout.simple_spinner_item);
    adapterRoomBanner1 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner1, android.R.layout.simple_spinner_item);
    adapterRoomBanner2 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner2, android.R.layout.simple_spinner_item);
    adapterRoomBanner3 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner3, android.R.layout.simple_spinner_item);
    adapterRoomBanner4 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner4, android.R.layout.simple_spinner_item);
    adapterRoomBanner5 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner5, android.R.layout.simple_spinner_item);
    adapterRoomBanner6 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner6, android.R.layout.simple_spinner_item);
    adapterRoomBanner7 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner7, android.R.layout.simple_spinner_item);
    adapterRoomBanner8 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner8, android.R.layout.simple_spinner_item);
    adapterRoomBanner9 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner9, android.R.layout.simple_spinner_item);
    adapterRoomBanner10 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner10, android.R.layout.simple_spinner_item);
    adapterRoomBanner11 = ArrayAdapter.createFromResource(this,
        R.array.room_array_banner11, android.R.layout.simple_spinner_item);*/
    adapterRoomUB4_1 = ArrayAdapter.createFromResource(this, R.array.room_array_ub4_1, android.R.layout.simple_spinner_item);

    spinnerBuilding.setOnItemSelectedListener(new OnItemSelectedListener()
    {
      public void onItemSelected(AdapterView<?> parent, View view, int pos,
          long id)
      {
        /*
         * Toast.makeText(parent.getContext(), "The building is " +
         * parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
         */

        if (pos == 0)
        {
          adapterFloorEmpty
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerFloor.setAdapter(adapterFloorEmpty);
        }
        else if (pos ==1)
        {
          adapterFloorUB4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerFloor.setAdapter(adapterFloorUB4);
        }
        /*else if (pos == 1)
        {
          adapterFloorCAES
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerFloor.setAdapter(adapterFloorCAES);
        } else if (pos == 2)
        {
          adapterFloorBanner
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerFloor.setAdapter(adapterFloorBanner);
        }*/

        if (mHomeSet)
        {
          spinnerFloor.setSelection(mHomeFloor);
        }

      }

      public void onNothingSelected(AdapterView parent)
      {
        // Do nothing.
      }
    });

    spinnerFloor.setOnItemSelectedListener(new OnItemSelectedListener()
    {
      public void onItemSelected(AdapterView<?> parent, View view, int pos,
          long id)
      {
        /*
         * Toast.makeText(parent.getContext(), "The building is " +
         * parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
         */

        if (spinnerBuilding.getSelectedItemPosition() == 0)
        {
          adapterRoomEmpty
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomEmpty);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 1)
            && (pos == 0))
        {
          adapterRoomEmpty
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomEmpty);
        }
        else if((spinnerBuilding.getSelectedItemPosition() == 1) && (pos == 1))
        {
          adapterRoomUB4_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomUB4_1);
        }
        /*else if ((spinnerBuilding.getSelectedItemPosition() == 1)
            && (pos == 1))
        {
          adapterRoomCAES1
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomCAES1);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 1)
            && (pos == 2))
        {
          adapterRoomCAES2
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomCAES2);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 0))
        {
          adapterRoomEmpty
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomEmpty);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 1))
        {
          adapterRoomBanner1
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner1);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 2))
        {
          adapterRoomBanner2
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner2);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 3))
        {
          adapterRoomBanner3
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner3);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 4))
        {
          adapterRoomBanner4
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner4);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 5))
        {
          adapterRoomBanner5
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner5);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 6))
        {
          adapterRoomBanner6
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner6);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 7))
        {
          adapterRoomBanner7
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner7);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 8))
        {
          adapterRoomBanner8
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner8);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 9))
        {
          adapterRoomBanner9
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner9);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 10))
        {
          adapterRoomBanner10
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner10);
        } else if ((spinnerBuilding.getSelectedItemPosition() == 2)
            && (pos == 11))
        {
          adapterRoomBanner11
              .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinnerRoom.setAdapter(adapterRoomBanner11);
        }*/

        if (mHomeSet)
        {
          spinnerRoom.setSelection(mHomeRoom);
          mHomeSet = false;
        }

      }

      public void onNothingSelected(AdapterView parent)
      {
        // Do nothing.
      }
    });

    handler = new Handler();

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    mArrayAdapter = new ArrayAdapter<String>(ComfortActivity.this,
        R.layout.list_item);

    this.text_TM = (TextView) findViewById(R.id.TextView_TM);
    // this.text_TM.setEnabled(false);

    this.text_RH = (TextView) findViewById(R.id.TextView_RH);
    // this.text_RH.setEnabled(false);

    this.text_LT = (TextView) findViewById(R.id.TextView_LT);
    // this.text_LT.setEnabled(false);

    this.BTList = (ListView) findViewById(R.id.listView1);

    // Test that BT is available
    if (mBluetoothAdapter == null)
    {
      Toast.makeText(ComfortActivity.this, "Device does not support BT!",
          Toast.LENGTH_LONG).show();
      // this.finish();
    } else
    {
      pairedDevices = mBluetoothAdapter.getBondedDevices();

      // Make sure that BT is enabled
      if (!mBluetoothAdapter.isEnabled())
      {
        Intent enableBtIntent = new Intent(
            BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }

      // Create a BroadcastReceiver

      // Register the BroadcastReceiver
      IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
      registerReceiver(mReceiver, filterFound); // Don't forget to unregister
                                                // during onDestroy

      IntentFilter filterStart = new IntentFilter(
          BluetoothAdapter.ACTION_DISCOVERY_STARTED);
      registerReceiver(mReceiver, filterStart); // Don't forget to unregister
                                                // during onDestroy

      IntentFilter filterStop = new IntentFilter(
          BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      registerReceiver(mReceiver, filterStop); // Don't forget to unregister
                                               // during onDestroy

      BTList.setOnItemClickListener(new OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
        {
          // When clicked, show a toast with the TextView text
          if (pairedDev)
          {
            Toast.makeText(
                getApplicationContext(),
                "Connecting to " + ((TextView) view).getText() + " "
                    + Integer.toString(position), Toast.LENGTH_SHORT).show();
            /*
             * System.out.println("Connecting to " + ((TextView) view).getText()
             * + " " + Integer.toString(position));
             */

            int cnt = 0;
            for (BluetoothDevice device : pairedDevices)
            {
              if (cnt == position)
              {
                con = new ConnectThread(device);
                con.start();
              }

              cnt++;
            }
          } else
          {
            Toast.makeText(
                getApplicationContext(),
                "Discovered " + ((TextView) view).getText() + " "
                    + Integer.toString(position), Toast.LENGTH_SHORT).show();
          }
        }
      });
    }
  }

  // Checks if initial data have been previously stored and if so, then
  // initializes the user selection
  public void initVal()
  {

    // Read the stored data
    try
    {
      //
      DataInputStream in = new DataInputStream(openFileInput(FILENAME));
      try
      {
        mTempState = Integer.parseInt(in.readUTF());
        mLightState = Integer.parseInt(in.readUTF());
        mVentState = Integer.parseInt(in.readUTF());

      } catch (EOFException e)
      {

      }
      in.close();
    } catch (IOException e)
    {

    }

  }
  
  private void submitData()
  {
 // Store the user selection for the next time
    try
    {
      // Write 20 Strings
      DataOutputStream out = new DataOutputStream(openFileOutput(FILENAME,
          Context.MODE_PRIVATE));

      out.writeUTF(Integer.toString(mTempState));
      out.writeUTF(Integer.toString(mLightState));
      out.writeUTF(Integer.toString(mVentState));

      out.close();
    } catch (IOException e)
    {
      Log.i("Data Input Sample", "I/O Error");
    }

    // Submit the data to the server
    // Temporary Code
    // The following code bypasses SSL Certificate checks and should be
    // removed in a production environment
    /*HttpClient httpclient;
    try
    {
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(null, null);

      SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
      sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

      HttpParams params = new BasicHttpParams();
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

      SchemeRegistry registry = new SchemeRegistry();
      registry.register(new Scheme("http", PlainSocketFactory
          .getSocketFactory(), 80));
      registry.register(new Scheme("https", sf, 443));

      ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
          registry);

      httpclient = new DefaultHttpClient(ccm, params);
    } catch (Exception e)
    {
      httpclient = new DefaultHttpClient();
    }*/
    // End Temporary Code
    // The following line should be uncommented once the code above is
    // removed
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(
        "https://mhrg.if.uidaho.edu/TEMST/insert.php");
    try
    {

      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

      /*nameValuePairs.add(new BasicNameValuePair("building", Integer
          .toString(spinnerBuilding.getSelectedItemPosition())));
      nameValuePairs.add(new BasicNameValuePair("floor", Integer
          .toString(spinnerFloor.getSelectedItemPosition())));
      nameValuePairs.add(new BasicNameValuePair("room", Integer
          .toString(spinnerRoom.getSelectedItemPosition())));*/
      nameValuePairs.add(new BasicNameValuePair("building", spinnerBuilding.getSelectedItem().toString()));
      nameValuePairs.add(new BasicNameValuePair("floor", spinnerFloor.getSelectedItem().toString()));
      nameValuePairs.add(new BasicNameValuePair("room", spinnerRoom.getSelectedItem().toString()));
      nameValuePairs.add(new BasicNameValuePair("heat", Integer
          .toString(mTempState)));
      nameValuePairs.add(new BasicNameValuePair("light", Integer
          .toString(mLightState)));
      nameValuePairs.add(new BasicNameValuePair("air", Integer
          .toString(mVentState)));
      if (str_TM != null)
      {
        nameValuePairs.add(new BasicNameValuePair("currentTemperature",
            str_TM));
      }
      if (str_RH != null)
      {
        nameValuePairs
            .add(new BasicNameValuePair("currentHumidity", str_RH));
      }
      if (str_LT != null)
      {
        nameValuePairs.add(new BasicNameValuePair("currentLight", str_LT));
      }
      if(con != null && con.mmDevice != null)
      {
        nameValuePairs.add(new BasicNameValuePair("sensor", con.mmDevice.getName()));
      }

      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      HttpResponse response = httpclient.execute(httppost);

      HttpEntity entity = response.getEntity();
      InputStream is = entity.getContent();
      StringBuilder sb = null;

      // Convert the response to string
      try
      {
        // BufferedReader reader = new BufferedReader(new
        // InputStreamReader(is, "iso-8859-1"),8);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(is));
        sb = new StringBuilder();
        sb.append(reader.readLine() + "\n");
        String line = "0";

        while ((line = reader.readLine()) != null)
        {
          sb.append(line + "\n");
        }

        is.close();

      } catch (Exception e)
      {
      }
    } catch (Exception e)
    {
      Toast.makeText(ComfortActivity.this, e.toString(), Toast.LENGTH_LONG)
          .show();
    }
    Toast.makeText(ComfortActivity.this, "Submitted", Toast.LENGTH_SHORT)
    .show();
  }
  
  public static boolean success =false;
}
