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
// OCM_Alg.h
// Implements the algorithm for online clustering modeling of intrusion/normal traffic behavior
// Date:  2010/6/21
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#ifndef __OCM_ALG_H__
#define __OCM_ALG_H__

#include <cstdlib>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <string>

#include "FeatureVec.h"
#include "Clusters.h"
#include "FLC.h"
#include "FLC_IT2.h"

// Class implementing the OCM algorithm
class OCM_Alg
{
public:
	// The clustering object
	Clusters * clusters;

	// Min Max normalization values
	float * m_ValMin;
	float * m_ValMax;

	// The fuzzy controller object
	CFLC1 * flc1;
	// The interval type-2 fuzzy controller object
	CFLCIT2 * flcIT2;

	// mode for the fuzzy logic used
	// mode == 0 -> FLC1, mode == 1 -> IT2 FLC
	int mode;

	// membership function
	// true - use triangular, false - use Gaussian
	bool tri;

	// dimensionality of the input data
	int dim;
	// Number of clusters
	int NClust;
	// maximum cluster radius
	float maxRad;	

	// Number of classified intances
	int Num;	

	// Noise amplitude
	float noise;

	// Classification results for IT2 FLC
	float memLow;
	float memHigh;

	// Parameters of the FLC
	float spread;
	float blur;

	//flag indicating whether to use the auto min max or not
	bool use_auto_min_max;

public:
	// Constructor
	OCM_Alg()
	{
		this->dim = 0;
		this->NClust = 0;
		this->maxRad = 0;

		this->tri = 0;

		this->clusters = NULL;	
		this->flc1 = NULL;
		this->flcIT2 = NULL;

		this->m_ValMax = NULL;
		this->m_ValMin = NULL;

		this->mode = 0;		

		this->Num = 0;
		this->noise = 0.0;

		this->spread = 0.0f;
		this->blur = 0.0f;

		this->use_auto_min_max = true;
	}

	// Constructor
	OCM_Alg(int _dim, float _rad, bool _memberMode)
	{
		this->dim = _dim;
		this->NClust = 0;
		this->maxRad = _rad;

		this->tri = _memberMode;

		this->clusters = new Clusters(this->dim, this->maxRad);	
		this->flc1 = new CFLC1(this->tri);
		this->flcIT2 = new CFLCIT2(this->tri);

		this->m_ValMax = new float[this->dim];
		this->m_ValMin = new float[this->dim];

		this->mode = 0;	
	
		this->Num = 0;
		this->noise = 0.0;

		this->spread = 0.0f;
		this->blur = 0.0f;
	}

	// Initialization function
	void initConst(int _dim, float _rad, bool _memberMode)
	{		
		this->dim = _dim;
		this->NClust = 0;
		this->maxRad = _rad;

		this->tri = _memberMode;

		this->clusters = new Clusters(this->dim, this->maxRad);	
		this->flc1 = new CFLC1(this->tri);
		this->flcIT2 = new CFLCIT2(this->tri);

		this->m_ValMax = new float[this->dim];
		this->m_ValMin = new float[this->dim];

		this->mode = 0;		

		this->Num = 0;
		this->noise = 0.0;		
	}

	// Initialization function
	void initFLCConst(){		
				
		this->flc1 = new CFLC1(this->tri);
		this->flcIT2 = new CFLCIT2(this->tri);		
	}

	// Initialization function
	void init(){	
		this->Num = 0;
		this->noise = 0.0;
	}

	// Reseting back the init state
	void reset(float _rad){	

		this->Num = 0;
		this->noise = 0.0;

		this->NClust = 0;
		this->maxRad = _rad;

		if (this->clusters != NULL){
			delete this->clusters;
		}

		this->clusters = new Clusters(this->dim, this->maxRad);	

		if (this->flc1 != NULL){
			delete this->flc1;
		}
		this->flc1 = new CFLC1(this->tri);

		if (this->flcIT2 != NULL){
			delete this->flcIT2;
		}
		this->flcIT2 = new CFLCIT2(this->tri);
	}	

	// Classifies the given input vector
	float classify(FVec * vec)
	{

		// Add noise if desired
		if (this->noise > 0.0)
		{
			for (int i = 0; i < vec->dim; i++)
			{
				vec->coord[i] += this->noise * (2.0f * ((float)rand() / RAND_MAX) - 1.0f);
			}
		}

		float result = 0;

		// Use T1 FLS (mode == 0) or IT2 FLS (mode == 1)
		if (mode == 0){
			result = 1.0f - this->flc1->evalOut(vec);			
		}
		else{
			result = 1.0f - this->flcIT2->evalOut(vec);
			this->memLow = 1.0f - this->flcIT2->maxDeg_U;
			this->memHigh = 1.0f - this->flcIT2->maxDeg_L;
		}		

		this->Num++;

		return result;
	}

	// Initializes the fuzzy logic engine from the extracted clusters
	void initFLCClusters(float spread, float blur){

		// Do this for both FLC
		this->flc1 = new CFLC1(this->dim, this->clusters->centers.size(), this->tri);
		this->flcIT2 = new CFLCIT2(this->dim, this->clusters->centers.size(), this->tri);			

		for (int i = 0; i < (int)this->clusters->centers.size(); i++){
			this->flc1->setRule(i, this->clusters->centers[i]->coord, this->clusters->centers[i]->minCoord,
				this->clusters->centers[i]->maxCoord, 1.0, spread);
			this->flcIT2->setRule(i, this->clusters->centers[i]->coord, this->clusters->centers[i]->minCoord,
				this->clusters->centers[i]->coord, 1.0, spread, blur);
		}

		//cout << "The FLCs were initializes based on the extracted clusters" << endl;
	}

	// Initializes the fuzzy logic engine from a text file with stored clusters
	void initFLCFile(string fileName, float spread, float blur){	

		this->spread = spread;
		this->blur = blur;

		this->clusters->centers.clear();

		// Do this for both FLC
		ifstream file;
		file.open(fileName.c_str());

		string line;
		
		// Open the file
		if (file.is_open())
		{

			getline(file, line);
			this->NClust = atoi(line.c_str());
			//cout << "Number of clusters: " << this->NClust << endl;

			getline(file, line);
			this->dim = atoi(line.c_str());
			//cout << "Input dimensionality: " << this->dim << endl;

			// Initialize the structure
			this->flc1 = new CFLC1(this->dim, this->NClust, this->tri);
			this->flcIT2 = new CFLCIT2(this->dim, this->NClust, this->tri);

			float * values = new float[this->dim];
			float * spreadL = new float[this->dim];
			float * spreadR = new float[this->dim];

			float clustRad = 0.0f;
			float clustWeight = 0.0f;

			int index;
			string val;
			// Iterate through the text file, extract cluster parameters and transform it into a fuzzy rule
			for (int i = 0; i < this->NClust; i++)
			{
				getline(file, line);
				
				for (int d = 0; d < this->dim; d++){

					index = line.find_first_of(',');				
					val = line.substr(0, index);
					values[d] = (float)atof(val.c_str());
					line = line.substr(index + 1);

					index = line.find_first_of(',');				
					val = line.substr(0, index);
					spreadL[d] = (float)atof(val.c_str());
					line = line.substr(index + 1);
					
					index = line.find_first_of(',');				
					val = line.substr(0, index);
					spreadR[d] = (float)atof(val.c_str());
					line = line.substr(index + 1);					
				}

				// Read the cluster weight and cluster radius
				index = line.find_first_of(',');				
				clustWeight = atof(line.substr(0, index).c_str());				
				line = line.substr(index + 1);			
				clustRad = atof(line.c_str());

				// Setup the fuzzy rule
				this->flc1->setRule(i, values, spreadL, spreadR, 1.0, this->spread);
				this->flcIT2->setRule(i, values, spreadL, spreadR, 1.0, this->spread, this->blur);

				// Also create the cluster 
				COG * c = new COG(this->dim);
				c->dim = this->dim;
				c->radius = this->maxRad;
				for (int d = 0; d < this->dim; d++){
					c->coord[d] = values[d];

					c->maxCoord[d] = spreadR[d];
					c->minCoord[d] = spreadL[d];
				}

				c->weight = clustWeight;
				// Add the new cluster into the vector
				this->clusters->centers.push_back(c);			
			}

			delete [] values;
			delete [] spreadL;
			delete [] spreadR;
		}
		else{
			cout << "Cluster File: " << fileName << " could not be opened." << endl;
		}

		file.close();
	}
	
	// Loads the min and max values from a provided text file
	void loadMinMax(string fileName)
	{
		if(this->use_auto_min_max == false)
			fileName = "Data_MinMax/dim_min_max.TXT";

		ifstream in;
		in.open(fileName);

		if (in.is_open()){
			char lineCh[1024];
			string line;		
			int index;			

			for (int i = 0; i < this->dim; i++){			
				in.getline(lineCh, 1024);
				line = string(lineCh);

				index = line.find_first_of(',');

				this->m_ValMin[i] = atof(line.substr(0, index).c_str());

				line = line.substr(index + 1);

				this->m_ValMax[i] = atof(line.c_str());
			}

		}
		else
		{
			cout << "Cannot open the min-max value file: " << fileName << endl;

			for (int i = 0; i < this->dim; i++)
			{
				this->m_ValMin[i] = 0.0;
				this->m_ValMax[i] = 1.0;
			}
		}



		for (int i = 0; i < this->dim; i++)
		{
			//cout << this->m_ValMin[i]  << "--" << this->m_ValMax[i] <<endl;
		}
	}
};


#endif // __OCM_ALG_H__


