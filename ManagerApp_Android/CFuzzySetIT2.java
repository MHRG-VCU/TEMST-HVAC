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

public class CFuzzySetIT2
{
	// Mean of the Gaussian
	float mean;
	
	// Left astandard deviation of the Upper and Lower Gaussian MF
	float stdL_U;
	float stdL_L;
	
	// Right astandard deviation of the Upper and Lower Gaussian MF
	float stdR_U;
	float stdR_L;
	
	// the amount of bluring / uncertainty
	float blur;
	
	// The membership to the lower and to the upper FOU
	float member_deg_U;
	float member_deg_L;
	
	// Constructor
	CFuzzySetIT2()
	{
		mean = 0.0f;
		stdL_U = 0.0f;
		stdL_L = 0.0f;
		stdR_U = 0.0f;
		stdR_L = 0.0f;
		
		blur = 0.3f;
		
		member_deg_L = 0.0f;
		member_deg_U = 0.0f;
	}
	
	// Assigns the parameters of the FS
	void setValues(float _mean, float _stdL, float _stdR, float spread,	float _blur)
	{
		blur = _blur;
		mean = _mean;
		stdL_L = Math.abs(_mean - _stdL) * (1 - blur) * spread;
		stdL_U = Math.abs(_mean - _stdL) * (1 + blur) * spread;
		
		stdR_L = Math.abs(_stdR - _mean) * (1 - blur) * spread;
		stdR_U = Math.abs(_stdR - _mean) * (1 + blur) * spread;
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
			if (stdL_L > 0.0f)
			{
				float deg_0_L = (float) ((1.0f / (stdL_L * (float) 
						Math.sqrt(2 * Math.PI))) * Math.exp(-(mean - mean) * (mean - mean)/ (2 * stdL_L * stdL_L)));
		
				member_deg_L = (float) ((1.0f / (stdL_L * (float) Math.sqrt(2 * Math.PI)))
						* Math.exp(-(value - mean) * (value - mean) / (2 * stdL_L * stdL_L)) / deg_0_L);
			} 
			else
			{
				member_deg_L = 0.0f;
			}
		
			if (stdL_U > 0.0f)
			{
		
				float deg_0_U = (float) ((1.0f / (stdL_U * (float) 
						Math.sqrt(2 * Math.PI))) * Math.exp(-(mean - mean) * (mean - mean)/ (2 * stdL_U * stdL_U)));
		
				member_deg_U = (float) ((1.0f / (stdL_U * (float) 
						Math.sqrt(2 * Math.PI)))
						* Math.exp(-(value - mean) * (value - mean) / (2 * stdL_U * stdL_U)) / deg_0_U);
			} 
			else
			{
				member_deg_U = 0.0f;
			}
		
		} 
		else
		{
			if (stdR_L > 0.0f)
			{
				float deg_0_L = (float) ((1.0f / (stdR_L * (float) 
						Math.sqrt(2 * Math.PI))) * Math.exp(-(mean - mean) * (mean - mean)/ (2 * stdR_L * stdR_L)));
		
				member_deg_L = (float) ((1.0f / (stdR_L * (float) 
						Math.sqrt(2 * Math.PI)))
						* Math.exp(-(value - mean) * (value - mean) / (2 * stdR_L * stdR_L)) / deg_0_L);
			} 
			else
			{
				member_deg_L = 0.0f;
			}
		
			if (stdR_U > 0.0)
			{
				float deg_0_U = (float) ((1.0f / (stdR_U * (float) 
						Math.sqrt(2 * Math.PI))) * Math.exp(-(mean - mean) * (mean - mean)
						/ (2 * stdR_U * stdR_U)));
		
				member_deg_U = (float) ((1.0f / (stdR_U * (float) 
						Math.sqrt(2 * Math.PI)))
						* Math.exp(-(value - mean) * (value - mean) / (2 * stdR_U * stdR_U)) / deg_0_U);
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
		// cout << this->mean << " , " << this->stdL_L << " , " << this->stdL_U <<
		// " , " << this->stdR_L << " , " << this->stdR_U << endl;
	}
}

