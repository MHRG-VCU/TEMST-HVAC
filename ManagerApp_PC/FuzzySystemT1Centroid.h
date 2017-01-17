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

#ifndef __FUZZYSYSTEMT1CTR_H__
#define __FUZZYSYSTEMT1CTR_H__

#define _USE_MATH_DEFINES
#include <time.h>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <cmath>
#include <iostream>
#include <fstream>
#include <string.h>
#include <WinDef.h>
#include "FuzzyNameLoader.h"
#include "FuzzyAnt.h"
#include "FuzzyRule.h"
#include "FuzzyRuleSet.h"
#include "FuzzySetT1Tri.h"

#ifndef M_PI 
	#define M_PI 3.1415926535897932384626433832795 
#endif

using namespace std;

// Class implementing the Type-1 Fuzzy Logic System for generating linguistic descriptions of the identified anomalies
class FuzzySystemT1Centroid
{
public:

	// Number of input FS
	int m_NDim;

	// Input antecedents	
	FuzzySetT1Tri ** m_InputFuzzy;	
	// Output fuzzy sets
	FuzzySetT1Tri * m_OutputFuzzy;

	// Number of input fuzzy sets in each dimension
	int * m_NInputFS;
	// filename for set parameters and term names for each dimension
	string * filename_set_param;
	//string * filename_term_name;
	// Number of output fuzzy sets
	int m_NOutputFS;
	string filename_out_set_param;
	//string filename_out_term_name;

	// Linguistic names of input and output attributes
	string * m_InputName;	
	string m_OutputName;

	// Accumulators for the membership degrees
	float ** m_InputMemSum;
	float * m_OutputMemSum;

	// Maximum membership labels
	int * m_InputMaxMemId;	
	int m_OutputMaxMemId;
	float output_max_mem_degree;

	// Accumulators for the linguistic label significance
	float * m_InputLabelSigSum;	

	// Number of discretization points in the output dimension
	int N_Out;

	// Use minimum t-norm
	bool minT;

	// Computing the most significant linguistic labels for anomaly description
	int * m_AntSelect;
	float * m_AntMF;
	// This vector keeps the MFs of each dimension of the maximum strength rule in unsorted original order
	float * m_AntMFOrig;

	// Index of the maximum firing strength expert rule
	int m_AnomRule_Idx;

public:
	// Constructor - Initializes the structural parameters	
	FuzzySystemT1Centroid()
	{
		// Set the number of input dimensions
		this->m_NDim = _FNL_num_dim_names;
		// Set the number of FS in each input and output dimension
		this->m_NInputFS = new int[this->m_NDim];
		this->filename_set_param = new string[this->m_NDim];
		
		//load the overall information (number of fuzzy sets, fuzzy set parameters filename, term names filename)
		ifstream in_file_info;
		in_file_info.open(_FNL_fuzzy_set_info_file_name);
		string inf_line = "";

		for (int i = 0; i < this->m_NDim; i++)
		{
			getline(in_file_info, inf_line);
			int ind = 0;
			//input number
			ind = inf_line.find_first_of(",");
			inf_line = inf_line.substr(ind+1);
			//number of fuzzy sets
			ind = inf_line.find_first_of(",");
			string temp_nfs = inf_line.substr(0,ind);
			inf_line = inf_line.substr(ind+1);
			//set param file
			ind = inf_line.find_first_of(",");
			string temp_fnp = inf_line.substr(0,ind);
			inf_line = inf_line.substr(ind+1);
			
			this->m_NInputFS[i] = atoi(temp_nfs.c_str());
			this->filename_set_param[i] = temp_fnp;
		}
		//for the output
		getline(in_file_info, inf_line);
		int ind = 0;
		//output number
		ind = inf_line.find_first_of(",");
		inf_line = inf_line.substr(ind+1);
		//number of fuzzy sets
		ind = inf_line.find_first_of(",");
		string temp_nfs = inf_line.substr(0,ind);
		inf_line = inf_line.substr(ind+1);
		//set param file
		ind = inf_line.find_first_of(",");
		string temp_fnp = inf_line.substr(0,ind);
		inf_line = inf_line.substr(ind+1);

		this->m_NOutputFS = 5;
		this->m_NOutputFS  = atoi(temp_nfs.c_str());
		this->filename_out_set_param = temp_fnp;

		in_file_info.close();

		// Set the linguistic descriptions of each dimension
		this->m_InputName = new string[this->m_NDim];
		for(int i = 0; i < this->m_NDim; i++)
		{
			this->m_InputName[i] = _FNL_dim_names[i];
		}	
		//output name is set where the FLS is created
		this->m_OutputName = "";		

		// Set the accumulators for the membership degrees		
		this->m_InputMemSum = new float*[this->m_NDim];
		for (int i = 0; i < this->m_NDim; i++)
		{
			this->m_InputMemSum[i] = new float[this->m_NInputFS[i]];
		}
		this->m_OutputMemSum = new float[this->m_NOutputFS];
		// Set the maximum membership labels
		this->m_InputMaxMemId = new int[this->m_NDim];	

		// Set the input antecedents
		this->m_InputFuzzy = new FuzzySetT1Tri*[this->m_NDim];
		//load the antecedent parameters from file
		for (int i = 0; i < this->m_NDim; i++)
		{
			this->m_InputFuzzy[i] = new FuzzySetT1Tri[this->m_NInputFS[i]];

			ifstream in_file;
			in_file.open("FL_DimInfo/" + this->filename_set_param[i]);
			string line = "";
			int index = 0;

			if (in_file.is_open())
			{
				while(!in_file.eof())
				{
					getline(in_file, line);
					this->m_InputFuzzy[i][index].setValues(line);
					index++;
				}
				in_file.close();
			}
		}	

		// Set the output FS
		this->m_OutputFuzzy = new FuzzySetT1Tri[this->m_NOutputFS];
		//load the output fuzzy sets from files
		ifstream in_file;
		in_file.open("FL_DimInfo/" + this->filename_out_set_param);
		string line = "";
		int index = 0;

		if (in_file.is_open())
		{
			while(!in_file.eof())
			{
				getline(in_file, line);
				this->m_OutputFuzzy[index].setValues(line);
				index++;
			}
			in_file.close();
		}

		this->m_InputLabelSigSum = new float[this->m_NDim];
		this->N_Out = 40;
		this->minT = true;

		this->m_AntSelect = new int[this->m_NDim];
		this->m_AntMF = new float[this->m_NDim];
		this->m_AntMFOrig = new float[this->m_NDim];

		this->m_AnomRule_Idx = -1;
	}	

	//Typical Fuzzy rules
	//clears membership degrees
	void clearMemDegrees()
	{
		for(int i = 0; i < this->m_NDim; i++)
		{
			for (int j = 0; j < this->m_NInputFS[i]; j++)
			{
				this->m_InputFuzzy[i][j].member_deg = 0;;
			}
		}
		for (int j = 0; j < this->m_NOutputFS; j++)
		{
			this->m_OutputFuzzy[j].member_deg = 0;;
		}
	}

	// Evaluates the T1 FLC for the given input values
	float evalOutFLS(FuzzyRuleSet* _frules, float* inputValues)
	{
		this->clearMemDegrees();
		// Evaluate the degree of membership for the distance and significance fuzzy sets
		
		// Calculate the t-norm operator for the fuzzy rules
		float * Rule_Fire = new float[_frules->rulesN];
		// Go through all rules in the FIS
		for (int i = 0; i < _frules->rulesN; i++)
		{	
			Rule_Fire[i] = 1.0;
			// Iterate through the antecedents of the current fuzzy rule
			FuzzyAnt * help = _frules->myRules[i].rule;
			_frules->myRules[i].clearOut();
			while(help != NULL)
			{
				if (help->dimIndex >= 0 && help->dimIndex < this->m_NDim)
				{
					//the strength of the rule fire
					float fire = 0;
					// Check for the second night fuzzy sets (night can be at the begining of a day as well as at the end of a day)
					if ((help->dimIndex == 1) && (help->antIndex == 0))
					{
						this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(inputValues[help->dimIndex]);
						float deg1 = this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg;

						this->m_InputFuzzy[help->dimIndex][5].getMembership(inputValues[help->dimIndex]);
						float deg2 = this->m_InputFuzzy[help->dimIndex][5].member_deg;
					
						fire = max(deg1, deg2);
					}
					else
					{
						this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(inputValues[help->dimIndex]);
						fire = this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg;
					}
					if (help->hedgeIndex == 1)
					{
						fire = pow(fire, 2.0f);
					}	
					if (help->hedgeIndex == 2)
					{
						fire = sqrt(fire);
					}
					if (this->minT)
					{
						Rule_Fire[i] = min(Rule_Fire[i], fire);
					}
					else
					{
						Rule_Fire[i] = Rule_Fire[i] * fire;
					}
				}
				help = help->next;
			}
		}	
		
		// Construct the output fuzzy sets and apply the rule firing strength
		float f_OX = 1.0 / this->N_Out;
		for (int i = 0; i < 2*this->N_Out + 1; i++)
		{	
			float input = -0.5 + i * f_OX;
			for (int j = 0; j < _frules->rulesN; j++)
			{
				this->m_OutputFuzzy[_frules->myRules[j].outIndex].getMembership(input);
				if (minT)
				{
					_frules->myRules[j].output[i] = min(this->m_OutputFuzzy[_frules->myRules[j].outIndex].member_deg, Rule_Fire[j]);			
				}
				else
				{
					_frules->myRules[j].output[i] = this->m_OutputFuzzy[_frules->myRules[j].outIndex].member_deg * Rule_Fire[j];			
				}	
			}
		}

		// Aggregate the outputs of fired rules
		float * Out_Aggr = new float[2*this->N_Out + 1];
		float maxDeg = 0.0;

		for (int i = 0; i < 2*this->N_Out + 1; i++)
		{
			float maxDegree = 0;
			for (int j = 0; j < _frules->rulesN; j++)
			{
				maxDegree = max(maxDegree, _frules->myRules[j].output[i]);
			}
			Out_Aggr[i] = maxDegree;
			if (maxDegree > maxDeg)
			{
				maxDeg = maxDegree;
			}
		}

		// Special, when no rules were fired
		if (maxDeg < 0.05)
		{
			delete [] Out_Aggr;
			delete [] Rule_Fire;
			return 0.5;
		}
		else
		{
			// Perform the Centroid-Type defuzzification of the output fuzzy set
			float sum = 0;
			float sum_Weight = 0;
			for (int i = 0; i < 2 * this->N_Out + 1; i++)
			{
				float input = -0.5 + i * f_OX;
				sum += input * Out_Aggr[i];
				sum_Weight += Out_Aggr[i];
			}
			delete [] Out_Aggr;
			delete [] Rule_Fire;
			return sum / sum_Weight;		
		}
	}


	// Evaluates the T1 FLC for the given input values
	float evalOutFLS(FuzzyRuleSet* _frules, FVec* _fvec)
	{
		this->clearMemDegrees();
		// Evaluate the degree of membership for the distance and significance fuzzy sets
		
		// Calculate the t-norm operator for the fuzzy rules
		float * Rule_Fire = new float[_frules->rulesN];
		// Go through all rules in the FIS
		for (int i = 0; i < _frules->rulesN; i++)
		{	
			Rule_Fire[i] = 1.0;
			// Iterate through the antecedents of the current fuzzy rule
			FuzzyAnt * help = _frules->myRules[i].rule;
			_frules->myRules[i].clearOut();
			while(help != NULL)
			{
				if (help->dimIndex >= 0 && help->dimIndex < this->m_NDim)
				{
					//the strength of the rule fire
					float fire = 0;
					// Check for the second night fuzzy sets (night can be at the begining of a day as well as at the end of a day)
					if ((help->dimIndex == 1) && (help->antIndex == 0))
					{
						this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(_fvec->coord[help->dimIndex]);
						float deg1 = this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg;

						this->m_InputFuzzy[help->dimIndex][5].getMembership(_fvec->coord[help->dimIndex]);
						float deg2 = this->m_InputFuzzy[help->dimIndex][5].member_deg;
					
						fire = max(deg1, deg2);
					}
					else
					{
						this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(_fvec->coord[help->dimIndex]);
						fire = this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg;
					}
					if (help->hedgeIndex == 1)
					{
						fire = pow(fire, 2.0f);
					}	
					if (help->hedgeIndex == 2)
					{
						fire = sqrt(fire);
					}
					if (this->minT)
					{
						Rule_Fire[i] = min(Rule_Fire[i], fire);
					}
					else
					{
						Rule_Fire[i] = Rule_Fire[i] * fire;
					}
				}
				help = help->next;
			}
		}	
		
		// Construct the output fuzzy sets and apply the rule firing strength
		float f_OX = 1.0 / this->N_Out;
		for (int i = 0; i < 2*this->N_Out + 1; i++)
		{	
			float input = -0.5 + i * f_OX;
			for (int j = 0; j < _frules->rulesN; j++)
			{
				this->m_OutputFuzzy[_frules->myRules[j].outIndex].getMembership(input);
				if (minT)
				{
					_frules->myRules[j].output[i] = min(this->m_OutputFuzzy[_frules->myRules[j].outIndex].member_deg, Rule_Fire[j]);			
				}
				else
				{
					_frules->myRules[j].output[i] = this->m_OutputFuzzy[_frules->myRules[j].outIndex].member_deg * Rule_Fire[j];			
				}	
			}
		}

		// Aggregate the outputs of fired rules
		float * Out_Aggr = new float[2*this->N_Out + 1];
		float maxDeg = 0.0;

		for (int i = 0; i < 2*this->N_Out + 1; i++)
		{
			float maxDegree = 0;
			for (int j = 0; j < _frules->rulesN; j++)
			{
				maxDegree = max(maxDegree, _frules->myRules[j].output[i]);
			}
			Out_Aggr[i] = maxDegree;
			if (maxDegree > maxDeg)
			{
				maxDeg = maxDegree;
			}
		}

		float ret_val = 0;
		// Special, when no rules were fired
		if (maxDeg < 0.05)
		{
			delete [] Out_Aggr;
			delete [] Rule_Fire;
			this->m_AnomRule_Idx = -1;
			ret_val = 0.5;
		}
		else
		{
			// Perform the Centroid-Type defuzzification of the output fuzzy set
			float sum = 0;
			float sum_Weight = 0;
			for (int i = 0; i < 2 * this->N_Out + 1; i++)
			{
				float input = -0.5 + i * f_OX;
				sum += input * Out_Aggr[i];
				sum_Weight += Out_Aggr[i];
			}

			//calculate the index of the maximum rule fired
			float max = Rule_Fire[0];
			this->m_AnomRule_Idx = 0;
			for (int i = 0; i < _frules->rulesN; i++)
			{
				if(Rule_Fire[i] > max)
				{
					max = Rule_Fire[i];
					this->m_AnomRule_Idx = i;
				}
			}
			delete [] Out_Aggr;
			delete [] Rule_Fire;
			ret_val = sum / sum_Weight;		
		}
		
		this->m_OutputFuzzy[0].getMembership(ret_val);
		float max_out_mem = this->m_OutputFuzzy[0].member_deg;
		this->m_OutputMaxMemId = 0;
		for(int i = 0; i < this->m_NOutputFS; i++)
		{
			this->m_OutputFuzzy[i].getMembership(ret_val);
			if(max_out_mem < this->m_OutputFuzzy[i].member_deg)
			{
				max_out_mem = this->m_OutputFuzzy[i].member_deg;
				this->m_OutputMaxMemId = i;
			}
		}

		return ret_val;
	}

	//END typical Fuzzy rules

	//************************************************************
	//Anomaly rules (no output FS) and antecedent ranking etc. 
	void setMFZero()
	{
		for (int i = 0; i < this->m_NDim; i++){
			for (int j = 0; j < this->m_NInputFS[i]; j++){
				this->m_InputMemSum[i][j] = 0.0f;
			}

			this->m_InputMaxMemId[i] = 0;
		}

		for (int i = 0; i < this->m_NOutputFS; i++){
			this->m_OutputMemSum[i] = 0.0f;
		}
		this->m_OutputMaxMemId = 0;
	}
	// Sets the accumualted linguistic labels significance to zero
	void setInputSignifZero(){
		for (int i = 0; i < this->m_NDim; i++){
			this->m_InputLabelSigSum[i] = 0.0;
		}
	}

	// Finds the linguistic labels with the highest membership
	void getMaxMF(){
		for (int i = 0; i < this->m_NDim; i++){			
			this->m_InputMaxMemId[i] = 0;
		}		
		this->m_OutputMaxMemId = 0;

		float * inputMaxVal = new float[this->m_NDim];
		for (int i = 0; i < this->m_NDim; i++){
			inputMaxVal[i] = -1.0;
		}
		
		float outputMaxVal = -1.0f;


		for (int i = 0; i < this->m_NDim; i++){
			for (int j = 0; j < this->m_NInputFS[i]; j++){
				if (this->m_InputFuzzy[i][j].member_deg > inputMaxVal[i]){
					inputMaxVal[i] = this->m_InputFuzzy[i][j].member_deg;
					this->m_InputMaxMemId[i] = j;
				}
			}
		}

		for (int j = 0; j < this->m_NOutputFS; j++){
			if (this->m_OutputFuzzy[j].member_deg > outputMaxVal){
				//cout << "NEW = " << this->m_OutputFuzzy[j].member_deg << endl;
				outputMaxVal = this->m_OutputFuzzy[j].member_deg;
				this->m_OutputMaxMemId = j;	
				this->output_max_mem_degree = outputMaxVal;
			}
		}		

		delete [] inputMaxVal;
	}

	// Finds the linguistic labels with the highest accumulated membership
	void getMaxAccuMF(){
		for (int i = 0; i < this->m_NDim; i++){			
			this->m_InputMaxMemId[i] = 0;
		}		
		this->m_OutputMaxMemId = 0;

		float * inputMaxVal = new float[this->m_NDim];
		for (int i = 0; i < this->m_NDim; i++){
			inputMaxVal[i] = -1.0;
		}
		
		float outputMaxVal = -1.0f;

		for (int i = 0; i < this->m_NDim; i++){
			for (int j = 0; j < this->m_NInputFS[i]; j++){				
				if (this->m_InputMemSum[i][j] > inputMaxVal[i]){
					inputMaxVal[i] = this->m_InputMemSum[i][j];
					this->m_InputMaxMemId[i] = j;
				}
			}
		}

		for (int j = 0; j < this->m_NOutputFS; j++){
			if (this->m_OutputMemSum[j] > outputMaxVal){
				outputMaxVal = this->m_OutputMemSum[j];
				this->m_OutputMaxMemId = j;				
			}
		}		

		delete [] inputMaxVal;
	}

	// Adds the current linguistic labels membership to the sum
	void accumMembership(){
		for (int i = 0; i < this->m_NDim; i++){
			for (int j = 0; j < this->m_NInputFS[i]; j++){
				this->m_InputMemSum[i][j] += this->m_InputFuzzy[i][j].member_deg;
			}
		}

		for (int i = 0; i < this->m_NOutputFS; i++){
			this->m_OutputMemSum[i] += this->m_OutputFuzzy[i].member_deg;
		}		
	}

	// Adds the current linguistic labels significance to the sums
	void accumInputSignificance(){

		for (int i = 0; i < this->m_NDim; i++){
			this->m_InputLabelSigSum[i] += this->m_AntMFOrig[i];
		}		
	}

	// This function takes as input the vector of membership degrees of the most active rule and computes the importance of each
	// antecedent
	void computeAntSelection(float * mfDeg, bool * on)
	{
		// Count the number of enabled dimensions
		int dimOn = 0;

		for (int i = 0; i < this->m_NDim; i++)
		{
			if (on[i])
			{
				dimOn++;
			}
			this->m_AntMFOrig[i] = mfDeg[i];
		}		

		// Select the enabled dimensions
		float * valSelect = new float[dimOn];
		int * idxSelect = new int[dimOn];
		int idx = 0;

		for (int i = 0; i < this->m_NDim; i++){
			if (on[i]){
				valSelect[idx] = mfDeg[i];
				idxSelect[idx] = i;
				idx++;
			}
		}

		// Sort the MF of the selected dimensions
		for (int i = 0; i < dimOn; i++){
			for (int j = 0; j < dimOn - 1; j++){
				if (valSelect[j] > valSelect[j + 1]){

					float swapF = valSelect[j];
					valSelect[j] = valSelect[j + 1];
					valSelect[j + 1] = swapF;

					int swapI = idxSelect[j];
					idxSelect[j] = idxSelect[j + 1];
					idxSelect[j + 1] = swapI;
				}
			}
		}

		// Store the indexes of the selected dimensions
		for (int i = 0; i < dimOn; i++){
			this->m_AntSelect[i] = idxSelect[i];
			this->m_AntMF[i] = mfDeg[idxSelect[i]];
		}

		delete [] valSelect;
		delete [] idxSelect;
	}


	// This function ransk the input labels based on their accumulated significance during the anomaly
	void computeAntAccumSelection(int * on){
		// Count the number of enabled dimensions
		int dimOn = 0;

		for (int i = 0; i < this->m_NDim; i++){
			if (on[i] == 1){
				dimOn++;
			}		
		}		

		// Select the enabled dimensions
		float * valSelect = new float[dimOn];
		int * idxSelect = new int[dimOn];
		int idx = 0;

		for (int i = 0; i < this->m_NDim; i++){
			if (on[i] == 1){
				valSelect[idx] = this->m_InputLabelSigSum[i];
				idxSelect[idx] = i;
				idx++;
			}
		}

		// Sort the MF of the selected dimensions
		for (int i = 0; i < dimOn; i++){
			for (int j = 0; j < dimOn - 1; j++){
				if (valSelect[j] > valSelect[j + 1]){

					float swapF = valSelect[j];
					valSelect[j] = valSelect[j + 1];
					valSelect[j + 1] = swapF;

					int swapI = idxSelect[j];
					idxSelect[j] = idxSelect[j + 1];
					idxSelect[j + 1] = swapI;
				}
			}
		}

		// Store the indexes of the selected dimensions
		for (int i = 0; i < dimOn; i++){
			this->m_AntSelect[i] = idxSelect[i];
			this->m_AntMF[i] = this->m_InputLabelSigSum[idxSelect[i]];
		}

		delete [] valSelect;
		delete [] idxSelect;
	}

	// Evaluates the anomaly of the given input vector with respect to the expert fuzzy rules
	float evalAnomaly(FuzzyRuleSet * fis, FVec * fvec)
	{
		float maxDegree = 0.0f;

		for (int i = 0; i < fis->rulesN; i++){
			float minDegree = 1.0f;

			FuzzyAnt * help = fis->myRules[i].rule;

			while(help != NULL){

				// Check for the second night fuzzy sets (night can be at the begining of a day as well as at the end of a day)
				if ((help->dimIndex == 1) && (help->antIndex == 0)){
					this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(fvec->coord[help->dimIndex]);				
					float deg1 = this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg;

					this->m_InputFuzzy[help->dimIndex][5].getMembership(fvec->coord[help->dimIndex]);				
					float deg2 = this->m_InputFuzzy[help->dimIndex][5].member_deg;
					
					minDegree = min(minDegree, max(deg1, deg2));
				}
				else{
					this->m_InputFuzzy[help->dimIndex][help->antIndex].getMembership(fvec->coord[help->dimIndex]);				

					minDegree = min(minDegree, this->m_InputFuzzy[help->dimIndex][help->antIndex].member_deg);
				}

				help = help->next;
			}

			if (minDegree > maxDegree){
				maxDegree = minDegree;
				this->m_AnomRule_Idx = i;
			}
		}

		return maxDegree;
	}

	// Prints out the structure of the FLC	
	void printOut()
	{
		cout << "T1 FIS" << endl;
		for (int i = 0; i < this->m_NDim; i++){
			cout << this->m_InputName[i] << endl;

			for (int j = 0; j < this->m_NInputFS[i]; j++){
				this->m_InputFuzzy[i][j].printOut();
			}
			cout << endl;
		}

		cout << endl;
		cout << "Output:" << this->m_OutputName << endl;

		for (int i = 0; i < this->m_NOutputFS; i++){
			this->m_OutputFuzzy[i].printOut();
		}
	}
};

#endif // __FUZZYSYSTEMT1CTR_H__