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
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getRawX();
        float y = e.getRawY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                  dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                  dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
                
                Toast.makeText(getContext(), String.format("x = %d, y = %d", (int)x, (int)y), 500).show();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}