//---------------------------------------------------------------------------
// Floor.h
// Implements data structures associated with storing the floor information and geometry
// Date:  4/12/2012
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------


#ifndef __FLOOR_H__
#define __FLOOR_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string.h>
#include <cstdio>

#include "Zone.h"

using namespace std;

// Returns the string with the file name for the normalization values for given building_floor_zone
string getMinMaxFileName(int floorN, int zoneN, string buildingName){
	string outName = "Model/";

	outName += buildingName;
	outName += "/";
	char * str = new char[5];
	sprintf_s(str, 5, "F%i", floorN);
	outName += string(str);
	outName += "_";
	sprintf_s(str, 5, "Z%i", zoneN);
	outName += string(str);
	outName += "_MinMax.txt";	

	return outName;
}

// Returns the string with the file name for the clusters for given building_floor_zone
string getClusterFileName(int floorN, int zoneN, string buildingName){
	string outName = "Model/";

	outName += buildingName;
	outName += "/";
	char * str = new char[5];
	sprintf_s(str, 5, "F%i", floorN);
	outName += string(str);
	outName += "_";
	sprintf_s(str, 5, "Z%i", zoneN);
	outName += string(str);
	outName += "_Cluster.txt";	

	return outName;
}


// Class implementing the floor
class C_Floor{
public:
	// ID of the floor
	int m_Id;

	// Number of occupants zoens
	int m_NZones;

	//Array of the zones
	C_Zone * m_Zones;

	// Boolean flags indicating if wall and fill data has been provided and loadeds
	bool m_HasWall;
	bool m_HasFill;	

	// id of the selected zone
	int m_SelectZone;
	
	// Avg Temp
	float m_AvgTemp;

	// For debuging purposes
	// Avg color
	float m_AvgCol;

	// Flag whether there is an anomaly present on given floor based on the clusters
	bool m_HasAnomaly_Cluster;

	// Highest anomaly confidence on the floor due to the cluster
	float m_MaxAnomConf_Cluster;

	// Flag whether there is an anomaly present on given floor based on the expert rules
	bool m_HasAnomaly_Rules;

	// Highest anomaly confidence on the floor due to the expert rules
	float m_MaxAnomConf_Rules;

	// Lowest comfort on the floor due to the expert comfort rules
	float m_MinAnomConf_Comfort;

	// Lowest efficiency on the floor due to the expert efficiency rules
	float m_MinAnomConf_Efficiency;

	// Flag whether there is an anomaly present due to comfort or efficiency on given floor based on the expert rules
	bool has_comfort_anomaly;
	bool has_efficiency_anomaly;

	// Boolean flag determining whether this floor has data available
	bool m_IsReady;

	// Data source of given floor
	C_DataSource * m_Data;

	// Number of attributes
	int m_NAttr;

	// Labels of the building data
	string * m_DataNames;

	// Units of the building data
	string * m_DataUnits;

	// Min and max attibutes values for the entire floor
	float * m_MinVal;	
	float * m_MaxVal;

	//flag indicating whether the current vlue is out of bounds or not
	bool out_of_bounds_flag;
public:

	// Constructor
	C_Floor(){
		this->m_Id = 0;
		this->m_NZones = 0;
		this->m_Zones;

		this->m_HasWall = false;
		this->m_HasFill = false;		

		this->m_SelectZone = -1;

		this->m_AvgCol = 0.0;
		this->m_AvgTemp = 0.0;

		this->m_HasAnomaly_Cluster = false;
		this->m_MaxAnomConf_Cluster = 0.0f;

		this->m_HasAnomaly_Rules = false;
		this->m_MaxAnomConf_Rules = 0.0f;

		this->has_comfort_anomaly = false;
		this->m_MinAnomConf_Comfort = 1.0f;

		this->has_efficiency_anomaly = false;
		this->m_MinAnomConf_Efficiency = 1.0f;

		this->m_IsReady = false;

		this->m_NAttr = _FNL_num_ahu_dims;

		this->m_DataNames = new string[this->m_NAttr];
		_FNL_GetDimNamesShort(this->m_DataNames, "ahu");
		/*
		this->m_DataNames[0] = "CLT";
		this->m_DataNames[1] = "MAT";
		this->m_DataNames[2] = "RAT";
		this->m_DataNames[3] = "DMP";
		this->m_DataNames[4] = "ELD";
		this->m_DataNames[5] = "ECR";
		this->m_DataNames[6] = "SLD";
		this->m_DataNames[7] = "SCR";
		*/
		this->m_DataUnits = new string[this->m_NAttr];
		_FNL_GetDimUnit(this->m_DataUnits, "ahu");
		/*
		this->m_DataUnits[0] = "F";
		this->m_DataUnits[1] = "F";
		this->m_DataUnits[2] = "F";
		this->m_DataUnits[3] = "%%";
		this->m_DataUnits[4] = "%%";
		this->m_DataUnits[5] = "A";
		this->m_DataUnits[6] = "%%";
		this->m_DataUnits[7] = "A";
		*/
		//for(int i = 0; i < this->m_NAttr; i++)
		//	cout << this->m_DataNames[i] << "::" << this->m_DataUnits[i] << endl;

		this->m_MinVal = new float[this->m_NAttr];	
		this->m_MaxVal = new float[this->m_NAttr];	

		this->out_of_bounds_flag = false;
	}

	// Floor init function
	void C_Floor::init(){
		this->compMidPoint();
		this->compAvgTemp(0);
	}

	// Reads the description of the floor from a text file
	void C_Floor::readWalls(char * fileName)
	{
		ifstream in;
		in.open(fileName);

		int index;

		if (in.is_open())
		{
			cout << "\tLoading Floor data (walls) - " << fileName << endl;
			char lineCh[1024];
			string line;		

			// Read the normalization values
			float minX, maxX, minY, maxY;
			in.getline(lineCh, 1024);
			line = string(lineCh);

			index = line.find_first_of(',');
			minX = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			index = line.find_first_of(',');
			maxX = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			index = line.find_first_of(',');
			minY = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			maxY = atof(line.c_str());

			// Read the number of zones
			in.getline(lineCh, 1024);
			line = string(lineCh);
			this->m_NZones = atoi(line.c_str());

			// Create the zones
			this->m_Zones = new C_Zone[this->m_NZones];

			// Read individual zones
			for (int i = 0; i < this->m_NZones; i++){
				in.getline(lineCh, 1024);
				line = string(lineCh);
				index = line.find_first_of(',');
				this->m_Zones[i].m_ID = atoi(line.substr(0, index).c_str());
				line = line.substr(index + 1);

				index = line.find_first_of(',');
				this->m_Zones[i].m_NVertex = atoi(line.substr(0, index).c_str());
				line = line.substr(index + 1);

				this->m_Zones[i].m_VertexX = new float[this->m_Zones[i].m_NVertex];
				this->m_Zones[i].m_VertexY = new float[this->m_Zones[i].m_NVertex];

				for (int j = 0; j < this->m_Zones[i].m_NVertex - 1; j++)
				{
					index = line.find_first_of(',');
					this->m_Zones[i].m_VertexX[j] = (atof(line.substr(0, index).c_str()) - minX) / (maxX - minX);
					line = line.substr(index + 1);

					index = line.find_first_of(',');
					this->m_Zones[i].m_VertexY[j] = 1.0 - (atof(line.substr(0, index).c_str()) - minY) / (maxY - minY);
					line = line.substr(index + 1);					
				}

				index = line.find_first_of(',');
				this->m_Zones[i].m_VertexX[this->m_Zones[i].m_NVertex - 1] = (atof(line.substr(0, index).c_str()) - minX) / (maxX - minX);
				line = line.substr(index + 1);

				this->m_Zones[i].m_VertexY[this->m_Zones[i].m_NVertex - 1] = 1.0 - (atof(line.c_str()) - minY) / (maxY - minY);
			}

			this->m_HasWall = true;

		}
		else{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}
	}

	// Reads the description of the floor from a text file
	void C_Floor::readFills(char * fileName){
		ifstream in;
		in.open(fileName);

		int index;

		if (in.is_open()){

			cout << "\tLoading Floor data (fills)- " << fileName << endl;
			char lineCh[1024];
			string line;		

			// Read the ID of the floor		

			// Read the normalization values
			float minX, maxX, minY, maxY;
			in.getline(lineCh, 1024);
			line = string(lineCh);	

			index = line.find_first_of(',');
			minX = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			index = line.find_first_of(',');
			maxX = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			index = line.find_first_of(',');
			minY = atof(line.substr(0, index).c_str());
			line = line.substr(index + 1);

			maxY = atof(line.c_str());

			// Read the number of zones
			in.getline(lineCh, 1024);
			line = string(lineCh);
			float NZones = atoi(line.c_str());						

			// Read individual zones
			for (int i = 0; i < NZones; i++){
				in.getline(lineCh, 1024);
				line = string(lineCh);
				index = line.find_first_of(',');
				this->m_Zones[i].m_ID = atoi(line.substr(0, index).c_str());
				line = line.substr(index + 1);

				index = line.find_first_of(',');
				this->m_Zones[i].m_NPoly = atoi(line.substr(0, index).c_str());

				this->m_Zones[i].m_Poly = new C_Poly[this->m_Zones[i].m_NPoly];

				for (int j = 0; j < this->m_Zones[i].m_NPoly; j++){
					in.getline(lineCh, 1024);
					line = string(lineCh);

					index = line.find_first_of(',');
					this->m_Zones[i].m_Poly[j].m_NVertex = atoi(line.substr(0, index).c_str());
					line = line.substr(index + 1);

					this->m_Zones[i].m_Poly[j].m_VertexX = new float[this->m_Zones[i].m_Poly[j].m_NVertex];
					this->m_Zones[i].m_Poly[j].m_VertexY = new float[this->m_Zones[i].m_Poly[j].m_NVertex];

					for (int v = 0; v < this->m_Zones[i].m_Poly[j].m_NVertex - 1; v++){
						index = line.find_first_of(',');
						this->m_Zones[i].m_Poly[j].m_VertexX[v] = (atof(line.substr(0, index).c_str()) - minX) / (maxX - minX);
						line = line.substr(index + 1);

						index = line.find_first_of(',');
						this->m_Zones[i].m_Poly[j].m_VertexY[v] = 1.0 - (atof(line.substr(0, index).c_str()) - minY) / (maxY - minY);
						line = line.substr(index + 1);					
					}

					index = line.find_first_of(',');
					this->m_Zones[i].m_Poly[j].m_VertexX[this->m_Zones[i].m_Poly[j].m_NVertex - 1] = (atof(line.substr(0, index).c_str()) - minX) / (maxX - minX);
					line = line.substr(index + 1);

					this->m_Zones[i].m_Poly[j].m_VertexY[this->m_Zones[i].m_Poly[j].m_NVertex - 1] = 1.0 - (atof(line.c_str()) - minY) / (maxY - minY);
				}
			}

			this->m_HasFill = true;

		}
		else{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}
	}

	// Reads the data source for each zone
	void C_Floor::readDataZone(char * fileName, int NData)
	{
		ifstream in;
		in.open(fileName);		

		if (in.is_open())
		{
			cout << "\tLoading zone data information file - " << fileName << endl;
			char lineCh[1024];
			string line;		

			//get the number of zone level datasets
			in.getline(lineCh, 1024);
			line = string(lineCh);
			int num_zone_data = atoi(line.c_str());

			int index;			
			//initialize the data source objects for each zone
			for (int i = 0; i < this->m_NZones; i++)
			{
				//this->m_Zones[i].m_Data = new C_DataSource(this->m_NAttr, NData);
				this->m_Zones[i].m_Data = new C_DataSource(_FNL_num_zone_dims, NData);
				//this->m_Zones[i].m_Data = new C_DataSource(3, NData);
			}			

			for (int i = 0; i < num_zone_data; i++)
			{
				in.getline(lineCh, 1024);
				line = string(lineCh);
				this->loadDataZone((char*)line.c_str(), NData, i);
			}
		}
		else
		{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}

		// Extract the min and max values for the entire floor
		this->extractMinMax();
	}

	//loads the data from a given file to the (_data_index)th index
	void C_Floor::loadDataZone(char * fileName, int NData, int _data_index)
	{
		ifstream in;
		in.open(fileName);		

		if (in.is_open())
		{
			cout << "\t\tLoading zone data - " << fileName << endl;
			char lineCh[1024];
			string line;		

			int index;			

			for (int i = 0; i < NData; i++)
			{
				in.getline(lineCh, 1024);
				line = string(lineCh);

				index = line.find_first_of(',');
				//read the date
				string date = line.substr(0, index);

				line = line.substr(index + 1);
				//for each zone read in the data except the last one (because no comma)
				for (int j = 0; j < this->m_NZones - 1; j++)
				{
					index = line.find_first_of(',');
					// check if the value is missing, if so then reuse previous value
					if (index < 2)
					{
						this->m_Zones[j].m_Data->m_Time[i] = date;
						this->m_Zones[j].m_Data->m_Val[_data_index][i] = this->m_Zones[j].m_Data->m_Val[_data_index][i - 1];
					}
					//otherwise read the value form file
					else
					{
						this->m_Zones[j].m_Data->m_Time[i] = date;
						this->m_Zones[j].m_Data->m_Val[_data_index][i] = atof(line.substr(0, index).c_str());
					}

					line = line.substr(index + 1);
				}
				//do the same for the last zone
				if (atof(line.c_str()) < 1.0)
				{
					this->m_Zones[this->m_NZones - 1].m_Data->m_Time[i] = date;
					this->m_Zones[this->m_NZones - 1].m_Data->m_Val[_data_index][i] = this->m_Zones[this->m_NZones - 1].m_Data->m_Val[_data_index][i - 1];
				}
				else
				{
					this->m_Zones[this->m_NZones - 1].m_Data->m_Time[i] = date;
					this->m_Zones[this->m_NZones - 1].m_Data->m_Val[_data_index][i] = atof(line.c_str());
				}
			}

			// Also convert the string data and time to their float representations for all zones and extract the min and max of each zone data
			for (int j = 0; j < this->m_NZones; j++)
			{
				this->m_Zones[j].m_Data->extractTime();
				//TODO
				this->m_Zones[j].m_Data->extractMinMax();
			}
		}
		else
		{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}
	}
	
	// Reads the data source for the floor sensors
	void C_Floor::readDataFloor(char * fileName, int NData)
	{
		ifstream in;
		in.open(fileName);		

		if (in.is_open())
		{
			cout << "\tLoading Floor sensors data - " << fileName << endl;
			char lineCh[1024];
			string line;		

			int index;			
			//number of attributes is set to 8
			this->m_Data = new C_DataSource(this->m_NAttr, NData);

			for (int i = 0; i < NData; i++)
			{
				in.getline(lineCh, 1024);
				line = string(lineCh);

				index = line.find_first_of(',');
				//read the date
				string date = line.substr(0, index);
				line = line.substr(index + 1);

				for (int j = 0; j < this->m_NAttr - 1; j++)
				{
					index = line.find_first_of(',');

					// check if the value is missing, if so then reuse previous value
					this->m_Data->m_Time[i] = date;
					this->m_Data->m_Val[j][i] = atof(line.substr(0, index).c_str());					

					line = line.substr(index + 1);
				}
				//same for the last attribute
				this->m_Data->m_Time[i] = date;
				this->m_Data->m_Val[this->m_NAttr - 1][i] = atof(line.c_str());				
			}

			// Also convert the string data and time to their float representations for all zones and extract the min and max of each zone data
			this->m_Data->extractTime();
			this->m_Data->extractMinMax();			
		}
		else{
			cout << "Error: File - " << fileName << " cannot be opened." << endl;
		}

		// Extract the min and max values for the entire floor
		//TODO
		this->extractMinMax();
	}


	// Extract the Min and Max values for the entire floor
	void C_Floor::extractMinMax()
	{
		for (int a = 0; a < this->m_NAttr; a++)
		{
			this->m_MinVal[a] = FLT_MAX;
			this->m_MaxVal[a] = -FLT_MAX;

			for (int z = 0; z < this->m_NZones; z++)
			{
				this->m_MinVal[a] = min(this->m_MinVal[a], this->m_Zones[z].m_Data->m_MinVal[a]);
				this->m_MaxVal[a] = max(this->m_MaxVal[a], this->m_Zones[z].m_Data->m_MaxVal[a]);
			}
		}
	}

	//  Evaluates the comfort of a give zone based on the expert fuzzy rules
	void C_Floor::updateMinMax()
	{
		for(int i = 0; i < this->m_Data->m_NAttr; i++)
		{
			this->m_Data->m_MinVal[i] = this->m_Zones[0].m_Alg->m_ValMin[i + _FNL_num_zone_dims + _FNL_num_building_dims + 1];
			this->m_Data->m_MaxVal[i] = this->m_Zones[0].m_Alg->m_ValMax[i + _FNL_num_zone_dims + _FNL_num_building_dims + 1];
		}
	}

	// Computes the mid-point of each zone - used for rendering the zone ID
	void C_Floor::compMidPoint()
	{
		for (int i = 0; i < this->m_NZones; i++)
		{
			this->m_Zones[i].m_MidX = 0.0;
			this->m_Zones[i].m_MidY = 0.0;

			for (int j = 0; j < this->m_Zones[i].m_Poly[0].m_NVertex; j++){
				this->m_Zones[i].m_MidX += this->m_Zones[i].m_Poly[0].m_VertexX[j];
				this->m_Zones[i].m_MidY += this->m_Zones[i].m_Poly[0].m_VertexY[j];
			}

			this->m_Zones[i].m_MidX /= this->m_Zones[i].m_Poly[0].m_NVertex;
			this->m_Zones[i].m_MidY /= this->m_Zones[i].m_Poly[0].m_NVertex;
		}
	}

	// Calculates the average floor temperature
	void C_Floor::compAvgTemp(int dataP)
	{
		this->m_AvgTemp = 0.0;

			for (int j = 0; j < this->m_NZones; j++){
				this->m_AvgTemp += this->m_Zones[j].m_Data->m_Val[0][dataP];
			}

			this->m_AvgTemp /= (float)this->m_NZones;
	}

	// Prints out the floor
	void printFloor(){
		cout << "Floor ID: " << this->m_Id << ", Zones: " << this->m_NZones << endl;

		for (int i = 0; i < this->m_NZones; i++){
			this->m_Zones[i].printOut();
		}
		cout << endl;
	}
//TODO
	// Evaluates the normalcy of all zones based on the clusters
	void C_Floor::evalZonesMinMax(FVec * fvec, int dataPoint, float threshold, int featureStart)
	{
		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++)
		{
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->out_of_bounds_flag = false;

		//this->m_HasAnomaly_Cluster = false;
		//this->m_MaxAnomConf_Cluster = 0.0;
		
		// Evaluate each zone
		for (int i = 0; i < this->m_NZones; i++)
		{
			this->m_Zones[i].evalZoneMinMax(fvec, dataPoint);
			if(this->m_Zones[i].out_of_bounds_flag)
				this->out_of_bounds_flag = true;
			/*
			if (this->m_Zones[i].m_AnomalI > threshold){
				this->m_HasAnomaly_Cluster = true;				
			}

			if (this->m_Zones[i].m_AnomalI > this->m_MaxAnomConf_Cluster){
				this->m_MaxAnomConf_Cluster = this->m_Zones[i].m_AnomalI;
			}
			*/
		}
	}

	// Evaluates the normalcy of all zones based on the clusters
	void C_Floor::evalZonesCluster(FVec * fvec, int dataPoint, float threshold, int featureStart)
	{
		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++)
		{
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->m_HasAnomaly_Cluster = false;
		this->m_MaxAnomConf_Cluster = 0.0;
		
		// Evaluate each zone
		for (int i = 0; i < this->m_NZones; i++)
		{
			this->m_Zones[i].evalZoneCluster(fvec, dataPoint);

			if (this->m_Zones[i].m_AnomalI > threshold)
			{
				this->m_HasAnomaly_Cluster = true;				
			}

			if (this->m_Zones[i].m_AnomalI > this->m_MaxAnomConf_Cluster)
			{
				this->m_MaxAnomConf_Cluster = this->m_Zones[i].m_AnomalI;
			}
		}
	}

	// Evaluates the normalcy of all zones based on the expert rules
	void C_Floor::evalZonesRule(FVec * fvec, int dataPoint, float threshold, int featureStart, FuzzyRuleSet * _expert_rule_set){

		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++){
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->m_HasAnomaly_Rules = false;
		this->m_MaxAnomConf_Rules = 0.0;
		// Evalute all zones
		for (int i = 0; i < this->m_NZones; i++){
			this->m_Zones[i].evalZoneRules(fvec, dataPoint, _expert_rule_set);

			if (this->m_Zones[i].m_AnomalI_Rules > threshold){
				this->m_HasAnomaly_Rules = true;
			}

			if (this->m_Zones[i].m_AnomalI_Rules > this->m_MaxAnomConf_Rules){
				this->m_MaxAnomConf_Rules = this->m_Zones[i].m_AnomalI_Rules;
			}
		}
	}

	// Evaluates the comfort of all zones based on the expert rules
	void C_Floor::evalZonesComfort(FVec * fvec, int dataPoint, float threshold, int featureStart, FuzzyRuleSet * _expert_rule_set){

		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++){
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->has_comfort_anomaly = false;
		this->m_MinAnomConf_Comfort = 1.0f;

		// Evalute all zones
		for (int i = 0; i < this->m_NZones; i++){
			this->m_Zones[i].evalZoneComfort(fvec, dataPoint, _expert_rule_set);

			if (this->m_Zones[i].comfort_level < 1 - threshold){
				this->has_comfort_anomaly = true;
			}

			if (this->m_Zones[i].comfort_level < this->m_MinAnomConf_Comfort)
			{
				this->m_MinAnomConf_Comfort = this->m_Zones[i].comfort_level;
			}
		}
	}

	// Evaluates the comfort of all zones based on the expert rules
	void C_Floor::evalZonesEfficiency(FVec * fvec, int dataPoint, float threshold, int featureStart, FuzzyRuleSet * _expert_rule_set){

		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++){
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->has_efficiency_anomaly = false;
		this->m_MinAnomConf_Efficiency = 1.0f;

		// Evalute all zones
		for (int i = 0; i < this->m_NZones; i++){
			this->m_Zones[i].evalZoneEfficiency(fvec, dataPoint, _expert_rule_set);

			if (this->m_Zones[i].efficiency_level < 1 - threshold){
				this->has_efficiency_anomaly = true;
			}

			if (this->m_Zones[i].efficiency_level < this->m_MinAnomConf_Efficiency)
			{
				this->m_MinAnomConf_Efficiency = this->m_Zones[i].efficiency_level;
			}
		}
	}

	// Updates the model of the normal behavior
	void C_Floor::updateModel(FVec * fvec, int dataPoint, int featureStart){

		// Add the floor features
		for (int a = 0; a < this->m_NAttr; a++)
		{
			fvec->coord[featureStart + a] = this->m_Data->m_Val[a][dataPoint];
		}

		this->m_Zones[this->m_SelectZone].updateModel(fvec, dataPoint);
	}

	// Updates the normalcy of all zones
	void C_Floor::evalUpdateZones(float threshold){

		this->m_HasAnomaly_Cluster = false;
		this->m_MaxAnomConf_Cluster = 0.0;

		for (int i = 0; i < this->m_NZones; i++){			

			if (this->m_Zones[i].m_AnomalI > threshold){
				this->m_HasAnomaly_Cluster = true;				
			}

			if (this->m_Zones[i].m_AnomalI > this->m_MaxAnomConf_Cluster){
				this->m_MaxAnomConf_Cluster = this->m_Zones[i].m_AnomalI;
			}

		}
	}

	// Loads the normal behavior models for each zone on the floor
	void C_Floor::loadModel(float spread, float blur, string buildingName){
		for (int i = 0; i < this->m_NZones; i++)
		{
			this->m_Zones[i].m_Alg->loadMinMax(getMinMaxFileName(this->m_Id, this->m_Zones[i].m_ID, buildingName));
			this->m_Zones[i].m_Alg->initFLCFile(getClusterFileName(this->m_Id, this->m_Zones[i].m_ID, buildingName), spread, blur);
		}
	}
};


#endif // __FLOOR_H__