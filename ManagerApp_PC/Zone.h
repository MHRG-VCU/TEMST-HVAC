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
// Zone.h
// Implements data structures associated with storing the zone information and geometry
// Date:  4/12/2012
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------


#ifndef __ZONE_H__
#define __ZONE_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string.h>
#include <cstdio>

#include "DataSource.h"
#include "OCM_Alg.h"

//#include "FuzzyLogicFIS.h"
#include "FuzzyRuleSet.h"
#include "FuzzySystemT1Centroid.h"

using namespace std;

// Class implementing a polygon (used for filling the zones)
class C_Poly{
public:
	// Number of vertexes
	int m_NVertex;
	// Arrays of the X and Y coordinates
	float * m_VertexX;
	float * m_VertexY;
public:
	// Constructor
	C_Poly(){
		this->m_NVertex = 0;
		this->m_VertexX = NULL;
		this->m_VertexY = NULL;
	}

	// Prints the parameters of this polygon
	void printPoly()
	{
		cout << "Poly, NVertex: " << this->m_NVertex << endl;

		for (int i = 0; i < this->m_NVertex - 1; i++){
			cout << this->m_VertexX[i] << ", " << this->m_VertexY[i] << " | ";
		}

		cout << this->m_VertexX[this->m_NVertex - 1] << ", " << this->m_VertexY[this->m_NVertex - 1] << endl;
	}
};

// Class implementing the zone
class C_Zone{
public:
	// ID of the zone
	int m_ID;

	// Number of polygons creating the zone
	int m_NPoly;
	// Array of polygons
	C_Poly * m_Poly;

	// Number of vertex in the wall of the zone
	int m_NVertex;

	// Array of X and Y coordinates
	float * m_VertexX;
	float * m_VertexY;	

	// Mid point
	float m_MidX;
	float m_MidY;

	// Data source of given zone
	C_DataSource * m_Data;

	// The Anomaly detection algorithm associated with this packet buffer
	OCM_Alg * m_Alg;

	// Anomaly indicator of this zone
	float m_AnomalI;

	// Anomaly indicator of this zone based on expert rules
	float m_AnomalI_Rules;

	// Index of rule from expert rule base which most apply to the given feature vector
	int m_AnomI_Rules_Idx;

	//performance metrics for zone
	float comfort_level;
	float efficiency_level;

	//index of most relevant comfort / efficiency rule
	int comfort_rule_index;
	int efficiency_rule_index;

	// Start of an anomaly
	int m_StartAnomal;

	// Flag stating if previously anomalous
	bool m_PrevAnomal;

	//out of bounds
	bool out_of_bounds_flag;
	bool * out_of_bounds;
	float * out_of_bound_values;

	// The FIS for generating linguistic descriptions based on the cluster date
	//CFISFuzzySystemT1_CTR * m_FIS_Cluster;
	FuzzySystemT1Centroid * cluster_fis;

	// The FIS for detecting anomalies and generating linguistic descriptions based on expert fuzzy rules
	//CFISFuzzySystemT1_CTR * m_FIS_Rules;
	FuzzySystemT1Centroid * expert_fis;

	// The FIS for measuring comfort based on expert fuzzy rules
	FuzzySystemT1Centroid * comfort_fis;

	// The FIS for measuring efficiency based on expert fuzzy rules
	FuzzySystemT1Centroid * efficiency_fis;

public:
	// Constructor
	C_Zone(){
		this->m_ID = 0;
		this->m_NPoly = 0;
		this->m_Poly = NULL;

		this->m_NVertex = 0;
		this->m_VertexX = NULL;
		this->m_VertexY = NULL;		

		this->m_MidX = 0.0f;
		this->m_MidY = 0.0f;		

		this->m_Alg = new OCM_Alg();

		this->m_AnomalI = 0.0f;

		this->m_AnomalI_Rules;		
		this->m_AnomI_Rules_Idx = -1;

		this->comfort_rule_index = -1;
		this->efficiency_rule_index = -1;

		this->comfort_level = 0.5;
		this->efficiency_level = 0.5;

		this->m_StartAnomal = 0;
		this->m_PrevAnomal = false;

		//this->m_FIS_Cluster = new CFISFuzzySystemT1_CTR();
		this->cluster_fis = new FuzzySystemT1Centroid();
		this->cluster_fis->m_OutputName = "Confidence";
		//this->m_FIS_Rules = new CFISFuzzySystemT1_CTR();
		this->expert_fis = new FuzzySystemT1Centroid();
		this->expert_fis->m_OutputName = "Confidence";

		this->comfort_fis = new FuzzySystemT1Centroid();
		this->comfort_fis->m_OutputName = "Comfort";

		this->efficiency_fis = new FuzzySystemT1Centroid();
		this->efficiency_fis->m_OutputName = "Efficiency";

		//out of bounds
		this->out_of_bounds_flag = false;
		this->out_of_bounds = new bool[_FNL_num_dim_names];
		this->out_of_bound_values = new float[_FNL_num_dim_names];
	}

	// Prints out zone parameters
	void C_Zone::printOut()
	{
		cout << "Zone ID: " << this->m_ID << " N Poly: " << this->m_NPoly << endl;
		for (int i = 0; i < this->m_NPoly; i++){
			cout << "Polygon: " << i << endl;
			this->m_Poly[i].printPoly();
		}		

		cout << endl;
		cout << "Zone Wall, NVertex: " << this->m_NVertex << endl;
		for (int i = 0; i < this->m_NVertex - 1; i++){
			cout << this->m_VertexX[i] << ", " << this->m_VertexY[i] << " | ";
		}

		cout << this->m_VertexX[this->m_NVertex - 1] << ", " << this->m_VertexY[this->m_NVertex - 1] << endl;

		cout << endl;
	}

//TODO multi line more sophisticated
	//returns a linguistic description of out of bounds
	string C_Zone::getDescriptionOutofBounds(int dataPoint, int _sensor_number)
	{
		string ret_val = "";
		stringstream ss;
		//find the dimension with the highest difference
		float diff1 = 0;
		float diff2 = 0;
		float diff3 = 0;
		int ind1 = -1;
		int ind2 = -1;
		int ind3 = -1;

		for (int d = 0; d < this->m_Alg->dim; d++)
		{
			if(	this->out_of_bounds[d])
			{
				float diff_temp = 0;
				if(this->out_of_bound_values[d] < this->m_Alg->m_ValMin[d])
				{
					diff_temp = abs(this->out_of_bound_values[d] - this->m_Alg->m_ValMin[d]);
				}
				else
				{
					diff_temp = abs(this->out_of_bound_values[d] - this->m_Alg->m_ValMax[d]);
				}

				if(diff_temp > diff1)
				{
					diff1 = diff_temp;
					ind1 = d;
				}
				if(diff_temp > diff2 && diff_temp < diff1)
				{
					diff2 = diff_temp;
					ind2 = d;
				}
				if(diff_temp > diff3 && diff_temp < diff2)
				{
					diff3 = diff_temp;
					ind3 = d;
				}
			}
		}

		if(_sensor_number == 1 && ind1 >= 0)
		{
			ss << _FNL_dim_names[ind1] << " = " << this->out_of_bound_values[ind1] << " | (Range :: " << this->m_Alg->m_ValMin[ind1] <<" to " << this->m_Alg->m_ValMax[ind1] <<")";
		}
		else if(_sensor_number == 2 && ind2 >= 0)
		{
			ss << _FNL_dim_names[ind2] << " Value = " << this->out_of_bound_values[ind2] << " | (Range :: " << this->m_Alg->m_ValMin[ind2] <<" to " << this->m_Alg->m_ValMax[ind2] <<")";
		}
		else if(_sensor_number == 3 && ind3 >= 0)
		{
			ss << _FNL_dim_names[ind3] << " Value = " << this->out_of_bound_values[ind3] << " | (Range :: " << this->m_Alg->m_ValMin[ind3] <<" to " << this->m_Alg->m_ValMax[ind3] <<")";
		}

		for (int d = 0; d < this->m_Alg->dim; d++)
		{
			if(	this->out_of_bounds[d])
			{
				//ss << "dim = " << d << " | Val = " << this->out_of_bound_values[d] << " | (Range = " << this->m_Alg->m_ValMin[d] <<"-" << this->m_Alg->m_ValMax[d] <<")";
			}
		}
		ret_val = ss.str();
		return ret_val;
	}

	//  Evaluates the anomality of a give zone based on the extracted clusters
	void C_Zone::evalZoneMinMax(FVec * fvec, int dataPoint)
	{
		/*if (dataPoint == 0)
		{
			this->m_AnomalI = 0.0f;
		}*/
		this->out_of_bounds_flag = false;
		for(int i = 0; i < _FNL_num_dim_names; i++)
		{
			this->out_of_bounds[i] = false;
			this->out_of_bound_values[i] = 0.0f;
		}


		int temp_ind = 0;
		for(int i = 0; i < _FNL_num_zone_dims; i++)
		{
			fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
			temp_ind ++;
		}			
		fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];							
			
		// find if any data point is out-of-bounds
		for (int d = 0; d < this->m_Alg->dim; d++)
		{
			if((fvec->coord[d] > this->m_Alg->m_ValMax[d]) || (fvec->coord[d] < this->m_Alg->m_ValMin[d]))
			{
				this->out_of_bounds_flag = true;
				this->out_of_bounds[d] = true;
				this->out_of_bound_values[d] = fvec->coord[d];
				//cout << this->m_ID <<"-"<< dataPoint << "::" << fvec->coord[d] << " <> " << this->m_Alg->m_ValMax[d] << " <> " << this->m_Alg->m_ValMin[d] << endl;
				//cout << "out of bounds\n";
			}
		}
/*
		else
		{			
			
			
			// Classify the status of the zone
			this->m_AnomalI = this->m_Alg->classify(fvec);

			this->cluster_fis->computeAntSelection(this->m_Alg->flc1->maxRuleMF, fvec->on);

			// Also compute the linguistic description of the inputs and outputs
			// Used for linguistic description of the anomaly
			for (int i = 0; i < this->cluster_fis->m_NDim; i++){
				for (int j = 0; j < this->cluster_fis->m_NInputFS[i]; j++){
					this->cluster_fis->m_InputFuzzy[i][j].getMembership(fvec->coord[i]);
				}
			}

			for (int i = 0; i < this->cluster_fis->m_NOutputFS; i++){
				this->cluster_fis->m_OutputFuzzy[i].getMembership(this->m_AnomalI);
			}
			// Denormalize the feature vector
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] *  (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d])) + this->m_Alg->m_ValMin[d];
			}
			
		}*/
	}

	//  Evaluates the anomality of a give zone based on the extracted clusters
	void C_Zone::evalZoneCluster(FVec * fvec, int dataPoint){

		if (dataPoint == 0)
		{
			this->m_AnomalI = 0.0f;			
		}
		else
		{			
			int temp_ind = 0;
			for(int i = 0; i < _FNL_num_zone_dims; i++)
			{
				fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
				temp_ind ++;
			}			
			fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];
			
			// Normalize the feature vectors
			for (int d = 0; d < this->m_Alg->dim; d++)
			{
				fvec->coord[d] = (fvec->coord[d] - this->m_Alg->m_ValMin[d]) / (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d]);
			}

			// Classify the status of the zone
			this->m_AnomalI = this->m_Alg->classify(fvec);

			this->cluster_fis->computeAntSelection(this->m_Alg->flc1->maxRuleMF, fvec->on);

			// Also compute the linguistic description of the inputs and outputs
			// Used for linguistic description of the anomaly
			for (int i = 0; i < this->cluster_fis->m_NDim; i++)
			{
				for (int j = 0; j < this->cluster_fis->m_NInputFS[i]; j++)
				{
					this->cluster_fis->m_InputFuzzy[i][j].getMembership(fvec->coord[i]);
				}
			}

			for (int i = 0; i < this->cluster_fis->m_NOutputFS; i++){
				this->cluster_fis->m_OutputFuzzy[i].getMembership(this->m_AnomalI);
			}
			// Denormalize the feature vector
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] *  (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d])) + this->m_Alg->m_ValMin[d];
			}
		}
	}

	//  Evaluates the anomality of a give zone based on the expert fuzzy rules
	void C_Zone::evalZoneRules(FVec * fvec, int dataPoint, FuzzyRuleSet * _expert_rule_set){
		if (dataPoint == 0){
			this->m_AnomalI_Rules = 0.0f;		
		}
		else{			
			int temp_ind = 0;
			for(int i = 0; i < _FNL_num_zone_dims; i++)
			{
				fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
				temp_ind ++;
			}			
			fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];									
			
			// Normalize the feature vectors
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] - this->m_Alg->m_ValMin[d]) / (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d]);
			}
			
			this->m_AnomalI_Rules = this->expert_fis->evalAnomaly(_expert_rule_set, fvec);
			this->m_AnomI_Rules_Idx = this->expert_fis->m_AnomRule_Idx;

			// Compute the linguistic label for the level of confidence in this anomaly
			for (int i = 0; i < this->expert_fis->m_NOutputFS; i++){
				this->expert_fis->m_OutputFuzzy[i].getMembership(this->m_AnomalI_Rules);
			}
			
			// Denormalize the feature vector
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] *  (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d])) + this->m_Alg->m_ValMin[d];
			}
		}
	}

	//  Evaluates the comfort of a give zone based on the expert fuzzy rules
	void C_Zone::evalZoneComfort(FVec * fvec, int dataPoint, FuzzyRuleSet * _expert_rule_set){
		if (dataPoint == 0){
			this->m_AnomalI_Rules = 0.0f;		
		}
		else{			
			int temp_ind = 0;
			for(int i = 0; i < _FNL_num_zone_dims; i++)
			{
				fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
				temp_ind ++;
			}			
			fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];									
			
			// Normalize the feature vectors
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] - this->m_Alg->m_ValMin[d]) / (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d]);
			}
			
			this->comfort_level = this->comfort_fis->evalOutFLS(_expert_rule_set, fvec);
			this->comfort_rule_index = this->comfort_fis->m_AnomRule_Idx;

			// Compute the linguistic label for the level of confidence in this anomaly
			for (int i = 0; i < this->comfort_fis->m_NOutputFS; i++){
				this->comfort_fis->m_OutputFuzzy[i].getMembership(this->comfort_level);
			}
			
			// Denormalize the feature vector
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] *  (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d])) + this->m_Alg->m_ValMin[d];
			}
		}
	}

	//  Evaluates the comfort of a give zone based on the expert fuzzy rules
	void C_Zone::evalZoneEfficiency(FVec * fvec, int dataPoint, FuzzyRuleSet * _expert_rule_set){
		if (dataPoint == 0){
			this->m_AnomalI_Rules = 0.0f;		
		}
		else{			
			int temp_ind = 0;
			for(int i = 0; i < _FNL_num_zone_dims; i++)
			{
				fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
				temp_ind ++;
			}			
			fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];									
			
			// Normalize the feature vectors
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] - this->m_Alg->m_ValMin[d]) / (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d]);
			}
			
			this->efficiency_level = this->efficiency_fis->evalOutFLS(_expert_rule_set, fvec);
			this->efficiency_rule_index = this->efficiency_fis->m_AnomRule_Idx;

			// Compute the linguistic label for the level of confidence in this anomaly
			for (int i = 0; i < this->efficiency_fis->m_NOutputFS; i++){
				this->efficiency_fis->m_OutputFuzzy[i].getMembership(this->efficiency_level);
			}
			
			// Denormalize the feature vector
			for (int d = 0; d < this->m_Alg->dim; d++){
				fvec->coord[d] = (fvec->coord[d] *  (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d])) + this->m_Alg->m_ValMin[d];
			}
		}
	}

	//  Evaluates the comfort of a give zone based on the expert fuzzy rules
	void C_Zone::updateMinMax()
	{
		for(int i = 0; i < this->m_Data->m_NAttr; i++)
		{
			this->m_Data->m_MinVal[i] = this->m_Alg->m_ValMin[i];
			this->m_Data->m_MaxVal[i] = this->m_Alg->m_ValMax[i];
		}
	}
	// Updates the cluster model and the FLC model
	void C_Zone::updateModel(FVec * fvec, int dataPoint)
	{
		// Get the feature vector
		int temp_ind = 0;
		for(int i = 0; i < _FNL_num_zone_dims; i++)
		{
			fvec->coord[temp_ind] = this->m_Data->m_Val[temp_ind][dataPoint];
			temp_ind ++;
		}			
		fvec->coord[temp_ind] = this->m_Data->m_TimeVal[dataPoint];												

		// Normalize the feature vectors
		for (int d = 0; d < this->m_Alg->dim; d++)
		{
			fvec->coord[d] = (fvec->coord[d] - this->m_Alg->m_ValMin[d]) / (this->m_Alg->m_ValMax[d] - this->m_Alg->m_ValMin[d]);
		}
		
		this->m_Alg->clusters->update(fvec);

		delete this->m_Alg->flc1;
		delete this->m_Alg->flcIT2;

		this->m_Alg->initFLCConst();
		this->m_Alg->initFLCClusters(this->m_Alg->spread, this->m_Alg->blur);
	}
};

#endif // __ZONE_H__