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
// FLC_IT2.cpp
// Date:  2012/2/17
// Author: Ondrej Linda, UI
//---------------------------------------------------------------------------

#include "FLC_IT2.h"

CFuzzySetIT2::CFuzzySetIT2(){
	this->mean = 0.0f;
	this->stdL_U = 0.0f;
	this->stdL_L = 0.0f;
	this->stdR_U = 0.0f;
	this->stdR_L = 0.0f;

	this->blur = 0.3f;		 
		
	this->member_deg_L = 0.0f;		
	this->member_deg_U = 0.0f;		
}

void CFuzzySetIT2::setValues(float _mean, float _stdL, float _stdR, float spread, float _blur){
	this->blur = _blur;
	this->mean = _mean;
	this->stdL_L = fabs(_mean - _stdL) * (1 - this->blur) * spread;
	this->stdL_U = fabs(_mean - _stdL) * (1 + this->blur) * spread;
		
	this->stdR_L = fabs(_stdR - _mean) * (1 - this->blur) * spread;
	this->stdR_U = fabs(_stdR - _mean) * (1 + this->blur) * spread;
}

void CFuzzySetIT2::getMembership(float value){	
	
	// Check if the left or the right standard deviation should be used	
	if (value == this->mean){
		this->member_deg_L = 1.0f;
		this->member_deg_U = 1.0f;
	}
	else if (value < this->mean){
		if (this->stdL_L > 0.0){
			float deg_0_L = (1.0f / (this->stdL_L * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / 
				(2 * this->stdL_L * this->stdL_L));			

			this->member_deg_L = (1.0f / (this->stdL_L * (float)sqrt(2*M_PI))) * exp(-(value - this->mean) * (value - this->mean) / 
				(2 * this->stdL_L * this->stdL_L)) / deg_0_L;			
		}
		else{
			this->member_deg_L = 0.0f;			
		}

		if (this->stdL_U > 0.0f){
			
			float deg_0_U = (1.0f / (this->stdL_U * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / 
				(2 * this->stdL_U * this->stdL_U));
			
			this->member_deg_U = (1.0f / (this->stdL_U * (float)sqrt(2*M_PI))) * exp(-(value - this->mean) * (value - this->mean) / 
				(2 * this->stdL_U * this->stdL_U)) / deg_0_U;
		}
		else{		
			this->member_deg_U = 0.0;
		}

	}
	else {
		if (this->stdR_L > 0.0){
			float deg_0_L = (1.0f / (this->stdR_L * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / 
				(2 * this->stdR_L * this->stdR_L));

			this->member_deg_L =  (1.0f / (this->stdR_L * (float)sqrt(2*M_PI))) * exp(-(value - this->mean) * (value - this->mean) / 
				(2 * this->stdR_L * this->stdR_L)) / deg_0_L;
		}
		else{
			this->member_deg_L = 0.0;
		}

		if (this->stdR_U > 0.0){
			float deg_0_U = (1.0f / (this->stdR_U * (float)sqrt(2*M_PI))) * exp(-(this->mean - this->mean) * (this->mean - this->mean) / 
				(2 * this->stdR_U * this->stdR_U));

			this->member_deg_U =  (1.0f / (this->stdR_U * (float)sqrt(2*M_PI))) * exp(-(value - this->mean) * (value - this->mean) / 
				(2 * this->stdR_U * this->stdR_U)) / deg_0_U;
		}
		else{
			this->member_deg_U = 0.0;
		}
	}	
}


void CFuzzySetIT2::printOut(){	
	cout << this->mean << " , " << this->stdL_L << " , " << this->stdL_U << " , " << this->stdR_L << " , " << this->stdR_U << endl;
}



CFuzzySetIT2Tri::CFuzzySetIT2Tri(){
	this->mean = 0.0f;
	this->left_U = 0.0f;
	this->left_L = 0.0f;
	this->right_U = 0.0f;
	this->right_L = 0.0f;

	this->blur = 0.3f;		 
		
	this->member_deg_L = 0.0f;		
	this->member_deg_U = 0.0f;		
}

void CFuzzySetIT2Tri::setValues(float _mean, float _left, float _right, float spread, float _blur){
	this->blur = _blur;
	this->mean = _mean;
	this->left_L = fabs(_mean - _left) * (1 - this->blur) * spread;
	this->left_U = fabs(_mean - _left) * (1 + this->blur) * spread;
		
	this->right_L = fabs(_right - _mean) * (1 - this->blur) * spread;
	this->right_U = fabs(_right - _mean) * (1 + this->blur) * spread;
}

void CFuzzySetIT2Tri::getMembership(float value){	
	
	// Check if the left or the right standard deviation should be used	
	if (value == this->mean){
		this->member_deg_L = 1.0;
		this->member_deg_U = 1.0;
	}
	else if (value < this->mean){

		if (this->left_L > 0.0){
			if (value < (this->mean - this->left_L)){
				this->member_deg_L = 0.0;			
			}
			else{
				this->member_deg_L = (value - (this->mean - this->left_L)) / (this->left_L);
			}
		}
		else{
			this->member_deg_L = 0.0;			
		}


		if (this->left_U > 0.0){
			if (value < (this->mean - this->left_U)){
				this->member_deg_U = 0.0;			
			}
			else{
				this->member_deg_U = (value - (this->mean - this->left_U)) / (this->left_U);
			}
		}
		else{		
			this->member_deg_U = 0.0;
		}

	}
	else {
		if (this->right_L > 0.0){
			if (value > (this->mean + this->right_L)){
				this->member_deg_L = 0.0;			
			}
			else{
				this->member_deg_L = ((this->mean + this->right_L) - value) / (this->right_L);
			}
		}
		else{
			this->member_deg_L = 0.0;			
		}


		if (this->right_U > 0.0){
			if (value > (this->mean + this->right_U)){
				this->member_deg_U = 0.0;			
			}
			else{
				this->member_deg_U = ((this->mean + this->right_U) - value) / (this->right_U);
			}
		}
		else{		
			this->member_deg_U = 0.0;
		}
	}	
}


void CFuzzySetIT2Tri::printOut(){	
	cout << this->mean << " , " << this->left_L << " , " << this->left_U << " , " << this->right_L << " , " << this->right_U << endl;
}



CFuzzyIT2Rule::CFuzzyIT2Rule(){
	this->dim = 0;
	this->output = 0;

	this->deg_L = 0;
	this->deg_U = 0;

	this->fuzzyInput = NULL;
}

CFuzzyIT2Rule::~CFuzzyIT2Rule(){
	if (this->fuzzyInput != NULL){
		delete [] this->fuzzyInput;
	}
}

void CFuzzyIT2Rule::init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread, float blur){
	this->dim = _dim;

	this->output = _output;

	this->fuzzyInput = new CFuzzySetIT2[this->dim];

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread, blur);
	}
}
		
void CFuzzyIT2Rule::getOutput(float * input){		

	this->deg_L = 1000;
	this->deg_U = 1000;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].getMembership(input[i]);

		if (this->fuzzyInput[i].member_deg_L < this->deg_L){
			this->deg_L = this->fuzzyInput[i].member_deg_L;
		}

		if (this->fuzzyInput[i].member_deg_U < this->deg_U){
			this->deg_U = this->fuzzyInput[i].member_deg_U;
		}

	}
}

void CFuzzyIT2Rule::printOut(){
	cout << "Rule: " << endl;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].printOut();
	}

	cout << "Output: " << this->output << endl;
}


CFuzzyIT2RuleTri::CFuzzyIT2RuleTri(){
	this->dim = 0;
	this->output = 0;

	this->deg_L = 0;
	this->deg_U = 0;

	this->fuzzyInput = NULL;
}

CFuzzyIT2RuleTri::~CFuzzyIT2RuleTri(){
	if (this->fuzzyInput != NULL){
		delete [] this->fuzzyInput;
	}
}

void CFuzzyIT2RuleTri::init(int _dim, float * values, float * spreadL, float * spreadR, float _output, float spread, float blur){
	this->dim = _dim;

	this->output = _output;

	this->fuzzyInput = new CFuzzySetIT2Tri[this->dim];

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].setValues(values[i], spreadL[i], spreadR[i], spread, blur);
	}
}
		
void CFuzzyIT2RuleTri::getOutput(float * input){		

	this->deg_L = 1000;
	this->deg_U = 1000;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].getMembership(input[i]);

		if (this->fuzzyInput[i].member_deg_L < this->deg_L){
			this->deg_L = this->fuzzyInput[i].member_deg_L;
		}

		if (this->fuzzyInput[i].member_deg_U < this->deg_U){
			this->deg_U = this->fuzzyInput[i].member_deg_U;
		}

	}
}

void CFuzzyIT2RuleTri::printOut(){
	cout << "Rule: " << endl;

	for (int i = 0; i < this->dim; i++){
		this->fuzzyInput[i].printOut();
	}

	cout << "Output: " << this->output << endl;
}



CFLCIT2::CFLCIT2(bool _memberMode){

	this->dim = 0;
	this->N_Rules = 0;

	this->tri = _memberMode;

	this->rules = NULL;
	this->rulesTri = NULL;
}	

CFLCIT2::CFLCIT2(int _dim, int _N_Rules, bool _memberMode){

	this->dim = _dim;
	this->N_Rules = _N_Rules;

	this->tri = _memberMode;

	this->rules = new CFuzzyIT2Rule[this->N_Rules];
	this->rulesTri = new CFuzzyIT2RuleTri[this->N_Rules];
}	

CFLCIT2::~CFLCIT2(){
	if (this->rules != NULL){
		delete [] this->rules;
	}

	if (this->rulesTri != NULL){
		delete [] this->rulesTri;
	}
}

void CFLCIT2::setRule(int index, float * values, float * spreadL, float * spreadR, float output, float spread, float blur){
	this->rules[index].init(this->dim, values, spreadL, spreadR, output, spread, blur);
	this->rulesTri[index].init(this->dim, values, spreadL, spreadR, output, spread, blur);
}

float CFLCIT2::evalOut(FVec * input){

	// First find the conorm of the FOU of individual rules
	maxDeg_L = 0.0;
	maxDeg_U = 0.0;	

	for (int i = 0; i < this->N_Rules; i++){

		if (this->tri){
			this->rulesTri[i].getOutput(input->coord);

			if (this->rulesTri[i].deg_L > maxDeg_L){
				maxDeg_L = this->rulesTri[i].deg_L;
			}

			if (this->rulesTri[i].deg_U > maxDeg_U){
				maxDeg_U = this->rulesTri[i].deg_U;
			}
		}
		else{
			this->rules[i].getOutput(input->coord);

			if (this->rules[i].deg_L > maxDeg_L){
				maxDeg_L = this->rules[i].deg_L;
			}

			if (this->rules[i].deg_U > maxDeg_U){
				maxDeg_U = this->rules[i].deg_U;
			}
		}
	}

	return (maxDeg_L + maxDeg_U) / 2.0f;	
}

void CFLCIT2::printOut(){
	cout << "Rules: " << this->N_Rules << endl;
	for (int i = 0; i < this->N_Rules; i++){
		if (this->tri){
			this->rulesTri[i].printOut();
		}
		else{
			this->rules[i].printOut();
		}
	}
}