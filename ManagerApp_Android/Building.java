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

package dumi.temst.namespace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class Building 
{
	public int m_NFloors;			// Number of floor
	public Floor [] m_Floors;		// Array of floors
	public String m_NameStr;		// Name of the building
	public int m_DataPoint; 		// Current data pointer
	public int m_SelectFloor;		// currently selected floor 
	public String m_TimeNow;		// Current data and time
	public int m_NData;				// Number of data values
	public int m_NAttr;				// Number of attributes for building data
	public DataSource m_Data;		// Data source of given building
	public String [] m_DataNames;	// Labels of the building data 
	public String [] m_DataUnits;	// Units of the building data
	public String bld_err;			// stores errors encountered by the building 
	public int debug_load_percentage;
	
	public Building()
	{
		this.debug_load_percentage = 0;
		initValues();
	}
	
	public Building(String _file_name)
	{
		this.debug_load_percentage = 0;
		initValues();
		try
		{
			//Process root = Runtime.getRuntime().exec("su");
			//File sdcard = Environment.getRootDirectory();
			
			File file = new File(_file_name);
			//BufferedWriter out = new BufferedWriter(new FileWriter(file));
			BufferedReader in_building_file = new BufferedReader(new FileReader(file));
			
			//name of the building
			String line = in_building_file.readLine();
			StringTokenizer line_tok = new StringTokenizer(line);
			this.m_NameStr = line_tok.nextToken(",");
			
			this.debug_load_percentage += 5;
			
			//number of floors of the building
			String temp = line_tok.nextToken(",");
			temp = temp.replaceAll(" ", "");
			this.m_NFloors = Integer.parseInt(temp);
			
			//number of data points in the OCA file
			//OAC is the only data for the whole building
			temp = line_tok.nextToken(",");
			temp = temp.replaceAll(" ", "");
			this.m_NData = Integer.parseInt(temp);
			
			//read the building  data
			line = in_building_file.readLine();
			line = line.replaceAll(" ", "");
			this.m_Data = new DataSource(this.m_NAttr, this.m_NData);
			this.readData(line);
			
			this.debug_load_percentage += 5;
			
			//create the floors
			this.m_Floors = new Floor[this.m_NFloors];
			
			//read individual floors
			for(int i = 0; i < this.m_NFloors; i++)
			{
				line = in_building_file.readLine();
				line_tok = new StringTokenizer(line);
				//floor ID
				temp = line_tok.nextToken(",");
				temp = temp.replaceAll(" ", "");
				int id = Integer.parseInt(temp);
				//Wall file name
				temp = line_tok.nextToken(",");
				String wall_file_name = temp;
				//Fill file name
				temp = line_tok.nextToken(",");
				String fill_file_name = temp;
				//zonetemp file name
				temp = line_tok.nextToken(",");
				String zonetemp_file_name = temp;
				//floor file name
				temp = line_tok.nextToken(",");
				String floor_file_name = temp;
				
				//create the floor
				this.m_Floors[i] = new Floor();
				this.m_Floors[i].readFills(id,fill_file_name);
				this.m_Floors[i].readWalls(wall_file_name);
				this.m_Floors[i].readZoneData(zonetemp_file_name, this.m_NData);
				this.m_Floors[i].readFloorData(floor_file_name, this.m_NData);
				//this.m_Floors[i].compMidPoint();
				this.m_Floors[i].init();
				
				this.debug_load_percentage += 5;
			}
			this.m_SelectFloor = 0;
			
			this.debug_load_percentage += 5;
			
		}
		catch (IOException e) 
		{
			this.m_NameStr = e.toString();
			this.bld_err = "Cannot read building file " + _file_name + ", ";
			
    	}
	}	
	
	public void initValues()
	{
		this.m_NFloors = 0;
		this.m_Floors = null;
		this.m_NameStr = "";
		this.m_SelectFloor = 0;
		this.m_DataPoint = 0;
		this.m_TimeNow = "";
		this.m_NAttr = 1;
		this.m_DataNames = new String[this.m_NAttr];
		this.m_DataNames[0] = "OAT";
		this.m_DataUnits = new String[this.m_NAttr];
		this.m_DataUnits[0] = "F";
		this.m_NData = -1;	
		this.bld_err = "";
	}
	
	public void readData(String _data_file_name)
	{
		_data_file_name = "/sdcard/TEMST_App/Data/" + _data_file_name;
		try
		{
			File file = new File(_data_file_name);
			BufferedReader in_oac_temp_file = new BufferedReader(new FileReader(file));
			String line = "";
			
			for(int i = 0; i < this.m_NData; i++)
			{
				line = in_oac_temp_file.readLine();
				StringTokenizer line_tok = new StringTokenizer(line);
				//get date (dont remove space)
				String date = line_tok.nextToken(",");
				
				//get value
				String val = line_tok.nextToken(",");
				val = val.replaceAll(" ", "");
				
				this.m_Data.m_Time[i] = date;
				this.m_Data.m_Val[0][i] = (float)Double.parseDouble(val);
			}
			
			// Also convert the string data and time to their float representations
			this.m_Data.extractTime();
			this.m_Data.extractMinMax();
		}
		catch (IOException e) 
		{
			this.bld_err = "Cannot read building data file " + _data_file_name + ", ";
    	}			
	}
	
	// updates the data pointer
	void updateDataPointer(int _new)
	{
		this.m_DataPoint = _new;

		if (this.m_DataPoint == this.m_Floors[0].m_Zones[0].m_Data.m_NVal)
		{
			this.m_DataPoint = 0;
		}

		for (int i = 0; i < this.m_NFloors; i++)
		{
			this.m_Floors[i].compAvgTemp(this.m_DataPoint);
		}

		this.m_TimeNow = this.m_Floors[0].m_Zones[0].m_Data.m_Time[this.m_DataPoint];
		
		this.updateTime();
	}
	
	// This function updates the current time
	void updateTime()
	{
		for (int i = 0; i < this.m_NFloors; i++)
		{
			this.m_Floors[i].compAvgTemp(this.m_DataPoint);
		}
		this.m_TimeNow = this.m_Floors[0].m_Zones[0].m_Data.m_Time[this.m_DataPoint];
	}
	
	// Sets the current time and data
	void setCurrTime()
	{
		this.m_TimeNow = this.m_Floors[0].m_Zones[0].m_Data.m_Time[this.m_DataPoint];
	}
}
