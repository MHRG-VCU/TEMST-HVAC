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

import java.io.BufferedReader;
import java.io.FileReader;

public class OCM_Alg
{
	// The clustering object
	Clusters clusters;
	
	// Min Max normalization values
	float m_ValMin[];
	float m_ValMax[];
	
	// The fuzzy controller object
	CFLC1 flc1;
	// The interval type-2 fuzzy controller object
	CFLCIT2 flcIT2;
	
	// mode for the fuzzy logic used
	// mode == 0 -> FLC1, mode == 1 -> IT2 FLC
	int mode;
	
	// membership function
	// true - use triangular, false - use Gaussian
	boolean tri;
	
	// dimensionality of the input data
	int dim;
	// Number of clusters
	int NClust;
	// maximum cluster radius
	float maxRad;
	
	// Number of classified intances
	int Num;
	
	// Noise amplitude
	float noise;
	
	// Classification results for IT2 FLC
	float memLow;
	float memHigh;
	
	// Parameters of the FLC
	float spread;
	float blur;

	public String temp = "";
	// Constructor
	OCM_Alg()
	{
		dim = 0;
		NClust = 0;
		maxRad = 0;
		
		tri = false;
		
		clusters = null;
		flc1 = null;
		flcIT2 = null;
		
		m_ValMax = null;
		m_ValMin = null;
		
		mode = 0;
		
		Num = 0;
		noise = 0.0f;
		
		spread = 0.0f;
		blur = 0.0f;
	}

	// Constructor
	OCM_Alg(int _dim, float _rad, boolean _memberMode)
	{
		dim = _dim;
		NClust = 0;
		maxRad = _rad;
		
		tri = _memberMode;
		
		clusters = new Clusters(dim, maxRad);
		flc1 = new CFLC1(tri);
		flcIT2 = new CFLCIT2(tri);
		
		m_ValMax = new float[dim];
		m_ValMin = new float[dim];
		
		mode = 0;
		
		Num = 0;
		noise = 0.0f;
		
		spread = 0.0f;
		blur = 0.0f;
	}

	// Initialization function
	void initConst(int _dim, float _rad, boolean _memberMode)
	{
		dim = _dim;
		NClust = 0;
		maxRad = _rad;
		
		tri = _memberMode;
		
		clusters = new Clusters(dim, maxRad);
		flc1 = new CFLC1(tri);
		flcIT2 = new CFLCIT2(tri);
		
		m_ValMax = new float[dim];
		m_ValMin = new float[dim];
		
		mode = 0;
		
		Num = 0;
		noise = 0.0f;
	}

	// Initialization function
	void initFLCConst()
	{
		flc1 = new CFLC1(tri);
		flcIT2 = new CFLCIT2(tri);
	}

	// Initialization function
	void init()
	{
		Num = 0;
		noise = 0.0f;
	}

	// Reseting back the init state
	void reset(float _rad)
	{
	
		Num = 0;
		noise = 0.0f;
		
		NClust = 0;
		maxRad = _rad;
		
		clusters = new Clusters(dim, maxRad);
		
		flc1 = new CFLC1(tri);
		
		flcIT2 = new CFLCIT2(tri);
	}

	// Classifies the given input vector
	float classify(FVec vec)
	{

		// Add noise if desired
		if (noise > 0.0)
		{
			for (int i = 0; i < vec.dim; i++)
			{
				vec.coord[i] += noise * (2.0f * ((float) Math.random()) - 1.0f);
			}
		}

		float result = 0;

		// Use T1 FLS (mode == 0) or IT2 FLS (mode == 1)
		if (mode == 0)
		{
			result = 1.0f - flc1.evalOut(vec);
		} 
		else
		{
			result = 1.0f - flcIT2.evalOut(vec);
			memLow = 1.0f - flcIT2.maxDeg_U;
			memHigh = 1.0f - flcIT2.maxDeg_L;
		}

		Num++;

		return result;
	}

	// Initializes the fuzzy logic engine from the extracted clusters
	void initFLCClusters(float spread, float blur)
	{

		// Do this for both FLC
		flc1 = new CFLC1(dim, clusters.centers.size(), tri);
		flcIT2 = new CFLCIT2(dim, clusters.centers.size(), tri);

		for (int i = 0; i < clusters.centers.size(); i++)
		{
			flc1.setRule(i, clusters.centers.get(i).coord,
					clusters.centers.get(i).minCoord, clusters.centers.get(i).maxCoord,
					1.0f, spread);
			flcIT2.setRule(i, clusters.centers.get(i).coord,
					clusters.centers.get(i).minCoord, clusters.centers.get(i).coord,
					1.0f, spread, blur);
		}

	// cout << "The FLCs were initializes based on the extracted clusters" <<
	// endl;
	}

	// Initializes the fuzzy logic engine from a text file with stored clusters
	void initFLCFile(String fileName, float spread, float blur)
	{
	
		this.spread = spread;
		this.blur = blur;
	
		clusters.centers.clear();

		// Do this for both FLC
		try
		{
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			/*FileHandle input = Gdx.files.internal(fileName);
			BufferedReader file = new BufferedReader(input.reader());*/
			String line;
			line = file.readLine();
			
			NClust = Integer.parseInt(line);
			// System.out.println("Number of clusters: " + NClust);
			
			line = file.readLine();
			dim = Integer.parseInt(line);
			// System.out.println("Input dimensionality: " + dim);
			
			// Initialize the structure
			flc1 = new CFLC1(dim, NClust, tri);
			flcIT2 = new CFLCIT2(dim, NClust, tri);
			
			float values[] = new float[dim];
			float spreadL[] = new float[dim];
			float spreadR[] = new float[dim];
			
			float clustRad = 0.0f;
			float clustWeight = 0.0f;
			
			int index;
			String val;
			// Iterate through the text file, extract cluster parameters and transform
			// it into a fuzzy rule
			for (int i = 0; i < NClust; i++)
			{
				line = file.readLine();

				for (int d = 0; d < dim; d++)
				{

					index = line.indexOf(',');
					val = line.substring(0, index);
					values[d] = Float.parseFloat(val);
					line = line.substring(index + 1);
					
					index = line.indexOf(',');
					val = line.substring(0, index);
					spreadL[d] = Float.parseFloat(val);
					line = line.substring(index + 1);
					
					index = line.indexOf(',');
					val = line.substring(0, index);
					spreadR[d] = Float.parseFloat(val);
					line = line.substring(index + 1);
				}

				// Read the cluster weight and cluster radius
				index = line.indexOf(',');
				clustWeight = Float.parseFloat(line.substring(0, index));
				line = line.substring(index + 1);
				clustRad = Float.parseFloat(line);
				
				// Setup the fuzzy rule
				flc1.setRule(i, values, spreadL, spreadR, 1.0f, spread);
				flcIT2.setRule(i, values, spreadL, spreadR, 1.0f, spread, blur);
				
				// Also create the cluster
				COG c = new COG(dim);
				c.dim = dim;
				c.radius = maxRad;
				for (int d = 0; d < dim; d++)
				{
					c.coord[d] = values[d];
					
					c.maxCoord[d] = spreadR[d];
					c.minCoord[d] = spreadL[d];
				}

				c.weight = (int) clustWeight;
				// Add the new cluster into the vector
				clusters.centers.add(c);
			}
			
			this.temp = fileName + " = LOADED";
			
			file.close();
		} 
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
			this.temp = "Error: " + e.getMessage();
		}
	}

	// Loads the min and max values from a provided text file
	void loadMinMax(String fileName)
	{
	
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			/*FileHandle input = Gdx.files.internal(fileName);
			BufferedReader in = new BufferedReader(input.reader());*/
			String line;
			int index;
			
			for (int i = 0; i < dim; i++)
			{
				line = in.readLine();
				
				index = line.indexOf(',');
				
				m_ValMin[i] = Float.parseFloat(line.substring(0, index));
				
				line = line.substring(index + 1);
				
				m_ValMax[i] = Float.parseFloat(line);
			}
			in.close();
			this.temp = fileName + " = LOADED";
		} 
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
			this.temp = "Error: " + e.getMessage();
			//for (int i = 0; i < dim; i++)
			//{
			//	m_ValMin[i] = 0.0f;
			//	m_ValMax[i] = 1.0f;
			//}
		}
	}
}

