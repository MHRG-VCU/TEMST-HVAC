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

#ifndef __FUZZYINFERENCE_H__
#define __FUZZYINFERENCE_H__

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

using namespace std;

// Class representing the entire fuzzy inference engine
class FuzzyRuleSet
{
public:
	// Pointer to a set of fuzzy rules
	FuzzyRule * myRules;
	//FuzzyRule * generatedRule;
	// Stores the number of rules
	int rulesN;
	// Discretization resolution of the output dimension
	int outN;
	//int cfisDim;
	string output_dim_name;
	//flag indicating whether the rules are loaded
	bool rules_loaded;

	// Constructor 1
	FuzzyRuleSet()
	{
		this->rulesN = 0;
		this->myRules = NULL;
		//this->generatedRule = new FuzzyRule();
		this->outN = 40;
		this->output_dim_name = "";
		this->rules_loaded = false;
	}

	// Constructor 2
	FuzzyRuleSet(int _N)
	{
		this->rulesN = 0;
		this->myRules = NULL;
		//this->generatedRule = new FuzzyRule();
		this->outN = _N;
		this->output_dim_name = "";
		this->rules_loaded = false;
	}

	// Destructor
	~FuzzyRuleSet()
	{
		delete [] this->myRules;
	}

	void setDataDim(int d)
	{
		//FIS_num_input_dim = d;
		//this->cfisDim = FIS_num_input_dim;
		//delete [] dataLabel;
		//dataLabel = new Label[FIS_num_input_dim];
	}
	
	void setDataNames()
	{
		//dataLabel[0].setName((char*)"SepalLength");
		//dataLabel[1].setName((char*)"SepalWidth");
		//dataLabel[2].setName((char*)"PetalLength");
		//dataLabel[3].setName((char*)"PetalWidth");
		//dataLabel[4].setName((char*)"roadDistance");
	}
	
	void setDataNames(int i, char* n)
	{
		//dataLabel[i].setName(n);
	}

	void setupOutputs(int nout)
	{
		this->outN = nout;
		for (int i = 0; i < this->rulesN; i++)
		{
			this->myRules[i].setup(this->outN);
		}
	}
	// Takes an input file and parse the text description into fuzzy rules
	// TODO: Improve error handling
	void loadRules(string file)
	{
		ifstream in;
		in.open(file);
		char buf[5000];
		// First get the number of rules
		in >> buf;
		this->rulesN = atoi(buf);
		cout << "Reading " << this->rulesN << " rules. " << endl;
		// Initialize the rules
		this->myRules = new FuzzyRule[this->rulesN];
		for (int i = 0; i < this->rulesN; i++)
		{
			// Read the if part
			in >> buf;
			// Init the rule			
			this->myRules[i].setup(this->outN);
			// read the next part, untill it is THEN codeword, we keep reading the antecedent
			in >> buf;
			while (strcmp(buf, "THEN") != 0)
			{
				// Start new antecedent
				FuzzyAnt * a = new FuzzyAnt();				
				this->myRules[i].addAnt(a);
				if (strcmp(buf, "AND") == 0)
				{
					in >> buf;
				}
				int temp = _FNL_getDimIndex(buf);
				if(temp == -1)
				{
					cout <<"getDimIndex returned -1\n";
				}
				else
				{
					a->dimIndex = _FNL_getDimIndex(buf);
				}
				// skip the IS part
				in >> buf;
				in >> buf;
				// Test if a hedge was used before the term
				int hedge = _FNL_getHedgeIndex(buf);
				if (hedge > -1)
				{					
					a->hedgeIndex = hedge;
					// move to the antecedent
					in >> buf;
				}
				// Get the antecedent index
				a->antIndex = _FNL_getTermIndex(buf, a->dimIndex);
				if (a->dimIndex == 1)
				{
					a->type = 1;					
				}
				else
				{
					a->type = 0;
				}
				in >> buf;
			}
			// skip the output name and the IS codeword
			in >> buf;
			in >> buf;
			in >> buf;
			// Get the output index
			this->myRules[i].outIndex = _FNL_getTermIndexOut(buf);
			this->myRules[i].output_dim_name = this->output_dim_name;
			// Print out the rule as a check
			this->myRules[i].printOut();
		}

		this->rules_loaded = true;
	}	

	// Reloads the fuzzy rule base from the provided text file
	void reloadRules(char * file)
	{
		delete [] this->myRules;
		this->loadRules(file);
	}
	
	/*
	// 
	void generateRule(int *term_index, int d)
	{
		//delete this->generatedRule;
		this->generatedRule = new CRule();
		this->generatedRule->setup(this->outN);
		int c = 0;
		for(int i = 0; i < d; i++)
		{
			if(term_index[i] != -1)
			{
				// Start new antecedent
				CAnt * a = new CAnt();
				this->generatedRule->addAnt(a);
				a->dimIndex = i;
				a->antIndex = term_index[i];
			}		
		}
		this->generatedRule->outIndex = c;
	}

	string getGeneratedRule()
	{
		string gen_rule = "";
		return this->generatedRule->CRulesToString();
	}

	// Prints out the generated fuzzy rules
	void generatedPrintOut()
	{
		cout << "*************************" << endl;
		cout << "    Generated Rule " << endl;
		this->generatedRule->printOut();
	}
	*/
	// Prints out the set of fuzzy rules
	void printOut()
	{
		cout << "*************************" << endl;
		cout << "    FIS Rule base " << endl;
		for (int i = 0; i < this->rulesN; i++)
		{
			cout << i << ": ";
			this->myRules[i].printOut();
		}
	}

	void printLabel(int i)
	{
		//dataLabel[i].printLabel();
	}

	char* returnTermName(int i)
	{
		//return getTermName(i);
	}
};

#endif // __FUZZYINFERENCE_H__