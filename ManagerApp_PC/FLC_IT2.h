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
// FLC_IT2.h
// Class responsible for implementation of the interval type-2 fuzzy logic
// Date:  2012/2/17
// Author: Ondrej Linda, UI
//---------------------------------------------------------------------------

#ifndef __FLC_IT2_H__
#define __FLC_IT2_H__

#define _USE_MATH_DEFINES
#include <time.h>
#include <cstdlib>
#include <vector>
#include <cmath>
#include <cstdio>
#include <iostream>

#include "FeatureVec.h"

#ifndef M_PI 
	#define M_PI 3.1415926535897932384626433832795 
#endif

using namespace std;


// implements the Interval Type-2 Gaussian membership function
class CFuzzySetIT2{
public:
	// Mean of the Gaussian
	float mean;

	// Left astandard deviation of the Upper and Lower Gaussian MF
	float stdL_U;		
	float stdL_L;		
	
	// Right astandard deviation of the Upper and Lower Gaussian MF
	float stdR_U;		
	float stdR_L;	

	// the amount of bluring / uncertainty
	float blur;

	// The membership to the lower and to the upper FOU
	float member_deg_U;
	float member_deg_L;
	
public:
	// Constructor
	CFuzzySetIT2();	

	// Assigns the parameters of the FS
	void setValues(float _mean, float _stdL, float _stdR, float spread, float _blur);

	// Evaluates the membership function
	void getMembership(float value);		

	// Prints out the parameters of the MF
	void printOut();
};


// implements the Interval Type-2 Triangular membership function
class CFuzzySetIT2Tri{
public:
	// Mean of the Triangle
	float mean;

	// Left spread of the Upper and Lower Triangle MF
	float left_U;		
	float left_L;		

	// Right spread of the Upper and Lower Triangle MF
	float right_U;		
	float right_L;	

	// the amount of bluring / uncertainty
	float blur;

	// The membership to the lower and to the upper FOU
	float member_deg_U;
	float member_deg_L;
	
public:
	// Constructor
	CFuzzySetIT2Tri();	

	// Assigns the parameters of the FS
	void setValues(float _mean, float _left, float _right, float spread, float blur);

	// Evaluates the membership function
	void getMembership(float value);		

	// Prints out the parameters of the MF
	void printOut();
};


// implements the Interval Type-2 Fuzzy Rule for IT2 FLC with Scatter partition of rules
class CFuzzyIT2Rule{
public:
	// Number of antecedents of the fuzzy rule
	int dim;

	// The output value of this fuzzy rule
	float output;

	// Lower firing strength
	float deg_L;
	// Upper firing strength
	float deg_U;

	// The set of input fuzzy sets for this rule
	CFuzzySetIT2 * fuzzyInput;
	
public:
	// Constructor
	CFuzzyIT2Rule();

	// Destructor
	~CFuzzyIT2Rule();

	// Initializes the antecedents of this fuzzy rule
	void init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread, float blur);
		
	//  Evaluates the output of this fuzzy rule
	void getOutput(float * input);		

	// Prints out the parameters of this fuzzy rule
	void printOut();
};


// implements the Interval Type-2 Fuzzy Rule for IT2 FLC with Scatter partition of rules using triangular 
class CFuzzyIT2RuleTri{
public:
	// Number of antecedents of the fuzzy rule
	int dim;

	// The output value of this fuzzy rule
	float output;

	// Lower firing strength
	float deg_L;
	// Upper firing strength
	float deg_U;

	// The set of input fuzzy sets for this rule
	CFuzzySetIT2Tri * fuzzyInput;
	
public:
	// Constructor
	CFuzzyIT2RuleTri();

	// Destructor
	~CFuzzyIT2RuleTri();

	// Initializes the antecedents of this fuzzy rule
	void init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread, float blur);
		
	//  Evaluates the output of this fuzzy rule
	void getOutput(float * input);		

	// Prints out the parameters of this fuzzy rule
	void printOut();
};

// This class implements the Interval Type-2 Fuzzy Logic Systems
class CFLCIT2{
public:
	// Array of the fuzzy rules with Gaussian IT2 FSs
	CFuzzyIT2Rule * rules;	
	// Array of the fuzzy rules with triangular IT2 FSs
	CFuzzyIT2RuleTri * rulesTri;	

	// Dimensionality of the input vector
	int dim;

	// Number of fuzzy rules
	int N_Rules;

	// Should the triangular MFs be used
	bool tri;

	// Classification results
	float maxDeg_L;
	float maxDeg_U;

public:
	// Constructor
	CFLCIT2(bool _memberMode);

	// Constructor
	CFLCIT2(int _dim, int _N_Rules, bool _memberMode);

	// Destructor
	~CFLCIT2();

	// Set the fuzzy rule at the specified index
	void setRule(int index, float * values, float * spreadL, float * spreadR, float output, float spread, float blur);

	// Evaluates the output of the IT2 FLS
	float evalOut(FVec * input);

	// Prints out the parameters of the IT2 FLS
	void printOut();
};


#endif // __FLC_IT2_H__

