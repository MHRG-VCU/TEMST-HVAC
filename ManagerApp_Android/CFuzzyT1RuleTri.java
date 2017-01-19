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

public class CFuzzyT1RuleTri

{
	// Number of antecedents of the rule
	int dim;
	
	// The output value of thisfuzzy rule
	float output;
	
	// The set of input fuzzy sets for this rule
	CFuzzySetT1Tri fuzzyInput[];
	
	// Constructor
	CFuzzyT1RuleTri()
	{
		dim = 0;
		output = 0;
		
		fuzzyInput = null;
	}
	
	// Initalizes the parameters of the rule
	void init(int _dim, float values[], float spreadL[], float spreadR[],
	float _output, float spread)
	{
		dim = _dim;
		
		output = _output;
		
		fuzzyInput = new CFuzzySetT1Tri[dim];
		
		for (int i = 0; i < dim; i++)
		{
			fuzzyInput[i] = new CFuzzySetT1Tri();
			fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread);
		}
	}
	
	// Evaluates the output of the rule
	float getOutput(float input[], boolean AttrUse[])
	{
		float minDeg = 1000;
		
		float deg;
		
		for (int i = 0; i < dim; i++)
		{
			if (AttrUse[i])
			{
				deg = fuzzyInput[i].getMembership(input[i]);
		
				if (deg < minDeg)
				{
					minDeg = deg;
				}
			}
		}
		
		if (minDeg == 1000)
		{
			return 1.0f;
		} 
		else
		{
			return minDeg;
		}
	}
	
	// Prints out the parameters of the rule
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

