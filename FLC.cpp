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
// FLC.cpp
// Date:  2012/2/17
// Author: Ondrej Linda, UI
//---------------------------------------------------------------------------

#include "FLC.h"

CFuzzySetT1::CFuzzySetT1(){
	this->mean = 0.0;
	this->stdL = 0.0;
	this->stdR = 0.0;

	this->deg0L = 0.0;
	this->deg0R = 0.0;

	this->auxL = 0.0;
	this->auxR = 0.0;
		
	this->member_deg = 0.0;		
}

void CFuzzySetT1::setValues(float _mean, float _stdL, float _stdR, float spread){
	this->mean = _mean;
	this->stdL = fabs(_mean - _stdL) * spread;		
	this->stdR = fabs(_stdR - _mean) * spread;

	this->deg0L = (1.0f / (this->stdL * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / (2 * this->stdL * this->stdL));
	this->deg0R = (1.0f / (this->stdR * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / (2 * this->stdR * this->stdR));

	this->auxL = (1.0f / (this->stdL * (float)sqrt(2*M_PI)));
	this->auxR = (1.0f / (this->stdR * (float)sqrt(2*M_PI)));

}

float CFuzzySetT1::getMembership(float value){
	
	// Check if the left or the right standard deviation should be used
	float deg;

	if (value == this->mean){
		deg =  1.0;
	}
	else if (value < this->mean){
		if (this->stdL > 0.0001){			
			deg = this->auxL * exp(-(value - this->mean) * (value - this->mean) / (2 * this->stdL * this->stdL)) / this->deg0L;
		}
		else{
			if (fabs(value - this->mean) < 0.0001){			
				deg = 1.0;
			}
			else{
				deg = 0.0;
			}
		}
	}
	else {
		if (this->stdR > 0.0001){			

			deg =  this->auxR * exp(-(value - this->mean) * (value - this->mean) / (2 * this->stdR * this->stdR)) / this->deg0R;
		}
		else{
			if (fabs(value - this->mean) < 0.0001){
				deg = 1.0;
			}
			else{
				deg = 0.0;
			}
		}
	}	

	this->member_deg = deg;

	return deg;	
}


void CFuzzySetT1::printOut(){	
	cout << this->mean << " , " << this->stdL << " , " << this->stdR << endl;
}

CFuzzySetT1Tri::CFuzzySetT1Tri(){
	this->mean = 0.0;
	this->left = 0.0;
	this->right = 0.0;
		
	this->member_deg = 0.0;		
}

void CFuzzySetT1Tri::setValues(float _mean, float _left, float _right, float spread){
	this->mean = _mean;
	this->left = (_mean - _left) * spread;
	this->right = (_right - _mean) * spread;
}

float CFuzzySetT1Tri::getMembership(float value){
	
	// Check if the left or the right standard deviation should be used
	float deg;
	if (value == this->mean){
		deg =  1.0;
	}
	else if (value < this->mean){
		if (this->left > 0.0){
			if (value < (this->mean - this->left)){
				deg = 0;
			}
			else{
				deg = (value - (this->mean - this->left)) / this->left;
			}			
		}
		else{
			deg = 0.0;
		}
	}
	else {
		if (this->right > 0.0){
			if (value > (this->mean + this->right)){
				deg = 0;
			}
			else{
				deg = ((this->mean + this->right) - value) / this->right;
			}			
		}
		else{
			deg = 0.0;
		}
	}

	return deg;	
}


void CFuzzySetT1Tri::printOut(){	
	cout << this->mean << " , " << this->left << " , " << this->right << endl;
}

CFuzzyT1Rule::CFuzzyT1Rule(){
	this->dim = 0;
	this->output = 0;

	this->fuzzyInput = NULL;
}

CFuzzyT1Rule::~CFuzzyT1Rule(){
	if (this->fuzzyInput != NULL){
		delete [] this->fuzzyInput;
	}
}

void CFuzzyT1Rule::init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread){
	this->dim = _dim;

	this->output = _output;

	this->fuzzyInput = new CFuzzySetT1[this->dim];

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread);
	}
}
		
float CFuzzyT1Rule::getOutput(float * input, bool * AttrUse){
	
	float minDeg = 1000;

	float deg; 

	for (int i = 0; i < this->dim; i++){		
		if (AttrUse[i]){
			deg = this->fuzzyInput[i].getMembership(input[i]);				
		
			if (deg < minDeg){
				minDeg = deg;
			}
		}
	}	
	
	if (minDeg == 1000){
		return 1.0;
	}
	else{
		return minDeg;
	}
}


void CFuzzyT1Rule::printOut(){
	cout << "Rule: " << endl;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].printOut();
	}

	cout << "Output: " << this->output << endl;
}

CFuzzyT1RuleTri::CFuzzyT1RuleTri(){
	this->dim = 0;
	this->output = 0;

	this->fuzzyInput = NULL;
}

CFuzzyT1RuleTri::~CFuzzyT1RuleTri(){
	if (this->fuzzyInput != NULL){
		delete [] this->fuzzyInput;
	}
}

void CFuzzyT1RuleTri::init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread){
	this->dim = _dim;

	this->output = _output;

	this->fuzzyInput = new CFuzzySetT1Tri[this->dim];

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread);
	}
}
		
float CFuzzyT1RuleTri::getOutput(float * input, bool * AttrUse){
	
	float minDeg = 1000;

	float deg; 

	for (int i = 0; i < this->dim; i++){
		if (AttrUse[i]){
			deg = this->fuzzyInput[i].getMembership(input[i]);
		
			if (deg < minDeg){
				minDeg = deg;
			}
		}
	}

	if (minDeg == 1000){
		return 1.0;
	}
	else{
		return minDeg;
	}
}

void CFuzzyT1RuleTri::printOut(){
	cout << "Rule: " << endl;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].printOut();
	}

	cout << "Output: " << this->output << endl;
}


CFLC1::CFLC1(bool _memberMode){

	this->dim = 0;
	this->N_Rules = 0;

	this->rules = NULL;
	this->rulesTri = NULL;

	this->tri = _memberMode;

	this->maxRuleMF = NULL;
}	

CFLC1::CFLC1(int _dim, int _N_Rules, bool _memberMode){

	this->dim = _dim;
	this->N_Rules = _N_Rules;

	this->rules = new CFuzzyT1Rule[this->N_Rules];
	this->rulesTri = new CFuzzyT1RuleTri[this->N_Rules];

	this->tri = _memberMode;

	this->maxRuleMF = new float[this->dim];
}

CFLC1::~CFLC1(){
	if (this->rules != NULL){
		delete [] this->rules;
	}

	if (this->rulesTri != NULL){
		delete [] this->rulesTri;
	}
}

void CFLC1::setRule(int index, float * values, float * spreadL, float * spreadR, float output, float spread){
	this->rules[index].init(this->dim, values, spreadL, spreadR, output, spread);
	this->rulesTri[index].init(this->dim, values, spreadL, spreadR, output, spread);
}

float CFLC1::evalOut(FVec * input){

	float maxDeg = 0.0;
	float outDeg;

	for (int i = 0; i < this->N_Rules; i++){
		if (this->tri){
			outDeg = this->rulesTri[i].getOutput(input->coord, input->on);
		}
		else{
			outDeg = this->rules[i].getOutput(input->coord, input->on);			
		}
	
		if (outDeg > maxDeg){
			maxDeg = outDeg;

			for (int j = 0; j < this->dim; j++){
				if (this->tri){
					this->maxRuleMF[j] = this->rulesTri[i].fuzzyInput[j].member_deg;					
				}
				else{
					this->maxRuleMF[j] = this->rules[i].fuzzyInput[j].member_deg;
				}
			}
		}		
	}

	return maxDeg;
	
/*
	float SumWeight = 0;
	float Sum = 0;

	for (int i = 0; i < this->N_Rules; i++){
		float weight = this->rules[i].getOutput(input->coord);

		Sum += weight * this->rules[i].output;
		SumWeight += weight;
	}

	if (SumWeight == 0.0){
		return 0.0;
	}
	else{
		//return Sum / SumWeight;
		return SumWeight;
	}
	*/
}

void CFLC1::printOut(){
	cout << "Rules: " << this->N_Rules << endl;
	for (int i = 0; i < this->N_Rules; i++){
		this->rules[i].printOut();
	}
}