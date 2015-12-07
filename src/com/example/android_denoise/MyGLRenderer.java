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

class Slider 
{
	final float[] m_line_color, m_circle_color;
	/** Store our model data in a float buffer. */
	private FloatBuffer mLineVertices4;
	private FloatBuffer mCircleVertices4;
	/** How many elements per vertex. XYZ+RGBA*/
	private final int mStrideBytes = 3 * 4; // XYZ * bytes per float
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	
	private void drawCircle(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mCircleVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mCircleVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(programHandle, "v_Color"), m_circle_color[0], m_circle_color[1], m_circle_color[2], m_circle_color[3]);
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	private void drawLine(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mLineVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mLineVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(programHandle, "v_Color"), m_line_color[0], m_line_color[1], m_line_color[2], m_line_color[3]);        
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	public Slider(float[] line_color, float[] circle_color){
		m_line_color = line_color;
		m_circle_color = circle_color;
		final float[] line_data = {
				-0.95f, 0.95f, 0.0f, 
	            0.95f, 0.95f, 0.0f, 
	            0.95f, 0.9f, 0.0f,
	            -0.95f, 0.9f, 0.0f
		};
		final float[] circle_data = {
				-0.9f, 0.97f, 0.0f, 
	            -0.8f, 0.97f, 0.0f, 
	            -0.8f, 0.87f, 0.0f,
	            -0.9f, 0.87f, 0.0f
		};
		mLineVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mLineVertices4.put(line_data).position(0);		
		mCircleVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCircleVertices4.put(circle_data).position(0);		
	}
	public void slide(float line_percent){
		
	}
	public void draw(int programHandle, int mMVPMatrixHandle, int mPositionHandle, float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		drawLine(programHandle, mMVPMatrixHandle, mPositionHandle, mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
		drawCircle(programHandle, mMVPMatrixHandle, mPositionHandle, mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
	}
}

public class MyGLRenderer implements GLSurfaceView.Renderer {
	public Context m_actvity_context;
	public String m_texture_path;
	private int m_init_lines_program, m_find_lines_program;
	private int[] m_textures;
	private int m_tex_width, m_tex_height;
	
	int m_programHandle;
	
	int vertexShaderHandle, fragmentShaderHandleInitLines, fragmentShaderHandleFindLines;
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
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

				final String vertexShader =
					"uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
					
				  + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
				  + "void main()                    \n"		// The entry point for our vertex shader.
				  + "{                              \n"
				  + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
				  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in 			                                            			 
				  + "}                              \n";    // normalized screen coordinates.
				
				final String fragmentShader =
					"precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a 
															// precision in the fragment shader.				
				  + "uniform vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the 
				  											// triangle per fragment.			  
				  + "void main()                    \n"		// The entry point for our fragment shader.
				  + "{                              \n"
				  + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.		  
				  + "}                              \n";												
				
				// Load in the vertex shader.
				int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

				if (vertexShaderHandle != 0) 
				{
					// Pass in the shader source.
					GLES20.glShaderSource(vertexShaderHandle, vertexShader);

					// Compile the shader.
					GLES20.glCompileShader(vertexShaderHandle);

					// Get the compilation status.
					final int[] compileStatus = new int[1];
					GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

					// If the compilation failed, delete the shader.
					if (compileStatus[0] == 0) 
					{				
						GLES20.glDeleteShader(vertexShaderHandle);
						vertexShaderHandle = 0;
					}
				}

				if (vertexShaderHandle == 0) throw new RuntimeException("Error creating vertex shader.");
				
				// Load in the fragment shader shader.
				int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

				if (fragmentShaderHandle != 0) 
				{
					// Pass in the shader source.
					GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

					// Compile the shader.
					GLES20.glCompileShader(fragmentShaderHandle);

					// Get the compilation status.
					final int[] compileStatus = new int[1];
					GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

					// If the compilation failed, delete the shader.
					if (compileStatus[0] == 0) 
					{				
						GLES20.glDeleteShader(fragmentShaderHandle);
						fragmentShaderHandle = 0;
					}
				}

				if (fragmentShaderHandle == 0) throw new RuntimeException("Error creating fragment shader.");
				
				// Create a program object and store the handle to it.
				m_programHandle = GLES20.glCreateProgram();
				
				if (m_programHandle != 0) 
				{
					// Bind the vertex shader to the program.
					GLES20.glAttachShader(m_programHandle, vertexShaderHandle);			

					// Bind the fragment shader to the program.
					GLES20.glAttachShader(m_programHandle, fragmentShaderHandle);
					
					// Bind attributes
					GLES20.glBindAttribLocation(m_programHandle, 0, "a_Position");
					GLES20.glBindAttribLocation(m_programHandle, 1, "a_Color");
					
					// Link the two shaders together into a program.
					GLES20.glLinkProgram(m_programHandle);

					// Get the link status.
					final int[] linkStatus = new int[1];
					GLES20.glGetProgramiv(m_programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

					// If the link failed, delete the program.
					if (linkStatus[0] == 0) 
					{				
						GLES20.glDeleteProgram(m_programHandle);
						m_programHandle = 0;
					}
				}
				
				if (m_programHandle == 0) throw new RuntimeException("Error creating program.");
				
		        // Set program handles. These will later be used to pass in values to the program.
		        mMVPMatrixHandle = GLES20.glGetUniformLocation(m_programHandle, "u_MVPMatrix");        
		        mPositionHandle = GLES20.glGetAttribLocation(m_programHandle, "a_Position");		        
		        
		        // Tell OpenGL to use this program when rendering.
		        GLES20.glUseProgram(m_programHandle);        
		        
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
	
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
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
	
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	
	/** This will be used to pass in model position information. */
	private int mPositionHandle;
	
	Slider my_slider;
	public MyGLRenderer(){
		my_slider = new Slider(new float[]{0.8f, 0.8f, 0.8f, 1.0f}, new float[]{0.4f, 0.3f, 1.0f, 1.0f});
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
        
        my_slider.draw(m_programHandle, mMVPMatrixHandle, mPositionHandle, mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);

	}
}