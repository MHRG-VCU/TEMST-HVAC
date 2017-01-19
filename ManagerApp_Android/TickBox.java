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

import javax.microedition.khronos.opengles.GL10;

public class TickBox 
{
	public int tick_num_elements;
	public int [] tick_state;
	Button [] tick_button;
	public float [] tick_vertex;	// intial vertices of the radio (upper r,lower r,lower l,upper l)
	
	public TickBox(float [] _vert, int _numelements)
	{
		this.tick_num_elements = _numelements;
		this.tick_vertex = _vert;
		this.tick_state = new int[this.tick_num_elements];
		for(int i = 0; i < this.tick_num_elements; i++)
		{
			this.tick_state[i] = 1;
		}
		this.initButtons();
		//this.updateSelected(this.radio_selected);
	}	
	
	public void initButtons()
	{
		float lx = this.tick_vertex[0];
		float rx = this.tick_vertex[4];
		float uy = this.tick_vertex[1];
		float ly = this.tick_vertex[3];
		
		float button_height = ly - uy;
		float button_width = rx - lx;
		
		this.tick_button = new Button[this.tick_num_elements];
		for(int i = 0; i < this.tick_num_elements; i++)
		{
			float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
			this.tick_button[i] = new Button(vert_b_temp);
			
			//initialize to pressed
			this.tick_button[i].button_down = true;
			
			//do multiple columns
			if(i == 3)
			{
				lx = this.tick_vertex[0] + button_width + 0.05f;
				rx = lx + button_width;
				uy = this.tick_vertex[1];
				ly = this.tick_vertex[3];
			}
			else if(i == 7)
			{
				lx = this.tick_vertex[0] + (2 * (button_width + 0.05f));
				rx = lx + button_width;
				uy = this.tick_vertex[1];
				ly = this.tick_vertex[3];
			}
			else
			{
				uy += button_height + 0.01f;
				ly += button_height + 0.01f;
			}
		}		
	}
	
	public void updateSelected()
	{
		for(int i = 0; i < this.tick_num_elements; i++)
		{
			if(this.tick_button[i].button_down == true)
			{
				this.tick_state[i] = 1;
			}
			else
			{
				this.tick_state[i] = 0;
			}
		}
	}
	
	public boolean tickPress(float _x, float _y)
	{
		for(int i = 0; i < this.tick_num_elements; i++)
		{
			if(this.tick_button[i].tap(_x, _y))
			{
				this.tick_button[i].button_down = !this.tick_button[i].button_down;
				this.updateSelected();
				return true;
			}
		}
		return false;
	}	
	public void draw(GL10 _gl)
	{
		for(int i = 0; i < this.tick_num_elements; i++)
		{
			this.tick_button[i].draw(_gl);
		}
	}	
}
