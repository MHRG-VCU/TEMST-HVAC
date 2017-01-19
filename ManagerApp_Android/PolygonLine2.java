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

public class PolygonLine2 
{
	private int num_verts;// = 6;
	private float vertices[] = 
		{
			-0.2f, -0.1f,  0.0f,        // V1 - bottom left
			-0.2f,  0.1f,  0.0f,        // V2 - top left
			-0.2f,  0.1f,  0.0f,        // V3 - top left
			0.2f,  0.1f,  0.0f,        // V4 - top right
			0.2f,  0.1f,  0.0f,        // V5 - top right
			0.2f,  -0.1f,  0.0f,        // V6 - bottom right
			0.2f,  -0.1f,  0.0f,        // V7 - bottom right
			-0.2f, -0.1f,  0.0f,        // V8 - bottom left
		};
	
	private FloatBuffer vertexBuffer;
	private float NormalizeValue[] = {0,1,0,1};
	public PolygonLine2() 
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
	//input _vert contains  TL(0,1), BL(2,3), BR(4,5), TR(6,7)
	public PolygonLine2(float [] _vert, float [] _norm)
	{
		this.NormalizeValue = _norm;
		for(int i = 0; i < 8; i++)
		{
			//bottom left
			if((i == 0) || (i == 7))
			{
				this.vertices[i*3] = _vert[2];
				this.vertices[i*3+1] = _vert[3];
			}
			//top left
			else if ((i == 1) || (i == 2))
			{
				this.vertices[i*3] = _vert[0];
				this.vertices[i*3+1] = _vert[1];
			}
			//top right
			else if ((i == 3) || (i == 4))
			{
				this.vertices[i*3] = _vert[6];
				this.vertices[i*3+1] = _vert[7];
			}
			//bottom right
			else if ((i == 5) || (i == 6))
			{
				this.vertices[i*3] = _vert[4];
				this.vertices[i*3+1] = _vert[5];
			}					
			this.vertices[i*3+2] = 0.0f;
		}
		this.num_verts = this.vertices.length/3;
		normalizeVector();
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);	
		//setUpIndeces();
	}	

	public PolygonLine2(float [] _vert, int _num_verts)
	{
		int num_lines = _num_verts;
		//two vertices per line and each vertex has 3 dims
		int num_vertex_indeces = (num_lines * 2) * 3;
		this.vertices = new float[num_vertex_indeces];
		
		int start_index = 0;
		int end_index = 1;
		//for each line
		for(int i = 0; i < num_lines; i++)
		{
			//for each vertex in the current line
			for(int j = 0; j < 2; j++)
			{
				int curr_line_index = 0;
				if(j == 0)
				{
					curr_line_index = start_index;
				}
				else if(j == 1)
				{
					curr_line_index = end_index;
				}
				int vertex_index = (i * 2) + j;
				this.vertices[vertex_index * 3] = _vert[curr_line_index * 2];	//x
				this.vertices[vertex_index * 3 + 1] = _vert[curr_line_index * 2 + 1];	//y
				this.vertices[vertex_index * 3 + 2] = 0f;	//z
			}
			start_index++;
			end_index++;
			if(end_index == num_lines)
			{
				end_index = 0;
			}
		}

		this.num_verts = this.vertices.length/3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);	
		//setUpIndeces();*/
	}		
	public PolygonLine2(float [] _vert, int _num_verts, float [] _norm)
	{
		this.NormalizeValue = _norm;
		int num_lines = _num_verts;
		//two vertices per line and each vertex has 3 dims
		int num_vertex_indeces = (num_lines * 2) * 3;
		this.vertices = new float[num_vertex_indeces];
		
		int start_index = 0;
		int end_index = 1;
		//for each line
		for(int i = 0; i < num_lines; i++)
		{
			//for each vertex in the current line
			for(int j = 0; j < 2; j++)
			{
				int curr_line_index = 0;
				if(j == 0)
				{
					curr_line_index = start_index;
				}
				else if(j == 1)
				{
					curr_line_index = end_index;
				}
				int vertex_index = (i * 2) + j;
				this.vertices[vertex_index * 3] = _vert[curr_line_index * 2];	//x
				this.vertices[vertex_index * 3 + 1] = _vert[curr_line_index * 2 + 1];	//y
				this.vertices[vertex_index * 3 + 2] = 0f;	//z
			}
			start_index++;
			end_index++;
			if(end_index == num_lines)
			{
				end_index = 0;
			}
		}

		this.num_verts = this.vertices.length/3;
		normalizeVector();
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);	
		//setUpIndeces();*/
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
