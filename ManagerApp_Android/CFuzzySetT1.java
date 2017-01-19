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

public class CFuzzySetT1 
{
	// Mean of the Gaussian
	public float mean;

	// Left and right  standard deviation of the Gaussian
	public float stdL;		
	public float stdR;		

	// Normalizationg degrees for the left and right shoulders
	public float deg0L;
	public float deg0R;

	// Auxilliary variables
	public float auxL;
	public float auxR;

	// The current membership degree (most recent input)
	public float member_deg;


	// Constructor
	CFuzzySetT1()
	{
		mean = 0.0f;
		stdL = 0.0f;
		stdR = 0.0f;
	
		deg0L = 0.0f;
		deg0R = 0.0f;
	
		auxL = 0.0f;
		auxR = 0.0f;
		   
		member_deg = 0.0f;  
	} 

	// Assigns the parameters of the FS
	void setValues(float _mean, float _stdL, float _stdR, float spread)
	{
		mean = _mean;
		stdL = Math.abs(_mean - _stdL) * spread;  
		stdR = Math.abs(_stdR - _mean) * spread;
	
		deg0L = (1.0f / (stdL * (float)Math.sqrt(2*Math.PI))) * (float)Math.exp(-(mean - mean) * (mean - mean) / (2 * stdL * stdL));
		deg0R = (1.0f / (stdR * (float)Math.sqrt(2*Math.PI))) * (float)Math.exp(-(mean - mean) * (mean - mean) / (2 * stdR * stdR));
	
		auxL = (1.0f / (stdL * (float)Math.sqrt(2*Math.PI)));
		auxR = (1.0f / (stdR * (float) Math.sqrt(2*Math.PI)));
	}

	// Evaluates the membership function
	float getMembership(float value)
	{
		//Check if the left or the right standard deviation should be used
		float deg;
	
		if (value == mean)
		{
			deg =  1.0f;
		}
		else if (value < mean)
		{
			if (stdL > 0.0001)
			{   
				deg = (float) (auxL * Math.exp(-(value - mean) * (value - mean) / (2 * stdL * stdL)) / deg0L);
			}
			else
			{
				if (Math.abs(value - mean) < 0.0001)
				{   
					deg = 1.0f;
				}
				else
				{
					deg = 0.0f;
				}
			}
		}
		else 
		{
			if (stdR > 0.0001)
			{   
				deg =  (float) (auxR * Math.exp(-(value - mean) * (value - mean) / (2 * stdR * stdR)) / deg0R);
			}
			else
			{
				if (Math.abs(value - mean) < 0.0001)
				{
					deg = 1.0f;
				}
				else
				{
					deg = 0.0f;
				}
			}
		} 

		member_deg = deg;

		return deg;
	}

	// Prints out the parameters of the MF
	void printOut()
	{
		// cout << this->mean << " , " << this->stdL << " , " << this->stdR << endl;
	}

}
