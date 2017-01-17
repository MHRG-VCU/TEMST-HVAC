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
************************************************************************/

//---------------------------------------------------------------------------
// Monitor.h
// Implements a class which is responsible for creating normal behavior model of
// the zones on the floor 
// Date:  2010/6/21
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------


#ifndef __MONITOR_H__
#define __MONITOR_H__

#include <cstdlib>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <string>

#include "OCM_Alg.h"
#include "Floor.h"
//#include "FuzzyLogicFIS.h"
//#include "FIS.h"
#include "FuzzyRuleSet.h"

// Class implementing the monitor object
class C_Monitor{
public:
	// Max cluster radius
	float m_ClusterRad;

	// Cluster membership mode
	bool m_MemberMode;

	// Dimensionality of the feature vector for each zone
	int m_DimF;	

	// Blur of the fuzzy sets
	float m_Spread;

	// Blur of the IT2 FSs
	float m_Blur;

	// Display mode
	int m_DispMode;

	// Anomaly detection threshold
	float m_Threshold;

	// Number of global floor attributes (including the building attributes)
	int m_NAttr;

	// Bool array for showing data view of additional attributes
	bool * m_ShowAttr;

	// Color map for visualizing different data
	float ** m_DataColors;

	// bool mask for attribute, which are to be used for the anomaly detection
	int * m_AttrUse;

	// Features names
	string * m_InputName;
	string m_OutputName;

	// Number of selected important antecedents
	int m_NAnt_Sel;

	// Number of enabled attributes
	int m_NAnt_Enabled;

	// Data Structure for parsing linguistic anomaly description rules
	//C_FIS * m_FIS;
	FuzzyRuleSet * expert_anomaly_rules;
	//Fuzzy rule sets describing comfort
	FuzzyRuleSet * expert_comfort_rules;
	//Fuzzy rule sets describing efficiency
	FuzzyRuleSet * expert_efficiency_rules;

	// Anomaly mode: 
	//0 - Uses clustering based, 1 - uses expert rules based
	//2 - Combined
	//3 - Comfort based
	//4 - Efficiency based
	int m_AnomMode;

	//flag indicating out of bounds
	bool out_of_bounds_flag;

	//flag indicating whether to use the auto min max or not
	bool use_auto_min_max;

public:
	// constructor
	C_Monitor::C_Monitor(){
		this->m_DimF = _FNL_num_dim_names;
		
		this->m_ClusterRad = 0.5f;
		
		this->m_MemberMode = false;

		this->m_Spread = 2.0f;
		this->m_Blur = 0.1f;

		this->m_DispMode = 0;

		this->m_Threshold = 0.8f;	

		this->m_AnomMode = 0;

		this->m_AttrUse = new int[this->m_DimF];

		for (int i = 0; i < this->m_DimF; i++){
			this->m_AttrUse[i] = 1;
		}

		this->m_NAttr = _FNL_num_dim_names;
		this->m_ShowAttr = new bool[this->m_NAttr];
		for (int i = 0; i < this->m_NAttr; i++)
		{
			this->m_ShowAttr[i] = false;
		}

		// Create a color pallete
		this->m_DataColors = new float*[this->m_NAttr];
		for (int i = 0; i < this->m_NAttr; i++){
			this->m_DataColors[i] = new float[3];
		}
		
		// Red
		this->m_DataColors[0][0] = 1.0f, this->m_DataColors[0][1] = 0.0f, this->m_DataColors[0][2] = 0.0f,
		// Green
		this->m_DataColors[1][0] = 0.0f, this->m_DataColors[1][1] = 1.0f, this->m_DataColors[1][2] = 0.0f,
		// Gray
		this->m_DataColors[2][0] = 0.5f, this->m_DataColors[2][1] = 0.5f, this->m_DataColors[2][2] = 0.5f,
		// Magenta
		this->m_DataColors[3][0] = 1.0f, this->m_DataColors[3][1] = 0.0f, this->m_DataColors[3][2] = 1.0f,
		// Cyan
		this->m_DataColors[4][0] = 0.0f, this->m_DataColors[4][1] = 1.0f, this->m_DataColors[4][2] = 1.0f,
		// Dark Red
		this->m_DataColors[5][0] = 0.5f, this->m_DataColors[5][1] = 0.0f, this->m_DataColors[5][2] = 0.0f,
		// 
		this->m_DataColors[6][0] = 0.0f, this->m_DataColors[6][1] = 0.5f, this->m_DataColors[6][2] = 0.5f,
		// 
		this->m_DataColors[7][0] = 0.6f, this->m_DataColors[7][1] = 0.6f, this->m_DataColors[7][2] = 1.0f,
		// 
		this->m_DataColors[8][0] = 1.0f, this->m_DataColors[8][1] = 0.5f, this->m_DataColors[8][2] = 0.5f,
		
		this->m_InputName = new string[this->m_DimF];
		_FNL_GetDimNames(this->m_InputName);
		/*
		this->m_InputName[0] = "Zone Temp.";
		this->m_InputName[1] = "Time";
		this->m_InputName[2] = "Outside Air Temp.";
		this->m_InputName[3] = "Chiller Temp.";
		this->m_InputName[4] = "Mixed Air Temp.";
		this->m_InputName[5] = "Return Air Temp.";
		this->m_InputName[6] = "Damper Position";
		this->m_InputName[7] = "Ex. Fan Load";
		this->m_InputName[8] = "Ex. Fan Current";
		this->m_InputName[9] = "Supp. Fan Load";
		this->m_InputName[10] = "Supp. Fan Current";		
		*/
		this->m_OutputName = "Confidence";

		this->m_NAnt_Sel = 1;
		
		this->m_NAnt_Enabled = this->m_DimF;

		//this->m_FIS = new C_FIS();
		//anomaly
		this->expert_anomaly_rules = new FuzzyRuleSet();
		this->expert_anomaly_rules->output_dim_name = "Anomaly";
		//comfort
		this->expert_comfort_rules = new FuzzyRuleSet();
		this->expert_comfort_rules->output_dim_name = "Comfort";
		//Efficiency
		this->expert_efficiency_rules = new FuzzyRuleSet();
		this->expert_efficiency_rules->output_dim_name = "Efficiency";

		this->out_of_bounds_flag = false;
		this->use_auto_min_max = false;
	}

	// Calculates the min and max normalization values for each zone
	void C_Monitor::calcMinMaxValFloor(C_Building * bl, int NTrain)
	{

		cout << "Computing min max values for Floor: " << bl->m_Floors[bl->m_SelectFloor].m_Id << " ..." ;

		float * minVal = new float[this->m_DimF];
		float * maxVal = new float[this->m_DimF];
		float * vec = new float[this->m_DimF];

		for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NZones; i++)
		{				
			// Extract the min and max
			for (int d = 0; d < this->m_DimF; d++)
			{
				minVal[d] = 100000;
				maxVal[d] = -100000;
			}
			
			for (int j = 1; j < NTrain; j++)
			{
				//zone level
				for(int a = 0; a < _FNL_num_zone_dims; a++)
				{
					vec[a] = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Data->m_Val[a][j];
				}
				//time
				vec[_FNL_num_zone_dims] = bl->m_Data->m_TimeVal[j];
				
				for (int a = 0; a < _FNL_num_building_dims; a++){
					vec[_FNL_num_zone_dims + 1 + a] = bl->m_Data->m_Val[a][j];					
				}

				for (int a = 0; a < bl->m_Floors[bl->m_SelectFloor].m_NAttr; a++){
					vec[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a] = bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[a][j];					
				}

				for (int d = 0; d < this->m_DimF; d++){
					minVal[d] = min(minVal[d], vec[d]);
					maxVal[d] = max(maxVal[d], vec[d]);
				}
			}
			//time
			minVal[_FNL_num_zone_dims] = 0.0;
			maxVal[_FNL_num_zone_dims] = 1.0;

			// Store the min and max
			string fileName = getMinMaxFileName(bl->m_Floors[bl->m_SelectFloor].m_Id, bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_ID, bl->m_NameStr);

			ofstream outF;
			outF.open(fileName.c_str());

			for (int d = 0; d < this->m_DimF; d++)
			{
				outF << minVal[d] << ", " << maxVal[d] << endl;
			}	
		}

		delete [] minVal;
		delete [] maxVal;
		delete [] vec;

		cout << " DONE!" << endl;			
	}

	// Train the normal behavior model for each zone	
	void C_Monitor::trainOCM(C_Building * bl, int NTrain)
	{
		cout << "Training the normal behavior model " << bl->m_Floors[bl->m_SelectFloor].m_Id << " ..." ;

		int dimF = this->m_DimF;		
		FVec * fvec = new FVec(this->m_DimF);

		// Go through all zones
		for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NZones; i++)
		{		
			bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->reset(0.02f);
			bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->initConst(this->m_DimF, this->m_ClusterRad, this->m_MemberMode);

			bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->use_auto_min_max = this->use_auto_min_max;

			// Load the min max normalization values
			if(this->use_auto_min_max)
			{
				bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->loadMinMax(getMinMaxFileName(bl->m_Floors[bl->m_SelectFloor].m_Id, 
						bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_ID, bl->m_NameStr));
			}
			else
			{
				bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->loadMinMax("Data_MinMax/dim_min_max.TXT");
			}
			// Go through the training data points
			for (int j = 1; j < NTrain; j++)
			{

				// Compose the training data vector
				
				//zone level
				for(int a = 0; a < _FNL_num_zone_dims; a++)
				{
					fvec->coord[a] = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Data->m_Val[a][j];
				}
				//time
				fvec->coord[_FNL_num_zone_dims] = bl->m_Data->m_TimeVal[j];
				
				for (int a = 0; a < _FNL_num_building_dims; a++)
				{
					fvec->coord[_FNL_num_zone_dims + 1 + a] = bl->m_Data->m_Val[a][j];					
				}

				for (int a = 0; a < bl->m_Floors[bl->m_SelectFloor].m_NAttr; a++)
				{
					fvec->coord[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a] = bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[a][j];					
				}

				// normalize the input vector
				for (int d = 0; d < this->m_DimF; d++)
				{
					float minVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->m_ValMin[d];
					float maxVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->m_ValMax[d];

					fvec->coord[d] = (fvec->coord[d] - minVal) / (maxVal - minVal);				
				}							

				// Add the input into the clusters
				bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->clusters->update(fvec);
			}

			// Store the extracted clusters
			bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Alg->clusters->saveClusters(getClusterFileName(bl->m_Floors[bl->m_SelectFloor].m_Id, 
				bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_ID, bl->m_NameStr));			
		}

		delete fvec;

		cout << " DONE!" << endl;			
	}

	// Saves the clusters models for the given floor
	void C_Monitor::saveModel(C_Floor * floor, string buildingName)
	{
		for (int i = 0; i < floor->m_NZones; i++)
		{
			// Store the extracted clusters
			floor->m_Zones[i].m_Alg->clusters->saveClusters(getClusterFileName(floor->m_Id, floor->m_Zones[i].m_ID, buildingName));		
		}
	}

	// Loads the normal behavior models for each zone on the floor
	void C_Monitor::loadModel(C_Building * bl, C_Floor * floor, string buildingName)
	{
		for (int i = 0; i < floor->m_NZones; i++)
		{
			floor->m_Zones[i].m_Alg->initConst(this->m_DimF, this->m_ClusterRad, this->m_MemberMode);
			floor->m_Zones[i].m_Alg->use_auto_min_max = this->use_auto_min_max;

			if(this->use_auto_min_max)
			{
				floor->m_Zones[i].m_Alg->loadMinMax(getMinMaxFileName(floor->m_Id, floor->m_Zones[i].m_ID, buildingName));			
			}
			else
			{
				floor->m_Zones[i].m_Alg->loadMinMax("Data_MinMax/dim_min_max.TXT");
			}
			floor->m_Zones[i].m_Alg->initFLCFile(getClusterFileName(floor->m_Id, floor->m_Zones[i].m_ID, buildingName), this->m_Spread, this->m_Blur);			
			floor->m_Zones[i].updateMinMax();
		}	
		floor->updateMinMax();
		bl->updateMinMax();
		floor->m_IsReady = true;
	}

	// Generates report for a given time-frame (startIndex to endIndex) using the cluster
	void C_Monitor::generateReportCluster(C_Building * bl, int startIndex, int endIndex)
	{
		ofstream out;

		C_Floor * floor = &(bl->m_Floors[bl->m_SelectFloor]);

		string fileName = bl->m_NameStr;
		char * str = new char[5];
		sprintf_s(str, 5, "_F%i",floor->m_Id);

		fileName += str;
		fileName += "_Cluster_Report.txt";
		
		out.open(fileName.c_str());
		// Check that the interval is correct
		if ((startIndex < 0) || (endIndex > bl->m_NData) || (startIndex > endIndex)){
			cout << "The interval was not correct: " << startIndex << " - " << endIndex << endl;
			return;
		}

		for (int j = 0; j < floor->m_NZones; j++){
			floor->m_Zones[j].m_StartAnomal = 0;
			floor->m_Zones[j].m_PrevAnomal = false;
		}

		cout << "Generating report (Clusters) from " << floor->m_Zones[0].m_Data->m_Time[startIndex] << " to " << 
			floor->m_Zones[0].m_Data->m_Time[endIndex - 1] << " ..." ;

		for (int i = startIndex; i < endIndex; i++){
			// create the feature vector and the building data
			FVec * fvec = new FVec(this->m_DimF);
			fvec->setOn(this->m_AttrUse);
		
			for (int a = 0; a < bl->m_NAttr; a++){
				fvec->coord[2 + a] = bl->m_Data->m_Val[a][i];
			}

			floor->evalZonesCluster(fvec, i, this->m_Threshold, 2 + bl->m_NAttr);

			for (int j = 0; j < floor->m_NZones; j++){
				if (floor->m_Zones[j].m_AnomalI > this->m_Threshold){
					if (!floor->m_Zones[j].m_PrevAnomal){
						floor->m_Zones[j].cluster_fis->setMFZero();		
						floor->m_Zones[j].cluster_fis->setInputSignifZero();
						floor->m_Zones[j].m_PrevAnomal = true;
						floor->m_Zones[j].m_StartAnomal = i;
					}				

					floor->m_Zones[j].cluster_fis->accumMembership();		
					floor->m_Zones[j].cluster_fis->accumInputSignificance();
				}
				else{
					if (floor->m_Zones[j].m_PrevAnomal)
					{
						floor->m_Zones[j].cluster_fis->getMaxAccuMF();
						floor->m_Zones[j].cluster_fis->computeAntAccumSelection(this->m_AttrUse);

						if (i == floor->m_Zones[j].m_StartAnomal + 1){
							out << "Anomaly - Zone: " << j + 1 <<  ", At: " << floor->m_Zones[j].m_Data->m_Time[floor->m_Zones[j].m_StartAnomal] << endl;
							
							out << "Description: ";
							for (int i = 0; i < this->m_NAnt_Sel - 1; i++){
								int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[i];
								out << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
									<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal << " and ";
							}

							int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[this->m_NAnt_Sel - 1];
								out << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
									<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal;

							out << " ( " << floor->m_Zones[j].cluster_fis->m_OutputName << " is " << 
								floor->m_Zones[j].cluster_fis->m_OutputFuzzy[floor->m_Zones[j].cluster_fis->m_OutputMaxMemId].lingVal << " ). " << endl << endl;								
						}
						else{							
							out << "Anomaly - Zone: " << j + 1 <<  ", From: " << floor->m_Zones[j].m_Data->m_Time[floor->m_Zones[j].m_StartAnomal] << " To: " << 
								floor->m_Zones[j].m_Data->m_Time[i - 1] << endl;

							out << "Description: ";
							for (int i = 0; i < this->m_NAnt_Sel - 1; i++){
								int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[i];
								out << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
									<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal << " and ";
							}

							int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[this->m_NAnt_Sel - 1];
								out << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
									<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal;

							out << " ( " << floor->m_Zones[j].cluster_fis->m_OutputName << " is " << 
								floor->m_Zones[j].cluster_fis->m_OutputFuzzy[floor->m_Zones[j].cluster_fis->m_OutputMaxMemId].lingVal << " ). " << endl << endl;									
						}

						floor->m_Zones[j].m_PrevAnomal = false;						
					}									
				}
			}

			delete fvec;
		}

		cout << " DONE!" << endl;
	}

	// Generates report for a given time-frame using the expert rules
	void C_Monitor::generateReportRules(C_Building * bl, int startIndex, int endIndex)
	{

		ofstream out;

		C_Floor * floor = &(bl->m_Floors[bl->m_SelectFloor]);

		string fileName = bl->m_NameStr;
		char * str = new char[5];
		sprintf_s(str, 5, "_F%i",floor->m_Id);

		fileName += str;
		fileName += "_Rules_Report.txt";
		
		out.open(fileName.c_str());
		// Check that the interval is correct
		if ((startIndex < 0) || (endIndex > bl->m_NData) || (startIndex > endIndex)){
			cout << "The interval was not correct: " << startIndex << " - " << endIndex << endl;
			return;
		}

		for (int j = 0; j < floor->m_NZones; j++){
			floor->m_Zones[j].m_StartAnomal = 0;
			floor->m_Zones[j].m_PrevAnomal = false;
		}

		cout << "Generating report (Rules) sfrom " << floor->m_Zones[0].m_Data->m_Time[startIndex] << " to " << 
			floor->m_Zones[0].m_Data->m_Time[endIndex - 1] << " ..." ;

		for (int i = startIndex; i < endIndex; i++){
			// create the feature vector and the building data
			FVec * fvec = new FVec(this->m_DimF);
			fvec->setOn(this->m_AttrUse);
		
			for (int a = 0; a < bl->m_NAttr; a++){
				fvec->coord[2 + a] = bl->m_Data->m_Val[a][i];
			}

			floor->evalZonesRule(fvec, i, this->m_Threshold, 2 + bl->m_NAttr, this->expert_anomaly_rules);

			for (int j = 0; j < floor->m_NZones; j++){
				if (floor->m_Zones[j].m_AnomalI_Rules > this->m_Threshold){
					out << "Anomaly - Zone: " << j + 1 <<  ", At: " << floor->m_Zones[j].m_Data->m_Time[i] << endl;
					
					out << "Description: " ;
					out << this->expert_anomaly_rules->myRules[floor->m_Zones[j].m_AnomI_Rules_Idx].printString() << endl;

					floor->m_Zones[j].expert_fis->getMaxMF();
					
					out << string(floor->m_Zones[j].expert_fis->m_OutputName) << " is " << 		
						floor->m_Zones[j].expert_fis->m_OutputFuzzy[floor->m_Zones[j].expert_fis->m_OutputMaxMemId].lingVal << endl;
				}			
			}
			delete fvec;
		}

		cout << " DONE!" << endl;
	}

	// This function prints out the anomaly (cluster) to the std::cout 
	void C_Monitor::printAnomalyCluster(C_Building * bl)
	{
		C_Floor * floor = &(bl->m_Floors[bl->m_SelectFloor]);	

		int j = floor->m_SelectZone;

		//floor->m_Zones[j].m_FIS_Cluster->getMaxMF();
		floor->m_Zones[j].cluster_fis->getMaxMF();

		cout << "Anomaly (Cluster) - Zone: " << j + 1 <<  ", At: " << bl->m_Data->m_Time[bl->m_DataPoint] << endl;

		cout << "Description: ";
		for (int i = 0; i < this->m_NAnt_Sel - 1; i++)
		{
			int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[i];
			cout << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
				<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal << " and ";
		}

		int idx = floor->m_Zones[j].cluster_fis->m_AntSelect[this->m_NAnt_Sel - 1];
		cout << floor->m_Zones[j].cluster_fis->m_InputName[idx] << " is " 
			<< floor->m_Zones[j].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[j].cluster_fis->m_InputMaxMemId[idx]].lingVal;
		
		cout << " ( " << floor->m_Zones[j].cluster_fis->m_OutputName << " is " 
			<< floor->m_Zones[j].cluster_fis->m_OutputFuzzy[floor->m_Zones[j].cluster_fis->m_OutputMaxMemId].lingVal << " ). " << endl << endl;
	}

	// This function prints out the anomaly (expert rule based) to the std::cout
	void C_Monitor::printAnomalyRule(C_Building * bl)
	{
		C_Floor * floor = &(bl->m_Floors[bl->m_SelectFloor]);	

		int j = floor->m_SelectZone;

		//floor->m_Zones[j].m_FIS_Cluster->getMaxMF();
		floor->m_Zones[j].cluster_fis->getMaxMF();

		cout << "Anomaly (Rule) - Zone: " << j + 1 <<  ", At: " << bl->m_Data->m_Time[bl->m_DataPoint] << endl;

		cout << "Description: ";
		
		this->expert_anomaly_rules->myRules[floor->m_Zones[j].m_AnomI_Rules_Idx].printOut();

		floor->m_Zones[floor->m_SelectZone].expert_fis->getMaxMF();
		
		string outConf = "";
		outConf += string(floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputName);		
		outConf += " is ";
		outConf += floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputMaxMemId].lingVal;

		cout << outConf << endl << endl;	
	}

	// Evaluates the anomalies in the building in the current time
	void C_Monitor::evalBuilding(C_Building * bl)
	{
		this->out_of_bounds_flag = false;

		// create the feature vector and the building data
		FVec * fvec = new FVec(this->m_DimF);
		fvec->setOn(this->m_AttrUse);

		//number of attributes in the zone level + 1 for time
		int num_attr_start = _FNL_num_zone_dims + 1;

		// Adds the building attributes
		for (int a = 0; a < bl->m_NAttr; a++)
		{
			fvec->coord[num_attr_start + a] = bl->m_Data->m_Val[a][bl->m_DataPoint];
		}

		// Evaluate all floors
		for (int i = 0; i < bl->m_NFloors; i++)
		{
			if (bl->m_Floors[i].m_IsReady)
			{
				//first check min max
				bl->m_Floors[i].evalZonesMinMax(fvec, bl->m_DataPoint, this->m_Threshold, num_attr_start + bl->m_NAttr);
				//if(!bl->m_Floors[i].out_of_bounds_flag)
				{
					//this->out_of_bounds_flag = false;
					bl->m_Floors[i].evalZonesCluster(fvec, bl->m_DataPoint, this->m_Threshold, num_attr_start + bl->m_NAttr);
					bl->m_Floors[i].evalZonesRule(fvec, bl->m_DataPoint, this->m_Threshold, num_attr_start + bl->m_NAttr, this->expert_anomaly_rules);
					bl->m_Floors[i].evalZonesComfort(fvec, bl->m_DataPoint, this->m_Threshold, num_attr_start + bl->m_NAttr, this->expert_comfort_rules);
					bl->m_Floors[i].evalZonesEfficiency(fvec, bl->m_DataPoint, this->m_Threshold, num_attr_start + bl->m_NAttr, this->expert_efficiency_rules);
				}
				if(bl->m_Floors[i].out_of_bounds_flag)
				{
					this->out_of_bounds_flag = true;
					//cout << "Out of bounds\n";
				}
			}
		}

		delete fvec;
	}

	// Updates the model of the selected zone to include to current feature vector
	void C_Monitor::updateMode(C_Building * bl)
	{
		// create the feature vector and the building data
		FVec * fvec = new FVec(this->m_DimF);
		fvec->setOn(this->m_AttrUse);
		// leave room in the front for zone level data and time
		int start_data_point = _FNL_num_zone_dims + 1; // +1 for time
		for (int a = 0; a < bl->m_NAttr; a++)
		{
			fvec->coord[start_data_point + a] = bl->m_Data->m_Val[a][bl->m_DataPoint];
		}

		bl->m_Floors[bl->m_SelectFloor].updateModel(fvec, bl->m_DataPoint, start_data_point + bl->m_NAttr);		

		delete fvec;
	}

	// Evaluates the anomalies on current floor in the current time
	void C_Monitor::evalFloor(C_Building * bl, int dataPoint)
	{
		// create the feature vector and the building data
		FVec * fvec = new FVec(this->m_DimF);
		fvec->setOn(this->m_AttrUse);
		
		//load the building level data tot he feature vector
		// leave room in the front for zone level data and time
		int start_data_point = _FNL_num_zone_dims + 1; // +1 for time
		for (int a = 0; a < bl->m_NAttr; a++)
		{
			fvec->coord[start_data_point + a] = bl->m_Data->m_Val[a][bl->m_DataPoint];
		}

		bl->m_Floors[bl->m_SelectFloor].evalZonesCluster(fvec, dataPoint, this->m_Threshold, start_data_point + bl->m_NAttr);
		bl->m_Floors[bl->m_SelectFloor].evalZonesRule(fvec, dataPoint, this->m_Threshold, start_data_point + bl->m_NAttr, this->expert_anomaly_rules);
		bl->m_Floors[bl->m_SelectFloor].evalZonesComfort(fvec, dataPoint, this->m_Threshold, start_data_point + bl->m_NAttr, this->expert_comfort_rules);
		bl->m_Floors[bl->m_SelectFloor].evalZonesEfficiency(fvec, dataPoint, this->m_Threshold, start_data_point + bl->m_NAttr, this->expert_efficiency_rules);

		delete fvec;
	}

	// Updates the anomalies on current floor for the given threshold value
	void C_Monitor::evalUpdateFloor(C_Floor * floor){
		floor->evalUpdateZones(this->m_Threshold);
	}
//TODO Multi line
	//returns a linguistic description of outof bounds information
	string C_Monitor::getDescriptionOutofBounds(C_Floor * floor, int dataPoint, int _sensor_number)
	{
		string outDesc = "";
		outDesc += floor->m_Zones[floor->m_SelectZone].getDescriptionOutofBounds(dataPoint, _sensor_number);
		return outDesc;
	}

	// Returns the linguistic description of the selected attribute rank 
	string C_Monitor::getDescription(C_Floor * floor, int rank, bool and)
	{	

		string outDesc = "";

		int idx = floor->m_Zones[floor->m_SelectZone].cluster_fis->m_AntSelect[rank];		

		outDesc += floor->m_Zones[floor->m_SelectZone].cluster_fis->m_InputName[idx];
		outDesc += " is ";
		outDesc += floor->m_Zones[floor->m_SelectZone].cluster_fis->m_InputFuzzy[idx][floor->m_Zones[floor->m_SelectZone].cluster_fis->m_InputMaxMemId[idx]].lingVal;				

		if (and){
			outDesc += " and";
		}		

		return outDesc;	
	}

	// Returns the linguistic description of the expert rule (up to 3 antecedents) 
	string C_Monitor::getDescriptionRule(C_Floor * floor, int rank, bool and)
	{	
		string outDesc = "";

		// Check the number of antecedents in the most relevant rule
		int NAnt = this->expert_anomaly_rules->myRules[floor->m_Zones[floor->m_SelectZone].m_AnomI_Rules_Idx].num_ant;

		if (rank + 1 > NAnt){
			return outDesc;
		}
		else{
			// Iterate through the rule up to the selected antecedent
			FuzzyAnt * help = this->expert_anomaly_rules->myRules[floor->m_Zones[floor->m_SelectZone].m_AnomI_Rules_Idx].rule;

			for (int i = 0; i < rank; i++){
				help = help->next;
			}

			outDesc += floor->m_Zones[floor->m_SelectZone].expert_fis->m_InputName[help->dimIndex];
			outDesc += " is ";
			outDesc += floor->m_Zones[floor->m_SelectZone].expert_fis->m_InputFuzzy[help->dimIndex][help->antIndex].lingVal;

			if ((and) && (NAnt > rank + 1)){
				outDesc += " and";
			}

			if ((rank == 2) && NAnt > 3){
				outDesc += " ... ";
			}
		}		

		return outDesc;	
	}

	// Returns the top fired complete linguistic rule for comfort
	string C_Monitor::getComfortRule(C_Floor * floor, int rank, bool and)
	{	
		string outDesc = "";

		if(floor->m_Zones[floor->m_SelectZone].comfort_rule_index == -1)
		{
			outDesc = "No Comfort Rules Fired";
			return outDesc;
		}
		else
		{
			outDesc = this->expert_comfort_rules->myRules[floor->m_Zones[floor->m_SelectZone].comfort_rule_index].FuzzyRulesToString(rank);
			return outDesc;
		}
		return outDesc;	
	}

	// Returns the top fired complete linguistic rule for Efficiency
	string C_Monitor::getEfficiencyRule(C_Floor * floor, int rank, bool and)
	{	
		string outDesc = "";

		if(floor->m_Zones[floor->m_SelectZone].efficiency_rule_index == -1)
		{
			outDesc = "No Efficiency Rules Fired";
			return outDesc;
		}
		else
		{
			outDesc = this->expert_efficiency_rules->myRules[floor->m_Zones[floor->m_SelectZone].efficiency_rule_index].FuzzyRulesToString(rank);
			return outDesc;
		}
		return outDesc;	
	}

	// Returns the linguistic anomaly confidence
	string C_Monitor::getAnomalyConfidence(C_Floor * floor){		
		
		string outConf = "";
		//cluster based
		if (this->m_AnomMode == 0){

			floor->m_Zones[floor->m_SelectZone].cluster_fis->getMaxMF();		
		
			outConf += string(floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputName);		
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputMaxMemId].lingVal;		
		}
		//expert rules based anomaly
		else if (this->m_AnomMode == 1){
			floor->m_Zones[floor->m_SelectZone].expert_fis->getMaxMF();

			outConf += string(floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputName);	
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputMaxMemId].lingVal;		
		}
		//combined
		else if (this->m_AnomMode == 2){
			floor->m_Zones[floor->m_SelectZone].expert_fis->getMaxMF();
		
			outConf += string(floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputName);	
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputMaxMemId].lingVal;		
		}
		//comfort based
		else if (this->m_AnomMode == 3){
			floor->m_Zones[floor->m_SelectZone].comfort_fis->getMaxMF();
	
			outConf += string(floor->m_Zones[floor->m_SelectZone].comfort_fis->m_OutputName);	
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].comfort_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].comfort_fis->m_OutputMaxMemId].lingVal;
		}
		//efficiency based
		else if (this->m_AnomMode == 4){
			floor->m_Zones[floor->m_SelectZone].efficiency_fis->getMaxMF();
	
			outConf += string(floor->m_Zones[floor->m_SelectZone].efficiency_fis->m_OutputName);	
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].efficiency_fis->m_OutputFuzzy[floor->m_Zones[floor->m_SelectZone].efficiency_fis->m_OutputMaxMemId].lingVal;
		}
		return outConf;	
	}

	// Returns the linguistic anomaly confidence
	string C_Monitor::getNormalConfidence(C_Floor * floor){			

		string outConf = "";

		if (this->m_AnomMode == 0){
			floor->m_Zones[floor->m_SelectZone].cluster_fis->getMaxMF();		

			outConf += string(floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputName);		
			outConf += " is ";
			outConf += floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputFuzzy[(floor->m_Zones[floor->m_SelectZone].cluster_fis->m_NOutputFS - 1) - 
				floor->m_Zones[floor->m_SelectZone].cluster_fis->m_OutputMaxMemId].lingVal;	
		}
		else{
			floor->m_Zones[floor->m_SelectZone].expert_fis->getMaxMF();		

			outConf += string(floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputName);		
			outConf += " is ";				
			outConf += floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputFuzzy[(floor->m_Zones[floor->m_SelectZone].expert_fis->m_NOutputFS - 1) - 
				floor->m_Zones[floor->m_SelectZone].expert_fis->m_OutputMaxMemId].lingVal;	
		}

		return outConf;
	}
};

#endif // __MONITOR_H__