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

public class PolygonLine 
{
	private int num_verts;
	private float vertices[];
	
	private FloatBuffer vertexBuffer;
	private float NormalizeValue[] = {0,1,0,1};
	
	public PolygonLine() 
	{
		this.num_verts = this.vertices.length/3;
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(this.vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(this.vertices);
		vertexBuffer.position(0);

	}
	
	public PolygonLine(float [] _vert, float [] _norm)
	{
		this.NormalizeValue = _norm;
		int num_lines = _vert.length / 2;
		this.num_verts = num_lines * 2;
		this.vertices = new float[this.num_verts * 3];
		
		int vert_iter = 0;
		for(int i = 0; i < num_lines * 2; i+=2)
		{
			this.vertices[i*3] = _vert[vert_iter];
			this.vertices[i*3+1] = _vert[vert_iter+1];

			vert_iter+=2;
			if(vert_iter < _vert.length -1)
			{
				this.vertices[(i+1)*3] = _vert[vert_iter];
				this.vertices[(i+1)*3+1] = _vert[vert_iter+1];
			}
			else
			{
				this.vertices[(i+1)*3] = _vert[0];
				this.vertices[(i+1)*3+1] = _vert[1];
			}
			this.vertices[i*3+2] = 0.0f;
			this.vertices[(i+1)*3+2] = 0.0f;
		}	
		
		normalizeVector();
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		//setUpIndeces();
	}	

	public PolygonLine(float [] _vert)
	{
		int num_lines = _vert.length / 2;
		this.num_verts = num_lines * 2;
		this.vertices = new float[this.num_verts * 3];
		
		int vert_iter = 0;
		for(int i = 0; i < num_lines * 2; i+=2)
		{
			this.vertices[i*3] = _vert[vert_iter];
			this.vertices[i*3+1] = _vert[vert_iter+1];

			vert_iter+=2;
			if(vert_iter < _vert.length -1)
			{
				this.vertices[(i+1)*3] = _vert[vert_iter];
				this.vertices[(i+1)*3+1] = _vert[vert_iter+1];
			}
			else
			{
				this.vertices[(i+1)*3] = _vert[0];
				this.vertices[(i+1)*3+1] = _vert[1];
			}
			this.vertices[i*3+2] = 0.0f;
			this.vertices[(i+1)*3+2] = 0.0f;
		}	
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		//setUpIndeces();
	}	
	public void normalizeVector()
	{
		float x_range = this.NormalizeValue[1] - this.NormalizeValue[0];
		float y_range = this.NormalizeValue[3] - this.NormalizeValue[2];
		for(int i = 0; i < this.num_verts; i++)
		{
			this.vertices[i*3] = (this.vertices[i*3] - this.NormalizeValue[0]) / x_range;
			this.vertices[i*3+1] = (this.vertices[i*3+1] - this.NormalizeValue[2]) / y_range;
		}
	}
	
	public void draw(GL10 gl) 
	{
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); 
		//gl.glDrawArrays(GL10.GL_TRIANGLES, 0, num_verts);
		
		// Disable the vertices buffer.
		//
		
        //gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
        gl.glLineWidth(2.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);
        gl.glDrawArrays(GL10.GL_LINES, 0, num_verts);
        gl.glLineWidth(1.0f);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}	
}
