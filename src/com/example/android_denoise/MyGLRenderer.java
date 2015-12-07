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
	private int m_init_lines_program, m_find_lines_program;
	private int[] m_textures;
	private int m_tex_width, m_tex_height;
	
	int vertexShaderHandle, fragmentShaderHandleInitLines, fragmentShaderHandleFindLines;
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		my_slider.initProgram();
		// Set the background clear color to gray.
				GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
			
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

				
				
		        
		        
		        
		        
/* be_prepared
		// initialize shader handles
		vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(m_actvity_context,
						R.raw.single_tex));
		fragmentShaderHandleInitLines = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(m_actvity_context, R.raw.init_denoise_lines));
		fragmentShaderHandleFindLines = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(m_actvity_context, R.raw.find_denoise_lines));
		
		
		GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		
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
be_prepared */
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
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	
	
	MySlider my_slider;
	public MyGLRenderer(){
		my_slider = new MySlider(new float[]{0.8f, 0.8f, 0.8f, 1.0f}, new float[]{0.4f, 0.3f, 1.0f, 1.0f});
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
/* be_prepared
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		m_init_lines_program = ShaderHelper.createAndLinkProgram(
				vertexShaderHandle,
				fragmentShaderHandleInitLines, 
				new String[] { "a_Position", "a_TexCoordinate" }
				);
		m_find_lines_program = ShaderHelper.createAndLinkProgram(
				vertexShaderHandle,
				fragmentShaderHandleFindLines, 
				new String[] { "a_Position", "a_TexCoordinate" }
				);
		
		// Add program to OpenGL environment
        GLES20.glUseProgram(m_init_lines_program);
        
        // Prepare the triangle data
        
        //Bind texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_textures[0]);
        //GLES20.glViewport(0, 0, m_tex_width, m_tex_height);
        
//        Matrix.
        
        final int _quadi[] = { 0, 1, 2, 2, 3, 0 };
        IntBuffer _qib;
        _qib = ByteBuffer.allocateDirect(_quadi.length
				* 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		_qib.put(_quadi);
		_qib.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, _quadi.length, GLES20.GL_UNSIGNED_INT, _qib);
        
        //Disable arrays
be_prepared */
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);			        
        
        // Draw one translated a bit to the right and rotated to be facing to the left.
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, -0.5f, 0.0f, 0.0f);
        //Matrix.rotateM(mModelMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
        
        my_slider.draw(mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);

	}
	
	private void renderTextureToScreen(){
		//m_textures[0];
	}
}