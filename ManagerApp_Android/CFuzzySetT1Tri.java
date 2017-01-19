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

public class CFuzzySetT1Tri
{
	// Position of the apppex of the triangular MF
	float mean;
	
	// Spread of the triangle to the left and right
	float left;
	float right;
	
	// The current membership degree (most recent input)
	float member_deg;
	
	// Constructor
	CFuzzySetT1Tri()
	{
		mean = 0.0f;
		left = 0.0f;
		right = 0.0f;
		
		member_deg = 0.0f;
	}
	
	// Assigns the parameters of the FS
	void setValues(float _mean, float _left, float _right, float spread)
	{
		mean = _mean;
		left = (_mean - _left) * spread;
		right = (_right - _mean) * spread;
	}
	
	// Evaluates the membership function
	float getMembership(float value)
	{
		// Check if the left or the right standard deviation should be used
		float deg;
		if (value == mean)
		{
			deg = 1.0f;
		} 
		else if (value < mean)
		{
			if (left > 0.0)
			{
				if (value < (mean - left))
				{
					deg = 0;
				} 
				else
				{
					deg = (value - (mean - left)) / left;
				}
			} 
			else
			{
				deg = 0.0f;
			}
		} 
		else
		{
			if (right > 0.0)
			{
				if (value > (mean + right))
				{
					deg = 0;
				} 
				else
				{
					deg = ((mean + right) - value) / right;
				}
			} 
			else
			{
				deg = 0.0f;
			}
		}
		
		return deg;
	}
	
	// Prints out the parameters of the MF
	void printOut()
	{
		// cout << this->mean << " , " << this->left << " , " << this->right <<
		// endl;
	}
}
