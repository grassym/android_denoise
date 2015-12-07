package com.example.android_denoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
//import android.util.Log;
import android.widget.Toast;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);		
	}

	@Override
	public void onDrawFrame(GL10 gl) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
}