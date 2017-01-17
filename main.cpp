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

//---------------------------------------------------------------------------
// Main.cpp
// This file contains the main method for the project. It implements the OpenGL rendering loop
// and even handling for the application
// Date:  4/12/2012
// Authors: Ondrej Linda, University of Idaho
//---------------------------------------------------------------------------

#define _USE_MATH_DEFINES
#include "GL/glui.h"
#include "_utils/GLUT/glut.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <cmath>
#include <math.h>
#include <time.h>
#include <cstdlib>
#include <iostream>

#include "Building.h"
#include "Monitor.h"
#include "TextureLoader.h"
#include "FuzzyNameLoader.h"
#include "FuzzyAnt.h"
#include "FuzzyRule.h"
#include "FuzzyRuleSet.h"
#include "FuzzySetT1Tri.h"
#include "FuzzySystemT1Centroid.h"

#ifndef M_PI 
	#define M_PI 3.1415926535897932384626433832795 
#endif

#include <GL\gl.h>

using namespace std;

/* Windows parameters */
#define WIDTH  1000
#define HEIGHT 600
#define TITLE  "CIEMS-BBB"
int winIdMain;

// the size of the display window
int widthD = WIDTH;
int heightD = HEIGHT;

// TextureLoader object for texture management
TextureLoader *pTextureLoader; 
// Textures IDs
glTexture UILogo,Graph,test_texture;
glTexture dial_texture_time, dial_texture_day, dial_texture_month, dial_texture_cover;

// Camera control parameters
static float anglex = 0.0f, angley = 0.0f;
static float eyex = 0.0f, eyey = -2.0f, eyez = -8.0f;
static float pointx = 0.0f, pointy = 0.0f, pointz = 0.0f;
static float lx=0.0f, ly=0.0f, lz=5.0f;
static int  lastX = 0, lastY = 0, lastZ = 0;
static float radius = 5;
float moveStep = 0.1f;
static int moveRatio = 40;
static float dx = -0.0f, dy = -0.5f, dz = -1.5f;
int specialKey;

//temporary
float x_temp = 0;
float y_temp = 0;
float z_temp = 0;
float temp_rotate = 0;


// Building object
C_Building * bl;

// The monitor object
C_Monitor * monitor;

// Color pallete for color picking
int colors[4096][3];

// The GUI subwindow
GLUI * glui;

// GLUI state variables
int gv_DispMode = 0; 
int use_alarms = 0; //1 = use , 0 = do not use
bool ctrl_display_anomalies = true; // additional control variable for demosntrating without anomaly indicators by the building and zone borders

// Globlal GLUI components 
GLUI_Translation * translateTime;
GLUI_Spinner * spinnThreshold;
GLUI_Button * buttonMark;
GLUI_Spinner * spinAnt;
GLUI_Checkbox * checkZT;
GLUI_Checkbox ** checkZL;
GLUI_Checkbox * checkTime;
GLUI_Checkbox ** checkBL;
GLUI_Checkbox ** checkAL;
int selected_zone_dim = 0;

int prev_anom_mode = 0;

// Select the building to be loaded - 0 = BBB, 1 = Mates
int buildingSelect = 0;

//position vectors for each type of gadget
float attrib_x = 0.05f;
float attrib_y = -0.03f;
float attrib_z = 0.0f;
float graph_x = 0.9f;
float graph_y = 0.1f;
float graph_z = 0.0f;

 // OpenGL text rendering function - 2D
void stroke_output(GLfloat x, GLfloat y, char *format,...)
{
	glDisable (GL_BLEND);
	va_list args;
	char buffer[200], *p;
  
	va_start(args, format);
	vsprintf(buffer, format, args);
	va_end(args);
	glPushMatrix();
	glTranslatef(x - 0.03f, y, 0.0001f);  
	glScalef(0.0002f, 0.0002f, 0.0002f);  
	for (p = buffer; *p; p++){	  
		glutStrokeCharacter(GLUT_STROKE_MONO_ROMAN, *p);	  
	}  
	glPopMatrix();
	glEnable (GL_BLEND);
}

// Initializes the color pallete for color picking of objects
 void getColors(){
	 for (int i = 0; i < 16; i++){
		 for (int j = 0; j < 16; j++){
			 for (int k = 0; k < 16; k++){
				 colors[i * 256 + j * 16 + k][0] = i * 16;
				 colors[i * 256 + j * 16 + k][1] = j * 16;
				 colors[i * 256 + j * 16 + k][2] = k * 16;
			 }
		 }
	 }
 }

//return time given the time stamp
float getTime()
{
	string time_stamp = bl->m_Data->m_Time[bl->m_DataPoint];
	float ret_time = 0;
	int ind = time_stamp.find_first_of(" ");

	time_stamp = time_stamp.substr(ind + 1);

	//cout << time_stamp;
	ind = time_stamp.find_first_of(":");
	string time_hr = time_stamp.substr(0,ind);
	string time_mn = time_stamp.substr(ind+1);
	
	float ftime_mn = atof(time_mn.c_str());
	ftime_mn = (ftime_mn / 60) * 100;
	ret_time = (atof(time_hr.c_str())) * 100;
	ret_time = ret_time + ftime_mn;
	//cout << time_mn << "    " << ftime_mn << endl;

	return ret_time;
}

//return month given the time stamp
float getMonth()
{
	string time_stamp = bl->m_Data->m_Time[bl->m_DataPoint];
	int ind = time_stamp.find_first_of(" ");
	time_stamp = time_stamp.substr(0, ind);

	ind = time_stamp.find_first_of("//");
	string m_str = time_stamp.substr(0,ind);
	time_stamp = time_stamp.substr(ind+1);

	float m_flt = atof(m_str.c_str());

	return m_flt;
}

//return time given the time stamp
float getDay()
{
	string time_stamp = bl->m_Data->m_Time[bl->m_DataPoint];
	int ind = time_stamp.find_first_of(" ");
	time_stamp = time_stamp.substr(0, ind);

	//cout << time_stamp << endl;

	ind = time_stamp.find_first_of("//");
	string m_str = time_stamp.substr(0,ind);
	time_stamp = time_stamp.substr(ind+1);

	ind = time_stamp.find_first_of("//");
	string d_str = time_stamp.substr(0,ind);
	time_stamp = time_stamp.substr(ind+1);

	ind = time_stamp.find_first_of("//");
	string y_str = time_stamp.substr(0,ind);
	
	//cout << y_str << ":" << m_str << ":" << d_str << endl;

	float y_flt = atof(y_str.c_str());
	float m_flt = atof(m_str.c_str());
	float d_flt = atof(d_str.c_str());

	float k = d_flt;
	float m = m_flt;

	float y = y_flt - 2000;
	float d = d_flt;
	//float y = Y;
	y -= m < 3;
	return (int)( y+y / 4-y / 100+y / 400 + "-bed=pen+mad." [(int)m]+d)%7;

}
 // OpenGL main rendering function
void mainDisplay (void)
{
	/* Clean drawing board */
	glutSetWindow (winIdMain);
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

	glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glDisable(GL_LIGHTING);
  
	glEnable (GL_BLEND);
	glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	// Handle the camera position
	glLoadIdentity();
	glRotatef(angley, 1.0f, 0.0f, 0.0f);
	glRotatef(anglex, 0.0f, 1.0f, 0.0f);

	glTranslatef( dx, dy, dz);

	glPushMatrix();

	glTranslatef(0.05f, 0.35f, -0.8f);

	glLineWidth(3.0f);
	glColor3f(0.7f, 0.7f, 0.7f);
	glBegin(GL_POLYGON);
		glVertex3f(0.0f, 0.0f, -0.03f);
		glVertex3f(0.0f, 1.0f, -0.03f);
		glVertex3f(1.0f, 1.0f, -0.03f);
		glVertex3f(1.0f, 0.0f, -0.03f);
	glEnd();
	glColor3f(0.0f, 0.0f, 1.0f);
	glBegin(GL_LINE_LOOP);
		glVertex3f(0.0f, 0.0f, 0.0f);
		glVertex3f(0.0f, 1.0f, 0.0f);
		glVertex3f(1.0f, 1.0f, 0.0f);
		glVertex3f(1.0f, 0.0f, 0.0f);
	glEnd();
	glLineWidth(1.0f);

	// Render the UofI logo
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, UILogo.TextureID);    
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);	

	glColor3f(0.0f, 0.0f, 0.0f);
	glBegin(GL_QUADS);
	glTexCoord2f(0.05f, 0.0f); glVertex2f(1.1f, 0.95f);
	glTexCoord2f(0.05f, 1.0f); glVertex2f(1.1f, 1.115f);
	glTexCoord2f(0.95f, 1.0f); glVertex2f(1.55f, 1.115f);
	glTexCoord2f(0.95f, 0.0f); glVertex2f(1.55f, 0.95f);
	glEnd();

	glDisable(GL_TEXTURE_2D);

	

	char * str = new char[20];

	// Draw the floor plane
	for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NZones; i++)
	{
		// First select the floor filling color
		if ((bl->m_Floors[bl->m_SelectFloor].m_HasFill))
		{	  		  
			//Display temperature
			if (monitor->m_DispMode == 0)
			{			  
				float minVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Data->m_MinVal[selected_zone_dim];
				float maxVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Data->m_MaxVal[selected_zone_dim];

				float col = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Data->m_Val[selected_zone_dim][bl->m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;

				col = min(col, 1.0f);
				col = max(col, -1.0f);

				if (col < 0)
				{
					glColor3f(0.0f, 1.0f + col, -col);
				}
				else
				{
					glColor3f(col, 1.0f - col, 0.0f);
				}		  
			}
			//Display anomaly
			else if(monitor->m_DispMode == 1 && monitor->m_AnomMode <= 2)
			{
				float col = 0.0f;
				if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
				{
					if (monitor->m_AnomMode == 0){
						col = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI;
					}
					else if (monitor->m_AnomMode == 1){
						col = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI_Rules;
					}
					else{
						col = max(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI_Rules, bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI);
					}
				}
				else
				{
					if (monitor->m_AnomMode == 0){
						col = bl->m_Floors[6].m_Zones[i].m_AnomalI;
					}
					else if (monitor->m_AnomMode == 1){
						col = bl->m_Floors[6].m_Zones[i].m_AnomalI_Rules;
					}
					else{
						col = max(bl->m_Floors[6].m_Zones[i].m_AnomalI, bl->m_Floors[6].m_Zones[i].m_AnomalI_Rules);
					}
				}

				col = min(col, 1.0f);
				col = max(col, 0.0f);

				glColor3f(col, 1.0f - col, 0.0f);			  
			}
			//rule based comfort
			else if(monitor->m_DispMode == 1 && monitor->m_AnomMode == 3)
			{
				float col = 0.0f;
				if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
				{
					col = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].comfort_level;
				}
				else
				{
					col = bl->m_Floors[6].m_Zones[i].comfort_level;
				}
				col = 2.0f * col - 1.0f;
				col = min(col, 1.0f);
				col = max(col, -1.0f);

				if (col < 0)
				{
					glColor3f(-col, 1.0f + col, 0.0f);
				}
				else
				{
					glColor3f(0.0f, 1.0f - col, col);
				}			  
			}
			//rule based efficiency
			else if(monitor->m_DispMode == 1 && monitor->m_AnomMode == 4)
			{
				float col = 0.0f;
				if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
				{
					col = bl->m_Floors[bl->m_SelectFloor].m_Zones[i].efficiency_level;
				}
				else
				{
					col = bl->m_Floors[6].m_Zones[i].efficiency_level;
				}
				col = 2.0f * col - 1.0f;
				col = min(col, 1.0f);
				col = max(col, -1.0f);

				if (col < 0)
				{
					glColor3f(-col, 1.0f + col, 0.0f);
				}
				else
				{
					glColor3f(0.0f, 1.0f - col, col);
				}			  
			}
			//out of bounds color
			if(monitor->out_of_bounds_flag && use_alarms == 1)
			{
				if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
				{
					if(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].out_of_bounds_flag)
					{
						glColor3f(1.0f, 0.0f, 1.0f);
						//cout << i << endl;
					}
				}
			}
			//now draw the polygon using the selected color
			for (int p = 0; p < bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_NPoly; p++)
			{			  			  
				glBegin(GL_POLYGON);
				for (int j = 0; j < bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_NVertex; j++)
				{
					glVertex3f(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_VertexX[j], bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_VertexY[j], 0.0);
				}
				glEnd();
			}
		}

		// Next draw the walls
		if (bl->m_Floors[bl->m_SelectFloor].m_HasWall)
		{
			glLineWidth(2.5f);
			//fist select the color and the size of the line
			if (i == bl->m_Floors[bl->m_SelectFloor].m_SelectZone)
			{
				glColor3f(1.0f, 1.0f, 1.0f);
			}
			else if(ctrl_display_anomalies)
			{
				//cluster based
				if (monitor->m_AnomMode == 0)
				{
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI > monitor->m_Threshold)
					{		
						glLineWidth(4.0f);
						glColor3f(1.0f, 0.0f, 0.0f);
					}
					else{
						glColor3f(0.5f, 0.5f, 0.5f);
					}
				}
				//rule based
				else if (monitor->m_AnomMode == 1)
				{
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI_Rules > monitor->m_Threshold){		
						glLineWidth(4.0f);
						glColor3f(1.0f, 0.0f, 0.0f);
					}
					else{
						glColor3f(0.5f, 0.5f, 0.5f);
					}
				}
				//combination of cluster and rule based
				else if (monitor->m_AnomMode == 2)
				{
					float anomL = max(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI, bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI_Rules);
					if (anomL > monitor->m_Threshold){		
						glLineWidth(4.0f);
						glColor3f(1.0f, 0.0f, 0.0f);
					}
					else{
						glColor3f(0.5f, 0.5f, 0.5f);
					}
				}
				//Comfort 
				else if (monitor->m_AnomMode == 3)
				{
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[i].comfort_level < 1 - monitor->m_Threshold){		
						glLineWidth(4.0f);
						glColor3f(1.0f, 0.0f, 0.0f);
					}
					else{
						glColor3f(0.5f, 0.5f, 0.5f);
					}
				}		
				//Efficiency
				else if (monitor->m_AnomMode == 4)
				{
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[i].efficiency_level < 1 - monitor->m_Threshold){		
						glLineWidth(4.0f);
						glColor3f(1.0f, 0.0f, 0.0f);
					}
					else{
						glColor3f(0.5f, 0.5f, 0.5f);
					}
				}	
			}
			//now draw the wall
			glBegin(GL_LINE_LOOP);
			for (int j = 0; j < bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_NVertex; j++)
			{		
				//selected
				if (i == bl->m_Floors[bl->m_SelectFloor].m_SelectZone)
				{
					glVertex3f(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexX[j], bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexY[j], 0.0002f);
				}
				else
				{
					//anomaly
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_AnomalI > monitor->m_Threshold && ctrl_display_anomalies)
					{
						glVertex3f(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexX[j], bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexY[j], 0.00015f);
					}
					//normal
					else
					{
						//glColor3f(0.5f, 0.5f, 0.5f);
						glVertex3f(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexX[j], bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_VertexY[j], 0.0001f);
					}
				}
			}
			glEnd();

			glLineWidth(1.5f);
		}

		// Draw the zone label
		glColor3f(0.0f, 0.0f, 0.0f);	  
		sprintf(str, "Z%i", bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_ID);
		stroke_output(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_MidX, bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_MidY, str);		  
	}

	glPopMatrix();

	// Display the date and time  
	glColor3f(0.0f, 0.0f, 0.0f);
	stroke_output(0.35f, 1.07f, (char *)bl->m_Data->m_Time[bl->m_DataPoint].c_str());

	// Display the floor number
	sprintf(str, "Floor: %i", bl->m_SelectFloor + 1);
	stroke_output(0.05f, 1.07f, str);		  

	// Display the name of the building  
	glColor3f(0.0f, 0.0f, 0.0f);
	stroke_output(-0.25f, 1.07f, (char *)bl->m_NameStr.c_str());  


	// Draw the 3D building view
	glEnable(GL_LIGHTING);
	glPushMatrix();

	glRotatef(30, 0.0f, 1.0f, 0.0f);    
	glTranslatef(0.25f, 0.05f, 0.0f);    

	for (int i = 0; i < bl->m_NFloors; i++)
	{ 
		glPushMatrix();	    
		glTranslatef(-0.45f, 0.4f +  i * 0.05f, 0.0f);	  

		// Set the anomaly indicator (red sphere to the left of the floor)
		//rule based and cluster base or combined
		if(ctrl_display_anomalies)
		{
			if ((bl->m_Floors[i].m_HasAnomaly_Cluster && (monitor->m_AnomMode == 0)) || 
				(bl->m_Floors[i].m_HasAnomaly_Rules && (monitor->m_AnomMode == 1)) ||
				((bl->m_Floors[i].m_HasAnomaly_Cluster || bl->m_Floors[i].m_HasAnomaly_Rules) && (monitor->m_AnomMode == 2)))
			{
				glPushMatrix();
				glColor3f(0.8f, 0.1f, 0.1f);
				glTranslatef(-0.13f, -0.01f, -0.1f);
				glutSolidSphere(0.02f, 25, 25);
				glTranslatef(0.0f, 0.0f, 0.02f);
				stroke_output(0.012f, -0.014f, "A");  
				glPopMatrix();
			}
			//comfort
			if(bl->m_Floors[i].has_comfort_anomaly && (monitor->m_AnomMode == 3))
			{
				glPushMatrix();
				glColor3f(0.8f, 0.1f, 0.1f);
				glTranslatef(-0.13f, -0.01f, -0.1f);
				glutSolidSphere(0.02f, 25, 25);
				glTranslatef(0.0f, 0.0f, 0.02f);
				stroke_output(0.012f, -0.014f, "C");  
				glPopMatrix();
			}
			//efficiency
			if(bl->m_Floors[i].has_efficiency_anomaly && (monitor->m_AnomMode == 4))
			{
				glPushMatrix();
				glColor3f(0.8f, 0.1f, 0.1f);
				glTranslatef(-0.13f, -0.01f, -0.1f);
				glutSolidSphere(0.02f, 25, 25);
				glTranslatef(0.0f, 0.0f, 0.02f);
				stroke_output(0.012f, -0.014f, "E");  
				glPopMatrix();
			}
			//out of bounds
			if(bl->m_Floors[i].out_of_bounds_flag && use_alarms == 1)
			{
				glPushMatrix();
				glColor3f(1.0f, 0.0f, 1.0f);
			
				glTranslatef(0.24f + x_temp, 0.038f  + y_temp, -0.07f  + z_temp);
				glRotatef(95.0f, 0, -1, 0);
				//glutSolidSphere(0.02f, 25, 25);
				glutSolidCone(0.02f, 0.035f, 25, 25);
				//glTranslatef(0.0f, 0.0f, 0.02f);
				//stroke_output(0.012f, -0.014f, "B");  
				glPopMatrix();
			}
		}
		float col = 0.0f;	  

		// Set the proper color of the floor
		if (monitor->m_DispMode == 0)
		{		  
			float minVal = bl->m_Floors[i].m_MinVal[0];
			float maxVal = bl->m_Floors[i].m_MaxVal[0];

			col = 2.0f * ((bl->m_Floors[i].m_AvgTemp - minVal) / (maxVal - minVal)) - 1.0f;
	  
			col = min(col, 1.0f);
			col = max(col, -1.0f);

			if (col < 0){
				glColor3f(0.0f, 1.0f + col, -col);
			}
			else{
				glColor3f(col, 1.0f - col, 0.0f);
			}
		}
		else
		{
			//cluster based
			if (monitor->m_AnomMode == 0)
			{
				col = bl->m_Floors[i].m_MaxAnomConf_Cluster;
			}
			//expert anomaly
			else if (monitor->m_AnomMode == 1)
			{
				col = bl->m_Floors[i].m_MaxAnomConf_Rules;
			}
			//combined
			else if (monitor->m_AnomMode == 2)
			{
				col = max(bl->m_Floors[i].m_MaxAnomConf_Cluster, bl->m_Floors[i].m_MaxAnomConf_Rules);
			}
			//comfort based
			else if (monitor->m_AnomMode == 3)
			{
				col = bl->m_Floors[i].m_MinAnomConf_Comfort;
				col = 1 - col;
				if(col <= 0.6)
					col = 0.1f;
			}
			//efficiency based
			else if (monitor->m_AnomMode == 4)
			{
				col = bl->m_Floors[i].m_MinAnomConf_Efficiency;
				col = 1 - col;
				if(col <= 0.6)
					col = 0.1f;
			}
			glColor3f(col, 1.0f - col, 0.0f);
		}

		//if no data for floor
		if(!bl->m_Floors[i].m_IsReady)
		{
			glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
		}

		glScalef(4.0f, 0.5f, 4.0f);
		glutSolidCube(0.05f);
		if (i == bl->m_SelectFloor)
		{
			glColor3f(0.0f, 0.0f, 1.0f);
			glutWireCube(0.052f);
		}


		glPopMatrix();
	}

	glPopMatrix();

	glPushMatrix();

	glTranslatef(graph_x, graph_y, graph_z);

	// Draw the data view for selected zone
	glDisable(GL_LIGHTING);  
	glColor3f(0.0f, 0.0f, 0.0f);
	glBegin(GL_LINES);
	glVertex3f(-1.0f, 0.15f, 0.0f);
	glVertex3f(0.0f, 0.15f, 0.0f);

	glVertex3f(-1.0f, 0.05f, 0.0f);
	glVertex3f(-1.0f, 0.25f, 0.0f);

	glColor3f(1.0f, 0.4f, 0.4f);
	glVertex3f(-0.5f, 0.05f, 0.0f);
	glVertex3f(-0.5f, 0.25f, 0.0f);

	glEnd();

	// Render the graph
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, Graph.TextureID);    
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);	

	glColor3f(0.0f, 0.0f, 0.0f);
	glBegin(GL_QUADS);
	glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, 0.05f, -0.01f);
	glTexCoord2f(0.0f, 1.0f); glVertex3f(-1.0f, 0.25f, -0.01f);
	glTexCoord2f(1.0f, 1.0f); glVertex3f(0.0f, 0.25f, -0.01f);
	glTexCoord2f(1.0f, 0.0f); glVertex3f(0.0f, 0.05f, -0.01f);
	glEnd();

	glDisable(GL_TEXTURE_2D);

	// Draw the value labels
	glColor3f(0.0f, 0.0f, 0.0f);
	stroke_output(-1.05f, 0.07f, "Low");  
	stroke_output(-1.12f, 0.15f, "Medium");  
	stroke_output(-1.08f, 0.23f, "High");  

	//plots
	if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1)
	{
		for (int i = bl->m_DataPoint - 10; i < bl->m_DataPoint + 10; i++)
		{
			if ((i >= 0) && (i < bl->m_NData - 1))
			{
				

				//float minVal = bl->m_Floors[bl->m_SelectFloor].m_MinVal[0];
				//float maxVal = bl->m_Floors[bl->m_SelectFloor].m_MaxVal[0];
				float minVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_Data->m_MinVal[selected_zone_dim];
				float maxVal = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_Data->m_MaxVal[selected_zone_dim];

				float val1 = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_Data->m_Val[selected_zone_dim][i] - minVal) / (maxVal - minVal)) - 1.0f;
				float val2 = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_Data->m_Val[selected_zone_dim][i + 1] - minVal) / (maxVal - minVal)) - 1.0f;

				if(val1 > 1)
					val1 = 1;
				if(val1 < -1 )
					val1 = -1;
				if(val2 > 1)
					val2 = 1;
				if(val2 < -1 )
					val2 = -1;

				int idx = i - bl->m_DataPoint + 10;			  
				glColor3f(0.0f, 0.0f, 1.0f);
				glBegin(GL_LINES);
				glVertex3f(-1.0f + idx * 0.05f, 0.15f + val1 * 0.1f, 0.0f);
				glVertex3f(-1.0f + (idx + 1) * 0.05f, 0.15f + val2 * 0.1f, 0.0f);
				glEnd();
			}
		}
	}  

	// Show the other data associated with floor and building if selected
	for (int a = 0; a < monitor->m_NAttr; a++)
	{
		if (monitor->m_ShowAttr[a])
		{
			if (a < bl->m_NAttr)
			{
				for (int i = bl->m_DataPoint - 10; i < bl->m_DataPoint + 10; i++)
				{		  
					if ((i >= 0) && (i < bl->m_NData - 1))
					{		  
						float minVal = bl->m_Data->m_MinVal[a];
						float maxVal = bl->m_Data->m_MaxVal[a];

						float val1 = 2.0f * ((bl->m_Data->m_Val[a][i] - minVal) / (maxVal - minVal)) - 1.0f;
						float val2 = 2.0f * ((bl->m_Data->m_Val[a][i + 1] - minVal) / (maxVal - minVal)) - 1.0f;

						if(val1 > 1)
							val1 = 1;
						if(val1 < -1 )
							val1 = -1;
						if(val2 > 1)
							val2 = 1;
						if(val2 < -1 )
							val2 = -1;

						int idx = i - bl->m_DataPoint + 10;					  
						glColor3f(monitor->m_DataColors[a][0], monitor->m_DataColors[a][1], monitor->m_DataColors[a][2]);
						glBegin(GL_LINES);
						glVertex3f(-1.0f + idx * 0.05f, 0.15f + val1 * 0.1f, 0.0f);
						glVertex3f(-1.0f + (idx + 1) * 0.05f, 0.15f + val2 * 0.1f, 0.0f);
						glEnd();				
					}
				}
			}
			else
			{		  
				int idxA = a - bl->m_NAttr;
				for (int i = bl->m_DataPoint - 10; i < bl->m_DataPoint + 10; i++)
				{	  
					if ((i >= 0) && (i < bl->m_NData - 1))
					{		
						float minVal = bl->m_Floors[bl->m_SelectFloor].m_Data->m_MinVal[idxA];
						float maxVal = bl->m_Floors[bl->m_SelectFloor].m_Data->m_MaxVal[idxA];

						float val1 = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[idxA][i] - minVal) / (maxVal - minVal)) - 1.0f;
						float val2 = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[idxA][i + 1] - minVal) / (maxVal - minVal)) - 1.0f;

						if(val1 > 1)
							val1 = 1;
						if(val1 < -1 )
							val1 = -1;
						if(val2 > 1)
							val2 = 1;
						if(val2 < -1 )
							val2 = -1;

						int idx = i - bl->m_DataPoint + 10;					  
						glColor3f(monitor->m_DataColors[a][0], monitor->m_DataColors[a][1], monitor->m_DataColors[a][2]);
						glBegin(GL_LINES);
						glVertex3f(-1.0f + idx * 0.05f, 0.15f + val1 * 0.1f, 0.0f);
						glVertex3f(-1.0f + (idx + 1) * 0.05f, 0.15f + val2 * 0.1, 0.0f);
						glEnd();	
					}
				}		  
			}
		}
	}

	glPopMatrix();

	// Display the per floor and per building attributes
	glPushMatrix();
	glTranslatef(attrib_x, attrib_y, attrib_z);
	//building level
	for (int i = 0; i < bl->m_NAttr; i++)
	{
		glColor3f(0.0f, 0.0f, 0.0f);
		stroke_output(0.7f, 1.02f - i * 0.07f, (char *)bl->m_DataNames[i].c_str());      

		if (monitor->m_ShowAttr[i])
		{
			glColor3f(monitor->m_DataColors[i][0], monitor->m_DataColors[i][1], monitor->m_DataColors[i][2]);

			glBegin(GL_QUADS);
			glVertex3f(0.74f, 0.99f - i * 0.07f, -0.001f);
			glVertex3f(0.74f, 1.06f - i * 0.07f, -0.001f);
			glVertex3f(0.81f, 1.06f - i * 0.07f, -0.001f);
			glVertex3f(0.81f, 0.99f - i * 0.07f, -0.001f);
			glEnd();
		}

		float minVal = bl->m_Data->m_MinVal[i];
		float maxVal = bl->m_Data->m_MaxVal[i];

		float col = 2.0f * ((bl->m_Data->m_Val[i][bl->m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;

		col = min(col, 1.0f);
		col = max(col, -1.0f);

		if (col < 0){
			glColor3f(0.0f, 1.0f + col, -col);
		}
		else{
			glColor3f(col, 1.0f - col, 0.0f);
		}		  

		glBegin(GL_QUADS);
		glVertex3f(0.75f, 1.00f - i * 0.07f, 0.0f);
		glVertex3f(0.75f, 1.05f - i * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.05f - i * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.00f - i * 0.07f, 0.0f);
		glEnd();

		glColor3f(0.0f, 0.0f, 0.0f);

		glBegin(GL_LINE_LOOP);
		glVertex3f(0.75f, 1.00f - i * 0.07f, 0.0f);
		glVertex3f(0.75f, 1.05f - i * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.05f - i * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.00f - i * 0.07f, 0.0f);
		glEnd();

		sprintf(str, "%4.1f%s", bl->m_Data->m_Val[i][bl->m_DataPoint], (char *)bl->m_DataUnits[i].c_str());
		stroke_output(0.85f, 1.02f - i * 0.07f, str);	
	}
	//floor level
	for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NAttr; i++)
	{
		glColor3f(0.0f, 0.0f, 0.0f);
		stroke_output(0.7f, 1.02f - (bl->m_NAttr + i) * 0.07f, (char*)bl->m_Floors[bl->m_SelectFloor].m_DataNames[i].c_str());      

		if (monitor->m_ShowAttr[bl->m_NAttr + i])
		{
			glColor3f(monitor->m_DataColors[bl->m_NAttr + i][0], monitor->m_DataColors[bl->m_NAttr + i][1], monitor->m_DataColors[bl->m_NAttr + i][2]);

			glBegin(GL_QUADS);
			glVertex3f(0.74f, 0.99f - ((bl->m_NAttr + i)) * 0.07f, -0.001f);
			glVertex3f(0.74f, 1.06f - ((bl->m_NAttr + i)) * 0.07f, -0.001f);
			glVertex3f(0.81f, 1.06f - ((bl->m_NAttr + i)) * 0.07f, -0.001f);
			glVertex3f(0.81f, 0.99f - ((bl->m_NAttr + i)) * 0.07f, -0.001f);
			glEnd();	 
		}

		float minVal = bl->m_Floors[bl->m_SelectFloor].m_Data->m_MinVal[i];
		float maxVal = bl->m_Floors[bl->m_SelectFloor].m_Data->m_MaxVal[i];

		float col = 2.0f * ((bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[i][bl->m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;

		col = min(col, 1.0f);
		col = max(col, -1.0f);

		if (col < 0){
			glColor3f(0.0f, 1.0f + col, -col);
		}
		else{
			glColor3f(col, 1.0f - col, 0.0f);
		}		  

		glBegin(GL_QUADS);
		glVertex3f(0.75f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
		glVertex3f(0.75f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
		glVertex3f(0.8f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
		glEnd();

		glColor3f(0.0f, 0.0f, 0.0f);

		glBegin(GL_LINE_LOOP);
		glVertex3f(0.75f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.001f);
		glVertex3f(0.75f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.001f);
		glVertex3f(0.8f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.001f);
		glVertex3f(0.8f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.001f);
		glEnd();

		sprintf(str, "%4.1f%s", bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[i][bl->m_DataPoint], (char *)bl->m_Floors[bl->m_SelectFloor].m_DataUnits[i].c_str());
		stroke_output(0.85f, 1.02f - ((bl->m_NAttr + i)) * 0.07f, str);	
	}

	glPopMatrix();
  
	glColor3f(0.0f, 0.0f, 0.0f);
	glBegin(GL_LINES);
	glVertex3f(-1.0f, 0.1f, 0.0f);
	glVertex3f(1.0f, 0.1f, 0.0f);
	glEnd();
    
	// Display the linguistic description of the anomaly if selected
	if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
	{
//TODO multi line
		if (monitor->out_of_bounds_flag && bl->m_Floors[bl->m_SelectFloor].out_of_bounds_flag  && use_alarms == 1)
		{
			if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1)
			{
				string out_of_bound_desc = "ALARM";
				
				stroke_output(-0.4f, 0.05f, (char *) out_of_bound_desc.c_str());

				string desc1 = "";
				string desc2 = "";
				string desc3 = "";

				desc1 = monitor->getDescriptionOutofBounds(&(bl->m_Floors[bl->m_SelectFloor]), bl->m_DataPoint, 1);
				desc2 = monitor->getDescriptionOutofBounds(&(bl->m_Floors[bl->m_SelectFloor]), bl->m_DataPoint, 2);
				desc3 = monitor->getDescriptionOutofBounds(&(bl->m_Floors[bl->m_SelectFloor]), bl->m_DataPoint, 3);

				if (monitor->m_NAnt_Sel == 1)
				{
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
				}
				else if (monitor->m_NAnt_Sel == 2)
				{
					if(desc2 != "")
					{
						desc1 += " AND";
					}
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
					stroke_output(-0.4f, -0.05f, (char *)desc2.c_str());
				}
				else
				{
					if(desc2 != "")
					{
						desc1 += " AND";
					}
					if (desc3 != "")
					{
						desc2 += " AND";
					}
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
					stroke_output(-0.4f, -0.05f, (char *)desc2.c_str());
					stroke_output(-0.4f, -0.1f, (char *)desc3.c_str());
				}
			}
		}
		else if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1)
		{
			bool checkAnom = false;

			float anomICl = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI;
			float anomIRl = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules;
			float anomIComfort = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].comfort_level;
			float anomIEfficiency = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].efficiency_level;
			//cluster based
			if (monitor->m_AnomMode == 0)
			{
				if (anomICl > monitor->m_Threshold)
				{
					checkAnom = true;
				}			  
			}
			//rule based
			else if (monitor->m_AnomMode == 1){
				if (anomIRl > monitor->m_Threshold){		    
					checkAnom = true;
				}			  
			}
			//combined
			else if (monitor->m_AnomMode == 2){
				float maxAnom = max(anomICl, anomIRl);

				if (maxAnom > monitor->m_Threshold){
					checkAnom = true;
				}
			}
//TODO		//comfort
			else if (monitor->m_AnomMode == 3)
			{
				checkAnom = true;
				if (anomIComfort < 1 - monitor->m_Threshold){		    
					checkAnom = true;
				}			  
			}
			//Efficiency
			else if (monitor->m_AnomMode == 4){
				checkAnom = false;
				if (anomIEfficiency < 1 - monitor->m_Threshold){
					checkAnom = true;
				}			  
			}

			if (checkAnom)
			{
				string conf = monitor->getAnomalyConfidence(&(bl->m_Floors[bl->m_SelectFloor]));		
				string confStr = "";
				if (monitor->m_AnomMode == 0){
					confStr = "Status : Anomaly - Cluster ( ";
				}
				else if (monitor->m_AnomMode == 1){
					confStr = "Status : Anomaly - Rule ( ";
				}
				else if (monitor->m_AnomMode == 2){
					if (anomICl > anomIRl){
						confStr = "Status : Anomaly - Cluster ( ";
					}
					else{
						confStr = "Status : Anomaly - Rule ( ";
					}
				}
				else if (monitor->m_AnomMode == 3){
					confStr = "Status : Comfort - Rule ( ";
				}
				else if (monitor->m_AnomMode == 4){
					confStr = "Status : Efficiency - Rule ( ";
				}

				confStr += conf;
				confStr += " )";
				stroke_output(-0.4f, 0.05f, (char *) confStr.c_str());	

				//NEW
				string desc1 = "";
				if (monitor->m_AnomMode == 0)
				{
					desc1 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);		  
				}
				else if (monitor->m_AnomMode == 1)
				{
					desc1 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);		  
				}
				else if (monitor->m_AnomMode == 2)
				{
					if (anomICl > anomIRl){
						desc1 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);	
					}
					else{
						desc1 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);
					}
				}
				//comfort
				else if (monitor->m_AnomMode == 3)
				{
					desc1 = monitor->getComfortRule(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);
				}
				//efficiency
				else if (monitor->m_AnomMode == 4)
				{
					desc1 = monitor->getEfficiencyRule(&(bl->m_Floors[bl->m_SelectFloor]), 0, false);
				}

				string desc2 = "";
				if (monitor->m_AnomMode == 0){
					desc2 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);		  
				}
				else if (monitor->m_AnomMode == 1)
				{
					desc2 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);		  
				}
				else if (monitor->m_AnomMode == 2)
				{
					if (anomICl > anomIRl){
						desc2 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);		  
					}
					else{
						desc2 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);		  
					}
				}
				else if (monitor->m_AnomMode == 3)
				{
					desc2 = monitor->getComfortRule(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);
				}
				else if (monitor->m_AnomMode == 4)
				{
					desc2 = monitor->getEfficiencyRule(&(bl->m_Floors[bl->m_SelectFloor]), 1, false);
				}

				string desc3 = "";
				if (monitor->m_AnomMode == 0){
					desc3 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);		  
				}
				else if (monitor->m_AnomMode == 1){
					desc3 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);		  
				}
				else if (monitor->m_AnomMode == 2){
					if (anomICl > anomIRl){
						desc3 = monitor->getDescription(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);	
					}
					else{
						desc3 = monitor->getDescriptionRule(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);		   
					}
				}
				else if (monitor->m_AnomMode == 3)
				{
					desc3 = monitor->getComfortRule(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);
				}
				else if (monitor->m_AnomMode == 4)
				{
					desc3 = monitor->getEfficiencyRule(&(bl->m_Floors[bl->m_SelectFloor]), 2, false);
				}

				//print
				if (monitor->m_NAnt_Sel == 1)
				{
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
				}
				else if (monitor->m_NAnt_Sel == 2)
				{
					if(desc2 != "")
					{
						desc1 += " AND";
					}
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
					stroke_output(-0.4f, -0.05f, (char *)desc2.c_str());
				}
				else
				{
					if(desc2 != "")
					{
						desc1 += " AND";
					}
					if (desc3 != "")
					{
						desc2 += " AND";
					}
					stroke_output(-0.4f, 0.0f, (char *)desc1.c_str());
					stroke_output(-0.4f, -0.05f, (char *)desc2.c_str());
					stroke_output(-0.4f, -0.1f, (char *)desc3.c_str());
				}
			}
			else
			{
				string conf = monitor->getNormalConfidence(&(bl->m_Floors[bl->m_SelectFloor]));		  
				string confStr = "Status : Normal: ( ";
				confStr += conf;
				confStr += " )";
				stroke_output(-0.4f, 0.05f, (char *) confStr.c_str());		
			}
		}
		else{	  	  
			stroke_output(-0.3f, 0.05f, "Status: N/A");		
		}
	}
	else{
		stroke_output(-0.3f, 0.05f, "Status: N/A");	
	}

	//draw the time dials
	//change coordinate system
	glPushMatrix();
	glTranslatef(-0.38f,-0.12f,-0.05f);

	//masking texture 1
	glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
		glVertex3f(1.1f, 0.215f, 0.025f);
		glVertex3f(1.1f, 0.8f, 0.025f);
		glVertex3f(1.8f, 0.8f, 0.025f);
		glVertex3f(1.8f, 0.215f, 0.025f);
	glEnd();
	
	//move coordinate system to the center of the dial
	glPushMatrix();
	glTranslatef(1.38f,0.05f,0);

	//masking texture 2
	glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
		glVertex3f(0.05f, -1.0f, 0.025f);
		glVertex3f(0.05f, 0.5f, 0.025f);
		glVertex3f(0.8f, 0.5f, 0.025f);
		glVertex3f(0.8f, -1.0f, 0.025f);
	glEnd();

	
	glEnable(GL_TEXTURE_2D);
	glEnable(GL_BLEND);

	// dial 1 (time) 
	glPushMatrix();
	float time_rotation = 0;
	time_rotation = (getTime() / 2400) * 360;
	//cout << "Time = " << getTime() << " :: Rot = " << day_rotation << endl;
	//roatation of the first dial
	glRotatef(time_rotation, 0, 0, 1);
	//size
	float dial1_size_width = 0.62f;
	float dial1_x_min = -(dial1_size_width / 2);
	float dial1_x_max = dial1_x_min + dial1_size_width;
	float dial1_y_min = -(dial1_size_width / 2);
	float dial1_y_max = dial1_y_min + dial1_size_width;
	
	

	//draw the first dial
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glBindTexture(GL_TEXTURE_2D, dial_texture_time.TextureID);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
		glTexCoord2f(0.0f, 0.0f); glVertex2f(dial1_x_min, dial1_y_min);
		glTexCoord2f(0.0f, 1.0f); glVertex2f(dial1_x_min, dial1_y_max);
		glTexCoord2f(1.0f, 1.0f); glVertex2f(dial1_x_max, dial1_y_max);
		glTexCoord2f(1.0f, 0.0f); glVertex2f(dial1_x_max, dial1_y_min);
	glEnd();

	//pop the matrix for thte first dial
	glPopMatrix();

	// dial 2 (Day) 
	glPushMatrix();
	float day_rotation = 0;
	day_rotation = ((float)getDay() / 7) * 360;
	//roatation of the second dial
	glRotatef(day_rotation + (time_rotation/7), 0, 0, 1);
	//size
	float dial2_size_width = 0.45f;
	float dial2_x_min = -(dial2_size_width / 2);
	float dial2_x_max = dial2_x_min + dial2_size_width;
	float dial2_y_min = -(dial2_size_width / 2);
	float dial2_y_max = dial2_y_min + dial2_size_width;

	//draw the first dial
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glBindTexture(GL_TEXTURE_2D, dial_texture_day.TextureID);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
		glTexCoord2f(0.0f, 0.0f); glVertex3f(dial2_x_min, dial2_y_min, 0.001f);
		glTexCoord2f(0.0f, 1.0f); glVertex3f(dial2_x_min, dial2_y_max, 0.001f);
		glTexCoord2f(1.0f, 1.0f); glVertex3f(dial2_x_max, dial2_y_max, 0.001f);
		glTexCoord2f(1.0f, 0.0f); glVertex3f(dial2_x_max, dial2_y_min, 0.001f);
	glEnd();
	
	glPopMatrix();

	// dial 3 (Day) 
	glPushMatrix();
	float month_rotation = 0;
	month_rotation = ((float)(getMonth() - 1) / 12.0f) * 360.0f;
	//month_rotation = ((float)(3 - 1) / 12.0f) * 360.0f;
	//cout << month_rotation << endl;
	//roatation of the third dial
	glRotatef(month_rotation, 0, 0, 1);
	//size
	float dial3_size_width = 0.285f;
	float dial3_x_min = -(dial3_size_width / 2);
	float dial3_x_max = dial3_x_min + dial3_size_width;
	float dial3_y_min = -(dial3_size_width / 2);
	float dial3_y_max = dial3_y_min + dial3_size_width;

	//draw the month dial
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glBindTexture(GL_TEXTURE_2D, dial_texture_month.TextureID);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
		glTexCoord2f(0.0f, 0.0f); glVertex3f(dial3_x_min, dial3_y_min, 0.0011f);
		glTexCoord2f(0.0f, 1.0f); glVertex3f(dial3_x_min, dial3_y_max, 0.0011f);
		glTexCoord2f(1.0f, 1.0f); glVertex3f(dial3_x_max, dial3_y_max, 0.0011f);
		glTexCoord2f(1.0f, 0.0f); glVertex3f(dial3_x_max, dial3_y_min, 0.0011f);
	glEnd();
	
	glPopMatrix();

	//draw the cover dial
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glBindTexture(GL_TEXTURE_2D, dial_texture_cover.TextureID);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
	glBegin(GL_QUADS);
		glTexCoord2f(0.0f, 0.0f); glVertex3f(dial1_x_min, dial1_y_min, 0.0013f);
		glTexCoord2f(0.0f, 1.0f); glVertex3f(dial1_x_min, dial1_y_max, 0.0013f);
		glTexCoord2f(1.0f, 1.0f); glVertex3f(dial1_x_max, dial1_y_max, 0.0013f);
		glTexCoord2f(1.0f, 0.0f); glVertex3f(dial1_x_max, dial1_y_min, 0.0013f);
	glEnd();

	glDisable(GL_TEXTURE_2D);
	glDisable(GL_BLEND);

	glTranslatef(0,0,0.05f);
	glColor3f(0,0,0);
	sprintf(str, "%4.0f%s", bl->m_Data->m_Val[0][bl->m_DataPoint], (char *)bl->m_DataUnits[0].c_str());
	stroke_output(-0.06f, 0.018f, str);	


	glPopMatrix();

		/*
	glLineWidth(2.0f);
	glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
	glBegin(GL_LINE_LOOP);
		glVertex3f(1.04f, 0.03f, 0.05f);
		glVertex3f(1.04f, 0.12f, 0.05f);
		glVertex3f(1.30f, 0.12f, 0.05f);
		glVertex3f(1.30f, 0.03f, 0.05f);
	glEnd();
	glLineWidth(1.0f);
	*/

	
	//pop the matrix for all the dials
	glPopMatrix();
	
	//cout << getDay() << endl;

	//temp_rotate += 0.1f;
	//cout << temp_rotate << endl;
	glutSwapBuffers ();
};

// Secondary rendering of the scene into the depth buffer for object color picking
void mainDisplayPick (void)
{
	/* Clean drawing board */  
	glDisable(GL_LIGHTING);
	glDisable(GL_DITHER);
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glLoadIdentity();
	glRotatef(angley, 1.0f, 0.0f, 0.0f);
	glRotatef(anglex, 0.0f, 1.0f, 0.0f);

	glTranslatef(dx, dy, dz);  
	
	glPushMatrix();
	glTranslatef(0.05f, 0.35f, -0.8f);

	glColor3f(1.0f, 1.0f, 1.0f);
	glBegin(GL_QUADS);
	glVertex3f(-5.0f, -5.0f, -0.1f);
	glVertex3f(-5.0f, 5.0f, -0.1f);
	glVertex3f(5.0f, 5.0f, -0.1f);
	glVertex3f(5.0f, -5.0f, -0.1f);
	glEnd();

   // Draw the zones
  for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NZones; i++){

	  if ((bl->m_Floors[bl->m_SelectFloor].m_HasFill)){		  
		
		  glColor3ub(colors[i][0], colors[i][1], colors[i][2]);

		  for (int p = 0; p < bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_NPoly; p++){			  			  
			  glBegin(GL_POLYGON);
			  for (int j = 0; j < bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_NVertex; j++){
				  glVertex3f(bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_VertexX[j], bl->m_Floors[bl->m_SelectFloor].m_Zones[i].m_Poly[p].m_VertexY[j], 0.0f);
			  }
			  glEnd();
		  }
	  }
  }

  glPopMatrix();
  

  glPushMatrix();
  glRotatef(30, 0.0f, 1.0f, 0.0f);    
  glTranslatef(0.3f, 0.05f, 0.0f);    

  // Draw the building
  for (int i = 0; i < bl->m_NFloors; i++){
	  glColor3ub(colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + i][0], colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + i][1], colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + i][2]);		 		  
	  glPushMatrix();	    
	  glTranslatef(-0.45f, 0.4f +  i * 0.05f, 0.0f);	  
	  glScalef(4.0f, 0.5f, 4.0f);
	  glutSolidCube(0.05f);
	  glPopMatrix();
  }

  glPopMatrix();

  glPushMatrix();
  glTranslatef(attrib_x, attrib_y, attrib_z);

  // Draw the selectable attributes
   for (int i = 0; i < bl->m_NAttr; i++){

	  glColor3ub(colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + i][0], colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + i][1],
		colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + i][2]);		 	

	  glBegin(GL_QUADS);
	  glVertex3f(0.75f, 1.00f - i * 0.07f, 0.0f);
	  glVertex3f(0.75f, 1.05f - i * 0.07f, 0.0f);
	  glVertex3f(0.8f, 1.05f - i * 0.07f, 0.0f);
	  glVertex3f(0.8f, 1.00f - i * 0.07f, 0.0f);
	  glEnd();	  
  }

  for (int i = 0; i < bl->m_Floors[bl->m_SelectFloor].m_NAttr; i++){	  

	  glColor3ub(colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + bl->m_NAttr + i][0],
		  colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + bl->m_NAttr + i][1],
		  colors[bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors + bl->m_NAttr + i][2]);	

	  glBegin(GL_QUADS);
	  glVertex3f(0.75f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
	  glVertex3f(0.75f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
	  glVertex3f(0.8f, 1.05f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
	  glVertex3f(0.8f, 1.00f - ((bl->m_NAttr + i)) * 0.07f, 0.0f);
	  glEnd();
  }

  glPopMatrix();

  glEnable(GL_DITHER);
  glEnable(GL_LIGHTING);
  glutPostRedisplay();
};

/* Callback function for reshaping the main window */
void mainReshape (int w, int h)
{
	widthD = w;
	heightD = h;
	glViewport (0, 0, w, h);
	glMatrixMode (GL_PROJECTION);
	glLoadIdentity ();
	glViewport(0, 0, w, h);
	gluPerspective(45.0f, (float)w/h, 0.01f, 1000); 
	glMatrixMode (GL_MODELVIEW);
	glLoadIdentity ();
	glTranslatef(eyex,eyey,eyez);
};

// OpenGL idle function
void idle (void)
{	
	glutSetWindow (winIdMain);        
	glutPostRedisplay ();
};

// Initialization function
void init(void) 
{	
	glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);

	glEnable(GL_LIGHTING);		
	glShadeModel(GL_SMOOTH); 
	
	float specular[] = {1.0f, 1.0f, 1.0f , 1.0f};
	glLightfv(GL_LIGHT0, GL_SPECULAR, specular);

	float diffuseLight[] = { 0.8f, 0.8f, 0.8f, 1.0f };
	glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);

	float ambient[] = { 0.1f, 0.1f, 0.1f };
	glLightfv(GL_LIGHT0, GL_AMBIENT, ambient);

	float position[] = { 0.0f, 0.0f, 5.0f, 0.0f };
	glLightfv(GL_LIGHT0, GL_POSITION, position);
	
	glEnable(GL_LIGHT0);

	float mcolor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, mcolor);
	
	float specReflection[] = { 0.8f, 0.8f, 0.8f, 1.0f };
	glMaterialfv(GL_FRONT, GL_SPECULAR, specReflection);
	glMateriali(GL_FRONT, GL_SHININESS, 1);

	// enable color tracking
	glEnable(GL_COLOR_MATERIAL);	

	glEnable(GL_DEPTH);
	glEnable (GL_DEPTH_TEST);

	glEnable(GL_POINT_SMOOTH);
	glEnable(GL_LINE_SMOOTH);

	glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
	/* initialize random seed: */
    srand ( time(NULL) );

	getColors();

	monitor = new C_Monitor();

	// Load the expert fuzzy rules
	cout << "**Expert Anomaly Rules**\n";
	monitor->expert_anomaly_rules->loadRules("Rules/rules1.txt");
	cout << "**Expert Comfort Rules**\n";
	monitor->expert_comfort_rules->loadRules("Rules/comfort_rules1.txt");
	cout << "**Expert Efficiency Rules**\n";
	monitor->expert_efficiency_rules->loadRules("Rules/efficiency_rules1.txt");
	bl = new C_Building();

	// Load the selected building
	if (buildingSelect == 0)
	{
		bl->readFile("Building/Building_BBB.txt");	
		
		//bl->m_SelectFloor = 4;
		//monitor->calcMinMaxValFloor(bl,100);
		//monitor->trainOCM(bl,100);

		bl->m_SelectFloor = 6;
		monitor->calcMinMaxValFloor(bl,200);
		monitor->trainOCM(bl,200);

		monitor->loadModel(bl, &(bl->m_Floors[6]), bl->m_NameStr);
		//monitor->loadModel(&(bl->m_Floors[4]), bl->m_NameStr);			
	}
	else if (buildingSelect == 1)
	{
		bl->readFile("Building/Building_Mates.txt");
		monitor->loadModel(bl, &(bl->m_Floors[0]), bl->m_NameStr);					
	}
		
	bl->setCurrTime();
	bl->m_SelectFloor = 0;
	bl->updateSelectFloor();
	// Evaluate the building
	monitor->evalBuilding(bl);

	// Create the GLUI GUI subwindow
	glui = GLUI_Master.create_glui_subwindow(winIdMain, GLUI_SUBWINDOW_LEFT);
	glui->set_main_gfx_window(winIdMain);

	cout << "Initializing the texture object." << endl;
	// Initialize the texturing object and load Textures
	pTextureLoader = new TextureLoader();
	pTextureLoader->LoadTextureFromDisk("Textures/IdFalls-GoldSilver.jpg", &UILogo);
	pTextureLoader->LoadTextureFromDisk("Textures/graph_bg_4.jpg", &Graph); 
	pTextureLoader->LoadTextureFromDisk("Textures/test.gif", &test_texture);
	pTextureLoader->LoadTextureFromDisk("Textures/Dial_Time2.gif", &dial_texture_time);
	pTextureLoader->LoadTextureFromDisk("Textures/Dial_Day2.gif", &dial_texture_day);
	pTextureLoader->LoadTextureFromDisk("Textures/Dial_Month2.gif", &dial_texture_month);
	pTextureLoader->LoadTextureFromDisk("Textures/Dial_Cover.gif", &dial_texture_cover);
}

// Matrix manipulation function for the camera positioning
void multMatrixPoint(float * matrix, float * pointIn, float * pointOut){
		
	pointOut[0] = matrix[0] * pointIn[0] + matrix[4] * pointIn[1] + matrix[8] * pointIn[2] + matrix[12] * pointIn[3];
	pointOut[1] = matrix[1] * pointIn[0] + matrix[5] * pointIn[1] + matrix[9] * pointIn[2] + matrix[13] * pointIn[3];
	pointOut[2] = matrix[2] * pointIn[0] + matrix[6] * pointIn[1] + matrix[10] * pointIn[2] + matrix[14] * pointIn[3];
	pointOut[3] = matrix[3] * pointIn[0] + matrix[7] * pointIn[1] + matrix[11] * pointIn[2] + matrix[15] * pointIn[3];

	pointOut[0] = pointOut[0] / pointOut[3];
	pointOut[1] = pointOut[1] / pointOut[3];
	pointOut[2] = pointOut[2] / pointOut[3];
}

// This function returns the id of the object at the current cursor position
int processPick (int cursorX, int cursorY)
{
	GLint viewport[4];
	GLubyte pixel[3];

	glGetIntegerv(GL_VIEWPORT,viewport);

	glReadPixels(cursorX,viewport[3]-cursorY,1,1, GL_RGB,GL_UNSIGNED_BYTE,(void *)pixel);	

	if ((pixel[0] == 255) && (pixel[1] == 255) && (pixel[2] == 255)){
		// No object selected
		return -1;
	}
	else{
		bool check = false;
		int count = 0;
		while ((!check) && (count < 4096)){
			if ((pixel[0] == colors[count][0]) && (pixel[1] == colors[count][1]) && (pixel[2] == colors[count][2])){				
				check = true;
				return count;
			}
			else{
				count++;
			}
		}

		return -1;
	}  
}

// OpenGL Keyboard handling
void myKeyboard(unsigned char key, int x, int y)
{
	float change = 0;	
	bool prvni = true;
	float dist = sqrt(dx*dx + dy*dy + dz*dz);
	GLfloat * dirMatrix = new GLfloat[16];
	float * direction = new float[4];
	float * newDir = new float[4];

	moveStep = 0.3f;

	switch (key){
		// Camera movement
		case 'q':
				
				change = ((y - (heightD/2.0f))/100);				
				angley += change;
				change = ((x - (widthD/2.0f))/100);				
				anglex += change;
				break;
		case 'w':

			    /// set the original view vector
				direction[0] = 0.0f; direction[1] = 0.0f; direction[2] = -1.0f; direction[3] = 1.0f;

				/// get the ModelView Matrix
				glPushMatrix();
				glLoadIdentity();

				glRotatef(-anglex, 0.0f, 1.0f, 0.0f);
				glRotatef(-angley, 1.0f, 0.0f, 0.0f);			
			
				glGetFloatv(GL_MODELVIEW_MATRIX, dirMatrix); 

				glPopMatrix();

				multMatrixPoint(dirMatrix, direction, newDir);


				change = ((y - (heightD/2.0f))/800.0);
				
				if(1)
				{					
					if (change < 0){
						dx -= moveStep * newDir[0];
						dy -= moveStep * newDir[1];
						dz -= moveStep * newDir[2];
					}
					else{
						dx += moveStep * newDir[0];
						dy += moveStep * newDir[1];
						dz += moveStep * newDir[2];
					}
				}
				
				break;
		case 's':
				/// set the original view vector
				direction[0] = 0.0f; direction[1] = 0.0f; direction[2] = -1.0f; direction[3] = 1.0f;

				/// get the ModelView Matrix
				glPushMatrix();
				glLoadIdentity();

				glRotatef(-(anglex - 90), 0.0f, 1.0f, 0.0f);
				glRotatef(-angley, 1.0f, 0.0f, 0.0f);			
			
				glGetFloatv(GL_MODELVIEW_MATRIX, dirMatrix); 

				glPopMatrix();

				multMatrixPoint(dirMatrix, direction, newDir);

				dx -= moveStep * newDir[0];
				dz -= moveStep * newDir[2];
				break;
		case 'f':
				/// set the original view vector
				direction[0] = 0.0f; direction[1] = 0.0f; direction[2] = -1.0f; direction[3] = 1.0f;

				/// get the ModelView Matrix
				glPushMatrix();
				glLoadIdentity();

				glRotatef(-(anglex - 90), 0.0f, 1.0f, 0.0f);
				glRotatef(-angley, 1.0f, 0.0f, 0.0f);			
			
				glGetFloatv(GL_MODELVIEW_MATRIX, dirMatrix); 

				glPopMatrix();

				multMatrixPoint(dirMatrix, direction, newDir);

				dx += moveStep * newDir[0];
				dz += moveStep * newDir[2];
				break;
		case 'e':
				dy += moveStep;
				break;
		case 'd':
				dy -= moveStep;
				break;			
		case 't':
			bl->m_SelectFloor = 4;
				monitor->calcMinMaxValFloor(bl,100);
				monitor->trainOCM(bl,100);
				//monitor->loadModel(&(bl->m_Floors[6]), bl->m_NameStr);
				monitor->loadModel(bl, &(bl->m_Floors[4]), bl->m_NameStr);
				//monitor->calcMinMaxVal(&(bl->m_Floors[4]));
				//monitor->trainOCM(&(bl->m_Floors[4]));
				break;	
		// Exit
		case 'j':
			exit(0);
			break;	
		case 'x':
			x_temp -= 0.05f;
			cout << x_temp << endl;
		break;
		case 'X':
			x_temp += 0.05f;
		break;
		case 'y':
			y_temp -= 0.01f;
			cout << y_temp << endl;
		break;
		case 'Y':
			y_temp += 0.01f;
		break;
		case 'z':
			z_temp -= 0.01f;
			cout << z_temp << endl;
		break;
		case 'Z':
			z_temp += 0.01f;
		break;
		case 'c':
			ctrl_display_anomalies = !ctrl_display_anomalies;
		break;
	}
	glutPostRedisplay();
}

// OpenGL callback function for handling special keys
void mySpecialFunc(int key, int x, int y)
{
	switch(key)
	{
	case GLUT_KEY_UP:
		{
			dz += moveStep;
			break;
		}
	case GLUT_KEY_DOWN:
		{
			dz -= moveStep;
			break;
		}
	}
	glutPostRedisplay ();
}

// OpenGL mouse callback function
void myMouseFunc(int button, int state, int x, int y){
	switch (button){
		case GLUT_LEFT_BUTTON:
			if (state == GLUT_DOWN){															

				// Handle object selection
				mainDisplayPick();	
				
				int sel = processPick(x, y);				
				
				if (sel == bl->m_Floors[bl->m_SelectFloor].m_SelectZone){
					bl->m_Floors[bl->m_SelectFloor].m_SelectZone = -1;
					buttonMark->disable();					
				}
				else if (sel < bl->m_Floors[bl->m_SelectFloor].m_NZones){

					if (sel > -1)
					{
						bl->m_Floors[bl->m_SelectFloor].m_SelectZone = sel;	
						cout << "Floor = " << bl->m_SelectFloor << endl;
						for(int x = 0; x < bl->m_Floors[bl->m_SelectFloor].m_Zones[sel].m_Data->m_NAttr; x++)
						{
							cout << "val = " << bl->m_Floors[bl->m_SelectFloor].m_Zones[sel].m_Data->m_Val[x][bl->m_DataPoint] << " :: ";
							cout << "min = " <<  bl->m_Floors[bl->m_SelectFloor].m_Zones[sel].m_Data->m_MinVal[x] << " :: ";
							cout << "max = " <<  bl->m_Floors[bl->m_SelectFloor].m_Zones[sel].m_Data->m_MaxVal[x] << endl;
						}

						float anomICl = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI;
						float anomIRl = bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules;

						if (monitor->m_AnomMode == 0){
							if (anomICl > monitor->m_Threshold){

								monitor->printAnomalyCluster(bl);

								buttonMark->enable();
							}
							else{
								buttonMark->disable();
							}					
						}
						else if (monitor->m_AnomMode == 1){
							if (anomIRl > monitor->m_Threshold){

								monitor->printAnomalyRule(bl);

								buttonMark->enable();
							}
							else{
								buttonMark->disable();
							}					
						}
						else{
							if (max(anomICl, anomIRl) > monitor->m_Threshold){
								if (anomICl > anomIRl){
									monitor->printAnomalyCluster(bl);
								}
								else{
									monitor->printAnomalyRule(bl);
								}

								buttonMark->enable();
							}
							else{
								buttonMark->disable();
							}
						}
					}
				}
				else{		

					if (sel < (bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors))
					{
						int sel_floor = sel - bl->m_Floors[bl->m_SelectFloor].m_NZones;
						if(bl->m_Floors[sel_floor].m_IsReady)
						{
							bl->m_SelectFloor = sel_floor;
							if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
							{
								monitor->evalFloor(bl, bl->m_DataPoint);				
							}
						}
					}
					else
					{
						int attrSel = sel - (bl->m_Floors[bl->m_SelectFloor].m_NZones + bl->m_NFloors);

						if(attrSel < bl->m_Floors[bl->m_SelectFloor].m_NAttr && attrSel > 0)
						{
							cout << "Attribute = " << attrSel << endl;
							cout << "val = " << bl->m_Floors[bl->m_SelectFloor].m_Data->m_Val[attrSel-1][bl->m_DataPoint] << endl;
							cout << "min = " <<  bl->m_Floors[bl->m_SelectFloor].m_Data->m_MinVal[attrSel-1] << endl;
							cout << "max = " <<  bl->m_Floors[bl->m_SelectFloor].m_Data->m_MaxVal[attrSel-1] << endl;
						}
						if(attrSel == 0)
						{
							cout << "Building = " << attrSel << endl;
							cout << "val = " << bl->m_Data->m_Val[0][bl->m_DataPoint] << endl;
							cout << "min = " <<  bl->m_Data->m_MinVal[0] << endl;
							cout << "max = " <<  bl->m_Data->m_MaxVal[0] << endl;
						}
						monitor->m_ShowAttr[attrSel] = !monitor->m_ShowAttr[attrSel];
					}
				}
			}
		break;
	}
}

// Callback function for GLUI
void control_cb( int ID )
{
	switch (ID)
	{
		//display mode
	case 10:
		monitor->evalBuilding(bl);
		//cout << use_alarms << endl;
	break;
	// Translate time
	case 100:
		if (translateTime->get_x() < 0)
		{
			bl->m_DataPoint = 0;
			translateTime->set_x(0);
		}
		else if (translateTime->get_x() >= bl->m_NData)
		{
			bl->m_DataPoint = bl->m_NData - 1;
			translateTime->set_x(bl->m_NData - 1);
		}
		else
		{
			bl->m_DataPoint = (int)translateTime->get_x();
		}					
		
		bl->updateTime();

		monitor->evalBuilding(bl);	

		if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1)
		{
			if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold)
			{
				buttonMark->enable();
			}
			else
			{
				buttonMark->disable();
			}
		}

	break;
	//time back
	case 110:
		bl->decData();			

		monitor->evalBuilding(bl);
		
		translateTime->set_x((float)bl->m_DataPoint);

		if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1){
			if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold){
				buttonMark->enable();
			}
			else{
				buttonMark->disable();
			}
		}

	break;
	//time forward
	case 120:
		bl->incData();	

		monitor->evalBuilding(bl);
		
		translateTime->set_x((float)bl->m_DataPoint);

		if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1){
			if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold){
				buttonMark->enable();
			}
			else{
				buttonMark->disable();
			}
		}		
	break;
	//anomaly mode radio
	case 200:
		prev_anom_mode = monitor->m_AnomMode;
	break;
//TODO :: evaluate min max alarms when going to next or previous anomaly
	// Go to next anomaly
	case 210:
		if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
		{
			if (bl->m_DataPoint < bl->m_NData - 2)
			{
				bl->m_DataPoint++;
				monitor->evalBuilding(bl);
				translateTime->set_x((float)bl->m_DataPoint);
				
				//cluster based
				if (monitor->m_AnomMode == 0)
				{
					while ((bl->m_DataPoint < bl->m_NData - 1) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Cluster))
					{
						bl->m_DataPoint++;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//rule based anomaly
				else if (monitor->m_AnomMode == 1)
				{
					while ((bl->m_DataPoint < bl->m_NData - 1) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl->m_DataPoint++;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//combined cluster and rule based anomaly
				else if (monitor->m_AnomMode == 2)
				{
					while ((bl->m_DataPoint < bl->m_NData - 1) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Cluster) &&
						(!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl->m_DataPoint++;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//Comfort based
				else if (monitor->m_AnomMode == 3)
				{
					while ((bl->m_DataPoint < bl->m_NData - 1) && (!bl->m_Floors[bl->m_SelectFloor].has_comfort_anomaly))
					{
						bl->m_DataPoint++;
						monitor->evalFloor(bl, bl->m_DataPoint);
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//efficiency based
				else if (monitor->m_AnomMode == 4)
				{
					while ((bl->m_DataPoint < bl->m_NData - 1) && (!bl->m_Floors[bl->m_SelectFloor].has_efficiency_anomaly))
					{
						bl->m_DataPoint++;
						monitor->evalFloor(bl, bl->m_DataPoint);			
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
			}

			monitor->evalBuilding(bl);

			if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1){
				if (monitor->m_AnomMode == 0){
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
				else if (monitor->m_AnomMode == 1)
				{
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
				else{
					if ((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold) ||
						(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold)){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
			}
		}	

	break;	
	// Go to the previous anomaly
	case 220:
		if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
		{
			if (bl->m_DataPoint > 0)
			{
				bl->m_DataPoint--;
				monitor->evalBuilding(bl);
				translateTime->set_x((float)bl->m_DataPoint);

				//cluster based
				if (monitor->m_AnomMode == 0)
				{
					while ((bl->m_DataPoint > 0) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Cluster))
					{
						bl->m_DataPoint--;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//rule based anomaly
				else if (monitor->m_AnomMode == 1)
				{
					while ((bl->m_DataPoint > 0) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl->m_DataPoint--;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//combined cluster and rule based anomaly
				else if (monitor->m_AnomMode == 2)
				{
					while ((bl->m_DataPoint > 0) && (!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Cluster) &&
						(!bl->m_Floors[bl->m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl->m_DataPoint--;
						monitor->evalFloor(bl, bl->m_DataPoint);					
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//Comfort based
				else if (monitor->m_AnomMode == 3)
				{
					while ((bl->m_DataPoint > 0) && (!bl->m_Floors[bl->m_SelectFloor].has_comfort_anomaly))
					{
						bl->m_DataPoint--;
						monitor->evalFloor(bl, bl->m_DataPoint);
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
				//efficiency based
				else if (monitor->m_AnomMode == 4)
				{
					while ((bl->m_DataPoint > 0) && (!bl->m_Floors[bl->m_SelectFloor].has_efficiency_anomaly))
					{
						bl->m_DataPoint--;
						monitor->evalFloor(bl, bl->m_DataPoint);			
						translateTime->set_x((float)bl->m_DataPoint);
					}
				}
			}

			monitor->evalBuilding(bl);

			if (bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1){
				if (monitor->m_AnomMode == 0){
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
				else if (monitor->m_AnomMode == 1){
					if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
				else{
					if ((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold) ||
						(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold)){
						buttonMark->enable();
					}
					else{
						buttonMark->disable();
					}
				}
			}
		}

	break;

	// Mark as normal button
	case 301:
		if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
		{
			monitor->updateMode(bl);
			monitor->evalBuilding(bl);
			monitor->evalFloor(bl, bl->m_DataPoint);
			cout << "Normal behavior model has been updated." << endl;
		}
	break;
	// Save the model
	case 302:
		if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
		{
			monitor->saveModel(&(bl->m_Floors[bl->m_SelectFloor]), bl->m_NameStr);
		}
		cout << "Saved the model for the selected floor." << endl;
	break;
	// Reload rules button :: Reloads the expert fuzzy rules
	case 303:
		//monitor->m_FIS->reloadRules("Rules/rules1.txt");
		monitor->expert_anomaly_rules->reloadRules("Rules/rules1.txt");
		monitor->expert_comfort_rules->reloadRules("Rules/comfort_rules1.txt");
		monitor->expert_efficiency_rules->reloadRules("Rules/efficiency_rules1.txt");

		monitor->evalBuilding(bl);

		cout << "Expert rules reloaded" << endl;
	break;
	// Generate Report button
	case 304:
		if (bl->m_Floors[bl->m_SelectFloor].m_IsReady)
		{
			if (monitor->m_AnomMode == 0)
			{
				monitor->generateReportCluster(bl, 0, bl->m_NData);	
			}
			else if (monitor->m_AnomMode == 1)
			{
				monitor->generateReportRules(bl, 0, bl->m_NData);
			}
			else
			{
				monitor->generateReportCluster(bl, 0, bl->m_NData);	
				monitor->generateReportRules(bl, 0, bl->m_NData);
			}
			
			monitor->evalBuilding(bl);
		}
		else
		{
			cout << "Data for the selected floor are not available." << endl;
		}
	break;

	// Feature Vector Selection
	case 400:		
		// Check the number of enabled attributes
		monitor->m_NAnt_Enabled = 0;
		cout <<  " ----- " << endl;
		for (int i = 0; i < monitor->m_DimF; i++)
		{
			cout << i << " :: " << monitor->m_AttrUse[i] << endl;
			if (monitor->m_AttrUse[i] == 1)
			{
				monitor->m_NAnt_Enabled++;
			}
		}

		if (monitor->m_NAnt_Enabled == 0)
		{
			monitor->m_NAnt_Enabled = 1;
			monitor->m_AttrUse[0] = 1;				
			checkZT->set_int_val(monitor->m_AttrUse[0]);
		}

		if (spinAnt->get_int_val() > monitor->m_NAnt_Enabled)
		{
			spinAnt->set_int_val(monitor->m_NAnt_Enabled);
		}
		spinAnt->set_int_limits(1, monitor->m_NAnt_Enabled);

		monitor->evalBuilding(bl);

		//set the check boxes
		checkZT->set_int_val(monitor->m_AttrUse[0]);
		for(int a = 1; a < _FNL_num_zone_dims; a++)
		{
			checkZL[a]->set_int_val(monitor->m_AttrUse[a]);
		}
		checkTime->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims]);
		for(int a = 0; a < _FNL_num_building_dims; a++)
		{
			checkBL[a]->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + a]);
		}
		for(int a = 0; a < _FNL_num_ahu_dims; a++)
		{
			checkAL[a]->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a]);
		}

		/*
		if (monitor->m_AnomMode == 0)
		{
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold)){

				monitor->printAnomalyCluster(bl);						
			}
		}
		else if (monitor->m_AnomMode == 1)
		{
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold)){

				monitor->printAnomalyRule(bl);						
			}
		}
		else
		{
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold) ||
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold))){

				if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI >
					bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules){

					monitor->printAnomalyCluster(bl);						
				}
				else{
					monitor->printAnomalyRule(bl);						
				}
			}
		}
		*/
	break;
	case 401:
		//select all zone level attribs
		for(int i = 0; i < _FNL_num_zone_dims; i++)
		{
			monitor->m_AttrUse[i] = 1;
		}
		//select time
		monitor->m_AttrUse[_FNL_num_zone_dims] = 1;
		control_cb(400);
	break;
	case 402:
		//deselect all zone level attribs
		for(int i = 0; i < _FNL_num_zone_dims; i++)
		{
			monitor->m_AttrUse[i] = 0;
		}
		//deselect time
		monitor->m_AttrUse[_FNL_num_zone_dims] = 0;
		control_cb(400);
	break;
	case 411:
		//select all building level attribs
		for(int i = 0; i < _FNL_num_building_dims;  i++)
		{
			monitor->m_AttrUse[_FNL_num_zone_dims + 1 + i] = 1;
		}
		control_cb(400);
	break;
	case 412:
		//deselect all bilding level attribs
		for(int i = 0; i < _FNL_num_building_dims;  i++)
		{
			monitor->m_AttrUse[_FNL_num_zone_dims + 1 + i] = 0;
		}
		control_cb(400);
	break;
	case 421:
		//select all building level attribs
		for(int i = 0; i < _FNL_num_ahu_dims;  i++)
		{
			monitor->m_AttrUse[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + i] = 1;
		}
		control_cb(400);
	break;
	case 422:
		//deselect all bilding level attribs
		for(int i = 0; i < _FNL_num_ahu_dims;  i++)
		{
			monitor->m_AttrUse[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + i] = 0;
		}
		control_cb(400);
	break;
	// Anomaly Threshold
	case 500:
		monitor->m_Threshold = spinnThreshold->get_float_val();
		monitor->evalBuilding(bl);
	break;
	//select number of sensors
	case 600:
		if (monitor->m_AnomMode == 0){
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold)){

				monitor->printAnomalyCluster(bl);						
			}
		}
		else if (monitor->m_AnomMode == 1){
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold)){
					
				monitor->printAnomalyRule(bl);						
			}
		}
		else{
			if ((bl->m_Floors[bl->m_SelectFloor].m_SelectZone > -1) && 
				((bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI > monitor->m_Threshold) ||
				(bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor->m_Threshold))){

				if (bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI >
					bl->m_Floors[bl->m_SelectFloor].m_Zones[bl->m_Floors[bl->m_SelectFloor].m_SelectZone].m_AnomalI_Rules){

					monitor->printAnomalyCluster(bl);						
				}
				else{
					monitor->printAnomalyRule(bl);						
				}
			}
		}
	break;
	//quit
	case 900:
		exit(1);
	break;
	}
}

// Creates the GLUI menu
void createGLUI()
{

	// Top panel		
	GLUI_Panel * panelDisp = glui->add_panel("Floor Display");

	GLUI_RadioGroup * groupDisp = glui->add_radiogroup_to_panel(panelDisp, &monitor->m_DispMode);//, 10, control_cb);
	glui->add_radiobutton_to_group(groupDisp, "Data");
	glui->add_radiobutton_to_group(groupDisp, "Anomaly");

	
	GLUI_Checkbox * checkAlarm = glui->add_checkbox_to_panel(panelDisp, "Alarms", &(use_alarms), 10, control_cb);
	checkAlarm->set_int_val(use_alarms);

	glui->add_column_to_panel(panelDisp, 0);

	GLUI_RadioGroup * dataDisp = glui->add_radiogroup_to_panel(panelDisp, &selected_zone_dim);//, 10, control_cb);
	glui->add_radiobutton_to_group(dataDisp, "ZTemp");
	glui->add_radiobutton_to_group(dataDisp, "Light");
	glui->add_radiobutton_to_group(dataDisp, "CO2");
	glui->add_radiobutton_to_group(dataDisp, "RTemp");
	
	//glui->add_radiobutton_to_group(groupDisp, "Comfort");
	//glui->add_radiobutton_to_group(groupDisp, "Efficiency");

	//time 100 set
	GLUI_Panel * panelTime = glui->add_panel("Set Time");
	
	translateTime = glui->add_translation_to_panel(panelTime, "Time", GLUI_TRANSLATION_X, NULL, 100, control_cb);		
	translateTime->set_x((float)bl->m_DataPoint);	
	glui->add_column_to_panel(panelTime, 0);	
	GLUI_Panel * panelMove = glui->add_panel_to_panel(panelTime, "Move");
	glui->add_button_to_panel(panelMove, "Back", 110, control_cb);
	glui->add_column_to_panel(panelMove, 0);
	glui->add_button_to_panel(panelMove, "Forward", 120, control_cb);	
	
	//anomaly 200 set
	GLUI_Panel * panelAnomaly = glui->add_panel("Anomaly");
	GLUI_Panel * panelAnomalySelect = glui->add_panel_to_panel(panelAnomaly, "");
	GLUI_RadioGroup * groupAnom = glui->add_radiogroup_to_panel(panelAnomalySelect, &monitor->m_AnomMode, 200, control_cb);
	glui->add_radiobutton_to_group(groupAnom, "Cluster Based");	
	glui->add_radiobutton_to_group(groupAnom, "Expert Rule Based");
	glui->add_radiobutton_to_group(groupAnom, "Combined");
	glui->add_radiobutton_to_group(groupAnom, "Comfort");
	glui->add_radiobutton_to_group(groupAnom, "Efficiency");
	glui->add_column_to_panel(panelAnomalySelect,1);
	GLUI_Panel * panelGoTo = glui->add_panel_to_panel(panelAnomalySelect, "Go To");
	glui->add_button_to_panel(panelGoTo, "Next", 210, control_cb);	
	glui->add_button_to_panel(panelGoTo, "Previous", 220, control_cb);	
	
	//model control 300 set
	GLUI_Panel * panelAnomalyCtrl = glui->add_panel_to_panel(panelAnomaly, "");
	
	buttonMark = glui->add_button_to_panel(panelAnomalyCtrl, "Mark as Normal", 301, control_cb);	
	buttonMark->disable();
	buttonMark->set_w(110);
	GLUI_Button * button_savemodel = glui->add_button_to_panel(panelAnomalyCtrl, "Save Model", 302, control_cb);		
	button_savemodel->set_w(110);
	glui->add_column_to_panel(panelAnomalyCtrl, 1);
	GLUI_Button * button_reload = glui->add_button_to_panel(panelAnomalyCtrl, "Reload Rules", 303, control_cb);
	button_reload->set_w(110);
	GLUI_Button * button_report = glui->add_button_to_panel(panelAnomalyCtrl, "Generate Report", 304, control_cb);	
	button_report->set_w(110);

	//feature selection 400 set
	GLUI_Panel * panelFeature = glui->add_panel_to_panel(panelAnomaly,"Feature Selection");	

	//zone level
	//zone temp
	GLUI_StaticText* zone_level_static = glui->add_statictext_to_panel(panelFeature, "Zone Level");
	checkZT = glui->add_checkbox_to_panel(panelFeature, (char*)_FNL_dim_names_short[0].c_str(), &(monitor->m_AttrUse[0]), 400, control_cb);
	checkZT->set_int_val(monitor->m_AttrUse[0]);
	//remaining zone level attribs
	checkZL = new GLUI_Checkbox*[_FNL_num_zone_dims - 1];
	for(int a = 1; a < _FNL_num_zone_dims; a++)
	{
		checkZL[a] = glui->add_checkbox_to_panel(panelFeature, (char*)_FNL_dim_names_short[a].c_str(), &(monitor->m_AttrUse[a]), 400, control_cb);
		checkZL[a]->set_int_val(monitor->m_AttrUse[a]);
	}
	//time
	checkTime = glui->add_checkbox_to_panel(panelFeature, 
					(char*)_FNL_dim_names_short[_FNL_num_zone_dims].c_str(), 
					&(monitor->m_AttrUse[_FNL_num_zone_dims]), 400, control_cb);
	checkTime->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims]);
	//select all button
	GLUI_Button * button_select_all_zone = glui->add_button_to_panel(panelFeature, "Select All", 401, control_cb);		
	button_select_all_zone->set_w(90);
	GLUI_Button * button_deselect_all_zone = glui->add_button_to_panel(panelFeature, "Deselect All", 402, control_cb);		
	button_deselect_all_zone->set_w(90);

	glui->add_column_to_panel(panelFeature);


	//Building level
	GLUI_StaticText* building_level_static = glui->add_statictext_to_panel(panelFeature, "Building Level");
	checkBL = new GLUI_Checkbox * [_FNL_num_building_dims];
	for(int a = 0; a < _FNL_num_building_dims; a++)
	{
		checkBL[a] = glui->add_checkbox_to_panel(panelFeature, 
						(char*)_FNL_dim_names_short[_FNL_num_zone_dims + 1 + a].c_str(), 
						&(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + a]), 400, control_cb);
		checkBL[a]->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + a]);
	}
	//select all button
	GLUI_Button * button_select_all_building = glui->add_button_to_panel(panelFeature, "Select All", 411, control_cb);		
	button_select_all_building->set_w(90);
	GLUI_Button * button_deselect_all_building = glui->add_button_to_panel(panelFeature, "Deselect All", 412, control_cb);		
	button_deselect_all_building->set_w(90);
	
	glui->add_column_to_panel(panelFeature);

	//AHU level
	GLUI_StaticText* ahu_level_static = glui->add_statictext_to_panel(panelFeature, "AHU Level");
	checkAL = new GLUI_Checkbox * [_FNL_num_ahu_dims];
	for(int a = 0; a < _FNL_num_ahu_dims; a++)
	{
		checkAL[a] = glui->add_checkbox_to_panel(panelFeature, 
						(char*)_FNL_dim_names_short[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a].c_str(), 
						&(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a]), 400, control_cb);
		checkAL[a]->set_int_val(monitor->m_AttrUse[_FNL_num_zone_dims + 1 + _FNL_num_building_dims + a]);
	}
	//select all button
	GLUI_Button * button_select_all_ahu = glui->add_button_to_panel(panelFeature, "Select All", 421, control_cb);		
	button_select_all_ahu->set_w(90);
	GLUI_Button * button_deselect_all_ahu = glui->add_button_to_panel(panelFeature, "Deselect All", 422, control_cb);		
	button_deselect_all_ahu->set_w(90);

	//threshold 500 set
	GLUI_Panel * panelSetNumbers = glui->add_panel_to_panel(panelAnomaly, "");
	spinnThreshold = glui->add_spinner_to_panel(panelSetNumbers, "Threshold   ", GLUI_SPINNER_FLOAT, NULL, 500, control_cb);
	spinnThreshold->set_float_limits(0.0, 1.0, GLUI_LIMIT_CLAMP);
	spinnThreshold->set_float_val(monitor->m_Threshold);
	//spinnThreshold->set_w(300);

	//number of sensors 600 set
	spinAnt = glui->add_spinner_to_panel(panelSetNumbers, "Number of Sensors ", GLUI_SPINNER_INT, &(monitor->m_NAnt_Sel), 600, control_cb);
	spinAnt->set_int_limits(1, monitor->m_NAnt_Enabled);

	//quit 900 set
	glui->add_button( "Quit", 0,(GLUI_Update_CB)exit );
}

// Main function
int main (int argc, char **argv)
{
	_FNL_loadFLNames();
	_FNL_print();
	/*
	FuzzyRuleSet * myfis = new FuzzyRuleSet();
	myfis->output_dim_name = "Comfort";
	//myfis->loadRules("FL_Rules/rules1.txt");
	myfis->loadRules("Rules/comfort_rules1.txt");

	FuzzySystemT1Centroid * my_fls = new FuzzySystemT1Centroid();
	my_fls->m_OutputName = "Comfort";
	//my_fls->printOut();

	ofstream out_file;
	out_file.open("TempOut/out7.csv");
	float in[10] = {0,0,0,0,0,0,0,0,0,0};
	float zt = 0;
	float dt = 0;
	float ov = 0;
	for(int i = 0; i < 100; i++)
	{
		for(int j = 0; j < 100; j++)
		{
			in[0] = zt;
			in[1] = dt;
			ov = my_fls->evalOutFLS(myfis,in);
			dt += 0.01f;
			out_file << ov;
			if(j < 99)
				out_file << ",";
		}
		out_file << endl;
		zt += 0.01f;
		dt = 0;
	}
	out_file.close();
	*/

	/* Glut initializations */
	glutInit (&argc, argv);	
	glutInitDisplayMode (GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH);
	glutInitWindowPosition (555, 0);
	glutInitWindowSize (WIDTH, HEIGHT);
 

	/* Main window creation and setup */
	winIdMain = glutCreateWindow (TITLE);
	glutDisplayFunc (mainDisplay); 
	//glutFullScreen();

	//glutMouseFunc(myMouseFunc);
	GLUI_Master.set_glutMouseFunc(myMouseFunc);
	//glutSpecialFunc(mySpecialFunc);
	GLUI_Master.set_glutSpecialFunc(mySpecialFunc);
	//glutKeyboardFunc(myKeyboard);
	GLUI_Master.set_glutKeyboardFunc(myKeyboard);
	//glutReshapeFunc (mainReshape);
	GLUI_Master.set_glutReshapeFunc(mainReshape);

	//glutIdleFunc (idle);
	GLUI_Master.set_glutIdleFunc(idle);

	init();

	createGLUI();   
	glutMainLoop();
}