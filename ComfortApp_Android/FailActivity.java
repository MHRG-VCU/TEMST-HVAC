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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FailActivity extends Activity {
	
	private static final int ACTIVITY_PHOTO=0;
	
	private Uri imageUri = null;
	Bitmap bitmap = null;
	
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
	
	ArrayAdapter<CharSequence> adapterSubSystem;
	ArrayAdapter<CharSequence> adapterFailEmpty;
	ArrayAdapter<CharSequence> adapterFailLight;
	ArrayAdapter<CharSequence> adapterFailHeat;
	ArrayAdapter<CharSequence> adapterFailVent;
	ArrayAdapter<CharSequence> adapterFailElectr;
	ArrayAdapter<CharSequence> adapterFailWater;
	
	Spinner spinnerBuilding;
	Spinner spinnerFloor;
	Spinner spinnerRoom;
	
	Spinner spinnerSubSystem;
	Spinner spinnerFailure;
	EditText edit1;	
	
	Button buttonRes;
	Button buttonSub;
	Button buttonPhoto;
	Button buttonHome;
	Button buttonSetHome;
	
	private String FILENAME = "hvacSaveFail";
	private String FILENAME2 = "hvacSaveHome";
	
	boolean initLoad = false;
	int saveSubSel = 0;
	int saveFailSel = 0;
	
	// home location
	int mHomeBuilding = 0;
	int mHomeFloor = 0;
	int mHomeRoom = 0;
	boolean mHomeSet = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fail_layout);
        
        // Identify the layout components
        spinnerBuilding = (Spinner) findViewById(R.id.spinnerBuildingF);
        spinnerFloor = (Spinner) findViewById(R.id.spinnerFloorF);
        spinnerRoom = (Spinner) findViewById(R.id.spinnerRoomF);
        spinnerSubSystem = (Spinner) findViewById(R.id.spinnerSubSystem);
        spinnerFailure = (Spinner) findViewById(R.id.spinnerFail);    
        buttonRes = (Button) findViewById(R.id.buttonReset2);
        buttonSub = (Button) findViewById(R.id.buttonSubmitF1);
        //buttonPhoto = (Button) findViewById(R.id.buttonPhoto1);
        buttonHome = (Button) findViewById(R.id.buttonHome2);
        buttonSetHome = (Button) findViewById(R.id.buttonSetHome2);
        edit1 = (EditText) findViewById(R.id.editText1);                            
        
        // Set the location selector        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.building_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuilding.setAdapter(adapter);
        
        // Floor adapters
        adapterFloorEmpty = ArrayAdapter.createFromResource(
				 this, R.array.floor_array_empty, android.R.layout.simple_spinner_item);
     /*   adapterFloorCAES = ArrayAdapter.createFromResource(
				 this, R.array.floor_array_caes, android.R.layout.simple_spinner_item);        
        adapterFloorBanner = ArrayAdapter.createFromResource(
				 this, R.array.floor_array_banner, android.R.layout.simple_spinner_item);*/
      adapterFloorUB4 = ArrayAdapter.createFromResource(this,R.array.floor_array_ub4,android.R.layout.simple_spinner_item);
            
      // Room adapters
        adapterRoomEmpty = ArrayAdapter.createFromResource(
				 this, R.array.room_array_empty, android.R.layout.simple_spinner_item);
     /*   adapterRoomCAES1 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_caes1, android.R.layout.simple_spinner_item);
        adapterRoomCAES2 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_caes2, android.R.layout.simple_spinner_item);
        adapterRoomBanner1 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner1, android.R.layout.simple_spinner_item);
        adapterRoomBanner2 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner2, android.R.layout.simple_spinner_item);
        adapterRoomBanner3 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner3, android.R.layout.simple_spinner_item);
        adapterRoomBanner4 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner4, android.R.layout.simple_spinner_item);
        adapterRoomBanner5 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner5, android.R.layout.simple_spinner_item);
        adapterRoomBanner6 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner6, android.R.layout.simple_spinner_item);
        adapterRoomBanner7 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner7, android.R.layout.simple_spinner_item);
        adapterRoomBanner8 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner8, android.R.layout.simple_spinner_item);
        adapterRoomBanner9 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner9, android.R.layout.simple_spinner_item);
        adapterRoomBanner10 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner10, android.R.layout.simple_spinner_item);
        adapterRoomBanner11 = ArrayAdapter.createFromResource(
				 this, R.array.room_array_banner11, android.R.layout.simple_spinner_item);*/
        adapterRoomUB4_1 = ArrayAdapter.createFromResource(this, R.array.room_array_ub4_1, android.R.layout.simple_spinner_item);
        
        spinnerBuilding.setOnItemSelectedListener(new OnItemSelectedListener(){
        	 public void onItemSelected(AdapterView<?> parent,
        	            View view, int pos, long id) {
        	        	/*
        	          Toast.makeText(parent.getContext(), "The building is " +
        	              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        	              */        	        	        	        
        	        	
        	        	if (pos == 0){        	        		 
        	        		adapterFloorEmpty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	        	       spinnerFloor.setAdapter(adapterFloorEmpty);      	
        	        	}
        	        	else if (pos ==1)
        	         {
        	           adapterFloorUB4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	           spinnerFloor.setAdapter(adapterFloorUB4);
        	         }
        	        	/*else if (pos == 1){
        	        		adapterFloorCAES.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	        spinnerFloor.setAdapter(adapterFloorCAES);      	
        	        	}
        	        	else if (pos == 2){
        	        		adapterFloorBanner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	        spinnerFloor.setAdapter(adapterFloorBanner);      	
        	        	}*/     
        	        	
        	        	if (mHomeSet){
        	        		spinnerFloor.setSelection(mHomeFloor);        	        	
        	        	}
        	        	
        	        }

        	        public void onNothingSelected(AdapterView parent) {
        	          // Do nothing.
        	        }
        });   
                        
        spinnerFloor.setOnItemSelectedListener(new OnItemSelectedListener(){
       	 public void onItemSelected(AdapterView<?> parent,
       	            View view, int pos, long id) {
       	        	/*
       	          Toast.makeText(parent.getContext(), "The building is " +
       	              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
       	              */       	        	       	        
       	        	
       	        	if (spinnerBuilding.getSelectedItemPosition() == 0){        	        		 
       	        		adapterRoomEmpty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       	        	       spinnerRoom.setAdapter(adapterRoomEmpty);      	
       	        	}
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 1) && (pos == 0)){
       	        		adapterRoomEmpty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomEmpty);      	
       	        	}
       	        	else if((spinnerBuilding.getSelectedItemPosition() == 1) && (pos == 1))
       	         {
       	           adapterRoomUB4_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       	           spinnerRoom.setAdapter(adapterRoomUB4_1);
       	         }
       	        	/*else if ((spinnerBuilding.getSelectedItemPosition() == 1) && (pos == 1)){
       	        		adapterRoomCAES1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomCAES1);      	
       	        	}
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 1) && (pos == 2)){
       	        		adapterRoomCAES2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomCAES2);      	
       	        	}       	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 0)){
       	        		adapterRoomEmpty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomEmpty);      	
       	        	}
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 1)){
       	        		adapterRoomBanner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner1);      	
       	        	}
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 2)){
       	        		adapterRoomBanner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner2);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 3)){
       	        		adapterRoomBanner3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner3);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 4)){
       	        		adapterRoomBanner4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner4);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 5)){
       	        		adapterRoomBanner5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner5);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 6)){
       	        		adapterRoomBanner6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner6);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 7)){
       	        		adapterRoomBanner7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner7);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 8)){
       	        		adapterRoomBanner8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner8);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 9)){
       	        		adapterRoomBanner9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner9);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 10)){
       	        		adapterRoomBanner10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner10);      	
       	        	}        	        	
       	        	else if ((spinnerBuilding.getSelectedItemPosition() == 2) && (pos == 11)){
       	        		adapterRoomBanner11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerRoom.setAdapter(adapterRoomBanner11);      	
       	        	}*/
       	        	
       	        	if (mHomeSet){
    	        		spinnerRoom.setSelection(mHomeRoom);
    	        		mHomeSet = false;
    	        	}
       	        	
       	        }

       	        public void onNothingSelected(AdapterView parent) {
       	          // Do nothing.
       	        }
       });
        
        
        // Set the location selector        
        ArrayAdapter<CharSequence> adapterSubSys = ArrayAdapter.createFromResource(
                this, R.array.subsystem_array, android.R.layout.simple_spinner_item);
        adapterSubSys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubSystem.setAdapter(adapterSubSys);
        
        // Text Array adapters       
        adapterFailEmpty = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_empty, android.R.layout.simple_spinner_item);
        adapterFailLight = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_light, android.R.layout.simple_spinner_item);
        adapterFailHeat = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_heat, android.R.layout.simple_spinner_item);
        adapterFailVent = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_vent, android.R.layout.simple_spinner_item);
        adapterFailElectr = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_electr, android.R.layout.simple_spinner_item);
        adapterFailWater = ArrayAdapter.createFromResource(
				 this, R.array.fail_array_water, android.R.layout.simple_spinner_item);
        
        // Read the previously stored values;
        initVal();
        
        spinnerSubSystem.setOnItemSelectedListener(new OnItemSelectedListener(){
       	 public void onItemSelected(AdapterView<?> parent,
       	            View view, int pos, long id) {       	        	       		        		 	
       	        	
       	        	if (pos == 0){        	        		 
       	        		adapterFailEmpty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       	        	       spinnerFailure.setAdapter(adapterFailEmpty);      	
       	        	}
       	        	else if (pos == 1){        	        		 
       	        		adapterFailLight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerFailure.setAdapter(adapterFailLight);      	
    	        	}
       	        	else if (pos == 2){        	        		 
       	        		adapterFailHeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerFailure.setAdapter(adapterFailHeat);      	
    	        	}
       	        	else if (pos == 3){        	        		 
       	        		adapterFailVent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerFailure.setAdapter(adapterFailVent);      	
    	        	}
       	        	else if (pos == 4){        	        		 
       	        		adapterFailElectr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerFailure.setAdapter(adapterFailElectr);      	
    	        	}
       	        	else if (pos == 5){        	        		 
       	        		adapterFailWater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        	       spinnerFailure.setAdapter(adapterFailWater);      	
    	        	}
       	        	
       	        	if (initLoad){
       	        		initLoad = false;
       	        		spinnerFailure.setSelection(saveFailSel);
       	        	}
       	        	        	        	       	        	
       	        }

       	        public void onNothingSelected(AdapterView parent) {
       	          // Do nothing.
       	        }
       	               	       
       });
                                
        buttonRes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Toast.makeText(FailActivity.this, "Reset", Toast.LENGTH_SHORT).show();   
                
                // Clear the text field                
                edit1.setText("");   
                
                // Set the subsystems type and the failure type to default
                spinnerSubSystem.setSelection(0);
            }
        });
                
        buttonSub.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	
            	if (spinnerSubSystem.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Sub-System.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	if (spinnerFailure.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Failure Type.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	if (spinnerBuilding.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Building.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	if (spinnerFloor.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Floor.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	if (spinnerRoom.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Room.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	
                Toast.makeText(FailActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                
             // Submit the data to the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(
                    "https://mhrg.if.uidaho.edu/TEMST/insert_failure.php");
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                try
                {
                  //reqEntity.addPart("building", new StringBody(Integer.toString(spinnerBuilding.getSelectedItemPosition())));
                  reqEntity.addPart("building", new StringBody(spinnerBuilding.getSelectedItem().toString()));
                  //reqEntity.addPart("floor", new StringBody(Integer.toString(spinnerFloor.getSelectedItemPosition())));
                  reqEntity.addPart("floor", new StringBody(spinnerFloor.getSelectedItem().toString()));
                  //reqEntity.addPart("room", new StringBody(Integer.toString(spinnerRoom.getSelectedItemPosition())));
                  reqEntity.addPart("room", new StringBody(spinnerRoom.getSelectedItem().toString()));
                  //reqEntity.addPart("sub_system", new StringBody(Integer.toString(spinnerSubSystem.getSelectedItemPosition())));
                  reqEntity.addPart("sub_system", new StringBody(spinnerSubSystem.getSelectedItem().toString()));
                  //reqEntity.addPart("failure_type", new StringBody(Integer.toString(spinnerFailure.getSelectedItemPosition())));
                  reqEntity.addPart("failure_type", new StringBody(spinnerFailure.getSelectedItem().toString()));
                  if(edit1.getText().toString().length() > 0)
                  {
                    reqEntity.addPart("comment", new StringBody(edit1.getText().toString()));
                  }
                  
                  httppost.setEntity(reqEntity);
                  HttpResponse response = httpclient.execute(httppost);

                  /*HttpEntity entity = response.getEntity();
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
                  }*/
                } catch (Exception e)
                {
                  Toast.makeText(FailActivity.this, e.toString(), Toast.LENGTH_LONG)
                      .show();
                }
                
                // Store the user selection for the next time
                /*try {
                    // Write 20 Strings
                    DataOutputStream out = 
                            new DataOutputStream(openFileOutput(FILENAME, Context.MODE_PRIVATE));
                                        
                    out.writeUTF(Integer.toString(spinnerSubSystem.getSelectedItemPosition()));
                    out.writeUTF(Integer.toString(spinnerFailure.getSelectedItemPosition()));
                    
                    final EditText edit1 = (EditText) findViewById(R.id.editText1);                    
                    out.writeUTF(edit1.getText().toString());
                    
                    out.close();
                } catch (IOException e) {
                	Toast.makeText(FailActivity.this, "I/O Error", Toast.LENGTH_SHORT).show();    
                }*/
            }
        });
        
        // Add functionality to the home button
        buttonHome.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	// Read the stored data
            	try {
            		 // 
            	    DataInputStream in = new DataInputStream(openFileInput(FILENAME2));
            	    try {
            	    	mHomeBuilding = Integer.parseInt(in.readUTF());
            	    	mHomeFloor = Integer.parseInt(in.readUTF());
            	    	mHomeRoom = Integer.parseInt(in.readUTF());
            	    	
            	    	mHomeSet = true;
            	    	
            	    	spinnerBuilding.setSelection(mHomeBuilding);
            	    	
            	    	    	    	    	       
            	    } catch (EOFException e) {
            	        
            	    }
            	    in.close();
            	} catch (IOException e) {            		
            		Toast.makeText(FailActivity.this, "No Home Location Available", Toast.LENGTH_SHORT).show();    
            	}
            }
        });
        
     // Add functionality to the Set Home button
        buttonSetHome.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	
            	// Check if the building is selected
            	if (spinnerBuilding.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Building.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	// Check if the floor is selected
            	if (spinnerFloor.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Floor.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	
            	// Check if the room is selected
            	if (spinnerRoom.getSelectedItemPosition() == 0){
            		Toast.makeText(FailActivity.this, "Select Room.", Toast.LENGTH_SHORT).show();
            		return;
            	}            	
            	
            	 // Store the user selection for the next time
                try {                 
                    DataOutputStream out = 
                            new DataOutputStream(openFileOutput(FILENAME2, Context.MODE_PRIVATE));
                                        
                    out.writeUTF(Integer.toString(spinnerBuilding.getSelectedItemPosition()));
                    out.writeUTF(Integer.toString(spinnerFloor.getSelectedItemPosition()));
                    out.writeUTF(Integer.toString(spinnerRoom.getSelectedItemPosition()));
                                        
                    Toast.makeText(FailActivity.this, "Home Location Set.", Toast.LENGTH_SHORT).show();
                    
                    out.close();
                } catch (IOException e) {
                	Toast.makeText(FailActivity.this, "I/O error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        
        // Check the availability of the camera
        /*PackageManager pm = getPackageManager();
        boolean hasCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);                
        
        if (!hasCam){
        	buttonPhoto.setEnabled(false);        
        }
        
        // Check if there is an application that can take the photo
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0){
        	buttonPhoto.setEnabled(false);
        }
        
        buttonPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks            	
            	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	//File picture = new File(Environment.getExternalStorageDirectory(), "failure.jpg");
            	//takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
            	//imageUri = Uri.fromFile(picture);
                startActivityForResult(takePictureIntent, ACTIVITY_PHOTO);                            
            }
        });*/
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	Bundle extras = intent.getExtras();
    	
    	switch(requestCode) {
    	case ACTIVITY_PHOTO:
    	  if(Activity.RESULT_OK == resultCode)
    	  {
    	    /*Uri selectedImage = imageUri;
    	    ContentResolver cr = getContentResolver();
    	    try
    	    {
    	    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
    	    }
    	    catch(IOException e)
    	    {
    	    }
    	    bitmap = (Bitmap) extras.get(MediaStore.EXTRA_OUTPUT);
    	    Bitmap mImageBitmap = (Bitmap) extras.get("data");
         
         final ImageView viewPhoto = (ImageView) findViewById(R.id.imagePhoto);
            viewPhoto.setImageBitmap(mImageBitmap);*/
    	  }
    		break;
    	}
    }
    
    // Checks if initial data have been previously stored and if so, then initializes the user selection
    public void initVal(){
    	
    	// Read the stored data
    	/*try {
    		 // 
    	    DataInputStream in = new DataInputStream(openFileInput(FILENAME));
    	    try {    	
    	    	    	    
    	    	initLoad = true;
    	    	saveSubSel = Integer.parseInt(in.readUTF());
    	    	spinnerSubSystem.setSelection(saveSubSel);
    	    	saveFailSel = Integer.parseInt(in.readUTF());
    	    	spinnerFailure.setSelection(saveFailSel);
    	    	
    	    	edit1.setText(in.readUTF());    	    	    	    	
    	    	    	    	    	       
    	    } catch (EOFException e) {
    	    	Toast.makeText(FailActivity.this, "In File Read error", Toast.LENGTH_SHORT).show();
    	    }
    	    in.close();
    	} catch (IOException e) {
    		Toast.makeText(FailActivity.this, "In File Open Error", Toast.LENGTH_SHORT).show();
    	}
    */
    }
}
