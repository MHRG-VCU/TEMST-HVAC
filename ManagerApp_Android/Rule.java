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

public class Rule
{
	// Number of discretization steps in the output dimension
	int outN;
	
	// stores the discretized T1 output fuzzy set
	float output[];
	
	// stores the discretized IT2 output fuzzy set
	float output2_low[];
	float output2_high[];
	
	// Pointer to the linked list of rule's antecedents
	Ant rule;
	
	// Number of antecedent in this rule
	int m_NAnt;
	
	// Index of the output fuzzy set assigned to this rule
	int outIndex;
	
	// Constructor 1
	Rule()
	{
		outN = 0;
		
		rule = null;
		m_NAnt = 0;
		output = null;
		output2_low = null;
		output2_high = null;
		outIndex = 0;
	}
	
	// Constructor 2
	Rule(int _N)
	{
		outN = _N;
		
		rule = null;
		m_NAnt = 0;
		
		output = new float[2 * outN + 1];
		output2_low = new float[2 * outN + 1];
		output2_high = new float[2 * outN + 1];
		
		for (int i = 0; i < 2 * outN + 1; i++)
		{
			output[i] = 0;
		}
		
		outIndex = 0;
	}
	
	// Initializes a fuzzy rule with the given resolution of the output dimension
	void setup(int _N)
	{
		outN = _N;
		
		output = new float[2 * outN + 1];
		output2_low = new float[2 * outN + 1];
		output2_high = new float[2 * outN + 1];
		
		for (int i = 0; i < 2 * outN + 1; i++)
		{
			output[i] = 0;
			output2_low[i] = 0;
			output2_high[i] = 0;
		}
	}
	
	// Adds the provided antecedent to the fuzzy rule
	void addAnt(Ant ant)
	{
		if (rule == null)
		{
			rule = ant;
		} 
		else
		{
			Ant help = rule;
		
			while (help.next != null)
			{
				help = help.next;
			}
		
			help.next = ant;
		}
		
		m_NAnt++;
	}
	
	// Prints out the linguistic description of the fuzzy rule
	void printOut()
	{
	
		System.out.print("IF ");
		
		Ant help = rule;
		while (help != null)
		{
			help.printOut();
			if (help.next != null)
			{
				System.out.print(" AND ");
			}
			help = help.next;
		}
		
		System.out.println(" THEN anomaly ");
	
	}
	
	// Generates the string with the description of the current rule
	public String printString()
	{
		String outDesc = "";
		outDesc += "IF ";
		
		Ant help = rule;
		while (help != null)
		{
			outDesc += help.printString();
			if (help.next != null)
			{
				outDesc += " AND ";
			}
			help = help.next;
		}
		
		outDesc += " THEN anomaly";
		
		return outDesc;
	}
}

