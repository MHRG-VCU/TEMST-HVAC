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
// FeatureVec.h
// Feature Vector extracted from the packet buffer
// Date:  2012/2/17
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#ifndef __FEATURE_VEC_H__
#define __FEATURE_VEC_H__
#include <iostream>

using namespace std;
// Feature vector
class FVec{
public:
	// Dimensionality of the vector
	int dim;
	// The data vector
	float * coord;	
	// The output value
	float out;

	// boolean mask for turning on/off individual attributes
	bool * on;

public:
	// Constructor
	FVec(int _dim){
		this->dim = _dim;
		this->coord = new float[_dim];		
		this->out = 0;
		this->on = new bool[_dim];
	}
	// Destructor
	~FVec(){
		delete [] coord;
		delete [] on;
	}

	// Initialization function
	void init(float * vec, bool _intr)
	{
		for (int i = 0; i < this->dim; i++)
		{
			this->coord[i] = vec[i];
		}		
	}

	// Initializes the boolean attribute use mask based on the provided int vector
	void setOn(int * AttrUse)
	{
		//cout << "ATTR use" << endl; 
		for (int i = 0; i < this->dim; i++)
		{
			
			if (AttrUse[i] == 0)
			{
				this->on[i] = false;
			}
			else
			{
				this->on[i] = true;
			}
			//cout << i << "::" << this->on[i] << endl;
		}
	}
};

#endif // __FEATURE_VEC_H__

