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
// FLC.h
// Class responsible for implementation of the T1 fuzzy logic
// Date:  2012/2/17
// Author: Ondrej Linda, UI
//---------------------------------------------------------------------------

#ifndef __FLC_H__
#define __FLC_H__

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


// implements the Type-1 Gaussian membership function
class CFuzzySetT1{
public:
	// Mean of the Gaussian
	float mean;

	// Left and right  standard deviation of the Gaussian
	float stdL;		
	float stdR;		

	// Normalizationg degrees for the left and right shoulders
	float deg0L;
	float deg0R;

	// Auxilliary variables
	float auxL;
	float auxR;

	// The current membership degree (most recent input)
	float member_deg;
	
public:
	// Constructor
	CFuzzySetT1();	

	// Assigns the parameters of the FS
	void setValues(float _mean, float _stdL, float _stdR, float spread);

	// Evaluates the membership function
	float getMembership(float value);		

	// Prints out the parameters of the MF
	void printOut();
};

// implements the Type-1 Triangular membership function
class CFuzzySetT1Tri{
public:
	// Position of the apppex of the triangular MF
	float mean;

	// Spread of the triangle to the left and right
	float left;		
	float right;		

	// The current membership degree (most recent input)
	float member_deg;
	
public:
	// Constructor
	CFuzzySetT1Tri();	

	// Assigns the parameters of the FS
	void setValues(float _mean, float _left, float _right, float spread);

	// Evaluates the membership function
	float getMembership(float value);		

	// Prints out the parameters of the MF
	void printOut();
};

// implements the Type-1 Fuzzy Rule for T-1 FLC with Scatter partition of rules
class CFuzzyT1Rule{
public:
	// Number of atecedents in the rule
	int dim;

	// The output value of thisfuzzy rule
	float output;

	// The set of input fuzzy sets for this rule
	CFuzzySetT1 * fuzzyInput;
	
public:
	// Constructor
	CFuzzyT1Rule();

	// Destructor
	~CFuzzyT1Rule();

	// Initalizes the parameters of the rule
	void init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread);
		
	// Evaluates the output of the rule
	float getOutput(float * input, bool * AttrUse);		

	// Prints out the parameters of the rule
	void printOut();
};

// implements the Type-1 Fuzzy Rule for T-1 FLC with Scatter partition of rules and Triangular membership functions
class CFuzzyT1RuleTri{
public:
	// Number of antecedents of the rule
	int dim;

	// The output value of thisfuzzy rule
	float output;

	// The set of input fuzzy sets for this rule
	CFuzzySetT1Tri * fuzzyInput;
	
public:
	// Constructor
	CFuzzyT1RuleTri();

	// Destructor
	~CFuzzyT1RuleTri();

	// Initalizes the parameters of the rule
	void init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread);
		
	// Evaluates the output of the rule
	float getOutput(float * input, bool * AttrUse);		

	// Prints out the parameters of the rule
	void printOut();
};

// Implements the T1 Fuzzy Logic System
class CFLC1{
public:
	// Array of fuzzy rules with Gaussian MFs
	CFuzzyT1Rule * rules;	

	// Array of fuzzy rules with Triangular MFs
	CFuzzyT1RuleTri * rulesTri;	

	// Dimensionality of the input vector
	int dim;
	
	// Number of rules
	int N_Rules;

	// Should triangular MFs be used
	bool tri;

	// Input membership degrees of the maximum rule
	float * maxRuleMF;

public:
	// Constructor
	CFLC1(bool memberMode);

	// Constructor
	CFLC1(int _dim, int _N_Rules, bool memberMode);

	// Destructor
	~CFLC1();

	// Set the specified fuzzy rule
	void setRule(int index, float * values, float * spreadL, float * spreadR, float output, float spread);

	// Evaluates the output of the FLS
	float evalOut(FVec * input);

	// Prints out the parameters of the FLS
	void printOut();
};


#endif // __FLC_H__

