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


public class CFuzzySetIT2Tri
{
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
	
	// Constructor
	CFuzzySetIT2Tri()
	{
		mean = 0.0f;
		left_U = 0.0f;
		left_L = 0.0f;
		right_U = 0.0f;
		right_L = 0.0f;
		
		blur = 0.3f;
		
		member_deg_L = 0.0f;
		member_deg_U = 0.0f;
	}
	
	// Assigns the parameters of the FS
	void setValues(float _mean, float _left, float _right, float spread,float _blur)
	{
		blur = _blur;
		mean = _mean;
		left_L = Math.abs(_mean - _left) * (1 - blur) * spread;
		left_U = Math.abs(_mean - _left) * (1 + blur) * spread;
		
		right_L = Math.abs(_right - _mean) * (1 - blur) * spread;
		right_U = Math.abs(_right - _mean) * (1 + blur) * spread;
	}
	
	// Evaluates the membership function
	void getMembership(float value)
	{
		// Check if the left or the right standard deviation should be used
		if (value == mean)
		{
			member_deg_L = 1.0f;
			member_deg_U = 1.0f;
		} 
		else if (value < mean)
		{
		
			if (left_L > 0.0f)
			{
				if (value < (mean - left_L))
				{
					member_deg_L = 0.0f;
				} 
				else
				{
					member_deg_L = (value - (mean - left_L)) / (left_L);
				}
			} 
			else
			{
				member_deg_L = 0.0f;
			}
		
			if (left_U > 0.0f)
			{
				if (value < (mean - left_U))
				{
					member_deg_U = 0.0f;
				} 
				else
				{
					member_deg_U = (value - (mean - left_U)) / (left_U);
				}
			} 
			else
			{
				member_deg_U = 0.0f;
			}
		
		} 
		else
		{
			if (right_L > 0.0)
			{
				if (value > (mean + right_L))
				{
					member_deg_L = 0.0f;
				} 
				else
				{
					member_deg_L = ((mean + right_L) - value) / (right_L);
				}
			} 
			else
			{
				member_deg_L = 0.0f;
			}
		
			if (right_U > 0.0f)
			{
				if (value > (mean + right_U))
				{
					member_deg_U = 0.0f;
				} 
				else
				{
					member_deg_U = ((mean + right_U) - value) / (right_U);
				}
			} 
			else
			{
				member_deg_U = 0.0f;
			}
		}
	}
	
	// Prints out the parameters of the MF
	void printOut()
	{
		// cout << this->mean << " , " << this->left_L << " , " << this->left_U <<
		// " , " << this->right_L << " , " << this->right_U << endl;
	}
}
