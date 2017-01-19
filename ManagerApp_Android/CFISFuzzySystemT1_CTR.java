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

public class CFISFuzzySystemT1_CTR
{
	// Number of input FS
	int m_NDim;
	
	// Input antecedents
	CFISFuzzySetT1Tri m_InputFuzzy[][];
	// Output fuzzy sets
	CFISFuzzySetT1Tri m_OutputFuzzy[];
	
	// Number of input fuzzy sets in each dimension
	int m_NInputFS[];
	// Number of output fuzzy sets
	int m_NOutputFS;
	
	// Linguistic names of input and output attributes
	String m_InputName[];
	String m_OutputName;
	
	// Accumulators for the membership degrees
	float m_InputMemSum[][];
	float m_OutputMemSum[];
	
	// Maximum membership labels
	int m_InputMaxMemId[];
	int m_OutputMaxMemId;
	
	// Accumulators for the linguistic label significance
	float m_InputLabelSigSum[];
	
	// Number of discretization points in the output dimension
	int N_Out;
	
	// Use minimum t-norm
	boolean minT;
	
	// Computing the most significant linguistic labels for anomaly description
	int m_AntSelect[];
	float m_AntMF[];
	// This vector keeps the MFs of each dimension of the maximum strength rule in
	// unsorted original order
	float m_AntMFOrig[];
	
	// Index of the maximum firing strength expert rule
	int m_AnomRule_Idx;
	
	// Constructor - Initializes the structural parameters
	CFISFuzzySystemT1_CTR()
	{
		
		// Set the number of input dimensions
		m_NDim = 11;
		
		// Set the number of FS in each input and output dimension
		m_NInputFS = new int[m_NDim];
		
		for (int i = 0; i < m_NDim; i++)
		{
		
			if (i == 1)
			{
				m_NInputFS[i] = 6;
			} 
			else
			{
				m_NInputFS[i] = 5;
			}
		}
		
		m_NOutputFS = 5;
		
		// Set the linguistic descriptions of each dimension
		m_InputName = new String[m_NDim];
		m_InputName[0] = "Zone Temp.";
		m_InputName[1] = "Time";
		m_InputName[2] = "Outside Air Temp.";
		m_InputName[3] = "Chiller Temp.";
		m_InputName[4] = "Mixed Air Temp.";
		m_InputName[5] = "Return Air Temp.";
		m_InputName[6] = "Damper Position";
		m_InputName[7] = "Ex. Fan Load";
		m_InputName[8] = "Ex. Fan Current";
		m_InputName[9] = "Supp. Fan Load";
		m_InputName[10] = "Supp. Fan Current";
		
		m_OutputName = "Confidence";
		
		// Set the accumulators for the membership degrees
		m_InputMemSum = new float[m_NDim][];
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputMemSum[i] = new float[m_NInputFS[i]];
		}
		
		m_OutputMemSum = new float[m_NOutputFS];
		
		// Set the maximum membership labels
		m_InputMaxMemId = new int[m_NDim];
		
		// Set the input antecedents
		m_InputFuzzy = new CFISFuzzySetT1Tri[m_NDim][];
		
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputFuzzy[i] = new CFISFuzzySetT1Tri[m_NInputFS[i]];
		
			if (i == 1)
			{
				m_InputFuzzy[i][0].setValues(0.0f, 0.25f, 0.33f, 0, "Night");
				m_InputFuzzy[i][1].setValues(0.25f, 0.375f, 0.5f, 1, "Morning");
				m_InputFuzzy[i][2].setValues(0.416f, 0.5f, 0.583f, 1, "Noon");
				m_InputFuzzy[i][3].setValues(0.5f, 0.625f, 0.75f, 1, "Afternoon");
				m_InputFuzzy[i][4].setValues(0.66f, 0.75f, 0.833f, 1, "Evening");
				m_InputFuzzy[i][5].setValues(0.75f, 0.833f, 1.0f, 2, "Night");
			} 
			else
			{
				m_InputFuzzy[i][0].setValues(0.0f, 0.0f, 0.25f, 0, "Low");
				m_InputFuzzy[i][1].setValues(0.0f, 0.25f, 0.5f, 1, "Lower");
				m_InputFuzzy[i][2].setValues(0.25f, 0.5f, 0.75f, 1, "Medium");
				m_InputFuzzy[i][3].setValues(0.5f, 0.75f, 1.0f, 1, "Higher");
				m_InputFuzzy[i][4].setValues(0.75f, 1.0f, 1.0f, 2, "High");
			}
		}
		
		// Set the output FS
		m_OutputFuzzy = new CFISFuzzySetT1Tri[m_NOutputFS];
		
		m_OutputFuzzy[0].setValues(0.0f, 0.0f, 0.25f, 0, "Very Low");
		m_OutputFuzzy[1].setValues(0.0f, 0.25f, 0.5f, 1, "Somewhat");
		m_OutputFuzzy[2].setValues(0.25f, 0.5f, 0.75f, 1, "Medium");
		m_OutputFuzzy[3].setValues(0.5f, 0.75f, 1.0f, 1, "Significant");
		m_OutputFuzzy[4].setValues(0.75f, 1.0f, 1.0f, 2, "Very High");
		
		m_InputLabelSigSum = new float[m_NDim];
		
		N_Out = 40;
		
		minT = false;
		
		m_AntSelect = new int[m_NDim];
		m_AntMF = new float[m_NDim];
		m_AntMFOrig = new float[m_NDim];
		
		m_AnomRule_Idx = 0;
	}
	
	// Sets the accumualted memberships for all linguistic labels to zero
	void setMFZero()
	{
	
		for (int i = 0; i < m_NDim; i++)
		{
			for (int j = 0; j < m_NInputFS[i]; j++)
			{
				m_InputMemSum[i][j] = 0.0f;
			}
		
			m_InputMaxMemId[i] = 0;
		}
		
		for (int i = 0; i < m_NOutputFS; i++)
		{
			m_OutputMemSum[i] = 0.0f;
		}
		m_OutputMaxMemId = 0;
	}
	
	// Sets the accumualted linguistic labels significance to zero
	void setInputSignifZero()
	{
	
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputLabelSigSum[i] = 0.0f;
		}
	}
	
	// Finds the linguistic labels with the highest membership
	void getMaxMF()
	{
		
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputMaxMemId[i] = 0;
		}
		m_OutputMaxMemId = 0;
		
		float inputMaxVal[] = new float[m_NDim];
		for (int i = 0; i < m_NDim; i++)
		{
			inputMaxVal[i] = -1.0f;
		}
		
		float outputMaxVal = -1.0f;
		
		for (int i = 0; i < m_NDim; i++)
		{
			for (int j = 0; j < m_NInputFS[i]; j++)
			{
				if (m_InputFuzzy[i][j].member_deg > inputMaxVal[i])
				{
					inputMaxVal[i] = m_InputFuzzy[i][j].member_deg;
					m_InputMaxMemId[i] = j;
				}
			}
		}
		
		for (int j = 0; j < m_NOutputFS; j++)
		{
			if (m_OutputFuzzy[j].member_deg > outputMaxVal)
			{
				outputMaxVal = m_OutputFuzzy[j].member_deg;
				m_OutputMaxMemId = j;
			}
		}
	}
	
	// Finds the linguistic labels with the highest accumulated membership
	void getMaxAccuMF()
	{
		
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputMaxMemId[i] = 0;
		}
		m_OutputMaxMemId = 0;
		
		float inputMaxVal[] = new float[m_NDim];
		for (int i = 0; i < m_NDim; i++)
		{
			inputMaxVal[i] = -1.0f;
		}
		
		float outputMaxVal = -1.0f;
		
		for (int i = 0; i < m_NDim; i++)
		{
			for (int j = 0; j < m_NInputFS[i]; j++)
			{
				if (m_InputMemSum[i][j] > inputMaxVal[i])
				{
					inputMaxVal[i] = m_InputMemSum[i][j];
					m_InputMaxMemId[i] = j;
				}
			}
		}
		
		for (int j = 0; j < m_NOutputFS; j++)
		{
			if (m_OutputMemSum[j] > outputMaxVal)
			{
				outputMaxVal = m_OutputMemSum[j];
				m_OutputMaxMemId = j;
			}
		}
	}
	
	// Adds the current linguistic labels membership to the sum
	void accumMembership()
	{
		
		for (int i = 0; i < m_NDim; i++)
		{
			for (int j = 0; j < m_NInputFS[i]; j++)
			{
				m_InputMemSum[i][j] += m_InputFuzzy[i][j].member_deg;
			}
		}
		
		for (int i = 0; i < m_NOutputFS; i++)
		{
			m_OutputMemSum[i] += m_OutputFuzzy[i].member_deg;
		}
	}
	
	// Adds the current linguistic labels significance to the sums
	void accumInputSignificance()
	{
		
		for (int i = 0; i < m_NDim; i++)
		{
			m_InputLabelSigSum[i] += m_AntMFOrig[i];
		}
	}
	
	// This function takes as input the vector of membership degrees of the most
	// active rule and computes the importance of each
	// antecedent
	void computeAntSelection(float mfDeg[], boolean on[])
	{
		// Count the number of enabled dimensions
		int dimOn = 0;
		
		for (int i = 0; i < m_NDim; i++)
		{
			if (on[i])
			{
				dimOn++;
			}
		
			m_AntMFOrig[i] = mfDeg[i];
		}
	
		// Select the enabled dimensions
		float valSelect[] = new float[dimOn];
		int idxSelect[] = new int[dimOn];
		int idx = 0;
		
		for (int i = 0; i < m_NDim; i++)
		{
			if (on[i])
			{
				valSelect[idx] = mfDeg[i];
				idxSelect[idx] = i;
				idx++;
			}
		}
		
		// Sort the MF of the selected dimensions
		for (int i = 0; i < dimOn; i++)
		{
			for (int j = 0; j < dimOn - 1; j++)
			{
				if (valSelect[j] > valSelect[j + 1])
				{
		
					float swapF = valSelect[j];
					valSelect[j] = valSelect[j + 1];
					valSelect[j + 1] = swapF;
					
					int swapI = idxSelect[j];
					idxSelect[j] = idxSelect[j + 1];
					idxSelect[j + 1] = swapI;
				}
			}
		}
		
		// Store the indexes of the selected dimensions
		for (int i = 0; i < dimOn; i++)
		{
			m_AntSelect[i] = idxSelect[i];
			m_AntMF[i] = mfDeg[idxSelect[i]];
		}
	}
	
	// This function ransk the input labels based on their accumulated
	// significance during the anomaly
	void computeAntAccumSelection(int on[])
	{
		// Count the number of enabled dimensions
		int dimOn = 0;
		
		for (int i = 0; i < m_NDim; i++)
		{
			if (on[i] == 1)
			{
				dimOn++;
			}
		}
	
		// Select the enabled dimensions
		float valSelect[] = new float[dimOn];
		int idxSelect[] = new int[dimOn];
		int idx = 0;
		
		for (int i = 0; i < m_NDim; i++)
		{
			if (on[i] == 1)
			{
				valSelect[idx] = m_InputLabelSigSum[i];
				idxSelect[idx] = i;
				idx++;
			}
		}
		
		// Sort the MF of the selected dimensions
		for (int i = 0; i < dimOn; i++)
		{
			for (int j = 0; j < dimOn - 1; j++)
			{
				if (valSelect[j] > valSelect[j + 1])
				{
		
					float swapF = valSelect[j];
					valSelect[j] = valSelect[j + 1];
					valSelect[j + 1] = swapF;
					
					int swapI = idxSelect[j];
					idxSelect[j] = idxSelect[j + 1];
					idxSelect[j + 1] = swapI;
				}
			}
		}
		
		// Store the indexes of the selected dimensions
		for (int i = 0; i < dimOn; i++)
		{
			m_AntSelect[i] = idxSelect[i];
			m_AntMF[i] = m_InputLabelSigSum[idxSelect[i]];
		}
	}
	
	// Evaluates the anomaly of the given input vector with respect to the expert
	// fuzzy rules
	float evalAnomaly(FIS fis, FVec fvec)
	{
		
		float maxDegree = 0.0f;
		
		for (int i = 0; i < fis.rulesN; i++)
		{
			float minDegree = 1.0f;
		
			Ant help = fis.myRules[i].rule;
		
			while (help != null)
			{
			
				// Check for the second night fuzzy sets (night can be at the begining
				// of a day as well as at the end of a day)
				if ((help.dimIndex == 1) && (help.antIndex == 0))
				{
					m_InputFuzzy[help.dimIndex][help.antIndex].getMembership(fvec.coord[help.dimIndex]);
					float deg1 = m_InputFuzzy[help.dimIndex][help.antIndex].member_deg;
				
					m_InputFuzzy[help.dimIndex][5].getMembership(fvec.coord[help.dimIndex]);
					float deg2 = m_InputFuzzy[help.dimIndex][5].member_deg;
			
					minDegree = Math.min(minDegree, Math.max(deg1, deg2));
				} 
				else
				{
					m_InputFuzzy[help.dimIndex][help.antIndex].getMembership(fvec.coord[help.dimIndex]);
			
					minDegree = Math.min(minDegree,	m_InputFuzzy[help.dimIndex][help.antIndex].member_deg);
				}
			
				help = help.next;
			}
		
			if (minDegree > maxDegree)
			{
				maxDegree = minDegree;
				m_AnomRule_Idx = i;
			}
		}
		
		return maxDegree;
	}
	
	// Prints out the structure of the FLC
	void printOut()
	{
		
		System.out.println("T1 FIS");
		for (int i = 0; i < m_NDim; i++)
		{
			System.out.println(m_InputName[i]);
		
			for (int j = 0; j < m_NInputFS[i]; j++)
			{
				m_InputFuzzy[i][j].printOut();
			}
		
			System.out.println();
		}
		
		System.out.println();
		
		System.out.println("Output:" + m_OutputName);
		for (int i = 0; i < m_NOutputFS; i++)
		{
			m_OutputFuzzy[i].printOut();
		}
	}
}

