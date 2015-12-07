package com.example.android_denoise;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

class MySlider 
{
	final float[] m_line_color, m_circle_color;
	/** Store our model data in a float buffer. */
	private FloatBuffer mLineVertices4;
	private FloatBuffer mCircleVertices4;
	/** How many elements per vertex. XYZ+RGBA*/
	private final int mStrideBytes = 3 * 4; // XYZ * bytes per float
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	
	/** This will be used to pass in model position information. */
	private int mPositionHandle;
	
	private void drawCircle(float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mCircleVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mCircleVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(m_programHandle, "v_Color"), m_circle_color[0], m_circle_color[1], m_circle_color[2], m_circle_color[3]);
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	private void drawLine(float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
		mLineVertices4.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mLineVertices4);        
		GLES20.glEnableVertexAttribArray(mPositionHandle);       
		GLES20.glUniform4f(GLES20.glGetUniformLocation(m_programHandle, "v_Color"), m_line_color[0], m_line_color[1], m_line_color[2], m_line_color[3]);        
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	}
	public MySlider(float[] line_color, float[] circle_color){
		m_line_color = line_color;
		m_circle_color = circle_color;
		final float[] line_data = {
				-0.95f, 0.95f, 0.0f, 
	            0.95f, 0.95f, 0.0f, 
	            0.95f, 0.93f, 0.0f,
	            -0.95f, 0.93f, 0.0f
		};
		final float[] circle_data = {
				-0.7f, 0.97f, 0.0f, 
	            -0.63f, 0.97f, 0.0f, 
	            -0.63f, 0.89f, 0.0f,
	            -0.7f, 0.89f, 0.0f
		};
		mLineVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mLineVertices4.put(line_data).position(0);		
		mCircleVertices4 = ByteBuffer.allocateDirect(line_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCircleVertices4.put(circle_data).position(0);		
	}
	public void slide(float line_percent){
		
	}
	public void draw(float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix){
        GLES20.glUseProgram(m_programHandle);
		drawLine(mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
		drawCircle(mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix);
	}
	
	int m_programHandle;
	public void initProgram(){
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
	}
}