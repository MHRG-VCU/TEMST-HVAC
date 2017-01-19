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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Floor 
{
	public int m_Id;				// ID of the floor
	public int m_NZones;			// Number of occupants zones
	public Zone m_Zones[];			// Array of the zones
	public boolean m_HasWall;		// Boolean flags indicating if wall data has been provided and loaded
	public boolean m_HasFill;		// Boolean flags indicating if fill data has been provided and loaded
	public int m_SelectZone;		// id of the selected zone
	public float m_AvgTemp;			// Avg Temp
	public float m_AvgCol;			// Avg color
	public boolean  m_HasAnomaly_Cluster;	// Flag whether there is an anomaly present on given floor based on the clusters
	public float m_MaxAnomConf_Cluster;		// Highest anomaly confidence on the floor due to the cluster
	public boolean m_HasAnomaly_Rules;		// Flag whether there is an anomaly present on given floor based on the expert rules
	public float m_MaxAnomConf_Rules;		// Highest anomaly confidence on the floor due to the expert rules
	public boolean m_IsReady;				// Boolean flag determining whether this floor has data available
	public DataSource m_Data;	// Data source of given floor
	public int m_NAttr;				// Number of attributes
	String [] m_DataNames;			// Labels of the floor data
	String [] m_DataUnits;			// Units of the building data
	public float [] m_MinVal;		// Min and max attibutes values for the entire floor
	public float [] m_MaxVal;		// Min and max attibutes values for the entire floor
	public String flr_err;			// stores errors encountered by the floor
	
	public String temp;
	
	public Floor()
	{
		initValues();
	}
	
	public void initValues()
	{
		this.m_Id = 0;
		this.m_NZones = 0;
		this.m_Zones = null;

		this.m_HasWall = false;
		this.m_HasFill = false;		

		this.m_SelectZone = -1;

		this.m_AvgCol = 0;
		this.m_AvgTemp = 0;

		this.m_HasAnomaly_Cluster = false;
		this.m_MaxAnomConf_Cluster = 0.0f;

		this.m_HasAnomaly_Rules = false;
		this.m_MaxAnomConf_Rules = 0.0f;

		this.m_IsReady = false;

		this.m_NAttr = 8;

		this.m_DataNames = new String[this.m_NAttr];
		this.m_DataNames[0] = "CLT";
		this.m_DataNames[1] = "MAT";
		this.m_DataNames[2] = "RAT";
		this.m_DataNames[3] = "DMP";
		this.m_DataNames[4] = "ELD";
		this.m_DataNames[5] = "ECR";
		this.m_DataNames[6] = "SLD";
		this.m_DataNames[7] = "SCR";

		this.m_DataUnits = new String[this.m_NAttr];
		this.m_DataUnits[0] = "F";
		this.m_DataUnits[1] = "F";
		this.m_DataUnits[2] = "F";
		this.m_DataUnits[3] = "%";
		this.m_DataUnits[4] = "%";
		this.m_DataUnits[5] = "A";
		this.m_DataUnits[6] = "%";
		this.m_DataUnits[7] = "A";

		this.m_MinVal = new float[this.m_NAttr];	
		this.m_MaxVal = new float[this.m_NAttr];	
		
		this.flr_err = "";
	}
	
	// Floor init function
	void init()
	{
		this.compMidPoint();
		this.compAvgTemp(0);
	}
	
	public void readFills(int _id, String _file_name)
	{
		this.m_Id = _id;
		_file_name = "/sdcard/TEMST_App/Data/" + _file_name;
		try
		{
			File file = new File(_file_name);
			BufferedReader in_building_file = new BufferedReader(new FileReader(file));
			//read min max data
			String line = in_building_file.readLine();
			line = line.replaceAll(" ", "");
			StringTokenizer line_tok = new StringTokenizer(line);
			String temp = line_tok.nextToken(",");
			float min_max[] = new float[4];
			min_max[0] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[1] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[2] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[3] = (float)Double.parseDouble(temp);
			
			//read in number of zones
			line = in_building_file.readLine();
			line = line.replaceAll(" ", "");
			this.m_NZones = Integer.parseInt(line);
			//create zones
			this.m_Zones = new Zone[this.m_NZones];
			//load data into Zones
			for(int i = 0; i < this.m_NZones; i++)
			{
				line = in_building_file.readLine();
				line = line.replaceAll(" ", "");
				line_tok = new StringTokenizer(line);
				//zone ID
				temp = line_tok.nextToken(",");
				int z_id = Integer.parseInt(temp);
				temp = line_tok.nextToken(",");
				//number of polygons
				temp = temp.replaceAll(" ", "");
				int n_poly = Integer.parseInt(temp);
				//initialize the zones
				this.m_Zones[i] = new Zone(z_id,n_poly);
				//read in each polygon
				for(int n = 0; n < n_poly; n++)
				{
					line = in_building_file.readLine();
					line = line.replaceAll(" ", "");
					//load each polygon
					this.m_Zones[i].m_Poly[n] = new ZPoly(line,min_max);
				}
			}
			
			//this.m_NFloors = Integer.parseInt(temp);
			//out.write("output to the file");
			//out.close();
			
			//this.m_Floors = new Floor[this.m_NFloors];
			this.m_HasFill = true;
			this.m_SelectZone = -1;
			
		}
		catch (IOException e) 
		{
			this.flr_err = "Floor " + this.m_Id + " No fill " + _file_name + ", ";
    	}		
	}
	
	public void readWalls(String _file_name)
	{
		_file_name = "/sdcard/TEMST_App/Data/" + _file_name;
		try
		{
			File file = new File(_file_name);
			BufferedReader in_building_file = new BufferedReader(new FileReader(file));
			//read min max data
			String line = in_building_file.readLine();
			line = line.replaceAll(" ", "");
			StringTokenizer line_tok = new StringTokenizer(line);
			String temp = line_tok.nextToken(",");
			float min_max[] = new float[4];
			min_max[0] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[1] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[2] = (float)Double.parseDouble(temp);
			temp = line_tok.nextToken(",");
			min_max[3] = (float)Double.parseDouble(temp);
			
			float x_range = min_max[1] - min_max[0];
			float y_range = min_max[3] - min_max[2];
			
			//read in number of zones
			line = in_building_file.readLine();
			line = line.replaceAll(" ", "");
			this.m_NZones = Integer.parseInt(line);			
			
			//load data into Zones
			for(int i = 0; i < this.m_NZones; i++)
			{
				line = in_building_file.readLine();
				line = line.replaceAll(" ", "");
				line_tok = new StringTokenizer(line);
				//zone ID
				temp = line_tok.nextToken(",");
				//int z_id = Integer.parseInt(temp);
				//number of vertices
				temp = line_tok.nextToken(",");
				int n_vert = Integer.parseInt(temp);
				
				this.m_Zones[i].m_NVertex = n_vert;
				this.m_Zones[i].m_Vertex = new float[n_vert * 2];
				
				//read in the wall vertices
				boolean reading_x = true;
				for(int n = 0; n < n_vert * 2; n++)
				{
					temp = line_tok.nextToken(",");
					float value_temp = (float)Double.parseDouble(temp);
					if(reading_x)
					{
						this.m_Zones[i].m_Vertex[n] = (value_temp - min_max[0])/x_range;
					}
					else
					{
						this.m_Zones[i].m_Vertex[n] = (value_temp - min_max[2])/y_range;
					}
					reading_x = !reading_x;
				}
			}
			this.m_HasWall = true;
		}
		catch (IOException e) 
		{
			this.flr_err = "Floor " + this.m_Id + " No wall " + _file_name + ", ";
    	}		
	}
	
	// Reads the data source for each zone
	public void readZoneData(String _file_name, int _NData)
	{
		_file_name = "/sdcard/TEMST_App/Data/" + _file_name;
		try
		{
			File file = new File(_file_name);
			BufferedReader in_zone_temp_file = new BufferedReader(new FileReader(file));
			
			//initialize the data source objects for each zone
			for (int i = 0; i < this.m_NZones; i++)
			{
				this.m_Zones[i].m_Data = new DataSource(this.m_NAttr, _NData);
			}	
			
			for (int i = 0; i < _NData; i++)
			{
				String line = in_zone_temp_file.readLine();
				String [] line_split = line.split(",",-1);
				
				String date = line_split[0];
				
				//for each zone read in the data
				for (int j = 0; j < this.m_NZones; j++)
				{
					this.m_Zones[j].m_Data.m_Time[i] = date;
					// get the values from split (index 0 is the date)
					String value = line_split[j + 1];
					value = value.replaceAll(" ", "");
					// check if the value is missing, if so then reuse previous value
					if(value.equals(""))
					{
						//if there is a previous value use it
						if( (i - 1) >= 0)
						{
							this.m_Zones[j].m_Data.m_Val[0][i] = this.m_Zones[j].m_Data.m_Val[0][i - 1];
						}
						//if no previous value use default
						else
						{
							this.m_Zones[j].m_Data.m_Val[0][i] = 70;
						}
					}
					//otherwise read the value form file
					else
					{
						float vlaue_float = (float)Double.parseDouble(value);
						this.m_Zones[j].m_Data.m_Val[0][i] = vlaue_float;
					}
				}
				
			}
			
			// Also convert the string data and time to their float representations for all zones and extract the min and max of each zone data
			for (int j = 0; j < this.m_NZones; j++)
			{
				this.m_Zones[j].m_Data.extractTime();
				this.m_Zones[j].m_Data.extractMinMax();
			}
			// Extract the min and max values for the entire floor
			//extractMinMax();

		}
		catch (IOException e) 
		{
			this.flr_err = "Floor " + this.m_Id + " No zone " + _file_name + ", ";
    	}	
		
	}
	
	// Reads the data source for each zone
	public void readFloorData(String _file_name, int _NData)
	{
		_file_name = "/sdcard/TEMST_App/Data/" + _file_name;
		try
		{
			File file = new File(_file_name);
			BufferedReader in_floor_data_file = new BufferedReader(new FileReader(file));
			
			//number of attributes is set to 8
			this.m_Data = new DataSource(this.m_NAttr, _NData);
			
			for(int i = 0; i < _NData; i++)
			{
				String line = in_floor_data_file.readLine();
				String [] line_split = line.split(",",-1);
				
				String date = line_split[0];
				
				for (int j = 0; j < this.m_NAttr; j++)
				{
					this.m_Data.m_Time[i] = date;
					// get the values from split (index 0 is the date)
					String value = line_split[j + 1];
					value = value.replaceAll(" ", "");
					float vlaue_float = (float)Double.parseDouble(value);
					this.m_Data.m_Val[j][i] = vlaue_float;
				}
			}
			
			// Also convert the string data and time to their float representations for all zones and extract the min and max of each zone data
			this.m_Data.extractTime();
			this.m_Data.extractMinMax();	
			// Extract the min and max values for the entire floor
			extractMinMax();
		}
		catch (IOException e) 
		{
			this.flr_err = "Floor " + this.m_Id + " No floor " + _file_name + ", ";
    	}	
	}
	
	// Extract the Min and Max values for the entire floor
	public void extractMinMax()
	{
		for (int a = 0; a < this.m_NAttr; a++)
		{
			this.m_MinVal[a] = Float.MAX_VALUE;
			this.m_MaxVal[a] = Float.MIN_VALUE;

			for (int z = 0; z < this.m_NZones; z++)
			{
				this.m_MinVal[a] = min(this.m_MinVal[a], this.m_Zones[z].m_Data.m_MinVal[a]);
				this.m_MaxVal[a] = max(this.m_MaxVal[a], this.m_Zones[z].m_Data.m_MaxVal[a]);
			}
		}
	}
	
	void compMidPoint()
	{
		for(int i = 0; i < this.m_NZones; i++)
		{
			this.m_Zones[i].m_MidX = 0;
			this.m_Zones[i].m_MidY = 0;
			
			boolean select_x = true;
			for(int j = 0; j < this.m_Zones[i].m_Poly[0].m_NVertex*2; j++)
			{
				if(select_x)
				{
					this.m_Zones[i].m_MidX += this.m_Zones[i].m_Poly[0].m_Vertex[j];
				}
				else
				{
					this.m_Zones[i].m_MidY += this.m_Zones[i].m_Poly[0].m_Vertex[j];
				}
				select_x = !select_x;
			}
			this.m_Zones[i].m_MidX /= (this.m_Zones[i].m_Poly[0].m_NVertex);
			this.m_Zones[i].m_MidY /= (this.m_Zones[i].m_Poly[0].m_NVertex);
		}
	}
	
	// Calculates the average floor temperature
	void compAvgTemp(int _dataP)
	{
		this.m_AvgTemp = 0;

			for (int j = 0; j < this.m_NZones; j++)
			{
				this.m_AvgTemp += this.m_Zones[j].m_Data.m_Val[0][_dataP];
			}

			this.m_AvgTemp /= (float)this.m_NZones;
	}
	
	public float min(float a, float b)
	{
		float min = b;
		if(a < b)
			min = a;
		return min;
	}
	
	public float max(float a, float b)
	{
		float max = b;
		if(a > b)
			max = a;
		return max;
	}	
	
	
	// Returns the string with the file name for the normalization values for given building_floor_zone
    public static String getMinMaxFileName(int floorN, int zoneN, String buildingName)
    {
	      String outName = "Model/";
	
	      outName += buildingName;
	      outName += "/";
	      outName += "F" + floorN;
	      outName += "_";
	      outName += "Z" + zoneN;
	      outName += "_MinMax.txt";
	
	      return outName;
     }

     // Returns the string with the file name for the clusters for given building_floor_zone
     public static String getClusterFileName(int floorN, int zoneN, String buildingName)
     {
	      String outName = "Model/";
	
	      outName += buildingName;
	      outName += "/";
	      outName += "F" + floorN;
	      outName += "_";
	      outName += "Z" + zoneN;
	      outName += "_Cluster.txt";
	
	      return outName;
     }

  // Evaluates the normalcy of all zones based on the clusters
     public void evalZonesCluster(FVec fvec, int dataPoint, float threshold, int featureStart)
     {

    	 // Add the floor features
    	 for (int a = 0; a < m_NAttr; a++)
    	 {
    		 fvec.coord[featureStart + a] = m_Data.m_Val[a][dataPoint];
    	 }

    	 m_HasAnomaly_Cluster = false;
    	 m_MaxAnomConf_Cluster = 0.0f;

    	 // Evaluate each zone
    	 for (int i = 0; i < m_NZones; i++)
    	 {
    		 m_Zones[i].evalZoneCluster(fvec, dataPoint);

    		 if (m_Zones[i].m_AnomalI > threshold)
    		 {
    			 m_HasAnomaly_Cluster = true;
    		 }

    		 if (m_Zones[i].m_AnomalI > m_MaxAnomConf_Cluster)
    		 {
    			 m_MaxAnomConf_Cluster = m_Zones[i].m_AnomalI;
    		 }
    	 }
     }

     // Evaluates the normalcy of all zones based on the expert rules
     public void evalZonesRule(FVec fvec, int dataPoint, float threshold, int featureStart, FIS fis)
     {

    	 // Add the floor features
    	 for (int a = 0; a < m_NAttr; a++)
    	 {
    		 fvec.coord[featureStart + a] = m_Data.m_Val[a][dataPoint];
    	 }

    	 m_HasAnomaly_Rules = false;
    	 m_MaxAnomConf_Rules = 0.0f;
    	 // Evalute all zones
    	 for (int i = 0; i < m_NZones; i++){
    		 m_Zones[i].evalZoneRules(fvec, dataPoint, fis);

    		 if (m_Zones[i].m_AnomalI_Rules > threshold)
    		 {
    			 m_HasAnomaly_Rules = true;
    		 }

    		 if (m_Zones[i].m_AnomalI_Rules > m_MaxAnomConf_Rules)
    		 {
    			 m_MaxAnomConf_Rules = m_Zones[i].m_AnomalI_Rules;
    		 }
    	 }
     }

     // Updates the model of the normal behavior
     public void updateModel(FVec fvec, int dataPoint, int featureStart)
     {
	
	     // Add the floor features
	     for (int a = 0; a < m_NAttr; a++)
	     {
	    	 fvec.coord[featureStart + a] = m_Data.m_Val[a][dataPoint];
	     }
	
	     m_Zones[m_SelectZone].updateModel(fvec, dataPoint);
     }

     // Updates the normalcy of all zones
     public void evalUpdateZones(float threshold)
     {
	
	     m_HasAnomaly_Cluster = false;
	     m_MaxAnomConf_Cluster = 0.0f;
	     for (int i = 0; i < m_NZones; i++)
	     {
		     if (m_Zones[i].m_AnomalI > threshold)
		     {
		    	 m_HasAnomaly_Cluster = true;
		     }
		     if (m_Zones[i].m_AnomalI > m_MaxAnomConf_Cluster)
		     {
		    	 m_MaxAnomConf_Cluster = m_Zones[i].m_AnomalI;
		     }
	     }
     }

     // Loads the normal behavior models for each zone on the floor
     public void loadModel(float spread, float blur, String buildingName)
     {
    	 for (int i = 0; i < m_NZones; i++)
    	 {
    		 m_Zones[i].m_Alg.loadMinMax(getMinMaxFileName(m_Id, m_Zones[i].m_ID, buildingName));	
    		 m_Zones[i].m_Alg.initFLCFile(getClusterFileName(m_Id, m_Zones[i].m_ID, buildingName), spread, blur);
    	 }
     }




     
     //public final float FLT_MAX = 3.402823466e+38F;



}
