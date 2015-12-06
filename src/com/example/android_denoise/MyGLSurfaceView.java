package com.example.android_denoise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    public MyGLRenderer getRenderer() { return mRenderer; }
    public MyGLSurfaceView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(this, context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:

    
                mRenderer.setAngle(
                        mRenderer.getAngle() + x);
                requestRender();
                
                Toast.makeText(getContext(), String.format("x = %d, y = %d", (int)x, (int)y), 500).show();
        }

        return true;
    }
}