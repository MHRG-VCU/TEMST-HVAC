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

public class FVec 
{
	// Dimensionality of the vector
	public int dim;
	// The data vector
	public float [] coord;	
	// The output value
	public float out;
	// boolean mask for turning on/off individual attributes
	public boolean [] on;
	
	// Constructor
	public FVec(int _dim)
	{
		this.dim = _dim;
		this.coord = new float[_dim];		
		this.out = 0;
		this.on = new boolean[_dim];
	}

	// Initialization function
	public void init(float [] vec, boolean _intr)
	{
		for (int i = 0; i < this.dim; i++)
		{
			this.coord[i] = vec[i];
		}		
	}

	// Initializes the boolean attribute use mask based on the provided int vector
	public void setOn(int [] AttrUse)
	{
		for (int i = 0; i < this.dim; i++)
		{
			if (AttrUse[i] == 0)
			{
				this.on[i] = false;
			}
			else
			{
				this.on[i] = true;
			}			
		}
	}	
}
