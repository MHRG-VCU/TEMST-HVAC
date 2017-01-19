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

public class Touchable 
{
	public float [] touch_area;		//(upper r,lower r,lower l,upper l)
	public boolean area_set;
	
	public Touchable()
	{
		this.touch_area = new float [8];
		this.touch_area[0] = 0;
		this.touch_area[1] = 0;
		this.touch_area[2] = 0;
		this.touch_area[3] = 0;
		this.touch_area[4] = 0;
		this.touch_area[5] = 0;
		this.touch_area[6] = 0;
		this.touch_area[7] = 0;
		this.area_set = false;
	}
	
	//(upper r,lower r,lower l,upper l)
	public Touchable(float [] _vert)
	{
		this.touch_area = new float [8];
		this.touch_area[0] = _vert[0];
		this.touch_area[1] = _vert[1];
		this.touch_area[2] = _vert[2];
		this.touch_area[3] = _vert[3];
		this.touch_area[4] = _vert[4];
		this.touch_area[5] = _vert[5];
		this.touch_area[6] = _vert[6];
		this.touch_area[7] = _vert[7];
		this.area_set = true;
	}	
	
	public Touchable(float [] _ur, float [] _lr, float [] _ll, float [] _ul)
	{
		this.touch_area = new float [4];
		this.touch_area[0] = _ur[0];
		this.touch_area[1] = _ur[1];
		this.touch_area[2] = _lr[0];
		this.touch_area[3] = _lr[1];
		this.touch_area[4] = _ll[0];
		this.touch_area[5] = _ll[1];
		this.touch_area[6] = _ul[0];
		this.touch_area[7] = _ul[1];
		this.area_set = true;
	}	
	
	public boolean isInside(float _x, float _y)
	{
		if(this.area_set)
		{
			//within x bounds
	    	if( (_x > this.touch_area[0]) && (_x < this.touch_area[4]) )
	    	{
	    		//within y bounds
	    		if( (_y > this.touch_area[1]) && (_y < this.touch_area[3]) )
	    		{
	    			return true;
	    		}
	    	}
			return false;
		}
		else
		{
			return false;
		}
		
	}
	
	public void drawTouchable(GL10 _gl)
	{
		PolygonLine ln = new PolygonLine(this.touch_area);
        _gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        ln.draw(_gl);
	}
}
