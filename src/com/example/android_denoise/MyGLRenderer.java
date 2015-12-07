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
	public Context m_actvity_context;
	public String m_texture_path;
	private int m_init_lines_program, m_find_lines_program;
	private int[] m_textures;
	
	int vertexShaderHandle, fragmentShaderHandleInitLines, fragmentShaderHandleFindLines;
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
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
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);		
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
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
        
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        
        //Disable arrays

	}
}