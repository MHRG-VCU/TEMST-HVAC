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

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

public class TEMSTAppTestActivity extends Activity 
{
    
    private GLSurfaceView mGLView;
    private ArrayAdapter mArrayAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new TEMSTSurfaceView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

class TEMSTSurfaceView extends GLSurfaceView 
{
    private TEMSTSurfaceViewRenderer mRenderer;
    public Context mContext;
    
    public TEMSTSurfaceView(Context context)
    {
        super(context);
        mRenderer = new TEMSTSurfaceViewRenderer();
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        //getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(mRenderer);
        mRenderer.mContext = context;
        //
        
        // Set the Renderer for drawing on the GLSurfaceView
        //setRenderer(new TEMSTSurfaceViewRenderer());
        
    }

    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();
        
        switch (e.getAction()) 
        {
            case MotionEvent.ACTION_UP:
    
                //float dx = x - mPreviousX;
                //float dy = y - mPreviousY;
    
                // reverse direction of rotation above the mid-line
                //if (y > getHeight() / 2) {
                //  dx = dx * -1 ;
                //}
    
                // reverse direction of rotation to left of the mid-line
                //if (x < getWidth() / 2) {
                //  dy = dy * -1 ;
                //}
              
                //mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;
                //requestRender();
            	mRenderer.m_touch_x = x;
            	mRenderer.m_touch_y = y;
            	mRenderer.m_touch_event_up = true;
            	break;
            case MotionEvent.ACTION_MOVE:
            	mRenderer.m_touch_move_x = x;
            	mRenderer.m_touch_move_y = y;
            	mRenderer.m_touch_event_move = true;
            	break;
            case MotionEvent.ACTION_DOWN:
            	mRenderer.m_touch_down_x = x;
            	mRenderer.m_touch_down_y = y;
            	mRenderer.m_touch_event_down = true;
            	break;
        }
        
        /*
        try 
        {
			Thread.sleep(10);
		} 
        catch (InterruptedException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
        //mPreviousX = x;
        //mPreviousY = y;
        return true;
    } 

}