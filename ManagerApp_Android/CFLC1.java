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

public class CFLC1
{
	//Array of fuzzy rules with Gaussian MFs
	CFuzzyT1Rule rules[];
	
	// Array of fuzzy rules with Triangular MFs
	CFuzzyT1RuleTri rulesTri[];
	
	// Dimensionality of the input vector
	int dim;
	
	// Number of rules
	int N_Rules;
	
	// Should triangular MFs be used
	boolean tri;
	
	// Input membership degrees of the maximum rule
	float maxRuleMF[];
	
	// Constructor
	CFLC1(boolean _memberMode)
	{
	
		dim = 0;
		N_Rules = 0;
		
		rules = null;
		rulesTri = null;
		
		tri = _memberMode;
		
		maxRuleMF = null;
	}
	
	// Constructor
	CFLC1(int _dim, int _N_Rules, boolean _memberMode)
	{
	
		dim = _dim;
		N_Rules = _N_Rules;
		
		rules = new CFuzzyT1Rule[N_Rules];
		rulesTri = new CFuzzyT1RuleTri[N_Rules];
		
		for(int i = 0; i < N_Rules; ++i)
		{
			rules[i] = new CFuzzyT1Rule();
			rulesTri[i] = new CFuzzyT1RuleTri();
		}
		
		tri = _memberMode;
		
		maxRuleMF = new float[dim];
	}
	
	// Set the specified fuzzy rule
	void setRule(int index, float values[], float spreadL[], float spreadR[], float output, float spread)
	{
		rules[index].init(dim, values, spreadL, spreadR, output, spread);
		rulesTri[index].init(dim, values, spreadL, spreadR, output, spread);
	}
	
	// Evaluates the output of the FLS
	float evalOut(FVec input)
	{
		
		float maxDeg = 0.0f;
		float outDeg;
		
		for (int i = 0; i < N_Rules; i++)
		{
			if (tri)
			{
				outDeg = rulesTri[i].getOutput(input.coord, input.on);
			}
			else
			{
				outDeg = rules[i].getOutput(input.coord, input.on);
			}
		
			if (outDeg > maxDeg)
			{
				maxDeg = outDeg;
		
				for (int j = 0; j < dim; j++)
				{
					if (tri)
					{
						maxRuleMF[j] = rulesTri[i].fuzzyInput[j].member_deg;
					}
					else
					{
						maxRuleMF[j] = rules[i].fuzzyInput[j].member_deg;
					}
				}	
			}
		}
		
		return maxDeg;
	}
	
	// Prints out the parameters of the FLS
	void printOut()
	{
		System.out.println("Rules: " + N_Rules);
		for (int i = 0; i < N_Rules; i++)
		{
			rules[i].printOut();
		}
	}
}

