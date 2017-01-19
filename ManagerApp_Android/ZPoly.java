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

import java.util.StringTokenizer;

public class ZPoly 
{
	public int m_NVertex;		// Number of vertexes
	public float m_Vertex[];	// Arrays of the X and Y coordinates
	
	public ZPoly()
	{
		this.m_NVertex = 0;
		this.m_Vertex = null;
	}
	
	//intitializes the vertices and normalizes them
	public ZPoly(String _data, float[] _min_max)
	{
		float x_range = _min_max[1] - _min_max[0];
		float y_range = _min_max[3] - _min_max[2];
		StringTokenizer line_tok = new StringTokenizer(_data);
		String temp = line_tok.nextToken(",");
		int n_vert = Integer.parseInt(temp);
		this.m_NVertex = n_vert;
		this.m_Vertex = new float[this.m_NVertex * 2];
		
		boolean reading_x = true;
		for(int i = 0; i < this.m_NVertex * 2; i++)
		{
			temp = line_tok.nextToken(",");
			if(reading_x)
			{
				this.m_Vertex[i] = ((float)Double.parseDouble(temp) - _min_max[0]) / x_range;
			}
			else
			{
				this.m_Vertex[i] = ((float)Double.parseDouble(temp) - _min_max[2]) / y_range;
			}
			reading_x = !reading_x;
		}
	}

}

