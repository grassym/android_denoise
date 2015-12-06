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
	
	public void setupTexture(Bitmap image, Context context){
		mTexSet = 1.0f - mTexSet;
		int[] textures = new int[1];
	    GLES20.glGenTextures(1, textures, 0);

	    // texturecount is just a public int in MyActivity extends Activity
	    // I use this because I have issues with glGenTextures() not working                
//	    textures[0] = context.texturecount;
//	    context.texturecount++;

	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

	    //Create Nearest Filtered Texture
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

	    //Different possible texture parameters, e.g. GLES20.GL_CLAMP_TO_EDGE
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
	    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);

	    image.recycle();   

	    textureID = textures[0];
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