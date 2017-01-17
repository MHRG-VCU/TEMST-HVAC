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

//This

#ifndef __FUZZYSETT1TRI_H__
#define __FUZZYSETT1TRI_H__

#define _USE_MATH_DEFINES
#include <time.h>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <cmath>
#include <iostream>
#include <fstream>
#include <string.h>
#include "FuzzyNameLoader.h"
#include "FuzzyAnt.h"
#include "FuzzyRule.h"
#include "FuzzyRuleSet.h"

#ifndef M_PI 
	#define M_PI 3.1415926535897932384626433832795 
#endif

using namespace std;

// Class implementing the T1 fuzzy set with triangulare MF
class FuzzySetT1Tri
{
public:
	// Parameters of the MF - left, right, center
	float a;
	float b;		
	float c;

	// 0 - left shoulder, 1 - mid, 2 - right shoulder
	int type;

	// The computed membership degree
	float member_deg;

	// Linguistic meaning of this fuzzy set
	string lingVal;

public:
	// Constructor
	FuzzySetT1Tri()
	{
		this->a = 0.0f;
		this->b = 0.0f;
		this->c = 0.0f;
		this->type = 0;
		
		this->member_deg = 0.0f;	

		this->lingVal = "Unknown";
	}

	// Initializes the parameter values
	void setValues(float _a, float _c, float _b, int _type)
	{
		this->a = _a;
		this->b = _b;		
		this->c = _c;
		this->type = _type;		
	}

	// Initializes the parameter values
	void setValues(float _a, float _c, float _b, int _type, string _lingVal)
	{
		this->a = _a;
		this->b = _b;		
		this->c = _c;
		this->type = _type;
		this->lingVal = _lingVal;
	}

	// Initializes the parameter values
	void setValues(string _line)
	{
		float _a; 
		float _c; 
		float _b; 
		int _type; 
		string _lingVal;

		int index = 0;
		index = _line.find_first_of(',');
		_a = atof(_line.substr(0, index).c_str());
		_line = _line.substr(index + 1);

		index = _line.find_first_of(',');
		_c = atof(_line.substr(0, index).c_str());
		_line = _line.substr(index + 1);

		index = _line.find_first_of(',');
		_b = atof(_line.substr(0, index).c_str());
		_line = _line.substr(index + 1);

		index = _line.find_first_of(',');
		_type = atoi(_line.substr(0, index).c_str());
		_line = _line.substr(index + 1);

		_lingVal = _line;
		this->a = _a;
		this->b = _b;		
		this->c = _c;
		this->type = _type;
		this->lingVal = _lingVal;
	}

	// Evaluates the membership degree of the input value
	void getMembership(float value){
		// Left shoulder
		if (this->type == 0){
			if (value < this->c){
				this->member_deg = 1.0;
			}			
			else if ((value >= this->c) && (value < this->b)){
				this->member_deg = (this->b - value) / (this->b - this->c);
			}
			else if (value >= this->b){
				this->member_deg = 0.0;
			}			
		}
		// Middle
		else if (this->type == 1){
			if (value < this->a){
				this->member_deg = 0.0;
			}
			else if ((value >= this->a) && (value < this->c)){
				this->member_deg = (value - this->a) / (this->c - this->a);
			}
			else if ((value >= this->c) && (value < this->b)){
				this->member_deg = (this->b - value) / (this->b - this->c);
			}
			else{
				this->member_deg = 0;
			}			
		}
		// Right shoulder
		else if (this->type == 2){			
			if (value < this->a){
				this->member_deg = 0.0;
			}
			else if ((value >= this->a) && (value < this->c)){
				this->member_deg = (value - this->a) / (this->c - this->a);
			}
			else if (value >= this->c){
				this->member_deg = 1.0;
			}			
		}		
	}

	// Prints out the fuzzy set paramters
	void printOut()
	{
		cout << "Fs: " << this->lingVal << " : " << this->a << ", " << this->c << ", " << this->b << " Type: " << this->type  << endl;
	}
};


#endif // __FUZZYSETT1TRI_H__