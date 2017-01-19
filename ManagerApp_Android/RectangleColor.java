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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class RectangleColor 
{
	private FloatBuffer vertexBuffer;   // buffer holding the vertices
	private float vertices[];
	public boolean draw_line;
	private float line_vertices[];
	
	//(upper r,lower r,lower l,upper l)
    public RectangleColor(float [] _vert) 
    {
    	this.draw_line = true;
    	
    	int num_elements = 18;
    	this.vertices = new float[num_elements];
    	
    	this.vertices [0] = _vert[0];
    	this.vertices [1] = _vert[1];
    	this.vertices [2] = 0;
    	this.vertices [3] = _vert[2];
    	this.vertices [4] = _vert[3];
    	this.vertices [5] = 0;
    	this.vertices [6] = _vert[6];
    	this.vertices [7] = _vert[7];
    	this.vertices [8] = 0;
    	this.vertices [9] = _vert[6];
    	this.vertices[10] = _vert[7];
    	this.vertices[11] = 0;
    	this.vertices[12] = _vert[2];
    	this.vertices[13] = _vert[3];
    	this.vertices[14] = 0;
    	this.vertices[15] = _vert[4];
    	this.vertices[16] = _vert[5];
    	this.vertices[17] = 0;
    	
        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // allocates the memory from the byte buffer
        vertexBuffer = byteBuffer.asFloatBuffer();

        // fill the vertexBuffer with the vertices
        vertexBuffer.put(vertices);

        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);
        
        this.line_vertices = _vert;
    }	
	
    /** The draw method for the square with the GL context */
    public void draw(GL10 gl) 
    {
    	  // Point to our buffers
    	  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	
    	  // Set the face rotation
    	  gl.glFrontFace(GL10.GL_CW);
    	
    	  // Point to our vertex buffer    	
    	  gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);    	

    	  // Draw the vertices as triangle strip
    	  gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
    	
    	  if(this.draw_line)
    	  {
    		  PolygonLine ln = new PolygonLine(this.line_vertices);
    		  gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
    	      ln.draw(gl);   
    	  }
    	  //Disable the client state before leaving
    	 gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);


    }   
}
