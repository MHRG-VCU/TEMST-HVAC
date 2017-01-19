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
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Polygon {
	// Our vertices.
	private int num_verts;// = 6;
	private float vertices[] = 
		{
			-0.2f,  0.1f,  0.0f,        // V2 - top left
			-0.2f, -0.1f,  0.0f,        // V1 - bottom left
			0.2f,  -0.1f,  0.0f,        // V3 - bottom right
			-0.2f,  0.1f,  0.0f,        // V2 - top left
			0.2f, -0.1f,  0.0f,        // V3 - bottom right
			0.2f,  0.1f,  0.0f,        // V4 - top right
		};
			   

	// The order we like to connect them.
	//private short[] indices = { 0, 1, 2, 0, 2, 3 };
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
	// Our vertex buffer.
	private FloatBuffer vertexBuffer;
	// Our index buffer.
	private ShortBuffer indexBuffer;
	private float NormalizeValue[] = {0,1,0,1};
	
	public Polygon() 
	{
		this.num_verts = this.vertices.length/3;
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		//setUpIndeces();
	}
	/*
	//input _vert contains  TL,BL,BR,TR
	public Polygon(float [] _vert)
	{
		for(int i = 0; i < 6; i++)
		{
			//bottom left
			if(i == 0)
			{
				this.vertices[i*3] = _vert[2];
				this.vertices[i*3+1] = _vert[3];
			}
			//top left
			else if ((i == 1) || (i == 4))
			{
				this.vertices[i*3] = _vert[0];
				this.vertices[i*3+1] = _vert[1];
			}
			//bottom right
			else if ((i == 2) || (i == 3))
			{
				this.vertices[i*3] = _vert[4];
				this.vertices[i*3+1] = _vert[5];
			}
			//top right
			else if (i == 5)
			{
				this.vertices[i*3] = _vert[6];
				this.vertices[i*3+1] = _vert[7];
			}					
			this.vertices[i*3+2] = 0.0f;
		}

		this.num_verts = this.vertices.length/3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);	
		//setUpIndeces();
	}
	*/
	//input _vert contains  TL,BL,BR,TR
	public Polygon(float [] _vert, int _num_verts)
	{
		int num_triangles = 0;
		if(_num_verts == 3)
		{
			num_triangles = 1;
		}
		else
		{
			num_triangles = (_num_verts - 4) + 2;
		}
		
		this.num_verts = (num_triangles * 3) * 3;
		this.vertices = new float[this.num_verts];
		
		int tri_index_1 = 0;
		int tri_index_2 = 1;
		int tri_index_3 = 2;
		//for each triangle
		for(int i = 0; i < num_triangles; i++)
		{
			//for each vertex in the current triangle
			for(int j = 0; j < 3; j++)
			{
				int curr_tri_index = 0;
				if(j == 0)
				{
					curr_tri_index = tri_index_1;
				}
				else if(j == 1)
				{
					curr_tri_index = tri_index_2;
				}
				else if(j == 2)
				{
					curr_tri_index = tri_index_3;
				}
				int vertex_index = (i * 3) + j;
				this.vertices[vertex_index * 3] = _vert[curr_tri_index * 2];	//x
				this.vertices[vertex_index * 3 + 1] = _vert[curr_tri_index * 2 + 1];	//y
				this.vertices[vertex_index * 3 + 2] = 0f;	//z
			}
			tri_index_2 ++;
			tri_index_3 ++;
		}
		this.num_verts = this.vertices.length/3;		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);	
		//setUpIndeces();
	}
	
	//input _vert contains  TL,BL,BR,TR
	public Polygon(float [] _vert, int _num_verts, float [] _norm)
	{
		this.NormalizeValue = _norm;
		int num_triangles = 0;
		if(_num_verts == 3)
		{
			num_triangles = 1;
		}
		else
		{
			num_triangles = (_num_verts - 4) + 2;
		}
		
		this.num_verts = (num_triangles * 3) * 3;
		this.vertices = new float[this.num_verts];
		
		int tri_index_1 = 0;
		int tri_index_2 = 1;
		int tri_index_3 = 2;
		//for each triangle
		for(int i = 0; i < num_triangles; i++)
		{
			//for each vertex in the current triangle
			for(int j = 0; j < 3; j++)
			{
				int curr_tri_index = 0;
				if(j == 0)
				{
					curr_tri_index = tri_index_1;
				}
				else if(j == 1)
				{
					curr_tri_index = tri_index_2;
				}
				else if(j == 2)
				{
					curr_tri_index = tri_index_3;
				}
				int vertex_index = (i * 3) + j;
				this.vertices[vertex_index * 3] = _vert[curr_tri_index * 2];	//x
				this.vertices[vertex_index * 3 + 1] = _vert[curr_tri_index * 2 + 1];	//y
				this.vertices[vertex_index * 3 + 2] = 0f;	//z
			}
			tri_index_2 ++;
			tri_index_3 ++;
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
	//input _vert contains  TL,BL,BR,TR
	public Polygon(float [] _vert, float [] _norm)
	{
		this.NormalizeValue = _norm;
		for(int i = 0; i < 6; i++)
		{
			//bottom left
			if(i == 0)
			{
				this.vertices[i*3] = _vert[2];
				this.vertices[i*3+1] = _vert[3];
			}
			//top left
			else if ((i == 1) || (i == 4))
			{
				this.vertices[i*3] = _vert[0];
				this.vertices[i*3+1] = _vert[1];
			}
			//bottom right
			else if ((i == 2) || (i == 3))
			{
				this.vertices[i*3] = _vert[4];
				this.vertices[i*3+1] = _vert[5];
			}
			//top right
			else if (i == 5)
			{
				this.vertices[i*3] = _vert[6];
				this.vertices[i*3+1] = _vert[7];
			}					
			this.vertices[i*3+2] = 0.0f;
		}
		normalizeVector();
		this.num_verts = this.vertices.length/3;
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

	public void setUpIndeces()
	{
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);		
	}
	/**
	 * This function draws our square on screen.
	 * @param gl
	 */
	public void draw(GL10 gl) 
	{
		//gl.glFrontFace(GL10.GL_CCW);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glFrontFace(GL10.GL_CCW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); 
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, num_verts);
		
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

}
