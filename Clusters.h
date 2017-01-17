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
// Clusters.h
// Implements the clustering structure together with the nearest-neighbor
// clustering algorithm
// Date:  2012/2/17
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#ifndef __CLUSTERS_H__
#define __CLUSTERS_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <vector>
#include <vector>
#include <iostream>
#include <fstream>

#include "COG.h"

using namespace std;

// Class implementing the clustering structure
class Clusters{
public:
	// dimensionality of the input space
	int dim;
	// maximum radius of a cluster
	float rad_max;
	// vector of clusters
	vector<COG *> centers;

public:
	// Constructor
	Clusters(int _dim, float _rad_max){
		this->dim = _dim;		
		this->rad_max = _rad_max;
	}

	// Destructor
	~Clusters(){
		centers.clear();
	}
	
	// Initialization function
	void initialize(float * vec){
		COG * c = new COG(this->dim);
		for (int d = 0; d < this->dim; d++){
			c->coord[d] = vec[d];
		}

		c->weight = 1;
		c->radius = this->rad_max;		

		this->centers.push_back(c);
	}

	// Cluster learning function - uses nearest neighbor method
	void update(FVec * vec){
		
		// find the closest COG created so far
		float dist_min = 1000;
		int index_min = 0;
		float dist = 0;
				
		for (int c = 0; c < (int)this->centers.size(); c++){
			dist = 0;
			for (int d = 0; d < this->dim; d++){
				dist += (this->centers[c]->coord[d] - vec->coord[d]) * (this->centers[c]->coord[d] - vec->coord[d]);
			}			

			dist = sqrt(dist);

			if (dist < dist_min){
				dist_min = dist;
				index_min = c;
			}
		}		

		// Check the minimal distance against the specified maximum radius
		if (dist_min < this->rad_max){
			// Update the COG of the nearest cluster
			for (int d = 0; d < this->dim; d++){
				this->centers[index_min]->coord[d] = (this->centers[index_min]->weight * this->centers[index_min]->coord[d] + vec->coord[d]) / 
														(this->centers[index_min]->weight + 1);

				// Check for the min and max values of cluster positions
				if (this->centers[index_min]->minCoord[d] > vec->coord[d]){
					this->centers[index_min]->minCoord[d] = vec->coord[d];
				}

				if (this->centers[index_min]->maxCoord[d] < vec->coord[d]){
					this->centers[index_min]->maxCoord[d] = vec->coord[d];
				}
			}
			this->centers[index_min]->weight++;		

			//cout << "Updated already existing cluster." << endl;
		}
		// Create a new cluster
		else{					
			COG * c = new COG(this->dim);
			c->dim = this->dim;
			c->radius = this->rad_max;
			for (int d = 0; d < this->dim; d++){
				c->coord[d] = vec->coord[d];

				c->maxCoord[d] = vec->coord[d];
				c->minCoord[d] = vec->coord[d];
			}

			c->weight = 1;
			// Add the new cluster into the vector
			this->centers.push_back(c);			

			//cout << "Created new cluster." << endl;
		}
	}	
	
	// Saves the set of clusters into a file
	void saveClusters(string fileName){
		ofstream file;
		file.open(fileName.c_str());		

		/// Open the file
		if (file.is_open())
		{
			// Store the number of clusters
			file << this->centers.size() << endl;
			// Store the dimensionality of the problem
			file << this->dim << endl;

			for (int c = 0; c < (int)this->centers.size(); c++){

				// save the coordinates and the min and max interval values
				for (int d = 0; d < this->dim; d++){
					file << this->centers[c]->coord[d] << "," << this->centers[c]->minCoord[d] << "," << this->centers[c]->maxCoord[d] << ",";
				}

				file << this->centers[c]->weight << "," << this->centers[c]->radius << endl;
			}
			file.close();
		}
		else{
			cout << "Unable to open file: " << fileName << endl; 
		}
	}	
};

#endif // __CLUSTERS_H__
