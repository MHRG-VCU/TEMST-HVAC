
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

public class RadioButton 
{
	public int radio_num_elements;
	public int [] radio_state;
	public int radio_selected;		//selected value
	Button [] radio_button;
	public float [] radio_vertex;	// intial vertices of the radio (upper r,lower r,lower l,upper l)
	
	public RadioButton(float [] _vert, int _numelements)
	{
		this.radio_num_elements = _numelements;
		this.radio_vertex = _vert;
		this.radio_state = new int[this.radio_num_elements];
		this.radio_selected = 0;
		this.initButtons();
		this.updateSelected(this.radio_selected);
	}
	
	public void initButtons()
	{
		float lx = this.radio_vertex[0];
		float rx = this.radio_vertex[4];
		float uy = this.radio_vertex[1];
		float ly = this.radio_vertex[3];
		
		float button_height = ly - uy;
		
		this.radio_button = new Button[this.radio_num_elements];
		for(int i = 0; i < this.radio_num_elements; i++)
		{
			float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
			this.radio_button[i] = new Button(vert_b_temp);
			
			uy += button_height + 0.01f;
	    	ly += button_height + 0.01f;
		}
	}
	
	public void updateSelected(int _sel)
	{
		for(int i = 0; i < this.radio_num_elements; i++)
		{
			this.radio_state[i] = 0;
			this.radio_button[i].button_down = false;
		}
		this.radio_selected = _sel;
		this.radio_state[this.radio_selected] = 1;
		this.radio_button[this.radio_selected].button_down = true;
	}

	public boolean radioPress(float _x, float _y)
	{
		for(int i = 0; i < this.radio_num_elements; i++)
		{
			if(this.radio_button[i].tap(_x, _y))
			{
				updateSelected(i);
				return true;
			}
		}
		return false;
	}
	
	public void draw(GL10 _gl)
	{
		for(int i = 0; i < this.radio_num_elements; i++)
		{
			this.radio_button[i].draw(_gl);
		}
	}
}
