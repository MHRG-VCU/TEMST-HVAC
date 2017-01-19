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

public class Zone 
{
	
	int m_ID;				// ID of the zone
	int m_NPoly;			// Number of polygons creating the zone
	ZPoly  m_Poly[];		// Array of polygons
	int m_NVertex;			// number of vertices in wall
	float [] m_Vertex;		// Array of vertices xi,yi,...(the size is 2*m_NVertex)
	float m_MidX;			// Mid point
	float m_MidY;			// Mid point
	DataSource m_Data;		// Data source of given zone
	// The Anomaly detection algorithm associated with this packet buffer
	public OCM_Alg m_Alg;

	// Anomaly indicator of this zone
	public float m_AnomalI;

	// Anomaly indicator of this zone based on expert rules
	public float m_AnomalI_Rules;

	// Index of rule from expert rule base which most apply to the given feature vector
	public int m_AnomI_Rules_Idx;

	// Start of an anomaly
	public int m_StartAnomal;

	// Flag stating if previously anomalous
	public boolean m_PrevAnomal;

	// The FIS for generating linguistic descriptions based on the cluster date
	public CFISTOneCTR m_FIS_Cluster;

	// The FIS for detecting anomalies and generating linguistic descriptions based on expert fuzzy rules
	public CFISTOneCTR m_FIS_Rules;
	
	
	String temp;
	
	public Zone()
	{
		initValues();
	}
	
	public Zone(int _id,int _n_poly)
	{
		initValues();
		this.m_ID = _id;
		this.m_NPoly = _n_poly;
		this.m_Poly = new ZPoly[this.m_NPoly];
	}
	
	public void initValues()
	{
		this.m_ID = 0;
		this.m_NPoly = 0;
		this.m_Poly = null;

		this.m_NVertex = 0;
		this.m_Vertex = null;		

		this.m_MidX = 0.0f;
		this.m_MidY = 0.0f;

		m_Alg = new OCM_Alg();

		m_AnomalI = 0.0f;

		m_StartAnomal = 0;
		m_PrevAnomal = false;

		m_FIS_Cluster = new CFISTOneCTR();
		m_FIS_Rules = new CFISTOneCTR();
	}
	
	//  Evaluates the anomality of a give zone based on the extracted clusters
	public void evalZoneCluster(FVec fvec, int dataPoint)
	{
		
		if (dataPoint == 0)
		{
			m_AnomalI = 0.0f;
		}
		else
		{
		
			fvec.coord[0] = m_Data.m_Val[0][dataPoint];
			fvec.coord[1] = m_Data.m_TimeVal[dataPoint];
		
			// Normalize the feature vectors
			for (int d = 0; d < m_Alg.dim; d++)
			{
				fvec.coord[d] = (fvec.coord[d] - m_Alg.m_ValMin[d]) / (m_Alg.m_ValMax[d] - m_Alg.m_ValMin[d]);
			}
		
			// 	Classify the status of the zone
			m_AnomalI = m_Alg.classify(fvec);
		
			m_FIS_Cluster.computeAntSelection(m_Alg.flc1.maxRuleMF, fvec.on);
		
			// Also compute the linguistic description of the inputs and outputs
			// Used for linguistic description of the anomaly
			for (int i = 0; i < m_FIS_Cluster.m_NDim; i++)
			{
				for (int j = 0; j < m_FIS_Cluster.m_NInputFS[i]; j++)
				{
					m_FIS_Cluster.m_InputFuzzy[i][j].getMembership(fvec.coord[i]);
				}
			}
		
			for (int i = 0; i < m_FIS_Cluster.m_NOutputFS; i++)
			{
				m_FIS_Cluster.m_OutputFuzzy[i].getMembership(m_AnomalI);
			}
		
			// Denormalize the feature vector
			for (int d = 0; d < m_Alg.dim; d++)
			{
				fvec.coord[d] = (fvec.coord[d] *  (m_Alg.m_ValMax[d] - m_Alg.m_ValMin[d])) + m_Alg.m_ValMin[d];
			}
		}
	}
	
	//  Evaluates the anomality of a give zone based on the expert fuzzy rules
	public void evalZoneRules(FVec fvec, int dataPoint, FIS fis)
	{
		if (dataPoint == 0)
		{
			m_AnomalI_Rules = 0.0f;
		}
		else
		{
		
			fvec.coord[0] = m_Data.m_Val[0][dataPoint];
			fvec.coord[1] = m_Data.m_TimeVal[dataPoint];
		
			// Normalize the feature vectors
			for (int d = 0; d < m_Alg.dim; d++)
			{
				fvec.coord[d] = (fvec.coord[d] - m_Alg.m_ValMin[d]) / (m_Alg.m_ValMax[d] - m_Alg.m_ValMin[d]);
			}
			
			m_AnomalI_Rules = m_FIS_Rules.evalAnomaly(fis, fvec);
			m_AnomI_Rules_Idx = m_FIS_Rules.m_AnomRule_Idx;
			
			
			// Compute the linguistic label for the level of confidence in this anomaly
			for (int i = 0; i < m_FIS_Rules.m_NOutputFS; i++)
			{
				m_FIS_Rules.m_OutputFuzzy[i].getMembership(m_AnomalI_Rules);
			}
			
			// Denormalize the feature vector
			for (int d = 0; d < m_Alg.dim; d++)
			{
				fvec.coord[d] = (fvec.coord[d] *  (m_Alg.m_ValMax[d] - m_Alg.m_ValMin[d])) + m_Alg.m_ValMin[d];
			}
		}
	}
	
	// Updates the cluster model and the FLC model
	public void updateModel(FVec fvec, int dataPoint)
	{
		// Get the feature vector
		fvec.coord[0] = m_Data.m_Val[0][dataPoint];
		fvec.coord[1] = m_Data.m_TimeVal[dataPoint];
		
		// Normalize the feature vectors
		for (int d = 0; d < m_Alg.dim; d++)
		{
			fvec.coord[d] = (fvec.coord[d] - m_Alg.m_ValMin[d]) / (m_Alg.m_ValMax[d] - m_Alg.m_ValMin[d]);
		}
		
		m_Alg.clusters.update(fvec);
		
		m_Alg.initFLCConst();
		m_Alg.initFLCClusters(m_Alg.spread, m_Alg.blur);
	}

}
