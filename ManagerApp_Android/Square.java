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

 
public class Square {
   private FloatBuffer vertexBuffer;   // buffer holding the vertices
	private float vertices[] = 
		{
			//-0.2f, -0.1f,  0.0f,        // V1 - bottom left
			//-0.2f,  0.1f,  0.0f,        // V2 - top left
			//0.2f,  -0.1f,  0.0f,        // V3 - bottom right
			//0.2f, -0.1f,  0.0f,        // V4 - bottom right
			//-0.2f,  0.1f,  0.0f,        // V5 - top left
			//0.2f,  0.1f,  0.0f,        // V6 - top right
			
			//0.2f,  0.1f,  0.0f,        // V1 - bottom left
			//0.2f,  -0.1f,  0.0f,        // V2 - top left
			//-0.2f,  0.1f,  0.0f,        // V3 - bottom right
			//-0.2f,  0.1f,  0.0f,        // V4 - bottom right
			//0.2f,  -0.1f,  0.0f,         // V5 - top left
			//-0.2f, -0.1f,  0.0f,        // V6 - top right
			
			0.4f,  0.1f,  0.0f,        // V1 - top right
			0.4f,  -0.1f,  0.0f,        // V2 - = V5 bottom right
			0.0f,  0.1f,  0.0f,        // V3 - top left
			0.0f,  0.1f,  0.0f,        // V4 - = V3 top left
			0.4f,  -0.1f,  0.0f,         // V5 - = V2 bottom right
			0.0f, -0.1f,  0.0f,        // V6 - top right			
			
		};
   
   private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
   
   private float texture[] = {
   // Mapping coordinates for the vertices   
		   //0.0f, 1.0f,		// top left (V1)
		   //0.0f, 0.0f,      // bottom left (V2)
           //1.0f, 1.0f,    	// top right (V3)
           //1.0f, 1.0f,      // top right (V4)
           //0.0f, 0.0f,      // bottom left (V5)
           //1.0f, 0.0f,     	// bottom right (V6)
           
           1.0f, 1.0f,     // top left     (V2)
           1.0f, 0.0f,     // bottom left  (V1)
           0.0f, 1.0f,     // top right    (V4)
           0.0f, 1.0f,      // bottom right (V3)
           1.0f, 0.0f,      // bottom right (V3)
           0.0f, 0.0f      // bottom right (V3)           
   };

    public Square() 
    {
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
    
    public Square(float _l)
    {
    	if(_l >= 5)
    	{
    		float factor = (_l - 4f) * 0.1f;
    		//BR
    		this.vertices[2*3] = this.vertices[2*3] +  factor; 
    		this.vertices[3*3] = this.vertices[3*3] +  factor; 
    		//TR
    		this.vertices[5*3] = this.vertices[5*3] +  factor; 
    	}
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