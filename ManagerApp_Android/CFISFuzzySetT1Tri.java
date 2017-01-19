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

public class CFISFuzzySetT1Tri
{
	//Parameters of the MF - left, right, center
	float a;
	float b;  
	float c;
	
	// 0 - left shoulder, 1 - mid, 2 - right shoulder
	int type;
	
	// The computed membership degree
	float member_deg;
	
	// Linguistic meaning of this fuzzy set
	String lingVal;
	
	// Constructor
	CFISFuzzySetT1Tri()
	{
		a = 0.0f;
		b = 0.0f;
		c = 0.0f;
		type = 0;
		 
		member_deg = 0.0f; 
		
		lingVal = "Unknown";
	}
	
	// Initializes the parameter values
	void setValues(float _a, float _c, float _b, int _type)
	{
		a = _a;
		b = _b;  
		c = _c;
		type = _type;  
	}
	
	// Initializes the parameter values
	void setValues(float _a, float _c, float _b, int _type, String _lingVal)
	{
		a = _a;
		b = _b;  
		c = _c;
		type = _type;  
		lingVal = _lingVal;
	}
	
	// Evaluates the membership degree of the input value
	void getMembership(float value)
	{
		// Left shoulder
		if (type == 0)
		{
			if (value < c)
			{
				member_deg = 1.0f;
			}   
			else if ((value >= c) && (value < b))
			{
				member_deg = (b - value) / (b - c);
			}
			else if (value >= b)
			{
				member_deg = 0.0f;
			}   
		}
		// Middle
		else if (type == 1)
		{
			if (value < a)
			{
				member_deg = 0.0f;
			}
			else if ((value >= a) && (value < c))
			{
				member_deg = (value - a) / (c - a);
			}
			else if ((value >= c) && (value < b))
			{
				member_deg = (b - value) / (b - c);
			}
			else
			{
				member_deg = 0;
			}   
		}
		// Right shoulder
		else if (type == 2)
		{   
			if (value < a)
			{
				member_deg = 0.0f;
			}
			else if ((value >= a) && (value < c))
			{
				member_deg = (value - a) / (c - a);
			}
			else if (value >= c)
			{
				member_deg = 1.0f;
			}   
		}  
	}
	
	// Prints out the fuzzy set paramters
	void printOut()
	{
		//cout << "Fs: " << this->lingVal << " : " << this->a << ", " << this->c << ", " << this->b << " Type: " << this->type  << endl;
	}
}

