/************************************************************************
*      __   __  _    _  _____   _____
*     /  | /  || |  | ||     \ /  ___|
*    /   |/   || |__| ||    _||  |  _
*   / /|   /| ||  __  || |\ \ |  |_| |
*  /_/ |_ / |_||_|  |_||_| \_\|______|
*    
* 
*   Written by Dumidu Wijayasekara, University of Idaho (2012)
*   Copyright (2012) Modern Heuristics Research Group (MHRG)
*	University of Idaho, Virginia Commonwealth University (VCU)
*   http://www.people.vcu.edu/~mmanic/
*   Do not redistribute without author's(s') consent
*  
*   Any opinions, findings, and conclusions or recommendations expressed 
*   in this material are those of the author's(s') and do not necessarily 
*   reflect the views of any other entity.
*  
************************************************************************/

// Use the appropriate package
package dumi.temst.namespace;

public class CFuzzyIT2RuleTri
{
	// Number of antecedents of the fuzzy rule
	int dim;
	
	// The output value of this fuzzy rule
	float output;
	
	// Lower firing strength
	float deg_L;
	// Upper firing strength
	float deg_U;
	
	// The set of input fuzzy sets for this rule
	CFuzzySetIT2Tri fuzzyInput[];
	
	// Constructor
	CFuzzyIT2RuleTri()
	{
		dim = 0;
		output = 0;
		
		deg_L = 0;
		deg_U = 0;
		
		fuzzyInput = null;
	}
	
	// Initializes the antecedents of this fuzzy rule
	void init(int _dim, float values[], float spreadL[], float spreadR[],float _output, float spread, float blur)
	{
		dim = _dim;
		
		output = _output;
		
		fuzzyInput = new CFuzzySetIT2Tri[dim];
		
		for (int i = 0; i < dim; i++)
		{
			fuzzyInput[i] = new CFuzzySetIT2Tri();
			fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread, blur);
		}
	}
	
	// Evaluates the output of this fuzzy rule
	void getOutput(float input[])
	{
		deg_L = 1000;
		deg_U = 1000;
		
		for (int i = 0; i < dim; i++)
		{
			fuzzyInput[i].getMembership(input[i]);
		
			if (fuzzyInput[i].member_deg_L < deg_L)
			{
				deg_L = fuzzyInput[i].member_deg_L;
			}
		
			if (fuzzyInput[i].member_deg_U < deg_U)
			{
				deg_U = fuzzyInput[i].member_deg_U;
			}
		
		}
	}
	
	// Prints out the parameters of this fuzzy rule
	void printOut()
	{
		// cout << "Rule: " << endl;
		
		for (int i = 0; i < dim; i++)
		{
			//fuzzyInput[i].printOut();
		}
		
		// cout << "Output: " << this->output << endl;
	}
}

