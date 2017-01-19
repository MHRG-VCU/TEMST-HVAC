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

public class RectangleTexture 
{
	private FloatBuffer vertexBuffer;   // buffer holding the vertices
	private float vertices[];
	private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
	   
	private float texture[] = {	        
			0.0f, 0.0f,      	// bottom left (V3)
			0.0f, 1.0f,     	// top left    (V4)
			1.0f, 0.0f,      	// bottom right (V3)
			1.0f, 0.0f,      	// bottom right (V3)
			0.0f, 1.0f,     	// top left    (V4)
			1.0f, 1.0f     		// top right     (V2)
          
	};
	
	//(upper r,lower r,lower l,upper l)
    public RectangleTexture(float [] _vert) 
    {
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
        
        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);        
        byteBuffer.order(ByteOrder.nativeOrder());        
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }	
	
    /** The draw method for the square with the GL context */
    public void draw(GL10 gl, int textures) 
    {

    	  gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	  gl.glEnable(gl.GL_BLEND);
    	  gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    	  // bind the previously generated texture
    	  gl.glBindTexture(GL10.GL_TEXTURE_2D, textures);
    	  
    	  // Point to our buffers
    	  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	  gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	
    	  // Set the face rotation
    	  gl.glFrontFace(GL10.GL_CW);
    	
    	  // Point to our vertex buffer    	
    	  gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);    	
    	  gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    	    	
    	  //gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
    	  gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    	  
    	  // Draw the vertices as triangle strip
    	  gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
    	
    	  //Disable the client state before leaving
    	 gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	 gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	 gl.glDisable(gl.GL_BLEND);
    	 gl.glDisable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )

    }   
}
