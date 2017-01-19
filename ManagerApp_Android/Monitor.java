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

public class Monitor 
{
	
	public float m_ClusterRad;			// Max cluster radius
	public boolean m_MemberMode;		// Cluster membership mode
	public int m_DimF;					// Dimensionality of the feature vector for each zone	
	public float m_Spread;				// Blur of the fuzzy sets
	public float m_Blur;				// Blur of the IT2 FSs
	public int m_DispMode;				// Display mode
	public float m_Threshold;			// Anomaly detection threshold
	public int m_NAttr;					// Number of global floor attributes (including the building attributes)
	public boolean [] m_ShowAttr;		// Bool array for showing data view of additional attributes
	public float [][] m_DataColors;		// Color map for visualizing different data	
	public int [] m_AttrUse;				// bool mask for attribute, which are to be used for the anomaly detection
	public String [] m_InputName;		// Features names	
	public String m_OutputName;
	public int m_NAnt_Sel;				// Number of selected important antecedents
	public int m_NAnt_Enabled;			// Number of enabled attributes
	public FIS m_FIS;				// Data Structure for parsing linguistic anomaly description rules
	public int m_AnomMode;				// Anomaly mode: 0 - Uses clustering based, 1 - uses expert rules based
	public String debug_status;
	
	public String temp;
	
	public Monitor()
	{
		this.m_ClusterRad = 0.5f;
		this.m_DimF = 11;		
		this.m_MemberMode = false;

		this.m_Spread = 2.0f;
		this.m_Blur = 0.1f;

		this.m_DispMode = 0;

		this.m_Threshold = 0.8f;	

		this.m_AnomMode = 0;

		this.m_AttrUse = new int[this.m_DimF];

		for (int i = 0; i < this.m_DimF; i++)
		{
			this.m_AttrUse[i] = 1;
		}

		this.m_NAttr = 9;
		this.m_ShowAttr = new boolean[this.m_NAttr];
		for (int i = 0; i < this.m_NAttr; i++)
		{
			this.m_ShowAttr[i] = false;
		}

		// Create a color pallete
		this.m_DataColors = new float[this.m_NAttr][];
		for (int i = 0; i < this.m_NAttr; i++)
		{
			this.m_DataColors[i] = new float[3];
		}
		
		// Red
		this.m_DataColors[0][0] = 1.0f; this.m_DataColors[0][1] = 0.0f; this.m_DataColors[0][2] = 0.0f;
		// Green
		this.m_DataColors[1][0] = 0.0f; this.m_DataColors[1][1] = 1.0f; this.m_DataColors[1][2] = 0.0f;
		// Gray
		this.m_DataColors[2][0] = 0.5f; this.m_DataColors[2][1] = 0.5f; this.m_DataColors[2][2] = 0.5f;
		// Magenta
		this.m_DataColors[3][0] = 1.0f; this.m_DataColors[3][1] = 0.0f; this.m_DataColors[3][2] = 1.0f;
		// Cyan
		this.m_DataColors[4][0] = 0.0f; this.m_DataColors[4][1] = 1.0f; this.m_DataColors[4][2] = 1.0f;
		// Dark Red
		this.m_DataColors[5][0] = 0.5f; this.m_DataColors[5][1] = 0.0f; this.m_DataColors[5][2] = 0.0f;
		// 
		this.m_DataColors[6][0] = 0.0f; this.m_DataColors[6][1] = 0.5f; this.m_DataColors[6][2] = 0.5f;
		// 
		this.m_DataColors[7][0] = 0.6f; this.m_DataColors[7][1] = 0.6f; this.m_DataColors[7][2] = 1.0f;
		// 
		this.m_DataColors[8][0] = 1.0f; this.m_DataColors[8][1] = 0.5f; this.m_DataColors[8][2] = 0.5f;
		
		this.m_InputName = new String[this.m_DimF];
		this.m_InputName[0] = "Zone Temp.";
		this.m_InputName[1] = "Time";
		this.m_InputName[2] = "Outside Air Temp.";
		this.m_InputName[3] = "Chiller Temp.";
		this.m_InputName[4] = "Mixed Air Temp.";
		this.m_InputName[5] = "Return Air Temp.";
		this.m_InputName[6] = "Damper Position";
		this.m_InputName[7] = "Ex. Fan Load";
		this.m_InputName[8] = "Ex. Fan Current";
		this.m_InputName[9] = "Supp. Fan Load";
		this.m_InputName[10] = "Supp. Fan Current";		
		
		this.m_OutputName = "Confidence";

		this.m_NAnt_Sel = 1;
		
		this.m_NAnt_Enabled = this.m_DimF;

		this.m_FIS = new FIS();
		
		this.debug_status = "";
	}
	// Calculates the min and max normalization values for each zone
	public void calcMinMaxValFloor(Building bl, int NTrain)
	{
	
		// cout << "Computing min max values for Floor: " <<
		// bl->m_Floors[bl->m_SelectFloor].m_Id << " ..." ;
	
		float minVal[] = new float[m_DimF];
		float maxVal[] = new float[m_DimF];
		float vec[] = new float[m_DimF];
	
		for (int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
		{
	
			// Extract the min and max
			for (int d = 0; d < m_DimF; d++)
			{
				minVal[d] = 100000;
				maxVal[d] = -100000;
			}
	
			for (int j = 1; j < NTrain; j++)
			{
				vec[0] = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Data.m_Val[0][j];
				vec[1] = bl.m_Data.m_TimeVal[j];
	
				for (int a = 0; a < bl.m_NAttr; a++)
				{
					vec[2 + a] = bl.m_Data.m_Val[a][j];
				}
	
				for (int a = 0; a < bl.m_Floors[bl.m_SelectFloor].m_NAttr; a++)
				{
					vec[2 + bl.m_NAttr + a] = bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[a][j];
				}
	
				for (int d = 0; d < m_DimF; d++)
				{
					minVal[d] = Math.min(minVal[d], vec[d]);
					maxVal[d] = Math.max(maxVal[d], vec[d]);
				}
			}
	
			minVal[1] = 0.0f;
			maxVal[1] = 1.0f;
	
			// Store the min and max
			String fileName = Floor.getMinMaxFileName(bl.m_Floors[bl.m_SelectFloor].m_Id,
					bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_ID, bl.m_NameStr);
	
			try
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
				for (int d = 0; d < m_DimF; d++)
				{
					out.write(minVal[d] + ", " + maxVal[d] + "\n");
				}
				out.close();
			} 
			catch (Exception e)
			{
				System.err.println("Error: " + e.getMessage());
			}
		}
	
		System.out.println(" DONE!");
	}

	// Train the normal behavior model for each zone
	public void trainOCM(Building bl, int NTrain)
	{
	
		System.out.print("Training the normal behavior model "
		+ bl.m_Floors[bl.m_SelectFloor].m_Id + " ...");
	
		int dimF = m_DimF;
		FVec fvec = new FVec(m_DimF);
	
		// Go through all zones
		for (int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
		{
	
			bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.initConst(m_DimF,
					m_ClusterRad, m_MemberMode);
	
			// Load the min max normalization values
			bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.loadMinMax(Floor.getMinMaxFileName(bl.m_Floors[bl.m_SelectFloor].m_Id,
					bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_ID, bl.m_NameStr));
	
			// Go through the training data points
			for (int j = 1; j < NTrain; j++)
			{
	
				// Compose the training data vector
				fvec.coord[0] = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Data.m_Val[0][j];
				fvec.coord[1] = bl.m_Data.m_TimeVal[j];
	
				for (int a = 0; a < bl.m_NAttr; a++)
				{
					fvec.coord[2 + a] = bl.m_Data.m_Val[a][j];
				}
	
				for (int a = 0; a < bl.m_Floors[bl.m_SelectFloor].m_NAttr; a++)
				{
					fvec.coord[2 + bl.m_NAttr + a] = bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[a][j];
				}
	
				// normalize the input vector
				for (int d = 0; d < m_DimF; d++)
				{
					float minVal = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.m_ValMin[d];
					float maxVal = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.m_ValMax[d];
	
					fvec.coord[d] = (fvec.coord[d] - minVal) / (maxVal - minVal);
				}
	
				// Add the input into the clusters
				bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.clusters.update(fvec);
			}
	
			// Store the extracted clusters
			bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Alg.clusters
			.saveClusters(Floor.getClusterFileName(	bl.m_Floors[bl.m_SelectFloor].m_Id,
					bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_ID, bl.m_NameStr));
		}
	
		System.out.println(" DONE!");
	}

	// Saves the clusters models for the given floor
	public void saveModel(Floor floor, String buildingName)
	{
		for (int i = 0; i < floor.m_NZones; i++)
		{
			// Store the extracted clusters
			floor.m_Zones[i].m_Alg.clusters.saveClusters(Floor.getClusterFileName(
					floor.m_Id, floor.m_Zones[i].m_ID, buildingName));
		}
	}

	// Loads the normal behavior models for each zone on the floor
	public void loadModel(String contextDirectory, Floor floor, String buildingName)
	{
		for (int i = 0; i < floor.m_NZones; i++)
		{
			this.debug_status = "Zone = " + Integer.toString(i);
			floor.m_Zones[i].m_Alg.initConst(m_DimF, m_ClusterRad, m_MemberMode);
			this.temp = contextDirectory + Floor.getMinMaxFileName(floor.m_Id,floor.m_Zones[i].m_ID, buildingName);
			floor.m_Zones[i].m_Alg.loadMinMax(contextDirectory + Floor.getMinMaxFileName(floor.m_Id,
					floor.m_Zones[i].m_ID, buildingName));
			this.temp = floor.m_Zones[i].m_Alg.temp;
			this.temp = contextDirectory + Floor.getClusterFileName(floor.m_Id,floor.m_Zones[i].m_ID, buildingName);
			floor.m_Zones[i].m_Alg.initFLCFile(contextDirectory + Floor.getClusterFileName(floor.m_Id,
					floor.m_Zones[i].m_ID, buildingName), m_Spread, m_Blur);
			this.temp = floor.m_Zones[i].m_Alg.temp;
		}
		floor.m_IsReady = true;
	}

	// Generates report for a given time-frame (startIndex to endIndex) using the
	// cluster
	public void generateReportCluster(Building bl, int startIndex, int endIndex)
	{
		Floor floor = bl.m_Floors[bl.m_SelectFloor];
	
		String fileName = bl.m_NameStr;
	
		fileName += "_F" + floor.m_Id;
		fileName += "_Cluster_Report.txt";
	
		// Check that the interval is correct
		if ((startIndex < 0) || (endIndex > bl.m_NData) || (startIndex > endIndex))
		{
			System.out.println("The interval was not correct: " + startIndex + " - "
					+ endIndex);
			return;
		}
	
		for (int j = 0; j < floor.m_NZones; j++)
		{
			floor.m_Zones[j].m_StartAnomal = 0;
			floor.m_Zones[j].m_PrevAnomal = false;
		}
	
			System.out.print("Generating report (Clusters) from "
					+ floor.m_Zones[0].m_Data.m_Time[startIndex] + " to "
					+ floor.m_Zones[0].m_Data.m_Time[endIndex - 1] + " ...");
	
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
	
			for (int i = startIndex; i < endIndex; i++)
			{
				// create the feature vector and the building data
				FVec fvec = new FVec(m_DimF);
				fvec.setOn(m_AttrUse);
	
				for (int a = 0; a < bl.m_NAttr; a++)
				{
					fvec.coord[2 + a] = bl.m_Data.m_Val[a][i];
				}
	
				floor.evalZonesCluster(fvec, i, m_Threshold, 2 + bl.m_NAttr);
	
				for (int j = 0; j < floor.m_NZones; j++)
				{
					if (floor.m_Zones[j].m_AnomalI > this.m_Threshold)
					{
						if (!floor.m_Zones[j].m_PrevAnomal)
						{
							floor.m_Zones[j].m_FIS_Cluster.setMFZero();
							floor.m_Zones[j].m_FIS_Cluster.setInputSignifZero();
							floor.m_Zones[j].m_PrevAnomal = true;
							floor.m_Zones[j].m_StartAnomal = i;
						}
	
						floor.m_Zones[j].m_FIS_Cluster.accumMembership();
	
						floor.m_Zones[j].m_FIS_Cluster.accumInputSignificance();
					} 
					else
					{
						if (floor.m_Zones[j].m_PrevAnomal)
						{
							floor.m_Zones[j].m_FIS_Cluster.getMaxAccuMF();
							floor.m_Zones[j].m_FIS_Cluster
							.computeAntAccumSelection(m_AttrUse);
	
							if (i == floor.m_Zones[j].m_StartAnomal + 1)
							{
	
								out.write("Anomaly - Zone: "
										+ (j + 1)
										+ ", At: "
										+ floor.m_Zones[j].m_Data.m_Time[floor.m_Zones[j].m_StartAnomal]
												+ "\n");
	
								out.write("Description: ");
								for (int k = 0; k < m_NAnt_Sel - 1; k++)
								{
									int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[k];
	
									out.write(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
											+ " is "
											+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal
											+ " and ");
								}
	
								int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[m_NAnt_Sel - 1];
	
								out.write(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
										+ " is "
										+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal);
	
								out.write(" ( "
										+ floor.m_Zones[j].m_FIS_Cluster.m_OutputName
										+ " is "
										+ floor.m_Zones[j].m_FIS_Cluster.m_OutputFuzzy[floor.m_Zones[j].m_FIS_Cluster.m_OutputMaxMemId].lingVal
										+ " ). " + "\n\n");
							} 
							else
							{
	
								out.write("Anomaly - Zone: "
										+ (j + 1)
										+ ", From: "
										+ floor.m_Zones[j].m_Data.m_Time[floor.m_Zones[j].m_StartAnomal]
												+ " To: " + floor.m_Zones[j].m_Data.m_Time[i - 1] + "\n");
	
								out.write("Description: ");
	
								for (int k = 0; k < m_NAnt_Sel - 1; k++)
								{
									int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[k];
	
									out.write(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
											+ " is "
											+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal
											+ " and ");
	
								}
	
								int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[m_NAnt_Sel - 1];
	
								out.write(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
										+ " is "
										+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal);
	
								out.write(" ( "
										+ floor.m_Zones[j].m_FIS_Cluster.m_OutputName
										+ " is "
										+ floor.m_Zones[j].m_FIS_Cluster.m_OutputFuzzy[floor.m_Zones[j].m_FIS_Cluster.m_OutputMaxMemId].lingVal
										+ " ). " + "\n\n");
	
							}
							
							floor.m_Zones[j].m_PrevAnomal = false;
						}
					}
				}
			}
	
			out.close();
		} 
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	
		System.out.println(" DONE!");
	}

	// Generates report for a given time-frame using the expert rules
	public void generateReportRules(Building bl, int startIndex, int endIndex)
	{
		Floor floor = bl.m_Floors[bl.m_SelectFloor];
	
		String fileName = bl.m_NameStr;
	
		fileName += "_F" + floor.m_Id;
		fileName += "_Rules_Report.txt";
	
		// Check that the interval is correct
		if ((startIndex < 0) || (endIndex > bl.m_NData) || (startIndex > endIndex))
		{
			System.out.println("The interval was not correct: " + startIndex + " - " + endIndex);
			return;
		}
	
		System.out.println("Generating report (Rules) sfrom "
				+ floor.m_Zones[0].m_Data.m_Time[startIndex] + " to "
				+ floor.m_Zones[0].m_Data.m_Time[endIndex - 1] + " ...");
	
		for (int j = 0; j < floor.m_NZones; j++)
		{
			floor.m_Zones[j].m_StartAnomal = 0;
			floor.m_Zones[j].m_PrevAnomal = false;
		}
	
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			for (int i = startIndex; i < endIndex; i++)
			{
				// create the feature vector and the building data
				FVec fvec = new FVec(m_DimF);
				fvec.setOn(m_AttrUse);
	
				for (int a = 0; a < bl.m_NAttr; a++)
				{
					fvec.coord[2 + a] = bl.m_Data.m_Val[a][i];
				}
	
				floor.evalZonesRule(fvec, i, m_Threshold, 2 + bl.m_NAttr, m_FIS);
	
				for (int j = 0; j < floor.m_NZones; j++)
				{
					if (floor.m_Zones[j].m_AnomalI_Rules > m_Threshold)
					{
	
						out.write("Anomaly - Zone: " + (j + 1) + ", At: "
								+ floor.m_Zones[j].m_Data.m_Time[i] + "\n");
	
						out.write("Description: ");
						out.write(m_FIS.myRules[floor.m_Zones[j].m_AnomI_Rules_Idx]
								.printString() + "\n");
	
						floor.m_Zones[j].m_FIS_Rules.getMaxMF();
	
						out.write(floor.m_Zones[j].m_FIS_Rules.m_OutputName
								+ " is "
								+ floor.m_Zones[j].m_FIS_Rules.m_OutputFuzzy[floor.m_Zones[j].m_FIS_Rules.m_OutputMaxMemId].lingVal
								+ "\n");
	
					}
				}
			}
			out.close();
		} 
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	
		System.out.println(" DONE!");
	}

	// This function prints out the anomaly (cluster) to the std::cout
	public void printAnomalyCluster(Building bl)
	{
		Floor floor = bl.m_Floors[bl.m_SelectFloor];
	
		int j = floor.m_SelectZone;
	
		floor.m_Zones[j].m_FIS_Cluster.getMaxMF();
	
		System.out.println("Anomaly (Cluster) - Zone: " + (j + 1) + ", At: "
		+ bl.m_Data.m_Time[bl.m_DataPoint]);
	
		System.out.print("Description: ");
	
		for (int i = 0; i < m_NAnt_Sel - 1; i++)
		{
			int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[i];
	
			System.out
			.print(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
					+ " is "
					+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal
					+ " and ");
	
		}
	
		int idx = floor.m_Zones[j].m_FIS_Cluster.m_AntSelect[m_NAnt_Sel - 1];
	
		System.out
			.print(floor.m_Zones[j].m_FIS_Cluster.m_InputName[idx]
					+ " is "
					+ floor.m_Zones[j].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[j].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal);
	
		System.out
			.println(" ( "
					+ floor.m_Zones[j].m_FIS_Cluster.m_OutputName
					+ " is "
					+ floor.m_Zones[j].m_FIS_Cluster.m_OutputFuzzy[floor.m_Zones[j].m_FIS_Cluster.m_OutputMaxMemId].lingVal
					+ " ). ");
		System.out.println();

	}

	// This function prints out the anomaly (expert rule based) to the std::cout
	public void printAnomalyRule(Building bl)
	{
		Floor floor = bl.m_Floors[bl.m_SelectFloor];
	
		int j = floor.m_SelectZone;
	
		floor.m_Zones[j].m_FIS_Cluster.getMaxMF();
	
		System.out.println("Anomaly (Rule) - Zone: " + (j + 1) + ", At: "
		+ bl.m_Data.m_Time[bl.m_DataPoint]);
	
		System.out.print("Description: ");
	
		m_FIS.myRules[floor.m_Zones[j].m_AnomI_Rules_Idx].printOut();
	
		floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.getMaxMF();
	
		String outConf = "";
		outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputName;
		outConf += " is ";
		outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputFuzzy[floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputMaxMemId].lingVal;
	
		System.out.println(outConf);
		System.out.println();
	}

	// Evaluates the anomalies in the building in the current time
	public void evalBuilding(Building bl)
	{

		// create the feature vector and the building data
		FVec fvec = new FVec(m_DimF);
		fvec.setOn(m_AttrUse);
	
		// Adds the building attributes
		for (int a = 0; a < bl.m_NAttr; a++)
		{
			fvec.coord[2 + a] = bl.m_Data.m_Val[a][bl.m_DataPoint];
		}
	
		// Evaluate all floors
		for (int i = 0; i < bl.m_NFloors; i++)
		{
			if (bl.m_Floors[i].m_IsReady)
			{
				bl.m_Floors[i].evalZonesCluster(fvec, bl.m_DataPoint, m_Threshold,2 + bl.m_NAttr);
				bl.m_Floors[i].evalZonesRule(fvec, bl.m_DataPoint, m_Threshold,2 + bl.m_NAttr, m_FIS);
			}
		}
	}

	// Updates the model of the selected zone to include to current feature vector
	public void updateMode(Building bl)
	{
	
		// create the feature vector and the building data
		FVec fvec = new FVec(m_DimF);
		fvec.setOn(m_AttrUse);
	
		for (int a = 0; a < bl.m_NAttr; a++)
		{
			fvec.coord[2 + a] = bl.m_Data.m_Val[a][bl.m_DataPoint];
		}
	
		bl.m_Floors[bl.m_SelectFloor].updateModel(fvec, bl.m_DataPoint,2 + bl.m_NAttr);
	}

	// Evaluates the anomalies on current floor in the current time
	public void evalFloor(Building bl, int dataPoint)
	{

		// create the feature vector and the building data
		FVec fvec = new FVec(m_DimF);
		fvec.setOn(m_AttrUse);
	
		for (int a = 0; a < bl.m_NAttr; a++)
		{
			fvec.coord[2 + a] = bl.m_Data.m_Val[a][bl.m_DataPoint];
		}
	
		bl.m_Floors[bl.m_SelectFloor].evalZonesCluster(fvec, dataPoint,m_Threshold, 2 + bl.m_NAttr);
		bl.m_Floors[bl.m_SelectFloor].evalZonesRule(fvec, dataPoint, m_Threshold,2 + bl.m_NAttr, m_FIS);
	}

	// Updates the anomalies on current floor for the given threshold value
	public void evalUpdateFloor(Floor floor)
	{
		floor.evalUpdateZones(m_Threshold);
	}

	// Returns the linguistic description of the selected attribute rank
	public String getDescription(Floor floor, int rank, boolean and)
	{

		String outDesc = "";
	
		int idx = floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_AntSelect[rank];
	
		outDesc += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_InputName[idx];
		outDesc += " is ";
		outDesc += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_InputFuzzy[idx][floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_InputMaxMemId[idx]].lingVal;
	
		if (and)
		{
			outDesc += " and";
		}
	
		return outDesc;
	}

	// Returns the linguistic description of the expert rule (up to 3 antecedents)
	public String getDescriptionRule(Floor floor, int rank, boolean and)
	{
	
		String outDesc = "";
	
		// Check the number of antecedents in the most relevant rule
		int NAnt = m_FIS.myRules[floor.m_Zones[floor.m_SelectZone].m_AnomI_Rules_Idx].m_NAnt;
	
		if (rank + 1 > NAnt)
		{
			return outDesc;
		} 
		else
		{
			// Iterate through the rule up to the selected antecedent
			Ant help = m_FIS.myRules[floor.m_Zones[floor.m_SelectZone].m_AnomI_Rules_Idx].rule;
	
			for (int i = 0; i < rank; i++)
			{
				help = help.next;
			}
	
			outDesc += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_InputName[help.dimIndex];
			outDesc += " is ";
			outDesc += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_InputFuzzy[help.dimIndex][help.antIndex].lingVal;
	
			if ((and) && (NAnt > rank + 1))
			{
				outDesc += " and";
			}
	
			if ((rank == 2) && NAnt > 3)
			{
				outDesc += " ... ";
			}
		}
	
		return outDesc;
	}

	// Returns the linguistic anomaly confidence
	public String getAnomalyConfidence(Floor floor)
	{
	
		String outConf = "";
	
		if (m_AnomMode == 0)
		{
			floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.getMaxMF();
	
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputName;
			outConf += " is ";
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputFuzzy[floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputMaxMemId].lingVal;
		} 
		else
		{
			floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.getMaxMF();
	
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputName;
			outConf += " is ";
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputFuzzy[floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputMaxMemId].lingVal;
		}
	
		return outConf;
	}

	// Returns the linguistic anomaly confidence
	public String getNormalConfidence(Floor floor)
	{

		String outConf = "";
	
		if (m_AnomMode == 0)
		{
			floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.getMaxMF();
	
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputName;
			outConf += " is ";
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputFuzzy[(floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_NOutputFS - 1)
			                                                                         - floor.m_Zones[floor.m_SelectZone].m_FIS_Cluster.m_OutputMaxMemId].lingVal;
		} 
		else
		{
			floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.getMaxMF();
	
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputName;
			outConf += " is ";
			outConf += floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputFuzzy[(floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_NOutputFS - 1)
			                                                                       - floor.m_Zones[floor.m_SelectZone].m_FIS_Rules.m_OutputMaxMemId].lingVal;
		}

		return outConf;
	}
	
}
