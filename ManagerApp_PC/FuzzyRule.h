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

#ifndef __FUZZYRULE_H__
#define __FUZZYRULE_H__

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

using namespace std;

// Class implementing the linguistic fuzzy rule
class FuzzyRule
{
public:
	// Number of discretization steps in the output dimension
	int outN;
	// stores the discretized T1 output fuzzy set
	float * output;
	// stores the discretized IT2 output fuzzy set
	float * output2_low;
	float * output2_high;
	// Pointer to the linked list of rule's antecedents
	FuzzyAnt * rule;
	// Index of the output fuzzy set assigned to this rule
	int outIndex;
	string output_dim_name;
	// Number of antecedent in this rule
	int num_ant;

	// Constructor 1
	FuzzyRule()
	{		
		this->outN = 0;
		this->rule = NULL;
		this->output = NULL;
		this->output2_low = NULL;
		this->output2_high = NULL;
		this->outIndex = 0;
		this->num_ant = 0;
		this->output_dim_name = "";
	}

	// Constructor 2
	FuzzyRule(int _N)
	{		
		this->outN = _N;
		this->rule = NULL;
		this->output = new float[2 * this->outN + 1];
		this->output2_low = new float[2 * this->outN + 1];
		this->output2_high = new float[2 * this->outN + 1];
		for (int i = 0; i < 2 * this->outN + 1; i++)
		{
			output[i] = 0;
		}
		this->outIndex = 0;
		this->num_ant = 0;
		this->output_dim_name = "";
	}

	// Destructor - Needed if the rule base gets reloaded
	~FuzzyRule()
	{		
		delete [] this->output;
		delete [] this->output2_low;
		delete [] this->output2_high;
		FuzzyAnt * help1 = this->rule;
		FuzzyAnt * help2 = this->rule;
		while (help1->next != NULL)
		{
			help1 = help1->next;
			delete help2;
			help2 = help1;
		}
		delete help1;		
	}

	// Initializes a fuzzy rule with the given resolution of the output dimension
	void setup(int _N)
	{
		this->outN = _N;
		this->output = new float[2 * this->outN + 1];
		this->output2_low = new float[2 * this->outN + 1];
		this->output2_high = new float[2 * this->outN + 1];
		for (int i = 0; i < 2 * this->outN + 1; i++)
		{
			this->output[i] = 0;
			this->output2_low[i] = 0;
			this->output2_high[i] = 0;
		}
	}

	void clearOut()
	{
		for (int i = 0; i < 2 * this->outN + 1; i++)
		{
			this->output[i] = 0;
			this->output2_low[i] = 0;
			this->output2_high[i] = 0;
		}
	}
	// Adds the provided antecedent to the fuzzy rule
	void addAnt(FuzzyAnt * ant)
	{
		if (this->rule == NULL)
		{
			this->rule = ant;
		}
		else
		{
			FuzzyAnt * help = this->rule;
			while(help->next != NULL)
			{
				help = help->next;
			}
			help->next = ant;
		}
		this->num_ant++;
	}

	// Prints out the linguistic description of the fuzzy rule
	void printOut()
	{
		cout << "IF ";
		FuzzyAnt * help = this->rule;
		while(help != NULL)
		{
			help->printOut();
			if (help->next != NULL)
			{
				cout << " AND ";
			}
			help = help->next;
		}
		cout << " THEN "<< this->output_dim_name;
		if(this->outIndex > -1)
			cout << " IS " << _FNL_getTermNameOut(this->outIndex);
		cout << endl;
	}

	string FuzzyRulesToString()
	{
		string outDesc = "";
		outDesc += "IF ";

		FuzzyAnt * help = this->rule;
		while(help != NULL)
		{
			outDesc += help->ruleToString();
			if (help->next != NULL)
			{
				outDesc += " AND ";
			}
			help = help->next;
		}

		outDesc += " THEN ";
		outDesc += this->output_dim_name;
		outDesc += " IS ";
		outDesc += _FNL_getTermNameOut(this->outIndex);
		return outDesc;
	}

	string FuzzyRulesToString(int _ant_index)
	{
		string outDesc = "";
		int i = 0;
		//outDesc += "IF ";

		FuzzyAnt * help = this->rule;
		while(help != NULL)
		{
			if(i == _ant_index)
			{
				outDesc += help->ruleToString();
			}			
			help = help->next;
			i ++;
		}

		//outDesc += " THEN ";
		//outDesc += this->output_dim_name;
		//outDesc += " IS ";
		//outDesc += _FNL_getTermNameOut(this->outIndex);
		return outDesc;
	}

		// Generates the string with the description of the current rule
	string printString(){
		string outDesc = "";
		outDesc += "IF ";

		FuzzyAnt * help = this->rule;
		while(help != NULL){
			outDesc += help->printString();
			if (help->next != NULL){
				outDesc + " AND ";
			}
			help = help->next;
		}

		outDesc += " THEN anomaly ";

		return outDesc;
	}
};

#endif // __FUZZYRULE_H__