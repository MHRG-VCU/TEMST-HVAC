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

public class COG 
{
	// Dimensionality of the input
	public int dim;
	// Position of this cluster
	public float [] coord;	
	// maximum coordinates of points assigned to this cluster
	public float [] maxCoord;
	// minimum coordinates of points assigned to this cluster
	public float [] minCoord;
	// Weight of this cluster, proportional to the number of assigned patterns
	public int weight;
	// Radius of this cluster
	public float radius;
	
	// Constructor
	public COG(int _dim)
	{
		this.dim = _dim;
		this.coord = new float[_dim];		
		this.minCoord = new float[_dim];
		this.maxCoord = new float[_dim];
		this.weight = 0;
		this.radius = 0;
		// Initialize the min and max dimensions
		for (int d = 0; d < this.dim; d++){
			this.minCoord[d] = 1000.0f;
			this.maxCoord[d] = -1000.0f;
		}
	}	
}
