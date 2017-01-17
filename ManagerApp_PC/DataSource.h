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
// DataSource.h
// Implements data structures associated with storing data source
// Date:  4/13/2012
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#ifndef __DATASOURCE_H__
#define __DATASOURCE_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string.h>
#include <cstdio>

using namespace std;

// Class implementing a data source
class C_DataSource{
public:
	// Number of attributes
	int m_NAttr;
	// Number of values for each attributes
	int m_NVal;
	// Array of data for each attributes
	float ** m_Val;
	// Array of the string representation of the date and time of the measurement
	string * m_Time;
	// Floating point representation of the time [days]
	float * m_TimeVal;
	// minimum values of each attribute
	float * m_MinVal;
	// maximum values of each attribute
	float * m_MaxVal;
public:

	// Constructor
	C_DataSource(){
		this->m_NAttr = 0;
		this->m_NVal = 0;
		this->m_Val = NULL;
		this->m_Time = NULL;
		this->m_TimeVal = NULL;
		this->m_MinVal = NULL;
		this->m_MaxVal = NULL;
	}

	// Constructor
	C_DataSource(int _nAttr, int _nVal)
	{
		this->m_NAttr = _nAttr;
		this->m_NVal = _nVal;
		this->m_Val = new float*[this->m_NAttr];

		for (int i = 0; i < this->m_NAttr; i++)
		{
			this->m_Val[i] = new float[this->m_NVal];
		}

		this->m_Time = new string[this->m_NVal];
		this->m_TimeVal = new float[this->m_NVal];

		this->m_MinVal = new float[this->m_NAttr];
		this->m_MaxVal = new float[this->m_NAttr];
	}

	// Extracts the min and max value of each attribute data
	void C_DataSource::extractMinMax()
	{
		for (int a = 0; a < this->m_NAttr; a++)
		{
			this->m_MinVal[a] = this->m_Val[a][0];
			this->m_MaxVal[a] = this->m_Val[a][0];

			for (int i = 0; i < this->m_NVal; i++)
			{
				this->m_MinVal[a] = min(this->m_MinVal[a], this->m_Val[a][i]);
				this->m_MaxVal[a] = max(this->m_MaxVal[a], this->m_Val[a][i]);
			}
		}
	}

	// Extracts the floating point representation of time from the date and time string
	void C_DataSource::extractTime(){

		for (int i = 0; i < this->m_NVal; i++){

			int index = this->m_Time[i].find_first_of(" ");
			string time = this->m_Time[i].substr(index + 1);

			index = time.find_first_of(":");
			float hour = atof(time.substr(0, index).c_str());
			float minute = atof(time.substr(index + 1).c_str());

			this->m_TimeVal[i] = (hour + minute / 60.0) / 24.0;
		}
	}

	// Prints out the data source
	void C_DataSource::printOut(){
		cout << "DatasSource, NVal: " << this->m_NVal << endl;
		for (int i = 0; i < this->m_NVal; i++){
			cout << this->m_Time[i].c_str() << " : " << this->m_TimeVal[i] << " : ";
			for (int j = 0; j < this->m_NAttr - 1; j++){
				cout << this->m_Val[j] << ", ";
			}

			cout << this->m_Val[this->m_NAttr - 1][i] << endl;
		}
	}
};

#endif // __DATASOURCE_H__