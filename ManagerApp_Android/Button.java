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

public class Button 
{
	public float [] vertex;				// vertices of the button (upper r,lower r,lower l,upper l)
	public boolean button_down;			// flag to indicate if the button is down
	public boolean button_over;			// flag to indicate if we are over the button
	public boolean button_disabled;		// flag to indicated whether the button is disabled
	int [] texture_up = new int [1]; 	// texture for button up
	int [] texture_down = new int [1]; 	// texture for button down
	int [] texture_disabled = new int [1]; 	// texture for button disbled
	public RectangleTexture button_drawable;		// the drawable object
	public Touchable button_touchable;	// touchable object
	String button_text;
	
	public Button ()
	{
		this.vertex = null;
		this.button_down = false;
		this.button_over = false;
		this.button_disabled = false;
		this.button_touchable = null;
		this.button_drawable = null;
		this.button_text = "";
	}
	
	public Button (float [] _vert)
	{
		this.vertex = _vert;
		this.button_down = false;
		this.button_over = false;
		this.button_touchable = new Touchable(_vert);
		this.button_drawable = new RectangleTexture(_vert);
		this.button_text = "";
	}
	
	public void draw(GL10 _gl)
	{
		if(this.button_down)
		{
			this.button_drawable.draw(_gl, this.texture_down[0]);
		}
		else if(this.button_over)
		{
			this.button_drawable.draw(_gl, this.texture_down[0]);
			this.button_over = false;
		}
		else if (this.button_disabled)
		{
			this.button_drawable.draw(_gl, this.texture_disabled[0]);
		}
		else
		{
			this.button_drawable.draw(_gl, this.texture_up[0]);
		}
	}
	
	public void press(float _x, float _y)
	{
		if(!this.button_disabled)
		{
			if(this.button_touchable.isInside(_x, _y))
			{
				this.button_down = !this.button_down; 
			}
		}
	}
	
	public boolean tap(float _x, float _y)
	{
		if(!this.button_disabled)
		{
			if(this.button_touchable.isInside(_x, _y))
			{
				return true; 
			}
		}
		return false;
	}
	

}
