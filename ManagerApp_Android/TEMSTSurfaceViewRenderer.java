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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.WindowManager;
import android.os.PowerManager;

public class TEMSTSurfaceViewRenderer implements GLSurfaceView.Renderer
{
	//Resolution of viewable area 1280 X 748
	
	public String temp_1 = "-none-";
	public Context mContext;
	public Building bl = null;
	public Monitor monitor;
	
	//flag to indicate whether the loading screen should be displayed
	public boolean load_screen = true;
	public boolean load_data_done = false;
	public boolean load_textures_done = false;
	public boolean load_buttons_done = false;
	public float load_percentage = 0;
	public String load_status_0 = "";
	public String load_status_1 = "";
	public String load_status_2 = "";
	//flag to indicate whether this is the first time the on surface created method is called 
	//if so then we need to load the data using the thread functions
	//otherwise we have to reload the textures
	public boolean first_run = true;
	
	//touch values and picked colors
    public float m_touch_x = 0;
    public float m_touch_y = 0;
    public float m_touch_x_relative = 0;
    public float m_touch_y_relative = 0;
    public int m_pick_color_r = 0;
    public int m_pick_color_g = 0;
    public int m_pick_color_b = 0;
    public boolean m_touch_event_up = false;
    public int colors[][];
	public int selected_floor = 4;
	public int selected_zone = -1;
	//touch values for move events
	public float m_touch_move_x = 0;
    public float m_touch_move_y = 0;
    public float m_touch_move_x_relative = 0;
    public float m_touch_move_y_relative = 0;
    public boolean m_touch_event_move = false;
    public float m_touch_move_x_prev = -10;
    public float m_touch_move_y_prev = -10;
    public float m_touch_move_d_x = 0;
    public float m_touch_move_d_y = 0;
    // touch values for touch down events
    public float m_touch_down_x = 0;
    public float m_touch_down_y = 0;
    public float m_touch_down_x_relative = 0;
    public float m_touch_down_y_relative = 0;
    public boolean m_touch_event_down = false;
    //touchable areas
    public Touchable floor_touchable;
    public Touchable graph_touchable;
    public Touchable dimbutton_touchable;
    public Touchable controlbutton_touchable;
    public Touchable building_touchable;
    public Touchable [] floor_select_touchable;
    public Touchable time_slide_touchable;
    //background texture
    public RectangleTexture background_texture;
    public RectangleTexture load_background_texture;
    //Buttons
    //Plot dimensionality select buttons
    public Button [] dim_select_buttons;
    public int num_dim_select_buttons;
    //view select radios
    public RadioButton view_select_radio;
    //anomaly select radios
    public RadioButton anom_select_radio;
    //Summary Dim select tick boxes
    public TickBox dim_select_tickbox;
    //threshold spinner
    public Spinner threshold_spinner;
    //antecedent spinner
    public Spinner antecedent_spinner;
    //buttons
    public Button button_goto_next;
    public Button button_goto_prev;
    public Button button_rule_reload;
    public Button button_mark_normal;
    public Button button_save_model;
    public Button button_time_slide;
    //temp debug button
    public String temp_button;
    //floors
    public RectangleColor [] floors;
    public int temp_sel_id = 0;
    /*
	public float velNow = 33.0f;
	public float velOpt = 68.0f;
	
	private FloatBuffer triangleBckgVB;
	private FloatBuffer frameLineVB;
	private FloatBuffer frameMarksVB;
	
	private FloatBuffer velLineVB;
	private FloatBuffer test_poly;
	
	private int seg = 100;
	
	private float rad = 1.5f;
	private float midX = 0.0f;
	private float midY = 0.0f;
	*/
	private int screenOrient = 0;

	//variables for writing text
	String [] alpha = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	String [] alpha_upper = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	String [] special_char = {",","-"," ",".","=","/","_","%","(",")",":"};
	int num_special_char = 11;
	
	//textures
	int [] textures_num = new int[11];	// numbers
	int [] textures_text_lower = new int[26]; //lower case letters
	int [] textures_text_upper = new int[26]; //upper case letters
	int [] textures_special = new int[num_special_char];	//special characters
	int [] textures_uilogo = new int [1]; //ui logo
	int [] textures_graph = new int [1]; //graph texture
	int [] textures_background = new int [1]; //background texture
	int [] textures_load_background = new int [1]; //load screen background texture
	
	Square s = new Square();
	TriangleFan f = new TriangleFan();
	
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {
    	
  	    
    	//old function calls
    	// initialize the triangle vertex array
        //initShapes();
    	
        // Set the background frame color
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
    	
        //render the text 
        renderNum(gl);
        renderText(gl);
        renderTextUpper(gl);
        renderSpecial(gl);
        textureLoadPreliminary(gl);
        
        if(first_run)
        {
        	monitor = new Monitor();
        	dataLoader();
        }
        // PercentageIncrease();
        
        //create the background texture object
        float lx = -1.768f;
    	float rx = 1.768f;
    	float uy = -0.127f;
    	float ly = 1.925f;
    	float vert_b1[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	background_texture = new RectangleTexture(vert_b1);
    	load_background_texture = new RectangleTexture(vert_b1);
    	
    	if(!first_run)
    	{
    		textureLoad(gl);
    		generateButtons(gl);
    		generateDimButtons(gl);
    		generateViewSelectRadio(gl);
    		generateAnomalySelectRadio(gl);
    		generateAnomalyDimSelectTick(gl);
    		generateThresholdSpinner(gl);
    		generateAntecedentSpinner(gl);
    	}
    }

    
    public void dataLoader() 
    {
        new Thread(new Runnable() 
        {
        	public void run() 
            {
            	//load colosr for color picking
            	//loadStatusUpdate("Loading colors for color picking .... ");
            	colors = new int[4096][3];
                getColors();
                //loadStatusUpdateCurrent("Loading colors for color picking  .... DONE");
                
                load_percentage = 10;
                
                //start loading the building data
                //loadStatusUpdate("abcdefghijklmnopqrstuvwxyz");
                loadStatusUpdate("Loading Building Data .... ");
                //PercentageIncrease();
                //create the building object
    	        
    	        //String n_rules = monitor.m_FIS.loadRules2("/sdcard/TEMST_App/Rules/rules1.txt");
    	        monitor.m_FIS.loadRules("/sdcard/TEMST_App/Rules/rules1.txt");
    	        //loadStatusUpdate(n_rules);
    	        bl = new Building("/sdcard/TEMST_App/Data/Building_BBB.txt");
    	        bl.setCurrTime();
    	    	bl.m_SelectFloor = 0;
    	    	loadStatusUpdateCurrent("Loading Building Data  .... DONE");
    	    	
    	    	load_percentage = 70;
    	    	
    	    	//load the OCM model
    	    	loadStatusUpdate("Loading Building Model .... ");
    	    	monitor.loadModel("/sdcard/TEMST_App/",(bl.m_Floors[6]), bl.m_NameStr);
    	    	loadStatusUpdate("Floor 7 loaded .... ");
    	    	monitor.loadModel("/sdcard/TEMST_App/",(bl.m_Floors[4]), bl.m_NameStr);
    	    	loadStatusUpdate("Floor 5 loaded .... ");
    	    	bl.setCurrTime();
    	    	bl.m_SelectFloor = 4;
    	    	bl.updateTime();
    	    	loadStatusUpdate("Time updated.... ");
    	    	monitor.evalBuilding(bl);
    	    	loadStatusUpdate("Building evaluated .... ");
    	    	loadStatusUpdate("Loading Building Model  .... DONE");

    	    	load_percentage = 80;
    	    	
    	    	//initialize the touchable areas
    	    	loadStatusUpdate("Initializing Touch Activity .... ");
    	    	float vert_surround_graph[] = {0f,1.07f,  0f,1.43f,  1.72f,1.43f,  1.72f,1.07f }; //(upper l,lower l,lower r,upper r) 
    	    	graph_touchable = new Touchable(vert_surround_graph);
    	    	float vert_surround_floor[] = {0,0,  0,1,  1,1,  1,0}; //(upper l,lower l,lower r,upper r)
    	        floor_touchable = new Touchable(vert_surround_floor);
    	        float vert_surround_dimbutton[] = {1.17f,-0.03f, 1.17f,1.0f, 1.75f,1.0f, 1.75f,-0.03f}; //(upper l,lower l,lower r,upper r)
    	        dimbutton_touchable = new Touchable(vert_surround_dimbutton);
    	        float vert_surround_controlbutton[] = {-1.75f,-0.12f, -1.75f,1.9f, -0.6f,1.9f, -0.6f,-0.12f}; //(upper l,lower l,lower r,upper r)
    	        controlbutton_touchable = new Touchable(vert_surround_controlbutton);
    	        float vert_surround_building[] = {-0.52f,0.03f, -0.52f,1.15f, -0.08f,1.15f, -0.08f,0.03f}; //(upper l,lower l,lower r,upper r)
    	        building_touchable = new Touchable(vert_surround_building);
    	        float vert_surround_timeslide[] = {-1.68f,0.20f, -1.68f,0.33f, -0.68f,0.33f, -0.68f,0.20f}; //(upper l,lower l,lower r,upper r)
    	        time_slide_touchable = new Touchable(vert_surround_timeslide);
    	        loadStatusUpdateCurrent("Initializing Touch Activity  .... DONE");
    	        
    	        load_percentage = 90;
    	        
    	        //generate the floor select areas
    	        loadStatusUpdate("Initializing Floors .... ");
    	        generateFloorSelect();
    	        loadStatusUpdateCurrent("Initializing Floors .... DONE");
    	        
    	        //update the status
    	        loadStatusUpdate("Loading basic textures .... ");
    	        load_data_done = true;
    	        /**/
    	        return;
            }
        }).start();
    }
      
    public void updatePercentage()
    {
    	load_percentage += 2;
    }
    
    public void loadStatusUpdate(String _update)
    {
    	load_status_2 = load_status_1;  
    	load_status_1 = load_status_0;
    	load_status_0 = _update;
    }
    
    public void loadStatusUpdateCurrent(String _update)
    {
    	load_status_0 = _update;
    }
    
    //keeps the progress bar updating while the data is bieng loaded
    public void PercentageIncrease() 
    {
        new Thread(new Runnable() 
        {
            public void run() 
            {
            	for(int i = 0; i < 25; i++)
            	{
            		try 
            		{
						Thread.sleep(1000);
					} 
            		catch (InterruptedException e) 
            		{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		updatePercentage();
            	}

            }
        }).start();
    }
    

    
    public void generateFloorSelect()
    {
    	int n_floor = bl.m_NFloors;
    	floor_select_touchable = new Touchable[n_floor];
    	floors = new RectangleColor[n_floor];
    	//right edge of the screen is 1.76
    	float height = 0.09f;
    	float width = 0.4f;
    	float lx = -0.5f;
    	float rx = lx + width;
    	float uy = 1.05f;
    	float ly = uy + height;
    	
    	for(int i = 0; i < n_floor; i++)
    	{
    		float vert_fl_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    		floor_select_touchable[i] = new Touchable(vert_fl_temp);
    		float vert_fl_cl_temp[] = {lx + 0.01f,uy + 0.01f,  lx + 0.01f,ly - 0.01f,  rx - 0.01f,ly - 0.01f,  rx - 0.01f,uy + 0.01f }; //(upper l,lower l,lower r,upper r)
    		floors[i] = new RectangleColor(vert_fl_cl_temp);
    		uy -= height + 0.01f;
    		ly -= height + 0.01f;
    	}
    	
    }
      
    //return min
    public float min(float a, float b)
	{
		float min = b;
		if(a < b)
			min = a;
		return min;
	}
	//return max
	public float max(float a, float b)
	{
		float max = b;
		if(a > b)
			max = a;
		return max;
	}	
	
	// Initializes the color palate for color picking of objects
    void getColors()
    {
	   	 for (int i = 0; i < 16; i++)
	   	 {
	   		 for (int j = 0; j < 16; j++)
	   		 {
	   			 for (int k = 0; k < 16; k++)
	   			 {
	   				 colors[i * 256 + j * 16 + k][0] = i * 16;
	   				 colors[i * 256 + j * 16 + k][1] = j * 16;
	   				 colors[i * 256 + j * 16 + k][2] = k * 16;
	   			 }
	   		 }
	   	 }
    }    
    
    //updates the realtive position
    public void updateRelativePosition()
    {
    	//if(m_touch_event_up)
    	{
    		m_touch_x_relative = (m_touch_x - (1280/2))/362; 
    		m_touch_y_relative = (m_touch_y - 47) / 362;// - (1280/2))/362;
    	}
    	//if(m_touch_event_down)
    	{
    		m_touch_down_x_relative = (m_touch_down_x - (1280/2))/362; 
    		m_touch_down_y_relative = (m_touch_down_y - 47) / 362;// - (1280/2))/362;
    	}
    	//if(m_touch_event_move)
    	{
    		m_touch_move_x_relative = (m_touch_move_x - (1280/2))/362; 
    		m_touch_move_y_relative = (m_touch_move_y - 47) / 362;// - (1280/2))/362;
    	}
    }
    
    //texture render functions
    // load screen texture loader
    public void textureLoadPreliminary(GL10 gl)
    {
   	    // loading load background texture
    	InputStream is = mContext.getResources().openRawResource(R.drawable.load_background);
    	
    	
    	//InputStream is = mContext.getResources().openRawResource(R.raw.load_background);
        Bitmap bitmap4 = null;
        try
        {
            bitmap4 = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            //Always clear and close
            try 
            {
                is.close();
                is = null;
            } 
            catch (IOException e) 
            {
            }
        }
    	
    	
    	//Bitmap bitmap4 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.load_background);
    	//Bitmap bitmap4 = BitmapFactory.decodeStream(is);
        
    	gl.glGenTextures(1, textures_load_background, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_load_background[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap4, 0);
   	    bitmap4.recycle();
    }       
    // basic texture loader
    public void textureLoad(GL10 gl)
    {
    	// loading UILOGO texture
    	/*
    	Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.uofilogo);
    	gl.glGenTextures(1, textures_uilogo, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_uilogo[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
   	    bitmap.recycle();
   	    */
    	// loading graph texture
    	Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.graph_bg_4);
    	gl.glGenTextures(1, textures_graph, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_graph[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap2, 0);
   	    bitmap2.recycle();
   	    
   	    // loading background texture
		 InputStream is = mContext.getResources().openRawResource(R.drawable.background13);
		 Bitmap bitmap3 = null;
		 try 
		 {
		     bitmap3 = BitmapFactory.decodeStream(is);
		 } 
		 finally 
		 {
		     //Always clear and close
		     try 
		     {
		         is.close();
		         is = null;
		     } 
		     catch (IOException e) 
		     {
		     }
		 }
   	    
    	//Bitmap bitmap3 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.background12);
    	gl.glGenTextures(1, textures_background, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_background[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap3, 0);
   	    bitmap3.recycle();
   	    
		
    }    
    
    
    //render the numbers
    private void renderNum(GL10 gl)
    {
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	// Create an empty, mutable bitmap
    	Bitmap bitmap = Bitmap.createBitmap(64, 32, Bitmap.Config.ARGB_8888);
    	// get a canvas to paint over the bitmap
    	Canvas canvas = new Canvas(bitmap);
    	Color c = new Color();
    	
    	//Generate one texture pointer...
    	gl.glGenTextures(11, textures_num, 0);    	    
    	
    	for (int i = 0; i < 11; i++)
    	{    	
    	
	    	bitmap.eraseColor(c.argb(0, 159, 159, 159));	    	
	    	
	    	// Draw the text
	    	Paint textPaint = new Paint();
	    	textPaint.setTextSize(20);
	    	textPaint.setAntiAlias(true);
	    	textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
	    	// draw the text centered	    	
	    	canvas.drawText(Integer.toString(i), 2,  25, textPaint);
	    	//canvas.drawText("a", 2,  22, textPaint);
	    		    	
	    	//...and bind it to our array    	    	
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_num[i]);

	    	//Create Nearest Filtered Texture
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);	    
	    	gl.glEnable(gl.GL_BLEND);
	    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
	
	    	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	gl.glDisable(gl.GL_BLEND);
    	}

    	//Clean up
    	bitmap.recycle();
    	gl.glDisable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	
    }
    
    //render the lower case text
    private void renderText(GL10 gl)
    {
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	// Create an empty, mutable bitmap
    	//Bitmap bitmap = Bitmap.createBitmap(32, 16, Bitmap.Config.ARGB_8888);
    	Bitmap bitmap = Bitmap.createBitmap(64, 32, Bitmap.Config.ARGB_8888);
    	// get a canvas to paint over the bitmap
    	Canvas canvas = new Canvas(bitmap);
    	Color c = new Color();
    	
    	//Generate one texture pointer...
    	gl.glGenTextures(26, textures_text_lower, 0); 
    	
    	
    	for (int i = 0; i < 26; i++)
    	{    	
	    	bitmap.eraseColor(c.argb(0, 159, 159, 159));	    	
	    	
	    	// Draw the text
	    	Paint textPaint = new Paint();
	    	textPaint.setTextSize(20);
	    	textPaint.setAntiAlias(true);
	    	textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
	    	// draw the text centered	    	
	    	canvas.drawText(alpha[i], 2,  25, textPaint);
	    	//canvas.drawText(alpha[i], 2,  10, textPaint);
	    	
	    	//canvas.drawText("a", 2,  22, textPaint);
	    		    	
	    	//...and bind it to our array    	    	
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_text_lower[i]);

	    	//Create Nearest Filtered Texture
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);	    
	    	gl.glEnable(gl.GL_BLEND);
	    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
	
	    	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	gl.glDisable(gl.GL_BLEND);
    	}

    	//Clean up
    	bitmap.recycle();
    	gl.glDisable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	
    }  

    // render the upper case text
    private void renderTextUpper(GL10 gl)
    {
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	// Create an empty, mutable bitmap
    	Bitmap bitmap = Bitmap.createBitmap(64, 32, Bitmap.Config.ARGB_8888);
    	// get a canvas to paint over the bitmap
    	Canvas canvas = new Canvas(bitmap);
    	Color c = new Color();
    	
    	//Generate one texture pointer...
    	gl.glGenTextures(26, textures_text_upper, 0); 
    	
    	
    	for (int i = 0; i < 26; i++)
    	{    	
    	
	    	bitmap.eraseColor(c.argb(0, 159, 159, 159));	    	
	    	
	    	// Draw the text
	    	Paint textPaint = new Paint();
	    	textPaint.setTextSize(20);
	    	textPaint.setAntiAlias(true);
	    	textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
	    	// draw the text centered	    	
	    	canvas.drawText(alpha_upper[i], 2,  25, textPaint);
	    	//canvas.drawText(alpha_upper[i], 2,  10, textPaint);
	    	//canvas.drawText("a", 2,  22, textPaint);
	    		    	
	    	//...and bind it to our array    	    	
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_text_upper[i]);

	    	//Create Nearest Filtered Texture
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);	    
	    	gl.glEnable(gl.GL_BLEND);
	    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
	
	    	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	gl.glDisable(gl.GL_BLEND);
    	}

    	//Clean up
    	bitmap.recycle();
    	gl.glDisable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	
    }  
    
    //render special characters
    private void renderSpecial(GL10 gl)
    {
    	
    	gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	// Create an empty, mutable bitmap
    	Bitmap bitmap = Bitmap.createBitmap(64, 32, Bitmap.Config.ARGB_8888);
    	// get a canvas to paint over the bitmap
    	Canvas canvas = new Canvas(bitmap);
    	Color c = new Color();
    	
    	//Generate one texture pointer...
    	gl.glGenTextures(num_special_char, textures_special, 0);    	    
    	
    	for (int i = 0; i < num_special_char; i++)
    	{    	
    	
	    	bitmap.eraseColor(c.argb(0, 159, 159, 159));	    	
	    	
	    	// Draw the text
	    	Paint textPaint = new Paint();
	    	textPaint.setTextSize(20);
	    	textPaint.setAntiAlias(true);
	    	textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
	    	// draw the text centered	    	
	    	canvas.drawText(special_char[i], 2,  25, textPaint);
	    	//canvas.drawText("a", 2,  22, textPaint);
	    		    	
	    	//...and bind it to our array    	    	
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures_special[i]);

	    	//Create Nearest Filtered Texture
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);	    
	    	gl.glEnable(gl.GL_BLEND);
	    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
	
	    	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	gl.glDisable(gl.GL_BLEND);
    	}

    	//Clean up
    	bitmap.recycle();
    	gl.glDisable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping ( NEW )
    	
    }      
    //Generate the buttons
    //generate the dimensionality select buttons
    public void generateDimButtons(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float lx = 1.20f;
    	float rx = 1.78f;
    	float uy = 0.00f;
    	float ly = uy + 0.10f;
	    
	    num_dim_select_buttons = 9;
	    dim_select_buttons = new Button[num_dim_select_buttons];
	    for (int i = 0; i < num_dim_select_buttons; i++)
	    {
	    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	    	dim_select_buttons[i] = new Button(vert_b_temp);
	    	
	    	Bitmap bitmap_up = null;
	    	Bitmap bitmap_down = null;
	    	
	    	//get the appropriate texture
	    	switch(i)
	    	{
	    	case 0:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_oat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_oat_down);
	    		break;
	    	case 1:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_clt_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_clt_down);
	    		break;
	    	case 2:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_mat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_mat_down);
	    		break;
	    	case 3:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_rat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_rat_down);
	    		break;
	    	case 4:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_dmp_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_dmp_down);
	    		break;
	    	case 5:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_eld_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_eld_down);
	    		break;
	    	case 6:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_ecr_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_ecr_down);
	    		break;
	    	case 7:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_sld_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_sld_down);
	    		break;
	    	case 8:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_scr_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_scr_down);
	    		break;	    			
	    	}
	    	
	    	// load button up texture
	    	gl.glGenTextures(1, dim_select_buttons[i].texture_up, 0);
	   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, dim_select_buttons[i].texture_up[0]);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	    bitmap_up.recycle();
	   	    
	   	    gl.glGenTextures(1, dim_select_buttons[i].texture_down, 0);
		    gl.glBindTexture(GL10.GL_TEXTURE_2D, dim_select_buttons[i].texture_down[0]);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
		    bitmap_down.recycle();
		    
		    dim_select_buttons[i].button_text = Integer.toString(i);
		    
		    uy += 0.11f;
	    	ly += 0.11f;
	    }
    }
    
    //radio button generator for the view radio
    public void generateViewSelectRadio(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.082f;
    	float width = 0.42f;
    	float lx = -1.66f;
    	float rx = lx + width;
    	float uy = -0.04f;
    	float ly = uy + height;
    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	int n_radios = 2;
    	//initialize the radios
    	view_select_radio = new RadioButton(vert_b_temp,n_radios);
    	
    	for(int i = 0; i < n_radios; i++)
    	{
	    	Bitmap bitmap_up = null;
	    	Bitmap bitmap_down = null;
	    	
	    	//select the correct texture
	    	switch(i)
	    	{
	    	case 0:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_view_temp_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_view_temp_down);
	    		break;
	    	case 1:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_view_anom_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_view_anom_down);
	    		break;
	    	}
	    	
	    	// load button up texture
	    	gl.glGenTextures(1, view_select_radio.radio_button[i].texture_up, 0);
	   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, view_select_radio.radio_button[i].texture_up[0]);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	    bitmap_up.recycle();
	   	    
	   	    //load the button down texture
	   	    gl.glGenTextures(1, view_select_radio.radio_button[i].texture_down, 0);
		    gl.glBindTexture(GL10.GL_TEXTURE_2D, view_select_radio.radio_button[i].texture_down[0]);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
		    bitmap_down.recycle();
    	}
    }
    
    //generate anomaly type selection radio buttons
    public void generateAnomalySelectRadio(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.082f;
    	float width = 0.42f;
    	float lx = -1.66f;
    	float rx = lx + width;
    	float uy = 0.43f;
    	float ly = uy + height;
    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	int n_radios = 3;
    	//initialize the radios
    	anom_select_radio = new RadioButton(vert_b_temp,n_radios);
    	
    	for(int i = 0; i < n_radios; i++)
    	{
	    	Bitmap bitmap_up = null;
	    	Bitmap bitmap_down = null;
	    	
	    	//select the correct texture
	    	switch(i)
	    	{
	    	case 0:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_clus_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_clus_down);
	    		break;
	    	case 1:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_expe_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_expe_down);
	    		break;
	    	case 2:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_comb_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.radio_anom_comb_down);
	    		break;
	    	}
	    	
	    	// load button up texture
	    	gl.glGenTextures(1, anom_select_radio.radio_button[i].texture_up, 0);
	   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, anom_select_radio.radio_button[i].texture_up[0]);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	    bitmap_up.recycle();
	   	    
	   	    //load the button down texture
	   	    gl.glGenTextures(1, anom_select_radio.radio_button[i].texture_down, 0);
		    gl.glBindTexture(GL10.GL_TEXTURE_2D, anom_select_radio.radio_button[i].texture_down[0]);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
		    bitmap_down.recycle();
    	}
    }    
    
    //generate the tick boxes for selecting the dimensions for anomally 
    public void generateAnomalyDimSelectTick(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.08f;
    	float width = 0.3f;
    	float lx = -1.67f;
    	float rx = lx + width;
    	float uy = 1.13f;
    	float ly = uy + height;
    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	int n_boxes = 11;
    	//initialize the radios
    	dim_select_tickbox = new TickBox(vert_b_temp,n_boxes);
    	
    	for(int i = 0; i < n_boxes; i++)
    	{
	    	Bitmap bitmap_up = null;
	    	Bitmap bitmap_down = null;
	    	
	    	//select the correct texture
	    	switch(i)
	    	{
	    	case 0:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_zte_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_zte_down);
	    		break;
	    	case 1:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_tme_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_tme_down);
	    		break;
	    	case 2:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_oat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_oat_down);
	    		break;
	    	case 3:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_clt_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_clt_down);
	    		break;
	    	case 4:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_mat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_mat_down);
	    		break;
	    	case 5:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_rat_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_rat_down);
	    		break;
	    	case 6:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_dmp_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_dmp_down);
	    		break;
	    	case 7:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_eld_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_eld_down);
	    		break;
	    	case 8:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_ecr_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_ecr_down);
	    		break;
	    	case 9:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_sld_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_sld_down);
	    		break;
	    	case 10:
	    		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_scr_up);
	    		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tick_dim_scr_down);
	    		break;

	    	}
	    	
	    	// load button up texture
	    	gl.glGenTextures(1, dim_select_tickbox.tick_button[i].texture_up, 0);
	   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, dim_select_tickbox.tick_button[i].texture_up[0]);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	    bitmap_up.recycle();
	   	    
	   	    //load the button down texture
	   	    gl.glGenTextures(1, dim_select_tickbox.tick_button[i].texture_down, 0);
		    gl.glBindTexture(GL10.GL_TEXTURE_2D, dim_select_tickbox.tick_button[i].texture_down[0]);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
		    bitmap_down.recycle();
    	}
    }      
    
    //generate spinner for selecting threshold
    public void generateThresholdSpinner(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.08f;
    	float width = 0.6f;
    	float lx = -1.59f;
    	float rx = lx + width;
    	float uy = 0.94f;
    	float ly = uy + height;
    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    
    	//initialize the spinner
    	threshold_spinner = new Spinner(vert_b_temp, 0.0f, 1.0f , 0.1f); 
    	threshold_spinner.spin_text = "Threshold";
    	threshold_spinner.spin_value = 0.8f;
    	
    	Bitmap bitmap_up = null;
    	Bitmap bitmap_down = null;
    	
    	//spin up button
    	bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_up_up);
		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_up_down);
		
		// load button up texture
    	gl.glGenTextures(1, threshold_spinner.spin_up.texture_up, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, threshold_spinner.spin_up.texture_up[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
   	    bitmap_up.recycle();
   	    
   	    //load the button down texture
   	    gl.glGenTextures(1, threshold_spinner.spin_up.texture_down, 0);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, threshold_spinner.spin_up.texture_down[0]);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	    bitmap_down.recycle();
	    
	    //spin down button
    	bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_down_up);
		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_down_down);
		
		// load button up texture
    	gl.glGenTextures(1, threshold_spinner.spin_down.texture_up, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, threshold_spinner.spin_down.texture_up[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
   	    bitmap_up.recycle();
   	    
   	    //load the button down texture
   	    gl.glGenTextures(1, threshold_spinner.spin_down.texture_down, 0);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, threshold_spinner.spin_down.texture_down[0]);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	    bitmap_down.recycle();
    }

    //generate spinner for selecting the number of antecedents
    public void generateAntecedentSpinner(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.08f;
    	float width = 0.6f;
    	float lx = -1.59f;
    	float rx = lx + width;
    	float uy = 1.53f;
    	float ly = uy + height;
    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    
    	//initialize the spinner
    	antecedent_spinner = new Spinner(vert_b_temp, 1.0f, 3.0f , 1.0f); 
    	antecedent_spinner.spin_text = "Antecedents";
    	antecedent_spinner.spin_value = 1.0f;
    	
    	Bitmap bitmap_up = null;
    	Bitmap bitmap_down = null;
    	
    	//spin up button
    	bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_up_up);
		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_up_down);
		
		// load button up texture
    	gl.glGenTextures(1, antecedent_spinner.spin_up.texture_up, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, antecedent_spinner.spin_up.texture_up[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
   	    bitmap_up.recycle();
   	    
   	    //load the button down texture
   	    gl.glGenTextures(1, antecedent_spinner.spin_up.texture_down, 0);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, antecedent_spinner.spin_up.texture_down[0]);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	    bitmap_down.recycle();
	    
	    //spin down button
    	bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_down_up);
		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spin_down_down);
		
		// load button up texture
    	gl.glGenTextures(1, antecedent_spinner.spin_down.texture_up, 0);
   	    gl.glBindTexture(GL10.GL_TEXTURE_2D, antecedent_spinner.spin_down.texture_up[0]);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
   	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
   	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
   	    bitmap_up.recycle();
   	    
   	    //load the button down texture
   	    gl.glGenTextures(1, antecedent_spinner.spin_down.texture_down, 0);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, antecedent_spinner.spin_down.texture_down[0]);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	    bitmap_down.recycle();
    }    

    //generate other buttons
    public void generateButtons(GL10 gl)
    {
    	//right edge of the screen is 1.76
    	float height = 0.082f;
    	float width = 0.42f;
    	float lx = -1.66f;
    	float rx = lx + width;
    	float uy = 0.81f;
    	float ly = uy + height;
    	
    	Bitmap bitmap_up = null;
    	Bitmap bitmap_down = null;
    	Bitmap bitmap_disabled = null;
  	  	//goto previous
    	float vert_b_temp1[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	    button_goto_prev = new Button(vert_b_temp1);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_gotoprev_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_gotoprev_down);

   		// load button up texture
	    gl.glGenTextures(1, button_goto_prev.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_goto_prev.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_goto_prev.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_goto_prev.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
		    
	   	button_goto_prev.button_text = "GOTO Prev";
	   	
    	//goto next
	   	lx = -1.1f;
    	rx = lx + width;
    	//uy = 0.53f;
    	ly = uy + height;
    	
    	
    	float vert_b_temp2[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	    button_goto_next = new Button(vert_b_temp2);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_gotonext_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_gotonext_down);

   		// load button up texture
	    gl.glGenTextures(1, button_goto_next.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_goto_next.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture
	   	gl.glGenTextures(1, button_goto_next.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_goto_next.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
		    
	   	button_goto_next.button_text = "GOTO Next";

	   	
	   	//reload rule
	   	
	   	lx = -1.12f;
    	rx = lx + width;
    	uy = 0.43f;
    	ly = uy + height;
    	
	   	float vert_b_temp3[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	   	button_rule_reload = new Button(vert_b_temp3);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_rulereload_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_rulereload_down);
   		bitmap_disabled = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_rulereload_disbaled);

   		// load button up texture
	    gl.glGenTextures(1, button_rule_reload.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_rule_reload.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_rule_reload.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_rule_reload.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
	   	// load button disbaled texture 
	   	gl.glGenTextures(1, button_rule_reload.texture_disabled, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_rule_reload.texture_disabled[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_disabled, 0);
	   	bitmap_disabled.recycle();
		    
	   	button_rule_reload.button_text = "Reload Rule";
	   	button_rule_reload.button_disabled = true;
	   	
	   	//mark normal
	   	//lx = -1.0f;
    	rx = lx + width;
    	uy = uy + 0.09f;
    	ly = uy + height;
    	
	   	float vert_b_temp4[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	   	button_mark_normal= new Button(vert_b_temp4);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_marknormal_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_marknormal_down);
   		bitmap_disabled = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_marknormal_disabled);

   		// load button up texture
	    gl.glGenTextures(1, button_mark_normal.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_mark_normal.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_mark_normal.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_mark_normal.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_mark_normal.texture_disabled, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_mark_normal.texture_disabled[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_disabled, 0);
	   	bitmap_disabled.recycle();
		    
	   	button_mark_normal.button_text = "mark normal";
	   	button_mark_normal.button_disabled = true;
	   	
	   	//save model
	   	//lx = -1.0f;
    	rx = lx + width;
    	uy = uy + 0.09f;
    	ly = uy + height;
    	
	   	float vert_b_temp5[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
	   	button_save_model= new Button(vert_b_temp5);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_savemodel_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_savemodel_down);
   		bitmap_disabled = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.button_other_savemodel_disabled);

   		// load button up texture
	    gl.glGenTextures(1, button_save_model.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_save_model.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_save_model.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_save_model.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
	   	// load button disabled texture 
	   	gl.glGenTextures(1, button_save_model.texture_disabled, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_save_model.texture_disabled[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_disabled, 0);
	   	bitmap_disabled.recycle();
	   	
	   	button_save_model.button_text = "Save Model";
	   	button_save_model.button_disabled = true;
	   	
	   	float vert_surround_timeslide[] = {-1.67f,0.22f, -1.67f,0.31f, -0.69f,0.31f, -0.69f,0.22f}; //(upper l,lower l,lower r,upper r)	   	
	   	button_time_slide = new Button(vert_surround_timeslide);
	    
   		bitmap_up = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.time_slide_button_up);
   		bitmap_down = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.time_slide_button_down);

   		// load button up texture
	    gl.glGenTextures(1, button_time_slide.texture_up, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_time_slide.texture_up[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_up, 0);
	   	bitmap_up.recycle();
	   	// load button down texture 
	   	gl.glGenTextures(1, button_time_slide.texture_down, 0);
	   	gl.glBindTexture(GL10.GL_TEXTURE_2D, button_time_slide.texture_down[0]);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	   	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	   	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap_down, 0);
	   	bitmap_down.recycle();
	   	
	   	button_time_slide.button_text = "Time Slide";
    }    
   
    //function for drawing a line of text
    public void drawTextLine(GL10 gl,String _text)
    {
    	gl.glPushMatrix();
		for(int i = 0; i < _text.length(); i++)
		{
			char t_char = _text.charAt(i);
			int t_int = Character.getNumericValue(t_char);
			//special characters
			if(t_int < 0)
			{
				 int t_special_int = t_char;
				 // comma
				 if(t_special_int == 44)
				 {
					 s.draw(gl, this.textures_special[0]);
				 }
				 // hyphen
				 else if(t_special_int == 45)
				 {
					 s.draw(gl, this.textures_special[1]);
				 }			
				 // space
				 else if(t_special_int == 32)
				 {
					 s.draw(gl, this.textures_special[2]);
				 }
				 // space
				 else if(t_special_int == 36)
				 {
					 s.draw(gl, this.textures_special[2]);
				 }	
				 // equals
				 else if(t_special_int == 61)
				 {
					 s.draw(gl, this.textures_special[4]);
				 }	
				 // slash
				 else if(t_special_int == 47)
				 {
					 s.draw(gl, this.textures_special[5]);
				 }	
				 // underscore
				 else if(t_special_int == 95)
				 {
					 s.draw(gl, this.textures_special[6]);
				 }	
				 // percentage
				 else if(t_special_int == 37)
				 {
					 s.draw(gl, this.textures_special[7]);
				 }	
				 // left bracket
				 else if(t_special_int == 40)
				 {
					 s.draw(gl, this.textures_special[8]);
				 }
				 // right bracket
				 else if(t_special_int == 41)
				 {
					 s.draw(gl, this.textures_special[9]);
				 }	
				 // colon
				 else if(t_special_int == 58)
				 {
					 s.draw(gl, this.textures_special[10]);
				 }	
				 // dot
				 else
				 {
					 s.draw(gl, this.textures_special[3]);
				 }				 
			}
			//numbers
			else if(t_int < 10)
			{
				s.draw(gl, this.textures_num[t_int]);
			}
			//text
			else
			{
				//upper case
				if(Character.isUpperCase(t_char))
				{
					boolean very_small_char = false;
					boolean small_char = false;
					boolean large_char = false;
					boolean very_large_char = false;
					//Letter I 
					if((t_int == 18))
					{
						very_small_char = true;
					}
					//Letter J
					if((t_int == 18) || (t_int == 19))
					{
						small_char = true;
					}
					//M Q
					if((t_int == 22) || (t_int == 26))
					{
						large_char = true;
					}		
					//W
					if(t_int == 32)
					{
						very_large_char = true;
					}		
					//move forward for small char
					if(small_char)
					{
						gl.glTranslatef(0.02f,0f, 0.0f);
					}
					//Draw the letter
					s.draw(gl, this.textures_text_upper[t_int-10]);
					//move for upper case
					gl.glTranslatef(0.0077f,0f, 0.0f);
					
					if(very_small_char)
					{
						gl.glTranslatef(-0.032f,0f, 0.0f);
					}
					if(small_char)
					{
						gl.glTranslatef(-0.017f,0f, 0.0f);
					}
					else if(large_char)
					{
						gl.glTranslatef(0.025f,0f, 0.0f);
					}
					else if(very_large_char)
					{
						gl.glTranslatef(0.037f,0f, 0.0f);
					}
				}
				//lower case
				else
				{
					boolean small_char = false;
					boolean large_char = false;
					//boolean very_large_char = false;
					//Letters F I J L Z R T
					if((t_int == 15) || (t_int == 18) || (t_int == 19) || (t_int == 21) || (t_int == 27) || (t_int == 29))
					{
						small_char = true;
					}
					//M W
					if((t_int == 22) || (t_int == 32))
					{
						large_char = true;
					}		
					//W
					//if(t_int == 32)
					//{
					//	very_large_char = true;
					//}	
					
					if((t_int == 18) || (t_int == 19))
					{
						//gl.glTranslatef(0.02f,0f, 0.0f);
					}				
					//Draw the letter
					s.draw(gl, this.textures_text_lower[t_int-10]);
					//translate little for smaller char Letter I and J
					if(small_char)
					{
						gl.glTranslatef(-0.03f,0f, 0.0f);
					}
					else if(large_char)
					{
						gl.glTranslatef(0.029f,0f, 0.0f);
					}

				}
			}
			gl.glTranslatef(0.065f,0f, 0.0f);
		}   
		gl.glPopMatrix();
    }

    // function to draw a line between the given vertices
    public void drawLine(GL10 gl, float [] _start, float [] _end, float _width)
    {
        float [] coordLine = new float[6];
        FloatBuffer lineVB;
        
        coordLine[0] = _start[0];
        coordLine[1] = _start[1];
        coordLine[2] = _start[2];
        
        coordLine[3] = _end[0];
        coordLine[4] = _end[1];
        coordLine[5] = _end[2];
        
        ByteBuffer vbbLine = ByteBuffer.allocateDirect(coordLine.length * 4); 
        vbbLine.order(ByteOrder.nativeOrder());
        lineVB = vbbLine.asFloatBuffer();
        lineVB.put(coordLine);
        lineVB.position(0); 
        
        gl.glEnable(gl.GL_BLEND);
    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glHint(gl.GL_LINE_SMOOTH_HINT, gl.GL_NICEST); 
    	//gl.glEnable(GL10.GL_DITHER);
    	gl.glEnable(GL10.GL_LINE_SMOOTH);
    	
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glLineWidth(_width);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVB);
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        gl.glLineWidth(1.0f);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glDisable(gl.GL_BLEND);
    	//gl.glDisable(GL10.GL_DITHER);
    	gl.glDisable(GL10.GL_LINE_SMOOTH);
    }
/*    
    public void drawArea(GL10 gl,float [] _vert, int _num_verts, float [] _norm)
    {
    	gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
        //PolygonLine p_line = new PolygonLine(_vert,_num_verts,_norm);
        //p_line.draw(gl);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
    	Polygon poly = new Polygon(_vert,_num_verts,_norm);
        poly.draw(gl);
    }
  */

  /*  
    public boolean locateInside(float [] _vert)//(upper r,lower r,lower l,upper r) 
    {
    	//within x bounds
    	if( (m_touch_x_relative > _vert[0]) && (m_touch_x_relative < _vert[4]) )
    	{
    		//within y bounds
    		if( (m_touch_y_relative > _vert[1]) && (m_touch_y_relative < _vert[3]) )
    		{
    			return true;
    		}
    	}
    	return false;
    }
    */
    
    //function to draw the wall of a zone
    public void drawWall(GL10 gl, float [] _line_vert)
    {
        PolygonLine p_line = new PolygonLine(_line_vert);
        p_line.draw(gl);
    }
    
    /*
    public void drawWall(GL10 gl, float [] _line_vert, boolean selected)
    {
    	gl.glPushMatrix();
    	if(selected)
    	{
    		//gl.glTranslatef(0.0155f, -0.012f, 0.05f);
    		//gl.glLineWidth(2);
    	}
        PolygonLine p_line = new PolygonLine(_line_vert);
        p_line.draw(gl);
        gl.glLineWidth(1);
        gl.glPopMatrix();
    }    
    */
    
    //function to draw the colored area of a zone
    public void drawArea(GL10 gl,float [] _poly_vert, int _num_verts)
    {
    	Polygon poly = new Polygon(_poly_vert,_num_verts);
        poly.draw(gl);
    }

    //function to draw the colored area of a zone
    public void drawRectangle(GL10 gl,float [] _vert_TL, float [] _vert_BL, float [] _vert_BR, float [] _vert_TR)
    {
    	float [] _poly_vert = new float[8];
    	
    	_poly_vert[0] = _vert_TL[0];
    	_poly_vert[1] = _vert_TL[1];
    	_poly_vert[2] = _vert_BL[0];
    	_poly_vert[3] = _vert_BL[1];
    	_poly_vert[4] = _vert_BR[0];
    	_poly_vert[5] = _vert_BR[1];
    	_poly_vert[6] = _vert_TR[0];
    	_poly_vert[7] = _vert_TR[1];
    	    	
    	Polygon poly = new Polygon(_poly_vert, 4);
        poly.draw(gl);
    }

    //function to draw a small rectangle
    public void drawRectangle(GL10 gl)
    {
    	float [] _poly_vert = new float[8];
    	
    	_poly_vert[0] = -0.01f;//_vert_TL[0];
    	_poly_vert[1] =  0.01f;//_vert_TL[1];
    	_poly_vert[2] = -0.01f;//_vert_BL[0];
    	_poly_vert[3] = -0.01f;//_vert_BL[1];
    	_poly_vert[4] =  0.01f;//_vert_BR[0];
    	_poly_vert[5] = -0.01f;//_vert_BR[1];
    	_poly_vert[6] =  0.01f;//_vert_TR[0];
    	_poly_vert[7] =  0.01f;//_vert_TR[1];
    	
    	Polygon poly = new Polygon(_poly_vert, 4);
        poly.draw(gl);
    }
    
    //draws a given dimensionality select button
    public void drawDimButton(GL10 gl, Button _button)
    {
    	_button.draw(gl);
        gl.glPushMatrix();
        gl.glTranslatef(_button.vertex[0] + 0.29f, _button.vertex[1] + 0.04f, 0.0f);
    	gl.glScalef(0.45f, 0.45f, 1f);
        drawTextLine(gl,_button.button_text);
        gl.glPopMatrix();      	
    }
    
    //draws a given spinner object
    public void drawSpinner(GL10 gl,Spinner _spin)
    {
    	//draw the buttons
    	_spin.draw(gl);
    	
    	//draw the text
    	gl.glPushMatrix();
        gl.glTranslatef(_spin.spin_vertex[0] - 0.05f, _spin.spin_vertex[1] + 0.03f, 0.0f);
    	gl.glScalef(0.6f, 0.6f, 1f);
        drawTextLine(gl,_spin.spin_text);
        gl.glPopMatrix();  
        
        //draw the value
        gl.glPushMatrix();
        gl.glTranslatef(_spin.spin_vertex[0] + 0.37f, _spin.spin_vertex[1] + 0.03f, 0.0f);
    	gl.glScalef(0.5f, 0.5f, 1f);
        drawTextLine(gl,String.format("%.1f", _spin.spin_value));
        gl.glPopMatrix();
    }
    
    // draw the value color box of a dimensionality select button
    public void drawDimButtonColorBox(GL10 gl, Button _button)
    {
    	//right edge of the screen is 1.76
    	//the size fo the box
    	float lx = _button.vertex[0] + 0.02f;
    	float rx = _button.vertex[4] - 0.5f;
    	float uy = _button.vertex[1] + 0.02f;
    	float ly = _button.vertex[3] - 0.02f;

    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	RectangleColor rect = new RectangleColor(vert_b_temp);
    	rect.draw(gl);  	
    }
    
    // draw the plot color of a dimensionality select button
    public void drawDimButtonPlotColorBox(GL10 gl, Button _button)
    {
    	//right edge of the screen is 1.76
    	float lx = _button.vertex[0] + 0.53f;
    	float rx = _button.vertex[4];
    	float uy = _button.vertex[1] + 0.02f;
    	float ly = _button.vertex[3] - 0.02f;

    	float vert_b_temp[] = {lx,uy,  lx,ly,  rx,ly,  rx,uy }; //(upper l,lower l,lower r,upper r)
    	RectangleColor rect = new RectangleColor(vert_b_temp);
    	rect.draw(gl);  	
    }
    
    //draw the loading percentages and states
    public void drawLoadPercentage(GL10 gl)
    {        
        gl.glPushMatrix();
    	gl.glTranslatef(0f,0f, 0.0f);  
    	//String load_per_str = Integer.toString(load_percentage);
    	//drawTextLine(gl,monitor.debug_status);
    	gl.glPopMatrix();
    	
    	//print the load status
    	gl.glPushMatrix();
    	gl.glTranslatef(-1.5f,1.75f, 0.0f);  
	    	gl.glPushMatrix();
		    	gl.glScalef(0.5f, 0.5f, 1f);
		    	drawTextLine(gl,load_status_0);
	    	gl.glPopMatrix();
	    	
    	gl.glTranslatef(0f, 0.05f, 0.0f);
	    	gl.glPushMatrix();
		    	gl.glScalef(0.5f, 0.5f, 1f);
		    	drawTextLine(gl,load_status_1);
	    	gl.glPopMatrix();
    	
	    gl.glTranslatef(0f, 0.05f, 0.0f);
	    	gl.glPushMatrix();
		    	gl.glScalef(0.5f, 0.5f, 1f);
		    	drawTextLine(gl,load_status_2);
	    	gl.glPopMatrix();
	    gl.glPopMatrix();  
	    
	    float [] vert_TL = new float[2];
	    float [] vert_BL = new float[2];
	    float [] vert_BR = new float[2];
	    float [] vert_TR = new float[2];
	    
	    //if(bl != null)
	    //	load_percentage += (float)bl.debug_load_percentage;
	    
	    load_percentage += 0.01;
	    
	    if(load_percentage >= 100)
	    	load_percentage = 100;
	    
	    float left_b = -1.5f;
	    float right_b = 1.4f;
	    float delta = right_b - left_b;
	    float curr_right = left_b + (( delta / 100) * ((float)load_percentage)); 
	    
	    vert_TL[0] = -1.5f;
	    vert_TL[1] = 1.62f;
	    
	    vert_BL[0] = -1.5f;
	    vert_BL[1] = 1.68f;

	    vert_BR[0] = curr_right;
	    vert_BR[1] = 1.68f;	
	    
	    vert_TR[0] = curr_right;
	    vert_TR[1] = 1.62f;
	    
	    gl.glColor4f(0.1f, 0.8f, 0.1f, 0.8f);
	    drawRectangle(gl, vert_TL, vert_BL, vert_BR, vert_TR);
    }
    
    //jump to the next anomaly
    public void gotoNextAnomaly()
    {
		if (bl.m_Floors[bl.m_SelectFloor].m_IsReady)
		{
			if (bl.m_DataPoint < bl.m_NData - 2)
			{
				bl.m_DataPoint++;
				monitor.evalBuilding(bl);
				//translateTime.set_x((float)bl.m_DataPoint);
		
				if (monitor.m_AnomMode == 0)
				{
					while ((bl.m_DataPoint < bl.m_NData - 1) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Cluster))
					{
						bl.m_DataPoint++;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
				else if (monitor.m_AnomMode == 1)
				{
					while ((bl.m_DataPoint < bl.m_NData - 1) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl.m_DataPoint++;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
				else
				{
					while ((bl.m_DataPoint < bl.m_NData - 1) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Cluster) &&
						(!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Rules))
					{
						bl.m_DataPoint++;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
			}

			monitor.evalBuilding(bl);

			if (bl.m_Floors[bl.m_SelectFloor].m_SelectZone > -1)
			{
				if (monitor.m_AnomMode == 0)
				{
					if (bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI > monitor.m_Threshold)
					
					{
						//buttonMark.enable();
					}
					else
					{
						//buttonMark.disable();
					}
				}
				else if (monitor.m_AnomMode == 1)
				{
					if (bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor.m_Threshold)
					{
						//buttonMark.enable();
					}
					else
					{
						//buttonMark.disable();
					}
				}
				else{
					if ((bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI > monitor.m_Threshold) ||
						(bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor.m_Threshold))
					{
						//buttonMark.enable();
					}
					else
					{
						//buttonMark.disable();
					}
				}
			}
		}	    	
    }
    
    //jump to the previous anomaly
    public void gotoPrevAnomaly()
    {
		if (bl.m_Floors[bl.m_SelectFloor].m_IsReady)
		{
			if (bl.m_DataPoint > 0){
				bl.m_DataPoint--;

				monitor.evalBuilding(bl);

				//translateTime.set_x((float)bl.m_DataPoint);

				if (monitor.m_AnomMode == 0){
					while ((bl.m_DataPoint > 0) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Cluster)){
						bl.m_DataPoint--;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
				else if (monitor.m_AnomMode == 1){
					while ((bl.m_DataPoint > 0) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Rules)){
						bl.m_DataPoint--;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
				else{
					while ((bl.m_DataPoint > 0) && (!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Cluster) && 
						(!bl.m_Floors[bl.m_SelectFloor].m_HasAnomaly_Rules)){
						bl.m_DataPoint--;
						monitor.evalFloor(bl, bl.m_DataPoint);					
						//translateTime.set_x((float)bl.m_DataPoint);
					}
				}
			}

			monitor.evalBuilding(bl);

			if (bl.m_Floors[bl.m_SelectFloor].m_SelectZone > -1){
				if (monitor.m_AnomMode == 0){
					if (bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI > monitor.m_Threshold){
						//buttonMark.enable();
					}
					else{
						//buttonMark.disable();
					}
				}
				else if (monitor.m_AnomMode == 1){
					if (bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor.m_Threshold){
						//buttonMark.enable();
					}
					else{
						//buttonMark.disable();
					}
				}
				else{
					if ((bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI > monitor.m_Threshold) ||
						(bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI_Rules > monitor.m_Threshold)){
						//buttonMark.enable();
					}
					else{
						//buttonMark.disable();
					}
				}
			}
		}
    	
    }
    // On draw frame function
    public void onDrawFrame(GL10 gl) 
    {
    	
        // Redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state
        
        // When using GL_MODELVIEW, you must set the view point
        if (this.screenOrient == 1)//portrait mode
        {
        	GLU.gluLookAt(gl, 0, 0, -10, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }
        else//landscape
        {
        	GLU.gluLookAt(gl, 0f, 0.9f, -3.1f, 0f, 0.9f, 0f, 0f, -1.0f, 0.0f);
    	}
        
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        gl.glPushMatrix();
        
        //TODO : The load screen
        //if the data is still loading then show the load screen
        if(load_screen)
        {
        	//draw the background
            load_background_texture.draw(gl, textures_load_background[0]);
            
            //if the data is loaded then load the textures
            if(load_data_done)
            {
            	if(!load_textures_done)
            	{
            		//load the basic textures
                    textureLoad(gl);
                    loadStatusUpdateCurrent("Loading basic textures .... DONE");
                    loadStatusUpdate("Initializing Buttons .... ");
                    load_textures_done = true;
                    load_percentage = 95;
            	}
            	else if(!load_buttons_done)
            	{
        	        //initialize the buttons
    				generateDimButtons(gl);
        	        generateViewSelectRadio(gl);
        	        generateAnomalySelectRadio(gl);
        	        generateAnomalyDimSelectTick(gl);
        	        generateThresholdSpinner(gl);
        	        generateAntecedentSpinner(gl);
        	        generateButtons(gl);
        	        temp_button = "Nothing";
        	        loadStatusUpdateCurrent("Initializing Buttons  .... DONE");       
        	        load_buttons_done = true;
        	        first_run = false;
            	}
            }
            
            drawLoadPercentage(gl);
            
            if(load_textures_done && load_buttons_done)
            {
            	load_screen = false;
            }
	    	
        }
        //if the data is loaded
        else
        {
        	//draw the background
            background_texture.draw(gl, textures_background[0]);
	        
	        //print building name
	        gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, -0.09f, 0.0f);
	    	gl.glScalef(0.65f, 0.65f, 1f);
	    	drawTextLine(gl,bl.m_NameStr);
	    	gl.glPopMatrix(); 
	    	
	    	//print Selected floor
	        gl.glPushMatrix();
	    	gl.glTranslatef(0.0f, -0.09f, 0.0f);
	    	gl.glScalef(0.6f, 0.6f, 1f);
	    	drawTextLine(gl,"Floor " + Integer.toString(bl.m_SelectFloor + 1));
	    	gl.glPopMatrix(); 
	    	
	    	//print the current time
	        gl.glPushMatrix();
	    	gl.glTranslatef(0.4f, -0.09f, 0.0f);
	    	gl.glScalef(0.6f, 0.6f, 1f);
	    	drawTextLine(gl,bl.m_Data.m_Time[bl.m_DataPoint]);
	    	gl.glPopMatrix(); 
	    	
	        //draw graph background
	    	gl.glPushMatrix();
	    	gl.glTranslatef(0f, 1.25f, 0.0f); 
	    	gl.glScalef(4.3f, 1.8f, 1);
	    	s.draw(gl, textures_graph[0]);
	    	gl.glPopMatrix();        
	        
	    	//draw graph surround
	        float vert_surround_graph[] = {0f, 1.07f,0f, 1.43f,  1.72f, 1.43f,1.72f, 1.07f }; //(upper r,lower r,lower l,upper l) 
	        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
	        PolygonLine p_line1 = new PolygonLine(vert_surround_graph);
	        p_line1.draw(gl); 
	        
	        //draw the floor surround
	        float vert_surround_floor[] = {0, 0, 0, 1, 1, 1, 1, 0};//(upper r,lower r,lower l,upper r) 
	        gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
	        PolygonLine p_line2 = new PolygonLine(vert_surround_floor);
	        p_line2.draw(gl);   
	        
	        //int index_bl= 0;
	        int index_fl= 0;
	        //draw the dimensionality selection buttons
	        for(int i = 0; i < num_dim_select_buttons; i++)
	        {
	        	float minVal = 0;
	        	float maxVal = 0;
	        	float col = 0;
	        	//update the button text
	        	if(i < 1)
	        	{
	        		dim_select_buttons[i].button_text = Integer.toString((int)bl.m_Data.m_Val[0][bl.m_DataPoint]) + " " + bl.m_DataUnits[0];
	        		minVal = bl.m_Floors[bl.m_SelectFloor].m_MinVal[0];
	        		maxVal = bl.m_Floors[bl.m_SelectFloor].m_MaxVal[0];
	        		col = 2.0f * ((bl.m_Data.m_Val[0][bl.m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;
	        	}
	        	else
	        	{
	        		dim_select_buttons[i].button_text = Integer.toString((int)bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[index_fl][bl.m_DataPoint]) + " " + bl.m_Floors[bl.m_SelectFloor].m_DataUnits[index_fl];
	        		minVal = bl.m_Floors[bl.m_SelectFloor].m_Data.m_MinVal[index_fl];
	        		maxVal = bl.m_Floors[bl.m_SelectFloor].m_Data.m_MaxVal[index_fl];
	        		col = 2.0f * ((bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[index_fl][bl.m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;
	        		index_fl++;
	        	}
	
	        	//draw button
	        	drawDimButton(gl,dim_select_buttons[i]);
	        	//draw the box with the color of the current value
	        	col = min(col, 1.0f);
	      	  	col = max(col, -1.0f);
	      	  	if (col < 0)
	      	  	{
	      	  		gl.glColor4f(0.0f, 1.0f + col, -col, 1.0f);
	      	  	}
	      	  	else
	      	  	{
	      	  		gl.glColor4f(col, 1.0f - col, 0.0f, 1.0f);
	      	  	}	        	
	        	drawDimButtonColorBox(gl,dim_select_buttons[i]);
	        	//draw the plot color box if button pressed
	        	if(dim_select_buttons[i].button_down)
	        	{
	        		gl.glColor4f(monitor.m_DataColors[i][0], monitor.m_DataColors[i][1], monitor.m_DataColors[i][2], 1.0f);
	        		drawDimButtonPlotColorBox(gl,dim_select_buttons[i]);
	        	}
	        }  
	        
	        view_select_radio.draw(gl);
	        anom_select_radio.draw(gl);
	        dim_select_tickbox.draw(gl);
	        //threshold_spinner.draw(gl);
	        drawSpinner(gl,threshold_spinner);
	        drawSpinner(gl,antecedent_spinner);
	        button_goto_next.draw(gl);
	        button_goto_prev.draw(gl);
	        button_rule_reload.draw(gl);
	        button_mark_normal.draw(gl);
	        button_save_model.draw(gl);
	        button_time_slide.draw(gl);
	
        	bl.m_SelectFloor = selected_floor;
	        
	        //draw the floor plan
	        //draw the fill
	        for(int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
	        {
	        	//default color
	    		gl.glColor4f(0.0f, 0.5f, 0.0f, 0.5f);
	    		//select the color
	    		if (monitor.m_DispMode == 0)
	    		{			  
	    			float minVal = bl.m_Floors[bl.m_SelectFloor].m_MinVal[0];
	    			float maxVal = bl.m_Floors[bl.m_SelectFloor].m_MaxVal[0];
	
	    			float col = 2.0f * ((bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Data.m_Val[0][bl.m_DataPoint] - minVal) / (maxVal - minVal)) - 1.0f;
	
	    			col = min(col, 1.0f);
	    			col = max(col, -1.0f);
	
	    			if (col < 0)
	    			{
	    				gl.glColor4f(0.0f, 1.0f + col, -col, 1.0f);
	    			}
	    			else
	    			{
	    				gl.glColor4f(col, 1.0f - col, 0.0f, 1.0f);
	    			}		  
	    		}
	    		else
	    		{
	  			  	float col = 0.0f;
	  			  	if (bl.m_Floors[bl.m_SelectFloor].m_IsReady)
	  			  	{
	  			  		temp_1 = "READY";
	  			  		temp_1 += Integer.toString(monitor.m_AnomMode);
	  			  		if (monitor.m_AnomMode == 0)
	  			  		{
	  			  			col = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI;
	  			  		}
	  			  		else if (monitor.m_AnomMode == 1)
	  			  		{
	  			  			col = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI_Rules;
	  			  		}
	  			  		else
	  			  		{
	  			  			col = max(bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI_Rules, bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI);
	  			  		}
	  			  	}
	  			  	else
	  			  	{
	  			  		temp_1 = "not ready";
	  			  		if (monitor.m_AnomMode == 0)
	  			  		{
	  			  			//col = bl.m_Floors[6].m_Zones[i].m_AnomalI;
	  			  		}
	  			  		else if (monitor.m_AnomMode == 1)
	  			  		{
	  			  			//col = bl.m_Floors[6].m_Zones[i].m_AnomalI_Rules;
	  			  		}
	  			  		else
	  			  		{
	  			  			//col = max(bl.m_Floors[6].m_Zones[i].m_AnomalI, bl.m_Floors[6].m_Zones[i].m_AnomalI_Rules);
	  			  		}
	  			  	}
	
	  			  	col = min(col, 1.0f);
	  			  	col = max(col, 0.0f);
	
	  			  	gl.glColor4f(col, 1.0f - col, 0.0f,1.0f);	
	    		}
	    		
	        	int num_poly = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_NPoly;
	        	for(int p = 0; p < num_poly; p++)
	        	{
	        		int num_verts = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Poly[p].m_NVertex;
	        		drawArea(gl,bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Poly[p].m_Vertex,num_verts);
	        	}
	        }
	        
	        //draw the wall
	        for(int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
	        {
	        	gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
	        	drawWall(gl,bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Vertex);
	        	
	        	gl.glLineWidth(1.0f);
	        	
	        	//draw the zone label
	        	gl.glPushMatrix();
	        	gl.glTranslatef(bl.m_Floors[selected_floor].m_Zones[i].m_MidX, bl.m_Floors[selected_floor].m_Zones[i].m_MidY, 0);
	        	gl.glTranslatef(-0.025f,0,0);
	        	gl.glScalef(0.3f, 0.3f, 1);
	        	String zone_name = "Z" + Integer.toString(bl.m_Floors[selected_floor].m_Zones[i].m_ID);
	        	drawTextLine(gl,zone_name);
	        	gl.glPopMatrix();
	        }
	        
	        //draw the wall for anomaly zones
	        for(int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
	        {
	        	boolean anomaly_zone = false;
	        	
	        	if (monitor.m_AnomMode == 0)
	        	{
	        		if (bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI > monitor.m_Threshold)
	        		{		
	        			gl.glLineWidth(3.0f);
	        			anomaly_zone = true;
	        		}
	        	}
	        	else if (monitor.m_AnomMode == 1)
	        	{
	        		if (bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI_Rules > monitor.m_Threshold){		
	        			anomaly_zone = true;
	        		}
	        	}
	        	else
	        	{
	        		float anomL = max(bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI, bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_AnomalI_Rules);
	        		if (anomL > monitor.m_Threshold){		
	        			anomaly_zone = true;
	        		}
	        	}
	        	
	        	if(anomaly_zone)
	        	{
	        		gl.glLineWidth(2.0f);
	        		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
	        		drawWall(gl,bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Vertex);
	        	}
	        	
	        	gl.glLineWidth(1.0f);
	        }
	        
	        //draw wall of the selected zone last
	        if(bl.m_Floors[bl.m_SelectFloor].m_SelectZone != -1)
	        {
	    		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    		drawWall(gl,bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_Vertex);        	
	        }
	        
	        
	        //draw the building floor select
	        for(int i = 0; i < 11; i ++)
	        {
	        	//draw the area around the floor if selected
	        	if(bl.m_SelectFloor == i)
	        	{
	        		floor_select_touchable[i].drawTouchable(gl);
	        	}
	        	
	        	// default color
	        	gl.glColor4f(0.0f, 0.3f, 0.0f, 0.5f);
	        	//set the color
	      	  	float col = 0.0f;	  
	      	  	
	      	  	// Set the proper color of the floor
	      	  	if (monitor.m_DispMode == 0)
	      	  	{			  
	      	  		float minVal = bl.m_Floors[i].m_MinVal[0];
	      	  		float maxVal = bl.m_Floors[i].m_MaxVal[0];
	
	      	  		col = 2.0f * ((bl.m_Floors[i].m_AvgTemp - minVal) / (maxVal - minVal)) - 1.0f;
	    	  
	      	  		col = min(col, 1.0f);
	      	  		col = max(col, -1.0f);
	
	      	  		if (col < 0)
	      	  		{
	      	  			gl.glColor4f(0.0f, 1.0f + col, -col, 1.0f);
	      	  		}
	      	  		else
	      	  		{
	      	  			gl.glColor4f(col, 1.0f - col, 0.0f, 1.0f);
	      	  		}		  	  	  
	      	  	}
	      	  	else
	      	  	{
	      	  		gl.glColor4f(0.0f, 0.3f, 0.0f, 0.5f);
	      	  		if (monitor.m_AnomMode == 0)
			      	{
			      	  	col = bl.m_Floors[i].m_MaxAnomConf_Cluster;
			      	}
			      	else if (monitor.m_AnomMode == 1)
			      	{
			      	  	col = bl.m_Floors[i].m_MaxAnomConf_Rules;
			      	}
			      	else
			      	{
			      		col = max(bl.m_Floors[i].m_MaxAnomConf_Cluster, bl.m_Floors[i].m_MaxAnomConf_Rules);
			      	}
		
			      	gl.glColor4f(col, 1.0f - col, 0.0f, 1.0f);
	      	  		
	      	  	}
	      	  	
	      	  	//grey out the floor if there is no data
	      	  	if(!bl.m_Floors[i].m_IsReady)
	      	  	{
	      	  		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
	      	  	}
	        	floors[i].draw(gl);
	        	
	        	//draw floor number
	        	gl.glPushMatrix();
	        	gl.glTranslatef(floor_select_touchable[i].touch_area[0] + 0.11f, floor_select_touchable[i].touch_area[1] + 0.04f, 0.0f);
	        	gl.glScalef(0.5f, 0.5f, 1f);
	        	drawTextLine(gl,"Floor " + Integer.toString(i + 1));
	        	gl.glPopMatrix();
	        	
	        	// Set the anomaly indicator (red square to the left of the floor)
	        	if ((bl.m_Floors[i].m_HasAnomaly_Cluster && (monitor.m_AnomMode == 0)) || 
	        		(bl.m_Floors[i].m_HasAnomaly_Rules && (monitor.m_AnomMode == 1)) ||
	        		((bl.m_Floors[i].m_HasAnomaly_Cluster || bl.m_Floors[i].m_HasAnomaly_Rules) && (monitor.m_AnomMode == 2)))
	        	{	  
	        		gl.glPushMatrix();
	        		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
	        		gl.glTranslatef(floor_select_touchable[i].touch_area[0] - 0.05f, floor_select_touchable[i].touch_area[1] + 0.05f, 0.0f);
	        		gl.glScalef(1.5f, 1.5f, 1f);
	        		drawRectangle(gl);
	        		gl.glPopMatrix();
	        	}
	        }
	        building_touchable.drawTouchable(gl);
	        
	    	//draw the zone temparature plot for the selected zone
	    	if (bl.m_Floors[bl.m_SelectFloor].m_SelectZone > -1)
	    	{
	    		for (int i = bl.m_DataPoint - 10; i < bl.m_DataPoint + 10; i++)
	    		{		  
	
	    			if ((i >= 0) && (i < bl.m_NData - 1))
	    			{
	    				
	    				//plot space
	    				//height = 0.34
	    				//top y = 1.08
	    				//bottom y = 1.42
	    				//mid y = 1.25
	    				
	    				//width = 1.72
	    				//left x = 0.01
	    				//right x = 1.73
	    				//mid x = 0.86
	    				
	    				float minVal = bl.m_Floors[bl.m_SelectFloor].m_MinVal[0];
	    				float maxVal = bl.m_Floors[bl.m_SelectFloor].m_MaxVal[0];
	    				
	    				float ori_val1 = bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_Data.m_Val[0][i];
	    				float ori_val2 = bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_Data.m_Val[0][i + 1];
	
	    				float val1 = -1.0f * ( 2.0f * ((ori_val1 - minVal) / (maxVal - minVal)) - 1.0f );
	    				float val2 = -1.0f * ( 2.0f * ((ori_val2 - minVal) / (maxVal - minVal)) - 1.0f );
	    				
	    				int idx = i - bl.m_DataPoint;
	    				gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    				
	    				float [] start = new float[3];
	    				float [] end = new float[3];
	    				
	    				start[0] = 0.86f + (idx * (0.86f/10.0f));
	    				start[1] = 1.25f + (val1 * 0.17f);
	    				start[2] = 0;
	    				
	    				end[0] = 0.86f + ((idx + 1) * (0.86f/10.0f));
	    				end[1] = 1.25f + (val2 * 0.17f);
	    				end[2] = 0;
	    				
	    				drawLine(gl, start, end, 1.5f);
	    			}
	    		}
	    	}
	    			
	    	//draw other selected plots
	    	for (int a = 0; a < monitor.m_NAttr; a++)
	    	{
	    		if (monitor.m_ShowAttr[a])
	    		{
	    			if (a < bl.m_NAttr)
	    			{
	    				for (int i = bl.m_DataPoint - 10; i < bl.m_DataPoint + 10; i++)
	    				{		  
	
	    					if ((i >= 0) && (i < bl.m_NData - 1))
	    					{
	    				  					  
	    						float minVal = bl.m_Data.m_MinVal[a];
	    						float maxVal = bl.m_Data.m_MaxVal[a];
	
	    						float val1 = -1.0f * ( (2.0f * ((bl.m_Data.m_Val[a][i] - minVal) / (maxVal - minVal))) - 1.0f);
	    						float val2 = -1.0f * ( (2.0f * ((bl.m_Data.m_Val[a][i + 1] - minVal) / (maxVal - minVal))) - 1.0f);
	
	    						int idx = i - bl.m_DataPoint;				  
	    						gl.glColor4f(monitor.m_DataColors[a][0], monitor.m_DataColors[a][1], monitor.m_DataColors[a][2],1.0f);
	    						
	    						float [] start = new float[3];
	    	    				float [] end = new float[3];
	    	    				
	    	    				start[0] = 0.86f + (idx * (0.86f/10.0f));
	    	    				start[1] = 1.25f + (val1 * 0.17f);
	    	    				start[2] = 0;
	    	    				
	    	    				end[0] = 0.86f + ((idx + 1) * (0.86f/10.0f));
	    	    				end[1] = 1.25f + (val2 * 0.17f);
	    	    				end[2] = 0;
	    	    				
	    	    				drawLine(gl, start, end, 1.5f);				
	    					}
	    				}
	    			}
	    			else
	    			{		  
	    				int idxA = a - bl.m_NAttr;
	    				for (int i = bl.m_DataPoint - 10; i < bl.m_DataPoint + 10; i++)
	    				{	  
	    					if ((i >= 0) && (i < bl.m_NData - 1))
	    					{		
	    				  
	    						float minVal = bl.m_Floors[bl.m_SelectFloor].m_Data.m_MinVal[idxA];
	    						float maxVal = bl.m_Floors[bl.m_SelectFloor].m_Data.m_MaxVal[idxA];
	
	    						float val1 = -1.0f * ( 2.0f * ((bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[idxA][i] - minVal) / (maxVal - minVal)) - 1.0f);
	    						float val2 = -1.0f * ( 2.0f * ((bl.m_Floors[bl.m_SelectFloor].m_Data.m_Val[idxA][i + 1] - minVal) / (maxVal - minVal)) - 1.0f);
	
	    						int idx = i - bl.m_DataPoint;				  
	    						gl.glColor4f(monitor.m_DataColors[a][0], monitor.m_DataColors[a][1], monitor.m_DataColors[a][2], 1.0f);
	    						
	    						float [] start = new float[3];
	    	    				float [] end = new float[3];
	    	    				
	    	    				start[0] = 0.86f + (idx * (0.86f/10.0f));
	    	    				start[1] = 1.25f + (val1 * 0.17f);
	    	    				start[2] = 0;
	    	    				
	    	    				end[0] = 0.86f + ((idx + 1) * (0.86f/10.0f));
	    	    				end[1] = 1.25f + (val2 * 0.17f);
	    	    				end[2] = 0;
	    	    				
	    	    				drawLine(gl, start, end, 1.5f);	
	    					}
	    				}		  
	    			}
	    		}
	    	}
	    	
	        boolean in_graph = false;
	        boolean in_time_slide = false;
	        
	        //handle the touch events
	        //touch event tap
	        if(m_touch_event_up)
	        {
	        	m_touch_move_x_prev = -10;
	        	m_touch_move_y_prev = -10;
	        	updateRelativePosition();
	        	
	        	//control button area
	        	if(controlbutton_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
		        	//view select radio
		        	if(view_select_radio.radioPress(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		monitor.m_DispMode = view_select_radio.radio_selected;
		        	}
		        	//anomaly type select radio
		        	else if(anom_select_radio.radioPress(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		monitor.m_AnomMode = anom_select_radio.radio_selected;
		        	}
		        	//anomaly dim select tick box
		        	else if (dim_select_tickbox.tickPress(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		for(int i = 0; i < dim_select_tickbox.tick_num_elements; i++)
		        		{
		        			monitor.m_AttrUse[i] = dim_select_tickbox.tick_state[i];
		        		}
		        		//set the number of enabled antecedents
		        		monitor.m_NAnt_Enabled = 0;
		        		for (int i = 0; i < monitor.m_DimF; i++)
		        		{
		        			if (monitor.m_AttrUse[i] == 1)
		        			{
		        				monitor.m_NAnt_Enabled++;
		        			}
		        		}
		        		//if zero use zone temp
		        		if (monitor.m_NAnt_Enabled == 0)
		        		{
		        			monitor.m_NAnt_Enabled = 1;
		        			monitor.m_AttrUse[0] = 1;				
		        			dim_select_tickbox.tick_state[0] = 1;
		        		}
		        		//set the antecedent number select spinner value
		        		if (antecedent_spinner.spin_value > monitor.m_NAnt_Enabled)
		        		{
		        			antecedent_spinner.spin_value = monitor.m_NAnt_Enabled;
		        			monitor.m_NAnt_Sel = (int)antecedent_spinner.spin_value;
		        		}
		        		antecedent_spinner.range_max = min(monitor.m_NAnt_Enabled, 3);

		        		monitor.evalBuilding(bl);
		        	}
		        	//threshold spinner
		        	else if(threshold_spinner.spinPress(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		monitor.m_Threshold = threshold_spinner.spin_value;
		        	}
		        	//antecedent spinner
		        	else if(antecedent_spinner.spinPress(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		monitor.m_NAnt_Sel = (int)antecedent_spinner.spin_value;
		        	}
		        	//goto next button
		        	else if(button_goto_next.tap(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		temp_button = button_goto_next.button_text;
		        	}
		        	//goto prev button
		        	else if(button_goto_prev.tap(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		temp_button = button_goto_prev.button_text;
		        	}
		        	//reload rule button
		        	else if(button_rule_reload.tap(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		temp_button = button_rule_reload.button_text;
		        	}
		        	//mark normal button
		        	else if(button_mark_normal.tap(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		temp_button = button_mark_normal.button_text;
		        	}
		        	//save model button
		        	else if(button_save_model.tap(m_touch_x_relative, m_touch_y_relative))
		        	{
		        		temp_button = button_save_model.button_text;
		        	}
		        	
	        	}
	        	
	        	//inside the floor select area
	        	else if(building_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
	        		for(int i = 0; i < bl.m_NFloors; i++)
	        		{
	        			if(floor_select_touchable[i].isInside(m_touch_x_relative, m_touch_y_relative))
	        			{
	        				temp_sel_id = i;
	        				if(bl.m_Floors[i].m_IsReady)
	        				{
	        					selected_floor = i;
	        				}
	        				
	        				bl.m_SelectFloor = selected_floor;
	        				
	        				break;
	        			}
	        		}
	        	}
	        	//dimensionality select buttons
	        	else if(dimbutton_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
		        	for(int i = 0; i < num_dim_select_buttons; i++)
		        	{
		        		dim_select_buttons[i].press(m_touch_x_relative, m_touch_y_relative);
		        		monitor.m_ShowAttr[i] = dim_select_buttons[i].button_down;
		        	}
	        	}
	        	// inside the floor plan
	        	else if(floor_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
	        		mainDisplayPick(gl);
	        	}
	        	// inside the graph
	        	else if(graph_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
	        		in_graph = true;
	        	}
	        	// inside the time slide
	        	else if(time_slide_touchable.isInside(m_touch_x_relative, m_touch_y_relative))
	        	{
	        		in_time_slide = true;
	        	}
	        	m_touch_event_up = false;
	        	m_touch_event_down = false;
	        	m_touch_event_move = false;
	        }
	
	        //move touch event
	        if(m_touch_event_move)
	        {
	        	updateRelativePosition();
	        	
	        	if (graph_touchable.isInside(m_touch_move_x_relative, m_touch_move_y_relative))
	        	{
	        		if(m_touch_move_x_prev == -10)
	        		{
	        			m_touch_move_x_prev = m_touch_move_x_relative;
	        			m_touch_move_y_prev = m_touch_move_y_relative;
	        		}
	        		else
	        		{
	        			m_touch_move_d_x += m_touch_move_x_prev - m_touch_move_x_relative;
	        			float step = 0.86f / 10.0f;
	        			if(m_touch_move_d_x >= step)
	        			{
	        				if(bl.m_DataPoint < bl.m_NData - 2)
	        				{
	        					bl.updateDataPointer(bl.m_DataPoint + 1);
	        				}
	        				m_touch_move_d_x = 0;
	        			}
	        			else if(m_touch_move_d_x <= -step)
	        			{
	        				if(bl.m_DataPoint > 0)
	        				{
	        					bl.updateDataPointer(bl.m_DataPoint - 1);
	        				}
	        				m_touch_move_d_x = 0;
	        			}
	        			monitor.evalBuilding(bl);
	        			//for debugging
			        	//String temp4 = "MOVE";
			        	//gl.glPushMatrix();
			        	//gl.glTranslatef(-0.55f, 1.85f, 0.0f);  
			        	//gl.glScalef(0.5f, 0.5f, 1f);
			        	//drawTextLine(gl,temp4);
			        	//gl.glPopMatrix(); 
			        	
			        	m_touch_move_x_prev = m_touch_move_x_relative;
			        	m_touch_move_y_prev = m_touch_move_y_relative;
	        		}
	        		
	        	}
	        	else if (time_slide_touchable.isInside(m_touch_move_x_relative, m_touch_move_y_relative))
	        	{
	        		float center = (time_slide_touchable.touch_area[0] + time_slide_touchable.touch_area[4]) / 2;
	        		float dx = m_touch_move_x_relative - center;
	        		float max_step = 10;
	        		float max_dx = time_slide_touchable.touch_area[4] - center;
	        		
	        		int n_step = (int)( (max_step / max_dx) * dx );
	        		int next_data = bl.m_DataPoint + n_step;
	        		
	        		if(next_data >= bl.m_NData - 2)
	        		{
	        			next_data = bl.m_NData - 2;
	        		}
	        		else if(next_data <= 0)
	        		{
	        			next_data = 0;
	        		}
	        		
	        		bl.updateDataPointer(next_data);
	        		monitor.evalBuilding(bl);
	        		//for debugging
	        		
	        		//String temp4 = "Center = " + Double.toString(center);
		        	//temp4 += " DX = " + Double.toString(dx);
		        	//temp4 += " n_step = " + Double.toString(n_step);
		        	//gl.glPushMatrix();
		        	//gl.glTranslatef(-0.55f, 1.9f, 0.0f);  
		        	//gl.glScalef(0.5f, 0.5f, 1f);
		        	//drawTextLine(gl,temp4);
		        	//gl.glPopMatrix(); 
			        	
		        	button_time_slide.button_over = true;
	        		
	        	}
	    
	        	//m_touch_event_down = false;
	        	//m_touch_event_move = false;
	        }
	        
	        //touch event down
	        if(m_touch_event_down)
	        {
	        	updateRelativePosition();
	        	//control button area
	        	if(controlbutton_touchable.isInside(m_touch_x_relative, m_touch_y_relative) && !m_touch_event_move)
	        	{
	        		//time slider
		        	if (time_slide_touchable.isInside(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		float center = (time_slide_touchable.touch_area[0] + time_slide_touchable.touch_area[4]) / 2;
		        		float dx = m_touch_down_x_relative - center;
		        		float max_step = 10;
		        		float max_dx = time_slide_touchable.touch_area[4] - center;
		        		
		        		int n_step = (int)( (max_step / max_dx) * dx );
		        		int next_data = bl.m_DataPoint + n_step;
		        		
		        		if(next_data >= bl.m_NData - max_step)
		        		{
		        			next_data = bl.m_NData - 2;
		        		}
		        		else if(next_data <= 0)
		        		{
		        			next_data = 0;
		        		}
		        		
		        		bl.updateDataPointer(next_data);
		        		monitor.evalBuilding(bl);
		        		
		        		//for debugging
		        		
		        		//String temp4 = "Center = " + Double.toString(center);
			        	//temp4 += " DX = " + Double.toString(dx);
			        	//temp4 += " n_step = " + Double.toString(n_step);
			        	//gl.glPushMatrix();
			        	//gl.glTranslatef(-0.55f, 1.85f, 0.0f);  
			        	//gl.glScalef(0.5f, 0.5f, 1f);
			        	//drawTextLine(gl,temp4);
			        	//gl.glPopMatrix(); 
			        	
			        	button_time_slide.button_over = true;
		        	}
		        	//other buttons
		        	//goto next button
		        	else if(button_goto_next.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		temp_button = button_goto_next.button_text;
		        		button_goto_next.button_over = true;
		        		gotoNextAnomaly();
		        	}
		        	//goto prev button
		        	else if(button_goto_prev.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		temp_button = button_goto_prev.button_text;
		        		button_goto_prev.button_over = true;
		        		gotoPrevAnomaly();
		        	}
		        	//reload rule button
		        	else if(button_rule_reload.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		temp_button = button_rule_reload.button_text;
		        		button_rule_reload.button_over = true;
		        	}
		        	//mark normal button
		        	else if(button_mark_normal.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		temp_button = button_mark_normal.button_text;
		        		button_mark_normal.button_over = true;
		        	}
		        	//save model button
		        	else if(button_save_model.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		temp_button = button_save_model.button_text;
		        		button_save_model.button_over = true;
		        	}
		        	//threshold spinner up
		        	else if(threshold_spinner.spin_up.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		threshold_spinner.spin_up.button_over = true;
		        	}
		        	//threshold spinner down
		        	else if(threshold_spinner.spin_down.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		threshold_spinner.spin_down.button_over = true;
		        	}
		        	//antecedent spinner up
		        	else if(antecedent_spinner.spin_up.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		antecedent_spinner.spin_up.button_over = true;
		        	}
		        	//antecedent spinner down
		        	else if(antecedent_spinner.spin_down.tap(m_touch_down_x_relative, m_touch_down_y_relative))
		        	{
		        		antecedent_spinner.spin_down.button_over = true;
		        	}
	        	}
	        	
	        }        
	        

    	    float desc_trans_x = -0.55f;
	        float desc_trans_y = 1.52f;
	        float desc_trans_z = 0.0f;
	        float desc_trans_inc = 0.07f;
	        
	        float desc_scale_x = 0.6f;
	        float desc_scale_y = 0.6f;
	        float desc_scale_z = 1.0f;
	        	        
	     // Display the linguistic description of the anomaly if selected
	        if (bl.m_Floors[bl.m_SelectFloor].m_IsReady)
	        {
	        	if (bl.m_Floors[bl.m_SelectFloor].m_SelectZone > -1)
	        	{
	        		boolean checkAnom = false;
	        		float anomICl = bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI;
	        		float anomIRl = bl.m_Floors[bl.m_SelectFloor].m_Zones[bl.m_Floors[bl.m_SelectFloor].m_SelectZone].m_AnomalI_Rules;

	        		//cluster based
	        		if (monitor.m_AnomMode == 0)
	        		{
	        			if (anomICl > monitor.m_Threshold)
	        			{
	        				checkAnom = true;
	        			}			  
	        		}
	        		//rules based
	        		else if (monitor.m_AnomMode == 1)
	        		{
	        			if (anomIRl > monitor.m_Threshold)
	        			{
	        				checkAnom = true;
	        			}			  
	        		}
	        		//combined
	        		else
	        		{
	        			float maxAnom = max(anomICl, anomIRl);

	        			if (maxAnom > monitor.m_Threshold)
	        			{
	        				checkAnom = true;
	        			}
	        		}
	        		//if there is an anomaly
	        		if (checkAnom)
	        		{
	        			String conf = monitor.getAnomalyConfidence((bl.m_Floors[bl.m_SelectFloor]));		
	        			String confStr = "";
	        			//cluster
	        			if (monitor.m_AnomMode == 0)
	        			{
	        				confStr = "Status : Anomaly - Cluster ( ";
	        			}
	        			//rules
	        			else if (monitor.m_AnomMode == 1)
	        			{
	        				confStr = "Status : Anomaly - Rule ( ";
	        			}
	        			//combined
	        			else
	        			{
	        				if (anomICl > anomIRl)
	        				{
	        					confStr = "Status : Anomaly - Cluster ( ";
	        				}
	        				else
	        				{
	        					confStr = "Status : Anomaly - Rule ( ";
	        				}
	        			}

	        			confStr += conf;
	        			confStr += " )";
	        			gl.glPushMatrix();
	        			gl.glTranslatef(desc_trans_x, desc_trans_y, desc_trans_z);  
	        	    	gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    	drawTextLine(gl,confStr);
	        	    	gl.glPopMatrix();

	        			//stroke_output(-0.3f, 0.05f, (char *) confStr.c_str());		
	        			//NEW
	        			String desc1 = "";
	        			String desc2 = "";
	        			String desc3 = "";
	        			//the first antecedent
	        			if (monitor.m_AnomMode == 0){
	        				desc1 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 0, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 1){
	        				desc1 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 0, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 2){
	        				if (anomICl > anomIRl){
	        					desc1 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 0, false);	
	        				}
	        				else{
	        					desc1 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 0, false);
	        				}
	        			}
	        			//The second antecedent
	        			if (monitor.m_AnomMode == 0){
	        				desc2 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 1, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 1){
	        				desc2 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 1, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 2){
	        				if (anomICl > anomIRl){
	        					desc2 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 1, false);		  
	        				}
	        				else{
	        					desc2 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 1, false);		  
	        				}
	        			}
	        			//The third antecedent
	        			if (monitor.m_AnomMode == 0){
	        				desc3 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 2, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 1){
	        				desc3 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 2, false);		  
	        			}
	        			else if (monitor.m_AnomMode == 2){
	        				if (anomICl > anomIRl){
	        					desc3 = monitor.getDescription((bl.m_Floors[bl.m_SelectFloor]), 2, false);	
	        				}
	        				else{
	        					desc3 = monitor.getDescriptionRule((bl.m_Floors[bl.m_SelectFloor]), 2, false);		   
	        				}
	        			}

	        			if (monitor.m_NAnt_Sel == 1)
	        			{
	        				//stroke_output(-0.3f, 0.0f, (char *)desc1.c_str());
	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc, desc_trans_z);
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc1);
	        	    		gl.glPopMatrix();
	        			}
	        			else if (monitor.m_NAnt_Sel == 2)
	        			{
	        				if(desc2 != "")
	        				{
	        					desc1 += " AND";
	        				}
	        				//stroke_output(-0.3f, 0.0f, (char *)desc1.c_str());
	        				//stroke_output(-0.3f, -0.05f, (char *)desc2.c_str());
	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc, desc_trans_z); 
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc1);
	        	    		gl.glPopMatrix();

	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc + desc_trans_inc, desc_trans_z); 
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc2);
	        	    		gl.glPopMatrix();
	        			}
	        			else
	        			{
	        				if(desc2 != "")
	        				{
	        					desc1 += " AND";
	        				}
	        				if (desc3 != "")
	        				{
	        					desc2 += " AND";
	        				}
	        				//stroke_output(-0.3f, 0.0f, (char *)desc1.c_str());
	        				//stroke_output(-0.3f, -0.05f, (char *)desc2.c_str());
	        				//stroke_output(-0.3f, -0.1f, (char *)desc3.c_str());

	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc, desc_trans_z);   
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc1);
	        	    		gl.glPopMatrix();

	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc + desc_trans_inc, desc_trans_z);   
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc2);
	        	    		gl.glPopMatrix();

	        				gl.glPushMatrix();
	        				gl.glTranslatef(desc_trans_x, desc_trans_y + desc_trans_inc + desc_trans_inc + desc_trans_inc, desc_trans_z);  
	        				gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    		drawTextLine(gl,desc3);
	        	    		gl.glPopMatrix();
	        			}		  
	        		}
	        		else
	        		{
	        			String conf = monitor.getNormalConfidence((bl.m_Floors[bl.m_SelectFloor]));		  
	        			String confStr = "Status : Normal: ( ";
	        			confStr += conf;
	        			confStr += " )";
	        			//stroke_output(-0.3f, 0.05f, (char *) confStr.c_str());

	        			gl.glPushMatrix();
	        			gl.glTranslatef(desc_trans_x, desc_trans_y, desc_trans_z);  
	        			gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    	drawTextLine(gl,confStr);
	        	    	gl.glPopMatrix();
	        		}
	        	   
	        	}
	        	else
	        	{  	  
	        		//stroke_output(-0.3f, 0.05f, "Status: N/A");		
	        		gl.glPushMatrix();
	        		gl.glTranslatef(desc_trans_x, desc_trans_y, desc_trans_z);   
	        		gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	    drawTextLine(gl,"Status: N /A");
	        	    gl.glPopMatrix();
	        	}
	        }
	        else
	        {
	        	//stroke_output(-0.3f, 0.05f, "Status: N/A");	
	        	gl.glPushMatrix();
	        	gl.glTranslatef(desc_trans_x, desc_trans_y, desc_trans_z);    
	        	gl.glScalef(desc_scale_x, desc_scale_y, desc_scale_z);
	        	drawTextLine(gl,"Status: N /A");
	        	gl.glPopMatrix();
	        }
	        
	        
	        //time_slide_touchable.drawTouchable(gl);
	
	        //print the touch position 
	        /*
	    	String temp = Integer.toString((int)this.m_touch_x) + " , " + Integer.toString((int)this.m_touch_y);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.5f, 0.0f);  
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,"ABS = " + temp + "   N Floors = " + Integer.toString(bl.m_NFloors));
	    	gl.glPopMatrix();
	
	    	//print the relative touch position 
	    	String temp2 = "REL = " + String.format("%.2f",this.m_touch_x_relative) + " , " + String.format("%.2f",this.m_touch_y_relative);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.55f, 0.0f);  
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,temp2);
	    	gl.glPopMatrix();
	    	
	        
	        
	        //print the pick color for debugging
	    	String temp4 = Integer.toString(m_pick_color_r) +","+Integer.toString(m_pick_color_g)+","+Integer.toString(m_pick_color_b);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.6f, 0.0f);  
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,"Picked Color = " + temp4);
	    	gl.glPopMatrix();
	    	temp4 = Integer.toString(selected_zone);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.65f, 0.0f); 
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,"Selected Zone = " + temp4);
	    	gl.glPopMatrix();  
	    	temp4 = "Disp = " + Integer.toString(monitor.m_DispMode) + " Anom = " + Integer.toString(monitor.m_AnomMode) + " Thresh = " + Double.toString(monitor.m_Threshold) + " Ants = " + Integer.toString(monitor.m_NAnt_Sel);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.7f, 0.0f); 
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,temp4);
	    	gl.glPopMatrix();  
	    	
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.75f, 0.0f); 
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,"Button Value = " + temp_button);
	    	gl.glPopMatrix();  
	    	
	    	String t_down = "ABS_Down = " + String.format("%.0f", m_touch_down_x) + ", " + String.format("%.0f", m_touch_down_y);
	    	t_down += "  Rel_Down = " + String.format("%.3f", m_touch_down_x_relative) + ", " + String.format("%.3f", m_touch_down_y_relative);
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.8f, 0.0f); 
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,t_down);
	    	gl.glPopMatrix(); 
	    	
	        
	    	//file loading errors for debugging
	        String err_msg = bl.bld_err;
	        for(int i = 0; i < bl.m_NFloors; i++)
	        {
	        	err_msg += bl.m_Floors[i].flr_err;
	        }
	        //print Error messages 
	        if(!err_msg.equals(""))
	        {
	        	err_msg = "Error = " + err_msg;
		    	gl.glPushMatrix();
		    	gl.glTranslatef(-0.55f, 1.7f, 0.0f); 
		    	gl.glScalef(0.4f, 0.4f, 1f);
		    	drawTextLine(gl,err_msg);
		    	gl.glPopMatrix();
	        }  
	        
	    	//print if touch is in graph deugging
	    	if(in_graph)
	    	{
	        	String temp4 = "In graph";
	        	gl.glPushMatrix();
	        	gl.glTranslatef(-0.55f, 1.8f, 0.0f);  
	        	gl.glScalef(0.5f, 0.5f, 1f);
	        	gl.glScalef(0.5f, 0.5f, 1f);
	        	drawTextLine(gl,temp4);
	        	gl.glPopMatrix();  
	    	}
	    	
	    	//temp_1 = monitor.temp;
	    	//temp_1 = "TEMP 1 = " + temp_1;
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-0.55f, 1.85f, 0.0f); 
	    	gl.glScalef(0.5f, 0.5f, 1f);
	    	drawTextLine(gl,temp_1);
	    	gl.glPopMatrix(); 
	    	*/
	    	
	    	/*
	    	float [] start_t = {0, 0, 0};
	    	float [] end_t = {-0.3f, m_touch_move_y_relative, 0};
	    	gl.glColor4f(1, 1, 1, 1);
	    	drawLine(gl,start_t,end_t,3);
	    	
	    	float [] start_a = {-0.3f, 1.6f, 0};
	    	float [] end_a = {1, 1, 0};
	    	gl.glColor4f(1, 0, 0, 1);
	    	drawLine(gl,start_a,end_a,3);
	    	*/
	    	
	    	  	
	    	//dimbutton_touchable.drawTouchable(gl);
	    	//controlbutton_touchable.drawTouchable(gl);
	    	//building_touchable.drawTouchable(gl);
	    	//floor_touchable.drawTouchable(gl);
	    	
	    	
	        // Draw the triangle
	        /*gl.glColor4f(0.5f, 0.5f, 0.5f, 0.0f);
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleBckgVB);
	        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, seg * 3);
	        
	        
	        // Draw the frame lines        
	        gl.glColor4f(0.8f, 0.8f, 0.8f, 0.0f);
	        gl.glLineWidth(5.0f);
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, frameLineVB);
	        gl.glDrawArrays(GL10.GL_LINES, 0, seg * 2);
	        gl.glLineWidth(1.0f);        
	        /*
	        float optAngle = 360.0f - (this.velOpt / 100.0f) * 270.0f + 135.0f;
	        optAngle = (optAngle / 360.0f) * (2.0f * (float)Math.PI);
	        
	        float nowAngle = 360.0f - (this.velNow / 100.0f) * 270.0f + 135.0f;
	        nowAngle = (nowAngle / 360.0f) * (2.0f * (float) Math.PI);
	        
	        float minAngle = Math.min(optAngle, nowAngle);
	        float maxAngle = Math.max(optAngle, nowAngle);               
	                
	        f.setTriangles(this.midX, this.midY, this.rad, minAngle, maxAngle);
	        
	        f.draw(gl);
	        
	        // Draw the velocity line
	        this.setSpeedLine();
	        gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
	        gl.glLineWidth(5.0f);
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.velLineVB);
	        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
	        gl.glLineWidth(1.0f);
	        /*
	        
	        //this.setPoly();
	        //gl.glColor4f(1.0f, 1.0f, 0.0f, 0.0f);
	        //gl.glLineWidth(5.0f);
	        //gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.test_poly);
	        //gl.glDrawArrays(GL10.GL_LINES, 0, 2);
	        //gl.glLineWidth(1.0f);
	        
	        //draw numbers
	        float ang = 0;
	        for (int i = 0; i < 11; i++)
	        {
	        	ang = 225.0f + i * 27.0f;        	
	        	ang = (ang / 360.0f) * (2.0f * (float)Math.PI);
	        	
	        	gl.glPushMatrix();
	        	
	        	gl.glTranslatef(this.midX + 0.8f * this.rad * (float)Math.sin(ang) - i * 0.01f, 
	        			this.midY + 0.8f * this.rad * (float)Math.cos(ang) + 0.015f * (5 - Math.abs(5 - i)), 0.0f);        	
	        	s.draw(gl, this.textures_num[10 - i]);          	
	        	gl.glPopMatrix();
	        }
	        
	       // Draw the velocity marks        
	        gl.glColor4f(0.8f, 0.8f, 0.8f, 0.0f);
	        gl.glLineWidth(5.0f);
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, frameMarksVB);
	        gl.glDrawArrays(GL10.GL_LINES, 0, 11 * 2);
	        gl.glLineWidth(1.0f);
	        */
	        //String filePath = mContext.getFilesDir().getPath().toString() + "/fileName.txt";
	
	    	/*
	    	//print the floor details
	       	float y = 1.4f;
	        for(int i = 0; i < bl.m_NFloors; i++)
	        {
	            gl.glPushMatrix();
	        	gl.glTranslatef(-2.6f,y, 0.0f);  
	        	drawTextLine(gl,Integer.toString(bl.m_Floors[i].m_Id) + 
	        			", NumZone " + Integer.toString(bl.m_Floors[i].m_NZones) +
	        			", ZoneID " + Integer.toString(bl.m_Floors[i].m_Zones[0].m_ID) + 
	        			", NumVert " + Integer.toString(bl.m_Floors[i].m_Zones[0].m_NVertex) + 
	        			",Vert0 = " + Double.toString(bl.m_Floors[i].m_Zones[0].m_Vertex[0]));
	        	gl.glPopMatrix();
	        	//",-" + bl.m_Floors[i].m_Zones[0].temp +"-"+
	        	y-=0.1;
	        }    	
	    	
	        */
	    	/*
	        float norm[] = {77,600,53,505};
	        //float vert[] = {0.2f,-0.1f,0.2f,-0.2f,0.4f, -0.2f, 0.4f, -0.1f,};
	        //float vert[] = {81f,360f, 81f,327f, 128f,327f, 128f,360f,};
	        float vert[] = {490f,503f, 490f,457f, 547f,457f, 558f,468f, 558f,503f};
	        //float vert[] = {557, 92, 557, 53, 599, 53, 599, 110, 567, 110};
	        gl.glPushMatrix();
	        //gl.glTranslatef(-2f, -2f, 0.0f); 
	        //drawArea(gl,vert,5,norm);
	        gl.glPopMatrix();
	        float vert2[] = {547, 442, 547, 398, 599, 398, 599, 442};
	        float vert3[] = {523, 415, 523, 398, 547, 398, 547, 415};
	        float vert4[] = {567, 450, 560, 442, 599, 442, 599, 450};
	        
	        float vert5[] = bl.m_Floors[0].m_Zones[0].m_Poly[0].m_Vertex;
	        //drawArea(gl,vert2,4,norm);
	        //drawArea(gl,vert3,4,norm);
	        //drawArea(gl,vert4,4,norm);
	        //drawArea(gl,vert5,4);
	
	        */
	
	        
	  /*      
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-2.5f,0f, 0.0f);  
	    	//drawText(gl,"testing banner bank building floors = 2 number of data points = 1000000");
	    	drawTextLine(gl,"abcdefghaiajakalamanopqarasatauavawaxayaz");
	    	//drawTextLine(gl,Integer.toString(vert5.length));
	    	gl.glPopMatrix();
	    	
	    	gl.glPushMatrix();
	    	gl.glTranslatef(-2.5f,-1f, 0.0f); 
	    	gl.glScalef(1f, 1f, 1f);
	    	//drawText(gl,"testing banner bank building floors = 2 number of data points = 1000000");
	    	drawTextLine(gl,"banne Bank Building, No. Floors = 2, Value = 1.11132453");
	    	//drawTextLine(gl,"NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNn");
	    	//drawTextLine(gl,Integer.toString(vert5.length));
	    	gl.glPopMatrix();
	    */	
	        //gl.glPixelStorei(gl.GL_UNPACK_ALIGNMENT, 1);
	        //gl.glPixelStorei(gl.GL_PACK_ALIGNMENT, 1);
        }

    	gl.glPopMatrix();
    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    
    //the display pick function
    public void mainDisplayPick(GL10 gl)
    {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0.001f);
        float vert_back[] = {0, 0, 0, 1, 1, 1, 1, 0};
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
        Polygon p_back = new Polygon(vert_back,4);
        p_back.draw(gl);
        
        // Draw the zones
        for (int i = 0; i < bl.m_Floors[bl.m_SelectFloor].m_NZones; i++)
        {
      	  	if ((bl.m_Floors[bl.m_SelectFloor].m_HasFill))
      	  	{		  	
      	  		gl.glColor4f( ((float)colors[i][0])/255, ((float)colors[i][1])/255, ((float)colors[i][2])/255, 1);		 		  
      	  		//gl.glColor4x( 40000, 0, 0, 1);
      	  		for (int p = 0; p < bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_NPoly; p++)
      	  		{
      	  			int num_verts = bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Poly[p].m_NVertex;
      	  			drawArea(gl,bl.m_Floors[bl.m_SelectFloor].m_Zones[i].m_Poly[p].m_Vertex,num_verts);
      	  		}
      	  	}
        }        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        selected_zone = getZonePick(gl);
        processZonePick();
        gl.glPopMatrix();
        //gl.glEnable(gl.GL_LIGHTING);
        m_touch_event_up = false;
    }
    
    // process the picked zone
    public void processZonePick()
    {
    	//if zone is already selected then unmark it
    	if(bl.m_Floors[bl.m_SelectFloor].m_SelectZone == selected_zone)
    	{
			bl.m_Floors[bl.m_SelectFloor].m_SelectZone = -1;
			//buttonMark.disable();					
		}   	
		else if (selected_zone < bl.m_Floors[bl.m_SelectFloor].m_NZones)
		{
			//set the selected zone
			if (selected_zone > -1)
			{
				bl.m_Floors[bl.m_SelectFloor].m_SelectZone = selected_zone;	
			}
		}
    }
    
    //get the picked zone
    public int getZonePick(GL10 gl)
    {
        gl.glFlush();
        ByteBuffer pixels = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
        gl.glReadPixels((int)m_touch_x, 750 - (int)m_touch_y, 1, 1, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, pixels);
        
        m_pick_color_r = pixels.get(0);
        m_pick_color_g = pixels.get(1);
        m_pick_color_b = pixels.get(2);
        
    	if(m_pick_color_r < 0)
    	{
    		m_pick_color_r = m_pick_color_r + 256;
    	} 
    	if(m_pick_color_g < 0)
    	{
    		m_pick_color_g = m_pick_color_g + 256;
    	} 
    	if(m_pick_color_b < 0)
    	{
    		m_pick_color_b = m_pick_color_b + 256;
    	} 
         
    	if(m_pick_color_r == 255 && m_pick_color_g == 255 && m_pick_color_b == 255)
    	{
    		return -1;
    	}
    	
    	if(m_pick_color_r == 77 && m_pick_color_g == 77 && m_pick_color_b == 77)
    	{
    		return -1;
    	}
    	
    	else
    	{
    		boolean check = false;
    		int count = 0;
    		while ((!check) && (count < 4096))
    		{
    			if ((m_pick_color_r == colors[count][0]) && (m_pick_color_g == colors[count][1]) && (m_pick_color_b == colors[count][2]))
    			{				
    				check = true;
    				return count;
    			}
    			else
    			{
    				count++;
    			}
    		}
    		return -1;    		
    	}
    }
    
    public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
    	gl.glViewport(0, 0, width, height);
        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 17);  // apply the projection matrix      
        
        if (width > height)
        {
        	this.screenOrient = 0;        
        }
        else
        {
        	this.screenOrient = 1;
        }
    }    
    
    /*
    private void initShapes()
    {            	      
    	
    	float angInc = (float) ((2.0 * Math.PI) / (float)seg);
    	float ang = 0.0f;
    	    	        
        float [] coord = new float[seg * 3 * 3];
        
        for (int i = 0; i < seg; i++)
        {
        	coord[i * 9 + 0] = (float)(midX + rad * Math.sin((double)ang));
        	coord[i * 9 + 1] = (float)(midY + rad * Math.cos((double)ang));
        	coord[i * 9 + 2] = 0.0f;
        	coord[i * 9 + 3] = (float)(midX);
        	coord[i * 9 + 4] = (float)(midY);
        	coord[i * 9 + 5] = 0.0f;
        	ang += angInc;
        	coord[i * 9 + 6] = (float)(midX + rad * Math.sin((double)ang));
        	coord[i * 9 + 7] = (float)(midY + rad * Math.cos((double)ang));
        	coord[i * 9 + 8] = 0.0f;
        }
               
        
        // initialize vertex Buffer for triangle  
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coord.length * 4); 
        vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        triangleBckgVB = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        triangleBckgVB.put(coord);    // add the coordinates to the FloatBuffer
        triangleBckgVB.position(0);            // set the buffer to read the first coordinate    
        
        ang = 0.0f;
        
        float [] coordFrame =new float[seg * 3 * 2];
        
        for (int i = 0; i < seg; i++)
        {
        	coordFrame[i * 6 + 0] = (float)(this.midX + rad * Math.sin((double)ang));
        	coordFrame[i * 6 + 1] = (float)(this.midY + rad * Math.cos((double)ang));
        	coordFrame[i * 6 + 2] = 0.0f;
        	
        	ang += angInc;
        	
        	coordFrame[i * 6 + 3] = (float)(this.midX + rad * Math.sin((double)ang));
        	coordFrame[i * 6 + 4] = (float)(this.midY + rad * Math.cos((double)ang));
        	coordFrame[i * 6 + 5] = 0.0f;
        }
        
        // initialize vertex Buffer for triangle  
        ByteBuffer vbbFrame = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordFrame.length * 4); 
        vbbFrame.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        frameLineVB = vbbFrame.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        frameLineVB.put(coordFrame);    // add the coordinates to the FloatBuffer
        frameLineVB.position(0);            // set the buffer to read the first coordinate   
        
        ang = 0.0f;
        
        float [] coordMarks =new float[11 * 3 * 2];
        
        for (int i = 0; i < 11; i++)
        {
        	ang = 225.0f + i * 27.0f;
        	ang = (float)((ang / 360.0f) * (2.0f * Math.PI));
        	
        	coordMarks[i * 6 + 0] = (float)(this.midX + rad * Math.sin((double)ang));
        	coordMarks[i * 6 + 1] = (float)(this.midY + rad * Math.cos((double)ang));
        	coordMarks[i * 6 + 2] = 0.0f;
        	
        	coordMarks[i * 6 + 3] = (float)(this.midX + 0.9f * rad * Math.sin((double)ang));
        	coordMarks[i * 6 + 4] = (float)(this.midY + 0.9f * rad * Math.cos((double)ang));
        	coordMarks[i * 6 + 5] = 0.0f;
        }
        
        // initialize vertex Buffer for triangle  
        ByteBuffer vbbMarks = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordMarks.length * 4); 
        vbbMarks.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        frameMarksVB = vbbMarks.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        frameMarksVB.put(coordMarks);    // add the coordinates to the FloatBuffer
        frameMarksVB.position(0);            // set the buffer to read the first coordinate        
        
        float [] coordLine = new float[6];
        
        coordLine[0] = this.midX;
        coordLine[1] = this.midY;
        coordLine[2] = 0.0f;
        
        coordLine[3] = this.midX;
        coordLine[4] = this.midY + 0.8f * this.rad;
        coordLine[5] = 0.0f;
        
        // initialize vertex Buffer for triangle  
        ByteBuffer vbbLine = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordLine.length * 4); 
        vbbLine.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        this.velLineVB = vbbLine.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        this.velLineVB.put(coordLine);    // add the coordinates to the FloatBuffer
        this.velLineVB.position(0);            // set the buffer to read the first coordinate   
        
    }
    */
    
    /*
    private void setPoly()
    {
    	float [] test_poly_c = new float[9];
    	for(int i = 0; i < 3; i++)
    	{
    		test_poly_c[i*3] = 0;
    		test_poly_c[i*3+1] = 0;
    		test_poly_c[i*3+2] = 0;
    	}
        this.test_poly.position(0);            // set the buffer to read the first coordinate
        this.test_poly.put(test_poly_c);    // add the coordinates to the FloatBuffer
        this.test_poly.position(0);            // set the buffer to read the first coordinate       
    }
    */
    
    /*
    private void setSpeedLine()
    {
    	float vel = Math.min(this.velNow, 100.0f);
    	
    	float posAngle = 360.0f - (vel / 100.0f) * 270.0f + 135.0f;    	
    	posAngle = (posAngle / 360.0f) * (2.0f * (float)Math.PI);    	    
    	
    	float [] coordLine = new float[6];
         
        coordLine[0] = this.midX;
        coordLine[1] = this.midY;
        coordLine[2] = 0.0f;
         
        coordLine[3] = this.midX + 0.8f * this.rad * (float)Math.sin(posAngle);
        coordLine[4] = this.midY + 0.8f * this.rad * (float)Math.cos(posAngle);
        coordLine[5] = 0.0f;
    	
        this.velLineVB.position(0);            // set the buffer to read the first coordinate
        this.velLineVB.put(coordLine);    // add the coordinates to the FloatBuffer
        this.velLineVB.position(0);            // set the buffer to read the first coordinate       
    }
    */
    

}
