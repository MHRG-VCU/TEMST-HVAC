/************************************************************************
*      __   __  _    _  _____   _____
*     /  | /  || |  | ||     \ /  ___|
*    /   |/   || |__| ||    _||  |  _
*   / /|   /| ||  __  || |\ \ |  |_| |
*  /_/ |_ / |_||_|  |_||_| \_\|______|
*    
* 
*   Written by Ondrej Linda, University of Idaho (2012)
*   Copyright (2012) Modern Heuristics Research Group (MHRG)
*	University of Idaho   
*	Virginia Commonwealth University (VCU), Richmond, VA
*   http://www.people.vcu.edu/~mmanic/
*   Do not redistribute without author's(s') consent
*  
*   Any opinions, findings, and conclusions or recommendations expressed 
*   in this material are those of the author's(s') and do not necessarily 
*   reflect the views of any other entity.
*  
*   ***********************************************************************/


//---------------------------------------------------------------------------
// Building.h
// Implements data structures associated with storing the building information
// Date:  4/15/2012
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------



#ifndef __BUILDING_H__
#define __BUILDING_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string.h>
#include <cstdio>

#include "Floor.h"

using namespace std;

// Class implementing the floor
class C_Building{
public:
	// Number of floor
	int m_NFloors;

	// Array of floors
	C_Floor * m_Floors;

	// Name of the building
	string m_NameStr;

	// Current data pointer
	int m_DataPoint;

	// currently selected floor 
	int m_SelectFloor;

	// Current data and time
	string m_TimeNow;

	// Number of data values
	int m_NData;

	// Number of attributes for building data
	int m_NAttr;

	// Data source of given building
	C_DataSource * m_Data;

	// Labels of the building data
	string * m_DataNames;

	// Units of the building data
	string * m_DataUnits;

public:
	// Constructor
	C_Building::C_Building(){
		this->m_NFloors = 0;
		this->m_Floors = NULL;
		this->m_NameStr = "";

		this->m_SelectFloor = 0;

		this->m_DataPoint = 0;

		this->m_TimeNow = "";

		this->m_NAttr = _FNL_num_building_dims;

		this->m_DataNames = new string[this->m_NAttr];
		_FNL_GetDimNamesShort(this->m_DataNames, "building");
		//cout << this->m_DataNames[0];
		//this->m_DataNames[0] = "OAT";

		this->m_DataUnits = new string[this->m_NAttr];
		_FNL_GetDimUnit(this->m_DataUnits, "building");
		//this->m_DataUnits[0] = "F";

		//cout << this->m_DataNames[0] << "::" << this->m_DataUnits[0];
		this->m_NData = -1;		
	}

	// Loads the building description from a text file
	void C_Building::readFile(char * fileName)
	{
		ifstream in;
		in.open(fileName);

		int index;

		if (in.is_open())
		{

			cout << "\n**Loading Building data - " << fileName << endl;
			char lineCh[1024];
			string line;		

			// Read the ID of the floor
			in.getline(lineCh, 1024);
			line = string(lineCh);

			index = line.find_first_of(',');
			//name of the building
			this->m_NameStr = line.substr(0, index);
			line = line.substr(index + 1);
			//number of floors
			index = line.find_first_of(',');
			this->m_NFloors = atoi(line.substr(0, index).c_str());
			this->m_Floors = new C_Floor[this->m_NFloors];
			//number of data points in the OAC file
			//OAC is the only data for the whole building
			line = line.substr(index + 1);
			this->m_NData = atoi(line.c_str());						

			// Read the source of the OAC data
			in.getline(lineCh, 1024);
			line = string(lineCh);

			this->m_Data = new C_DataSource(this->m_NAttr, this->m_NData);
			this->readData((char *)line.c_str());

			// Load individual floors
			for (int i = 0; i < this->m_NFloors; i++)
			{
				cout << "Loading Floor: " << i + 1 << endl;
				in.getline(lineCh, 1024);
				line = string(lineCh);	

				index = line.find_first_of(',');
				//floor id (number)
				this->m_Floors[i].m_Id = atoi(line.substr(0, index).c_str());
				line = line.substr(index + 1);
				//floor walls file
				index = line.find_first_of(',');
				this->m_Floors[i].readWalls((char *)line.substr(0, index).c_str());
				line = line.substr(index + 1);
				//floor fill file
				index = line.find_first_of(',');
				this->m_Floors[i].readFills((char *)line.substr(0, index).c_str());
				line = line.substr(index + 1);
				//zone data file
				index = line.find_first_of(',');
				this->m_Floors[i].readDataZone((char *)line.substr(0, index).c_str(), this->m_NData);
				line = line.substr(index + 1);
				//floor data file
				this->m_Floors[i].readDataFloor((char *)line.c_str(), this->m_NData);

				this->m_Floors[i].compMidPoint();

				this->m_Floors[i].init();
			}

		}
		else
		{
			cout << "Error Building: File - " << fileName << " cannot be opened." << endl;
		}
	}

	// Read the builing data
	void C_Building::readData(char * fileName)
	{
		ifstream in;
		in.open(fileName);

		if (in.is_open())
		{
			cout << "\tLoading building sensor data - " << fileName << endl;
			char lineCh[1024];
			string line;		

			int index;						

			for (int i = 0; i < this->m_NData; i++)
			{
				in.getline(lineCh, 1024);
				line = string(lineCh);

				index = line.find_first_of(',');

				string date = line.substr(0, index);

				line = line.substr(index + 1);

				this->m_Data->m_Time[i] = date;
				this->m_Data->m_Val[0][i] = atof(line.c_str());						
			}

			// Also convert the string data and time to their float representations
			this->m_Data->extractTime();	
			this->m_Data->extractMinMax();
		}
		else{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}
	}

	// Increases the data pointer
	void C_Building::incData(){
		this->m_DataPoint++;

		if (this->m_DataPoint == this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_NVal)
		{
			this->m_DataPoint = 0;
		}

		for (int i = 0; i < this->m_NFloors; i++)
		{
			this->m_Floors[i].compAvgTemp(this->m_DataPoint);
		}

		this->m_TimeNow = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_Time[this->m_DataPoint];
	}

	// Decreases the data pointer
	void C_Building::decData(){
		this->m_DataPoint--;

		if (this->m_DataPoint < 0.0)
		{
			this->m_DataPoint = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_NVal - 1;
		}

		for (int i = 0; i < this->m_NFloors; i++)
		{
			this->m_Floors[i].compAvgTemp(this->m_DataPoint);
		}

		this->m_TimeNow = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_Time[this->m_DataPoint];
	}

	// This function updates the current time
	void C_Building::updateTime()
	{
		for (int i = 0; i < this->m_NFloors; i++)
		{
			this->m_Floors[i].compAvgTemp(this->m_DataPoint);
		}

		this->m_TimeNow = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_Time[this->m_DataPoint];
	}

	// Sets the current time and data
	void C_Building::setCurrTime()
	{
		this->m_TimeNow = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Data->m_Time[this->m_DataPoint];
	}

	//  Evaluates the comfort of a give zone based on the expert fuzzy rules
	void C_Building::updateMinMax()
	{
		for(int i = 0; i < this->m_Data->m_NAttr; i++)
		{
			this->m_Data->m_MinVal[i] = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Alg->m_ValMin[i + _FNL_num_zone_dims + 1];
			this->m_Data->m_MaxVal[i] = this->m_Floors[this->m_SelectFloor].m_Zones[0].m_Alg->m_ValMax[i + _FNL_num_zone_dims + 1];
		}
	}

	// Slect the lowest floor that has data
	void C_Building::updateSelectFloor()
	{
		int sel_floor = 0;
		for(int i = 0; i < this->m_NFloors; i++)
		{
			if(this->m_Floors[i].m_IsReady)
			{
				sel_floor = i;
				break;
			}
		}
		this->m_SelectFloor = sel_floor;
	}
};

#endif // __BUILDING_H__