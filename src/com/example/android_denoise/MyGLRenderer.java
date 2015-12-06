package com.example.android_denoise;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
//import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	
	protected int textureID = -1;
	
	protected float mTexSet = 1.0f;
	
	public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

	public void setupTexture(Context context, String path){
		mTexSet = 1.0f - mTexSet;
		int[] textures = new int[1];
	    GLES20.glGenTextures(1, textures, 0);

	    int noisy_tex = TextureHelper.loadTextureFromFile(context, path);
	    
	    textureID = noisy_tex;
	}
	
	public static int loadShader(int type, String shaderCode){
	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(mTexSet, 1.0f, 0.0f, 1.0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClearColor(mTexSet, 1.0f, 0.0f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
}