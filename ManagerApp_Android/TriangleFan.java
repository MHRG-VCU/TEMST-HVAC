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

 
public class TriangleFan {
   private FloatBuffer vertexBuffer;   // buffer holding the vertices
   private float vertices[];        // V3 - third vertex       
   
   private int seg = 50;
      
    public TriangleFan() {

        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.seg * 3 * 3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // allocates the memory from the byte buffer
        this.vertexBuffer = byteBuffer.asFloatBuffer();
        
        this.vertices = new float[this.seg * 3 * 3];

        // fill the vertexBuffer with the vertices
        // vertexBuffer.put(vertices);

        // set the cursor position to the beginning of the buffer
        //vertexBuffer.position(0);                       
    }

    /** The draw method for the triangle with the GL context */
    public void draw(GL10 gl) {
    	      
    	  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	  
    	  // Set the face rotation
    	  gl.glFrontFace(GL10.GL_CW);
    	
    	  // Point to our vertex buffer    	
    	  gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);    	    	 
    	    	
    	  gl.glColor4f(0.4f, 0.4f, 0.8f, 1.0f);
    	  
    	  // Draw the vertices as triangle strip
    	  gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.seg * 3);
    	
    	  //Disable the client state before leaving
    	 gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    
    // Sets the coordinates of the triangle in the fan based on the min and max angle
    public void setTriangles(float midX, float midY, float rad, float minAngle, float maxAngle){
    	
    	float angInc = (maxAngle - minAngle) / (float)this.seg;
    	float ang = minAngle;    	    
    	
    	for (int i = 0; i < this.seg; i++){
    		
    		this.vertices[i * 9 + 0] = midX + 0.95f * rad * (float)Math.sin(ang);
    		this.vertices[i * 9 + 1] = midY + 0.95f * rad * (float)Math.cos(ang);
    		this.vertices[i * 9 + 2] = 0.0f;
    		
    		this.vertices[i * 9 + 3] = midX;
    		this.vertices[i * 9 + 4] = midY;
    		this.vertices[i * 9 + 5] = 0.0f;
    		
    		ang += angInc;
    		
    		this.vertices[i * 9 + 6] = midX + 0.95f * rad * (float)Math.sin(ang);
    		this.vertices[i * 9 + 7] = midY + 0.95f * rad * (float)Math.cos(ang);
    		this.vertices[i * 9 + 8] = 0.0f;
    	}    	    	
    	
    	// set the cursor position to the beginning of the buffer
        this.vertexBuffer.position(0);       
        
    	 // fill the vertexBuffer with the vertices
        this.vertexBuffer.put(this.vertices);

        // set the cursor position to the beginning of the buffer
        this.vertexBuffer.position(0);       	
    }
}
