package com.example.android_denoise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;
    
    public void reallocRenderer(String path_to_texture)
    {
    	mRenderer = null;
    	mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    public MyGLSurfaceView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        reallocRenderer(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                //mRenderer.PASS_VALUES(x,y);
                requestRender();
                
                Toast.makeText(getContext(), String.format("x = %d, y = %d", (int)x, (int)y), 500).show();
        }

        return true;
    }
}