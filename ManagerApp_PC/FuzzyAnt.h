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

#ifndef __FUZZYANT_H__
#define __FUZZYANT_H__

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

using namespace std;

// This class implements and antecedent of a fuzzy rule
class FuzzyAnt
{
public:
	// Index of the dimension (attribute) of this antecedent
	int dimIndex;
	// Index of the fuzzy set of this antecedent
	int antIndex;
	// Index of the linguistic hedge if present
	int hedgeIndex;
	// Type of the antecedent - 0 = sensor, 1 = time
	int type;
	// Pointer to the next antecedent of the rule
	FuzzyAnt * next;

public:
	// Constructor
	FuzzyAnt()
	{
		this->dimIndex = 0;
		this->antIndex = 0;
		this->hedgeIndex = -1;
		this->next = NULL;
	}

	// Print out of the antecedent class
	void printOut()
	{
		cout << _FNL_getDimName(this->dimIndex) << " IS "; 
		if (this->hedgeIndex > -1)
		{
			cout << _FNL_getHedgeName(this->hedgeIndex) << " ";
		}

		cout << _FNL_getTermName(this->antIndex, this->dimIndex);

	}

	string ruleToString()
	{
		string outDesc = "";
		outDesc += _FNL_getDimName(this->dimIndex);
		outDesc += " IS "; 
		
		if (this->hedgeIndex > -1)
		{
			outDesc += _FNL_getHedgeName(this->hedgeIndex);
			outDesc += " ";
		}

		outDesc += _FNL_getTermName(this->antIndex, this->dimIndex);

		return outDesc;
	}

	// Returns the string with the antecedent class
	string printString(){
		
		string outDesc = "";
		outDesc += _FNL_getDimName(this->dimIndex);
		outDesc += " IS "; 
		
		if (this->hedgeIndex > -1){
			outDesc += _FNL_getHedgeName(this->hedgeIndex);
			outDesc += " ";
		}

		outDesc += _FNL_getTermName(this->antIndex, this->dimIndex);

		return outDesc;
	}
};

#endif // __FUZZYANT_H__