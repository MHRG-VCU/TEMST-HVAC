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

public class Ant
{
	// Index of the dimension (attribute) of this antecedent
	int dimIndex;
	// Index of the fuzzy set of this antecedent
	int antIndex;
	// Index of the linguistic hedge if present
	int hedgeIndex;
	
	// Type of the antecedent - 0 = sensor, 1 = time
	int type;
	
	// Pointer to the next antecedent of the rule
	Ant next;
	
	// Constructor
	Ant()
	{
		dimIndex = 0;
		antIndex = 0;
		hedgeIndex = -1;
		
		type = 0;
		
		next = null;
	}
	
	// Print out of the antecedent class
	void printOut()
	{
		
		System.out.print(getDimName(dimIndex) + " IS ");
		
		if (hedgeIndex > -1)
		{
			System.out.print(getHedgeName(hedgeIndex) + " ");
		}
		
		if (type == 0)
		{
			System.out.print(getTermNameSensor(antIndex));
		} 
		else
		{
			System.out.print(getTermNameTime(antIndex));
		}
		
	}
	
	// Returns the string with the antecedent class
	String printString()
	{
		
		String outDesc = "";
		outDesc += getDimName(dimIndex);
		outDesc += " IS ";
		
		if (hedgeIndex > -1)
		{
			outDesc += getHedgeName(hedgeIndex);
			outDesc += " ";
		}
		
		if (type == 0)
		{
			outDesc += getTermNameSensor(antIndex);
		} 
		else
		{
			outDesc += getTermNameTime(antIndex);
		}
		
		return outDesc;
	}
		
	static int getDimIndex(String dim)
	{
		if (dim.equals("ZoneTemperature"))
		{
			return 0;
		} 
		else if (dim.equals("Time"))
		{
			return 1;
		} 
		else if (dim.equals("OutsideAirTemperature"))
		{
			return 2;
		} 
		else if (dim.equals("ChillerTemperature"))
		{
			return 3;
		} 
		else if (dim.equals("MixedAirTemperature"))
		{
			return 4;
		} 
		else if (dim.equals("ReturnAirTemperature"))
		{
			return 5;
		} 
		else if (dim.equals("DamperPosition"))
		{
			return 6;
		} 
		else if (dim.equals("ExhaustFanLoad"))
		{
			return 7;
		} 
		else if (dim.equals("ExhaustFanCurrent"))
		{
			return 8;
		} 
		else if (dim.equals("SupplyFanLoad"))
		{
			return 9;
		} 
		else if (dim.equals("SupplyFanCurrent"))
		{
			return 10;
		} 
		else
		{
			return -1;
		}
	}
	
	static String getDimName(int dim)
	{
		if (dim == 0)
		{
			return "ZoneTemperature";
		} 
		else if (dim == 1)
		{
			return "Time";
		} 
		else if (dim == 2)
		{
			return "OutsideAirTemperature";
		} 
		else if (dim == 3)
		{
			return "ChillerTemperature";
		} 
		else if (dim == 4)
		{
			return "MixedAirTemperature";
		} 
		else if (dim == 5)
		{
			return "ReturnAirTemperature";
		} 
		else if (dim == 6)
		{
			return "DamperPosition";
		} 
		else if (dim == 7)
		{
			return "ExhaustFanLoad";
		} 
		else if (dim == 8)
		{
			return "ExhaustFanCurrent";
		} 
		else if (dim == 9)
		{
			return "SupplyFanLoad";
		} 
		else if (dim == 10)
		{
			return "SupplyFanCurrent";
		} 
		else
		{
			return "Unknown";
		}
	}
	
	// Takes the index of the hedge and returns its name
	static String getHedgeName(int dim)
	{
		if (dim == 1)
		{
			return "very";
		} 
		else if (dim == 2)
		{
			return "kinda";
		} 
		else
		{
			return "Unknown";
		}
	}
	
	// Takes the name of the fuzzy set and returns its index for the sensor values
	static int getTermIndexSensor(String dim)
	{
		if (dim.equals("low"))
		{
			return 0;
		} 
		else if (dim.equals("lower"))
		{
			return 1;
		} 
		else if (dim.equals("medium"))
		{
			return 2;
		} 
		else if (dim.equals("higher"))
		{
			return 3;
		} 
		else if (dim.equals("high"))
		{
			return 4;
		} 
		else
		{
			return -1;
		}
	}
	
	// Takes the name of the fuzzy set and returns its index for time
	static int getTermIndexTime(String dim)
	{
		if (dim.equals("night"))
		{
			return 0;
		} 
		else if (dim.equals("morning"))
		{
			return 1;
		} 
		else if (dim.equals("noon"))
		{
			return 2;
		} 
		else if (dim.equals("afternoon"))
		{
			return 3;
		} 
		else if (dim.equals("evening"))
		{
			return 4;
		} 
		else
		{
			return -1;
		}
	}
	
	// Takes the name of the hedge and returns its index
	static int getHedgeIndex(String dim)
	{
		if (dim.equals("very"))
		{
			return 1;
		} 
		else if (dim.equals("kinda"))
		{
			return 2;
		} 
		else
		{
			return -1;
		}
	}
	
	// Takes the index of the fuzzy set and returns its name for the sensors
	static String getTermNameSensor(int dim)
	{
		if (dim == 0)
		{
			return "low";
		} 
		else if (dim == 1)
		{
			return "lower";
		} 
		else if (dim == 2)
		{
			return "medium";
		} 
		else if (dim == 3)
		{
			return "higher";
		} 
		else if (dim == 4)
		{
			return "high";
		} 
		else
		{
			return "Unknown";
		}
	}
	
	// Takes the index of the fuzzy set and returns its name for time
	static String getTermNameTime(int dim)
	{
		if (dim == 0)
		{
			return "night";
		} 
		else if (dim == 1)
		{
			return "morning";
		} 
		else if (dim == 2)
		{
			return "noon";
		} 
		else if (dim == 3)
		{
			return "afternoon";
		} 
		else if (dim == 4)
		{
			return "evening";
		} 
		else
		{
			return "Unknown";
		}
	}
}
