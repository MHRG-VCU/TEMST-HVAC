/************************************************************************
*      __   __  _    _  _____   _____
*     /  | /  || |  | ||     \ /  ___|
*    /   |/   || |__| ||    _||  |  _
*   / /|   /| ||  __  || |\ \ |  |_| |
*  /_/ |_ / |_||_|  |_||_| \_\|______|
*    
* 
*   Written by Ondrej Linda, University of Idaho (2012)
*   Copyright (2012) Modern Heuristics Research Group (MHRG)
*	University of Idaho   
*	Virginia Commonwealth University (VCU), Richmond, VA
*   http://www.people.vcu.edu/~mmanic/
*   Do not redistribute without author's(s') consent
*  
*   Any opinions, findings, and conclusions or recommendations expressed 
*   in this material are those of the author's(s') and do not necessarily 
*   reflect the views of any other entity.
*  
*   ***********************************************************************/

//---------------------------------------------------------------------------
// COG.h
// Center of Gravity of a cluster
// Date:  2012/2/7
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#ifndef __COG_H__
#define __COG_H__

// Class implementing the COG of a cluster
class COG{
public:
	// Dimensionality of the input
	int dim;
	// Position of this cluster
	float * coord;	
	// maximum coordinates of points assigned to this cluster
	float * maxCoord;
	// minimum coordinates of points assigned to this cluster
	float * minCoord;
	// Weight of this cluster, proportional to the number of assigned patterns
	int weight;
	// Radius of this cluster
	float radius;

public:
	// Constructor
	COG(int _dim){
		this->dim = _dim;
		this->coord = new float[_dim];		
		this->minCoord = new float[_dim];
		this->maxCoord = new float[_dim];
		this->weight = 0;
		this->radius = 0;

		// Initialize the min and max dimensions

		for (int d = 0; d < this->dim; d++){
			this->minCoord[d] = 1000.0;
			this->maxCoord[d] = -1000.0;
		}
	}

	// Destructor
	~COG(){
		delete [] this->coord;
		delete [] this->minCoord;
		delete [] this->maxCoord;
	}
};


#endif // __COG_H__

