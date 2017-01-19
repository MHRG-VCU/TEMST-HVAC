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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

public class Clusters 
{
	// dimensionality of the input space
	public int dim;
	// maximum radius of a cluster
	public float rad_max;
	// vector of clusters
	public Vector<COG> centers = new Vector<COG>();
	
	// Constructor
	public Clusters(int _dim, float _rad_max)
	{
		this.dim = _dim;		
		this.rad_max = _rad_max;
	}
	
	// Initialization function
	void initialize(float [] vec)
	{
		COG c = new COG(this.dim);
		for (int d = 0; d < this.dim; d++)
		{
			c.coord[d] = vec[d];
		}

		c.weight = 1;
		c.radius = this.rad_max;		

		this.centers.insertElementAt(c, 0);
	}

	// Cluster learning function - uses nearest neighbor method
	void update(FVec vec)
	{
		
		// find the closest COG created so far
		float dist_min = 1000;
		int index_min = 0;
		float dist = 0;
				
		for (int c = 0; c < (int)this.centers.size(); c++)
		{
			dist = 0;
			for (int d = 0; d < this.dim; d++)
			{
				dist += (this.centers.get(c).coord[d] - vec.coord[d]) * (this.centers.get(c).coord[d] - vec.coord[d]);
			}			

			dist = (float) Math.sqrt(dist);

			if (dist < dist_min)
			{
				dist_min = dist;
				index_min = c;
			}
		}		

		// Check the minimal distance against the specified maximum radius
		if (dist_min < this.rad_max)
		{
			// Update the COG of the nearest cluster
			for (int d = 0; d < this.dim; d++)
			{
				this.centers.get(index_min).coord[d] = (this.centers.get(index_min).weight * this.centers.get(index_min).coord[d] + vec.coord[d]) / 
														(this.centers.get(index_min).weight + 1);

				// Check for the min and max values of cluster positions
				if (this.centers.get(index_min).minCoord[d] > vec.coord[d]){
					this.centers.get(index_min).minCoord[d] = vec.coord[d];
				}

				if (this.centers.get(index_min).maxCoord[d] < vec.coord[d]){
					this.centers.get(index_min).maxCoord[d] = vec.coord[d];
				}
			}
			this.centers.get(index_min).weight++;		

			//cout << "Updated already existing cluster." << endl;
		}
		// Create a new cluster
		else
		{			
			COG c = new COG(this.dim);
			c.dim = this.dim;
			c.radius = this.rad_max;
			for (int d = 0; d < this.dim; d++)
			{
				c.coord[d] = vec.coord[d];
				c.maxCoord[d] = vec.coord[d];
				c.minCoord[d] = vec.coord[d];
			}

			c.weight = 1;
			// Add the new cluster into the vector
			this.centers.insertElementAt(c, 0);			

			//cout << "Created new cluster." << endl;
		}
	}	
	
	// Saves the set of clusters into a file
	void saveClusters(String fileName)
	{
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
			// Store the number of clusters
			file.write(centers.size() + "\n");
			// Store the dimensionality of the problem
			file.write(dim + "\n");
	
			for (int c = 0; c < centers.size(); c++)
			{
	
				// save the coordinates and the min and max interval values
				for (int d = 0; d < dim; d++)
				{
					file.write(centers.get(c).coord[d] + "," + centers.get(c).minCoord[d]
							+ "," + centers.get(c).maxCoord[d] + ",");
				}
	
				file.write(centers.get(c).weight + "," + centers.get(c).radius + "\n");
			}
			file.close();
		} 
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
	
/*
	// Saves the set of clusters into a file
	void saveClusters(String fileName)
	{
		ofstream file;
		file.open(fileName.c_str());		

		/// Open the file
		if (file.is_open())
		{
			// Store the number of clusters
			file << this.centers.size() << endl;
			// Store the dimensionality of the problem
			file << this.dim << endl;

			for (int c = 0; c < (int)this.centers.size(); c++){

				// save the coordinates and the min and max interval values
				for (int d = 0; d < this.dim; d++){
					file << this.centers[c].coord[d] << "," << this.centers[c].minCoord[d] << "," << this.centers[c].maxCoord[d] << ",";
				}

				file << this.centers[c].weight << "," << this.centers[c].radius << endl;
			}
			file.close();
		}
		else{
			cout << "Unable to open file: " << fileName << endl; 
		}
	}
	*/	
	
}
