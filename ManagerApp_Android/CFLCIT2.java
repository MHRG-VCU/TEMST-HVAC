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

public class CFLCIT2
{
	//Array of the fuzzy rules with Gaussian IT2 FSs
	CFuzzyIT2Rule rules[];
	// Array of the fuzzy rules with triangular IT2 FSs
	CFuzzyIT2RuleTri rulesTri[];
	
	// Dimensionality of the input vector
	int dim;
	
	// Number of fuzzy rules
	int N_Rules;
	
	// Should the triangular MFs be used
	boolean tri;
	
	// Classification results
	float maxDeg_L;
	float maxDeg_U;
	
	// Constructor
	CFLCIT2(boolean _memberMode)
	{
	
		dim = 0;
		N_Rules = 0;
		
		tri = _memberMode;
		
		rules = null;
		rulesTri = null;
	}
	
	// Constructor
	CFLCIT2(int _dim, int _N_Rules, boolean _memberMode)
	{
	
		dim = _dim;
		N_Rules = _N_Rules;
		
		tri = _memberMode;
		
		rules = new CFuzzyIT2Rule[N_Rules];
		rulesTri = new CFuzzyIT2RuleTri[N_Rules];
		
		for(int i = 0; i < N_Rules; ++i)
		{
			rules[i] = new CFuzzyIT2Rule();
			rulesTri[i] = new CFuzzyIT2RuleTri();
		}
	}
	
	// Set the fuzzy rule at the specified index
	void setRule(int index, float values[], float spreadL[], float spreadR[], float output, float spread, float blur)
	{
		rules[index].init(dim, values, spreadL, spreadR, output, spread, blur);
		rulesTri[index].init(dim, values, spreadL, spreadR, output, spread, blur);
	}
	
	// Evaluates the output of the IT2 FLS
	float evalOut(FVec input)
	{
		
		// First find the conorm of the FOU of individual rules
		maxDeg_L = 0.0f;
		maxDeg_U = 0.0f;
		
		for (int i = 0; i < N_Rules; i++)
		{
			
			if (tri)
			{
				rulesTri[i].getOutput(input.coord);
			
				if (rulesTri[i].deg_L > maxDeg_L)
				{
					maxDeg_L = rulesTri[i].deg_L;
				}
			
				if (rulesTri[i].deg_U > maxDeg_U)
				{
					maxDeg_U = rulesTri[i].deg_U;
				}
			}
			else
			{
				rules[i].getOutput(input.coord);
			
				if (rules[i].deg_L > maxDeg_L)
				{
					maxDeg_L = rules[i].deg_L;
				}
			
				if (rules[i].deg_U > maxDeg_U)
				{
					maxDeg_U = rules[i].deg_U;
				}
			}
		}
		
		return (maxDeg_L + maxDeg_U) / 2.0f;
	}
	
	// Prints out the parameters of the IT2 FLS
	void printOut()
	{
		System.out.println("Rules: " + N_Rules);
		for (int i = 0; i < N_Rules; i++)
		{
			if (tri)
			{
				rulesTri[i].printOut();
			}
			else
			{
				rules[i].printOut();
			}
		}
	}
}

