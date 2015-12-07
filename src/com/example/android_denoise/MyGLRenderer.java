package com.example.android_denoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
//import android.util.Log;
import android.widget.Toast;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	public Context m_actvity_context;
	public String m_texture_path;
	private int[] m_textures;
	private int m_tex_width, m_tex_height;
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	
	MySlider my_slider;
	MyTexture my_texture;
	public MyGLRenderer(){
		my_slider = new MySlider(new float[]{0.8f, 0.8f, 0.8f, 1.0f}, new float[]{0.4f, 0.3f, 1.0f, 1.0f});
		my_texture = new MyTexture();
	}
	
	private void setViewMatrix(){
		// Position the eye behind the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	}
	
	private void loadTextureFromBitmap(){
		// read texture from file
		m_textures = new int[1];
		GLES20.glGenTextures(1, m_textures, 0);
		
		// if can alloc nice texture, do it;
		// else sticky black screen or some splash/logo
		if (m_textures[0] != 0 && m_texture_path != null)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;	// No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeFile(m_texture_path, options);
			m_tex_width = bitmap.getWidth();
			m_tex_height = bitmap.getHeight();
			
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textures[0]);
			
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();						
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		setViewMatrix();
		my_slider.initProgram();
		loadTextureFromBitmap();
		my_texture.setTextures(m_textures, m_actvity_context);
    }

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);		
		final float ratio = (float) width / height;
		final float near = 1.0f;
		final float far = 2.0f;
		Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, near, far);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
        // Draw one translated a bit to the right and rotated to be facing to the left.
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, -0.5f, 0.0f, 0.0f);
        
        my_slider.draw(mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
        my_texture.render(0.0198f, 17);
	}
}