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
************************************************************************/


//These functions load all the terms and dimensions for the fuzzy logic

#ifndef __FUZZYNAMELOADER_H__
#define __FUZZYNAMELOADER_H__

#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string.h>
#include <cstdio>
#include <fstream>
#include <sstream>

using namespace std;

//number of names for each type
int _FNL_num_dim_names = -1;
int _FNL_num_building_dims = 0;
int _FNL_num_zone_dims = 0;
int _FNL_num_ahu_dims = 0;
int _FNL_num_time_dims = 0;
int _FNL_num_hedge_names = -1;
int * _FNL_num_term_names;
int _FNL_num_out_term_names = -1;

//File names
string _FNL_dim_file_name   = "FL_DimInfo/dim_names.TXT";
string _FNL_hedge_file_name = "FL_DimInfo/hedge_names.TXT";
string * _FNL_term_file_name;
string _FNL_out_term_file_name = "FL_DimInfo/term_names_1.TXT";
string _FNL_fuzzy_set_info_file_name = "FL_DimInfo/fuzzy_set_info.TXT";
//dimensions
string * _FNL_dim_names = NULL;
string * _FNL_dim_names_short = NULL;
string * _FNL_dim_unit  = NULL;
string * _FNL_dim_type  = NULL;
string * _FNL_hedge_names = NULL;
string ** _FNL_term_names = NULL;
string * _FNL_out_term_names = NULL;

//function definitions
void _FNL_UpdateDimTypes();

//read the number of lines given the file name
int _FNL_readNumLines(string _file_name)
{
	//cout << _file_name << endl;
	ifstream in_file;
	in_file.open(_file_name);
	string line = "";
	int index = 0;

	if (in_file.is_open())
	{
		while(!in_file.eof())
		{
			index++;
			getline(in_file, line);
		}
		in_file.close();
	}
	return index;
}

//initialize the dimensions
void _FNL_initDim()
{
	_FNL_dim_names      = new string[_FNL_num_dim_names];
	_FNL_dim_names_short= new string[_FNL_num_dim_names];
	_FNL_dim_unit       = new string[_FNL_num_dim_names];
	_FNL_dim_type       = new string[_FNL_num_dim_names];
	_FNL_hedge_names    = new string[_FNL_num_hedge_names];
	_FNL_term_file_name	= new string[_FNL_num_dim_names];
	_FNL_num_term_names = new int[_FNL_num_dim_names];
	_FNL_term_names		= new string*[_FNL_num_dim_names];
	_FNL_out_term_names	= new string[_FNL_num_out_term_names];
}

//load dim names
void _FNL_LoadDim()
{
	ifstream in_file;
	in_file.open(_FNL_dim_file_name);
	string line = "";
	int var_index = 0;

	if (in_file.is_open())
	{
		while(!in_file.eof())
		{
			string dim_name = "";
			string dim_name_short = "";
			string dim_unit = "";
			string dim_type = "";
			string term_filename = "";

			getline(in_file, line);
			//get dim name
			int index = 0;
			index = line.find_first_of(",");
			dim_name = line.substr(0,index);
			line = line.substr(index+1);
			//get short dim name
			index = line.find_first_of(",");
			dim_name_short = line.substr(0,index);
			line = line.substr(index+1);
			//get unit
			index = line.find_first_of(",");
			dim_unit = line.substr(0,index);
			line = line.substr(index+1);
			//get type
			index = line.find_first_of(",");
			dim_type = line.substr(0,index);
			line = line.substr(index+1);
			//get term name filename
			index = line.find_first_of(",");
			term_filename = line.substr(0,index);
			line = line.substr(index+1);

			_FNL_dim_names[var_index] = dim_name;
			_FNL_dim_names_short[var_index] = dim_name_short;
			_FNL_dim_unit[var_index] = dim_unit;
			_FNL_dim_type[var_index] = dim_type;
			_FNL_term_file_name[var_index] = "FL_DimInfo/" + term_filename;

			var_index++;
		}
		in_file.close();
	}
}

//load hedge names
void _FNL_LoadHedge()
{
	ifstream in_file;
	in_file.open(_FNL_hedge_file_name);
	string line = "";
	int index = 0;

	if (in_file.is_open())
	{
		while(!in_file.eof())
		{
			getline(in_file, line);
			_FNL_hedge_names[index] = line;
			index++;
		}
		in_file.close();
	}
}

//load term all names
void _FNL_LoadTerm()
{
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		ifstream in_file;
		in_file.open(_FNL_term_file_name[i]);
		string line = "";
		int index = 0;

		if (in_file.is_open())
		{
			while(!in_file.eof())
			{
				getline(in_file, line);
				_FNL_term_names[i][index] = line;
				index++;
			}
			in_file.close();
		}
	}

	ifstream in_file;
	in_file.open(_FNL_out_term_file_name);
	string line = "";
	int index = 0;

	if (in_file.is_open())
	{
		while(!in_file.eof())
		{
			getline(in_file, line);
			_FNL_out_term_names[index] = line;
			index++;
		}
		in_file.close();
	}
}

//load everything
void _FNL_loadFLNames()
{
	_FNL_num_dim_names = _FNL_readNumLines(_FNL_dim_file_name);
	_FNL_num_hedge_names = _FNL_readNumLines(_FNL_hedge_file_name);
	_FNL_num_out_term_names = _FNL_readNumLines(_FNL_out_term_file_name);

	_FNL_initDim();
	_FNL_LoadDim();
	_FNL_LoadHedge();
	_FNL_UpdateDimTypes();

	//load the number of terms for each dimension
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		_FNL_num_term_names[i] = _FNL_readNumLines(_FNL_term_file_name[i]);
		_FNL_term_names[i] = new string[_FNL_num_term_names[i]];
	}

	_FNL_LoadTerm();
}

//update the number of dimensions fo each type
void _FNL_UpdateDimTypes()
{
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		if(_FNL_dim_type[i] == "zone")
		{
			_FNL_num_zone_dims++;
		}
		else if(_FNL_dim_type[i] == "time")
		{
			_FNL_num_time_dims++;
		}
		else if(_FNL_dim_type[i] == "building")
		{
			_FNL_num_building_dims++;
		}
		else if(_FNL_dim_type[i] == "ahu")
		{
			_FNL_num_ahu_dims++;
		}
	}
}

//get all the names of the dimensions
void _FNL_GetDimNames(string * _in)
{
	int index = 0;
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		_in[index] = _FNL_dim_names_short[i];
		index++;
	}
}

//get the short name of the dimensions of the given type
void _FNL_GetDimNamesShort(string * _in, string _type)
{
	int index = 0;
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		if(_FNL_dim_type[i] == _type)
		{
			_in[index] = _FNL_dim_names_short[i];
			index++;
		}	
	}
}

//get the unit of the dimensions of the given type
void _FNL_GetDimUnit(string * _in, string _type)
{
	int index = 0;
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		if(_FNL_dim_type[i] == _type)
		{
			_in[index] = _FNL_dim_unit[i];
			index++;
		}	
	}
}

// Takes the name of the dimension and returns its index
int _FNL_getDimIndex(char * dim)
{
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		if(strcmp(_FNL_dim_names[i].c_str(), dim) == 0)
			return i;
	}

	return -1;
}



// Takes the index of the dimension and returns its name
char * _FNL_getDimName(int dim)
{
	if(dim < _FNL_num_dim_names)
		return (char*)_FNL_dim_names[dim].c_str();

	return "unknown";
}

// Takes the name of the fuzzy set and returns its index for the sensor values
int _FNL_getTermIndex(char * dim, int _dim_index)
{
	for(int i = 0; i < _FNL_num_term_names[_dim_index]; i++)
	{
		if(strcmp(_FNL_term_names[_dim_index][i].c_str(), dim) == 0)
			return i;
	}

	return -1;
}

// Takes the index of the fuzzy set and returns its name for the sensors
char * _FNL_getTermName(int dim, int _dim_index)
{
	if(dim < _FNL_num_term_names[_dim_index])
		return (char*)_FNL_term_names[_dim_index][dim].c_str();

	return "unknown";
}

// Takes the index of the fuzzy set and returns its name for output
char * _FNL_getTermNameOut(int dim)
{
	if(dim < _FNL_num_out_term_names)
		return (char*)_FNL_out_term_names[dim].c_str();

	return "unknown";

}

// Takes the name of the fuzzy set and returns its index for Output
int _FNL_getTermIndexOut(char * dim)
{
	for(int i = 0; i < _FNL_num_out_term_names; i++)
	{
		if(strcmp(_FNL_out_term_names[i].c_str(), dim) == 0)
			return i;
	}

	return -1;
}

// Takes the name of the hedge and returns its index
int _FNL_getHedgeIndex(char * dim)
{
	for(int i = 0; i < _FNL_num_hedge_names; i++)
	{
		if(strcmp(_FNL_hedge_names[i].c_str(), dim) == 0)
			return i;
	}

	return -1;

}

// Takes the index of the hedge and returns its name
char * _FNL_getHedgeName(int dim)
{
	if(dim < _FNL_num_hedge_names)
		return (char*)_FNL_hedge_names[dim].c_str();

	return "unknown";
}
//print everything
void _FNL_print()
{
	cout << "Number of FNL dimensions = " << _FNL_num_dim_names << " :: Zone = " << _FNL_num_zone_dims << " :: AHU = " << _FNL_num_ahu_dims << " :: Building = " << _FNL_num_building_dims << endl;
	for(int i = 0; i < _FNL_num_dim_names; i++)
	{
		cout << _FNL_dim_names[i] << endl;
	}
	cout << endl;

	for(int i = 0; i < _FNL_num_hedge_names; i++)
	{
		//cout << _FNL_hedge_names[i] << endl;
	}
	cout << endl;
}

//Convert float to string
string _FNL_floatToString(float _in)
{
	stringstream ss (stringstream::in | stringstream::out);
	ss << _in;
	return ss.str();
}

//Convert int to string
string _FNL_intToString(int _in)
{
	stringstream ss (stringstream::in | stringstream::out);
	ss << _in;
	return ss.str();
}
#endif // __FUZZYNAMELOADER_H__