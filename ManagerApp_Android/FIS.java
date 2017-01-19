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

import java.io.FileReader;
import java.util.Scanner;

public class FIS
{
	// Pointer to a set of fuzzy rules
	public Rule myRules[];
	
	// Stores the number of rules
	public int rulesN;
	
	// Discretization resolution of the output dimension
	int outN;
	
	// Constructor 1
	FIS()
	{
		rulesN = 0;
		myRules = null;
		outN = 0;
	}
	
	// Constructor 2
	FIS(int _N)
	{
		rulesN = 0;
		myRules = null;
		outN = _N;
	}
	
	// Takes an input file and parse the text description into fuzzy rules
	// TODO: Improve error handling
	public void loadRules(String file)
	{
		try
		{
			Scanner in = new Scanner(new FileReader(file));
			//Scanner in = new Scanner(Gdx.files.internal(file).reader());
	
			String buf;
	
			// First get the number of rules
			rulesN = in.nextInt();
	
			//System.out.println("Reading " + rulesN + " rules. ");
	
			// Initialize the rules
			myRules = new Rule[rulesN];
	
			for (int i = 0; i < rulesN; i++)
			{
				// Read the if part
				in.next();
	
				myRules[i] = new Rule();
				// Init the rule
				myRules[i].setup(outN);
	
				// read the next part, untill it is THEN codeword, we keep reading the
				// antecedent
				buf = in.next();
	
				while (!buf.equals("THEN"))
				{
					// Start new antecedent
					Ant a = new Ant();
					myRules[i].addAnt(a);
					if (buf.equals("AND"))
					{
						buf = in.next();
					}
	
					a.dimIndex = Ant.getDimIndex(buf); // Check if this is the time
					// antecedent
					if (a.dimIndex == 1)
					{
						a.type = 1;
					}
	
					// skip the IS part
					in.next();
	
					buf = in.next();
					// Test if a hedge was used before the term
					int hedge = Ant.getHedgeIndex(buf);
					if (hedge > -1)
					{
						a.hedgeIndex = hedge;
						// move to the antecedent
						buf = in.next();
					}
	
					// Get the antecedent index
					if (a.dimIndex == 1)
					{
						a.antIndex = Ant.getTermIndexTime(buf);
					} 
					else
					{
						a.antIndex = Ant.getTermIndexSensor(buf);
					}
	
					buf = in.next();
				}
	
				// Skip the anomaly part
				in.next();
	
				// Print out the rule as a check
				myRules[i].printOut();
			}
		} 
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	//TODO
	public String loadRules2(String file)
	{
		try
		{
			
			Scanner in = new Scanner(new FileReader(file));
			//Scanner in = new Scanner(Gdx.files.internal(file).reader());
	
			String buf;
	
			// First get the number of rules
			rulesN = in.nextInt();
	
			//System.out.println("Reading " + rulesN + " rules. ");
	
			// Initialize the rules
			//myRules = new Rule[rulesN];
			
			return Integer.toHexString(rulesN);
	
		} 
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		return "";
	}	
	public void loadRulesFromString(String rules)
	{
		try
		{
			String[] lines = rules.split("\n");
			rulesN = lines.length;
			myRules = new Rule[rulesN];
			String buf;
			for(int i = 0; i < rulesN; ++i)
			{
				Scanner in = new Scanner(lines[i]);
				// Read the if part
				in.next();
		
				myRules[i] = new Rule();
				// Init the rule
				myRules[i].setup(outN);
	
				// read the next part, untill it is THEN codeword, we keep reading the
				// antecedent
				buf = in.next();
	
				while (!buf.equals("THEN"))
				{
					// Start new antecedent
					Ant a = new Ant();
					myRules[i].addAnt(a);
					if (buf.equals("AND"))
					{
						buf = in.next();
					}
	
					a.dimIndex = Ant.getDimIndex(buf); // Check if this is the time
					// antecedent
					if (a.dimIndex == 1)
					{
						a.type = 1;
					}
	
					// skip the IS part
					in.next();
	
					buf = in.next();
					// Test if a hedge was used before the term
					int hedge = Ant.getHedgeIndex(buf);
					if (hedge > -1)
					{
						a.hedgeIndex = hedge;
						// move to the antecedent
						buf = in.next();
					}
	
					// Get the antecedent index
					if (a.dimIndex == 1)
					{
						a.antIndex = Ant.getTermIndexTime(buf);
					} 
					else
					{
						a.antIndex = Ant.getTermIndexSensor(buf);
					}
	
					buf = in.next();
				}
	
				// Skip the anomaly part
				in.next();
	
				// Print out the rule as a check
				myRules[i].printOut();
			}
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	// Reloads the fuzzy rule base from the provided text file
	void reloadRules(String file)
	{
		loadRules(file);
	}
	
	// Prints out the set of fuzzy rules
	void printOut()
	{
		System.out.println("*************************");
		System.out.println("    FIS Rule base ");
		for (int i = 0; i < rulesN; i++)
		{
			System.out.print(i + ": ");
			myRules[i].printOut();
		}
	}
}

