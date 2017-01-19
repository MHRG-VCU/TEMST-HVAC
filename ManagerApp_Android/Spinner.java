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

public class Spinner 
{

	public float range_min;
	public float range_max;
	public float increment;
	public float spin_value;
	public Button spin_up;
	public Button spin_down;
	public RectangleColor text_area;
	public float [] spin_vertex;
	public String spin_text;
	
	public Spinner(float [] _vert, float _min, float _max, float _inc)
	{
		this.spin_vertex = _vert;
		this.range_min = _min;
		this.range_max = _max;
		this.increment = _inc;
		this.spin_value = this.range_min;
		initButtons();
	}
	
	public void initButtons()
	{
		float lx = this.spin_vertex[0];
		float rx = this.spin_vertex[4];
		float uy = this.spin_vertex[1];
		float ly = this.spin_vertex[3];
		
		float button_height = ly - uy;
		float text_width = 0.15f;
		float button_width = 0.12f;
		
		lx += 0.37f;
		rx = lx + text_width;
		float vert_text_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy };
		this.text_area = new RectangleColor(vert_text_temp);
		
		lx = rx +  0.02f;
		rx = lx + button_width;
		float vert_up_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy };
		this.spin_up = new Button(vert_up_temp);
		
		lx = rx + 0.02f;
		rx = lx + button_width;
		float vert_down_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy };
		this.spin_down = new Button(vert_down_temp);
	}
	
	public boolean spinPress(float _x, float _y)
	{
		if(this.spin_up.tap(_x, _y))
		{
			this.spin_value += this.increment;
			if(this.spin_value > this.range_max)
			{
				this.spin_value = this.range_max;
				this.spin_up.button_down = true;
			}
			return true;
		}
		else if(this.spin_down.tap(_x, _y))
		{
			this.spin_value -= this.increment;
			if(this.spin_value < this.range_min)
			{
				this.spin_value = this.range_min;
				this.spin_down.button_down = true;
			}
			return true;
		}
		return false;
	}
	
	public void draw(GL10 gl)
	{
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.text_area.draw(gl);
		this.spin_up.draw(gl);
		this.spin_down.draw(gl);
		this.spin_up.button_down = false;
		this.spin_down.button_down = false;
	}
}
