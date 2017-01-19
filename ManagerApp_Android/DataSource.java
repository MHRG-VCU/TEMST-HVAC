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

public class DataSource 
{
	// Number of attributes
	public int m_NAttr;
	// Number of values for each attributes
	public int m_NVal;
	// Array of data for each attributes
	public float [][] m_Val;
	// Array of the string representation of the date and time of the measurement
	public String [] m_Time;
	// Floating point representation of the time [days]
	public float [] m_TimeVal;
	// minimum values of each attribute
	public float [] m_MinVal;
	// maximum values of each attribute
	public float [] m_MaxVal;
	
	// Constructor
	public DataSource()
	{
		this.m_NAttr = 0;
		this.m_NVal = 0;
		this.m_Val = null;
		this.m_Time = null;
		this.m_TimeVal = null;
		this.m_MinVal = null;
		this.m_MaxVal = null;
	}
	
	// Constructor
	public DataSource(int _nAttr, int _nVal)
	{
		this.m_NAttr = _nAttr;
		this.m_NVal = _nVal;
		this.m_Val = new float[this.m_NAttr][];

		for (int i = 0; i < this.m_NAttr; i++)
		{
			this.m_Val[i] = new float[this.m_NVal];
		}

		this.m_Time = new String[this.m_NVal];
		this.m_TimeVal = new float[this.m_NVal];

		this.m_MinVal = new float[this.m_NAttr];
		this.m_MaxVal = new float[this.m_NAttr];
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
	
	// Extracts the min and max value of each attribute data
	public void extractMinMax()
	{
		for (int a = 0; a < this.m_NAttr; a++)
		{
			this.m_MinVal[a] = this.m_Val[a][0];
			this.m_MaxVal[a] = this.m_Val[a][0];
			for (int i = 0; i < this.m_NVal; i++)
			{
				this.m_MinVal[a] = min(this.m_MinVal[a], this.m_Val[a][i]);
				this.m_MaxVal[a] = max(this.m_MaxVal[a], this.m_Val[a][i]);
			}
		}
	}	
	

	// Extracts the floating point representation of time from the date and time string
	public void extractTime()
	{

		for (int i = 0; i < this.m_NVal; i++)
		{

			int index = this.m_Time[i].indexOf(" ");//.find_first_of(" ");
			String time = this.m_Time[i].substring(index + 1);

			index = time.indexOf(":");//find_first_of(":");
			float hour = (float) Double.parseDouble(time.substring(0, index));
			float minute = (float) Double.parseDouble(time.substring(index + 1));

			this.m_TimeVal[i] = (float) ((hour + minute / 60.0) / 24.0);
		}
	}	
	
	// Prints out the data source
	public String printOut()
	{
		String out = "";
		out += "DatasSource, NVal: " + this.m_NVal + "\n";
		for (int i = 0; i < this.m_NVal; i++)
		{
			out += this.m_Time[i] + " : " + this.m_TimeVal[i] + " : ";
			for (int j = 0; j < this.m_NAttr - 1; j++)
			{
				out += this.m_Val[j] + ", ";
			}

			out += this.m_Val[this.m_NAttr - 1][i] + "\n";
		}
		return out;
	}	

}
